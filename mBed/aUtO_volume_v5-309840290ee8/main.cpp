#include "mbed.h"
#include "Pixy.h"
#include "rtos.h"

// init board
Pixy pixy(Pixy::SPI, p11, p12, p13);
Serial pc(USBTX, USBRX);
DigitalOut pin1(p24);
DigitalOut pin2(p25);

#if   defined(TARGET_LPC1768)
Serial blue(p9, p10);          // TX, RX
//Serial blue(p13, p14);         // TX, RX
#elif defined(TARGET_LPC4330_M4)
Serial blue(P6_4, P6_5);         // UART0_TX, UART0_RX
//Serial blue(P2_3, P2_4);         // UART3_TX, UART3_RX
#endif

// global vars
int num_readings = 0;
int total_y = 0;
int numOverflows = 0;
float total_UO = 0.0;
float result_level = 0.0; // mL
float result_hourly = 0.0; // mL/hr
float prev_level = 0.0;

int gotFirstReading = 0;
int isDraining = 0;
int gotFirstHourly = 0;

// funcs and threads
void get_volume(int i);
void adjustRate();
void get_hourly(void const *args);
void check_overflow();
RtosTimer * rate_thread;

int main() {
    
    // init bluetooth
    blue.baud(9600);
    
    // init pc
    pc.baud(9600);
    pc.printf("Bluetooth Start\r\n");
    pc.printf("ready\n\r");
    
    // init pixy
    pixy.setSerialOutput(&pc);
    
    // start with pumps off
    pin1 = 0;
    pin2 = 0;
    
    // start hourly thread
    rate_thread = new RtosTimer(get_hourly, osTimerPeriodic, (void *)0);
    rate_thread->start(15000);

    while (1) {
       
        // get pixy data
        uint16_t blocks;
        blocks = pixy.getBlocks();
        
        // store data
        if (blocks) {
            for (int j = 0; j < blocks; j++) {
                get_volume(pixy.blocks[j].y);
            }
        }
        
        // check for overflow
        check_overflow();
        
        // adjust rate
        //if (!isDraining) adjustRate();
    }
}

void adjustRate() {
    
    if (gotFirstHourly) {
        if (result_hourly < 60.0) {
            
        }
        else if (result_hourly < 120.0) {
        
        }
        else {
            
        }
    }
}

void check_overflow() {
    if (result_level >= 30.0 && !isDraining) { //led1 = 1;
        pin1 = 0;
        pin2 = 1;
        isDraining = 1;
        numOverflows++;
    }
    if (result_level < 4.0) {
        pin2 = 0;
        pin1 = 1;
        isDraining = 0;
    }
}

void get_hourly(void const *args) {
    pc.printf("calc'ing hourly...%f %f\r\n", result_level, prev_level);
    if (!isDraining && gotFirstReading) {
        float temp = (result_level-prev_level)*4.0;
        if (temp >= 0) {
            result_hourly = temp;
            prev_level = result_level;
            gotFirstHourly = 1;
        }
    }
    if (isDraining) gotFirstReading = 0;
}

void get_volume(int y) {
    
    // update data
    total_y += y;
    num_readings++;
    
    // output results
    if (num_readings >= 10) {
        float average_y = (float)total_y/num_readings;
        result_level = -0.2642*average_y + 38.453;
        
        if (!gotFirstReading && !isDraining) {
            gotFirstReading = 1;
            prev_level = result_level;
        }
        
        // to pc
        pc.printf("y = %d, num_readings = %d, average = %.2f, mL = %.2f, rate = %.2f\r\n", y, num_readings, average_y, result_level, result_hourly);
        pc.printf("%.2f %.2f\r\n", result_level, result_hourly);
        
        // to bluetooth
        if (!isDraining) total_UO = result_level+(float)numOverflows*26.0;
        blue.printf("%.2f %.2f\r\n", total_UO, result_hourly);

        // reset vars
        num_readings = 0;
        total_y = 0;
    }
}