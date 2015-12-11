package com.ese519.auto;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.app.Activity;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

public class MainActivity extends Activity {

    // global vars
    private Button main_b;
    private BluetoothAdapter myBluetoothAdapter;
    private static final String UUID_SERIAL_PORT_PROFILE = "00001101-0000-1000-8000-00805F9B34FB";
    public static BluetoothSocket mSocket = null;
    public static BufferedReader mBufferedReader = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // listen for connect
        main_b = (Button) findViewById(R.id.main_b);
        main_b.setText("Connect to Bluetooth");
        main_b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                find();
                //goToMonitor(); // debugger!!!
            }
        });

    }

    private void goToMonitor() {
        Intent i = new Intent(getApplicationContext(), Monitor.class);
        startActivity(i);
    }

    private void find() {

        main_b.setText("searching...");

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (myBluetoothAdapter.isDiscovering()) {
            myBluetoothAdapter.cancelDiscovery();
        }
        myBluetoothAdapter.startDiscovery();
        BroadcastReceiver bReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    //Toast.makeText(getApplicationContext(),"Found " + device.getName(),Toast.LENGTH_LONG).show();
                    if (device.getName().contains("HC-05")) {
                        //Toast.makeText(getApplicationContext(),"Found HC-05!",Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(),"Found + " + device.getAddress(),Toast.LENGTH_LONG).show();


                        try {
                            openDeviceConnection(device);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }
        };
        registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    private void openDeviceConnection(BluetoothDevice aDevice) throws IOException {
        InputStream aStream = null;
        InputStreamReader aReader = null;
        try {
            mSocket = aDevice.createRfcommSocketToServiceRecord(getSerialPortUUID());
            mSocket.connect();
            aStream = mSocket.getInputStream();
            aReader = new InputStreamReader( aStream );
            mBufferedReader = new BufferedReader( aReader );
            Toast.makeText(getApplicationContext(),"Connected to HC-05!",Toast.LENGTH_LONG).show();

            // go to different view
            goToMonitor();

        } catch ( IOException e ) {
            Toast.makeText(getApplicationContext(),"Couldn't Connect",Toast.LENGTH_LONG).show();
        }
    }

    public static BluetoothSocket getmSocket() {
        return mSocket;
    }

    public static BufferedReader getmBufferedReader() {
        return mBufferedReader;
    }

    private UUID getSerialPortUUID() {
        return UUID.fromString( UUID_SERIAL_PORT_PROFILE );
    }

}