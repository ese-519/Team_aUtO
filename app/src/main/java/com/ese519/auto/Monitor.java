package com.ese519.auto;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.widget.Toast;

import org.achartengine.GraphicalView;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class Monitor extends Activity {

    private static GraphicalView graphicalView;
    private LineGraph lineGraph = new LineGraph();
    private static Thread thread;
    private BluetoothSocket mSocket;
    private BufferedReader mBufferedReader;
    private float totalUO = 0;
    private Point [] myPoints = new Point [60]; // previous minute

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        // init
        mSocket = MainActivity.getmSocket();
        mBufferedReader = MainActivity.getmBufferedReader();

        // init points array
        for (int i=0;i<60;i++) {
            myPoints[i] = new Point(-60+i, 0);
        }

        // start data collection
        thread = new Thread() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // get point and update counter
                    updatePlotArray(MockData.getDataFromReceiver(1).getY());

                    graphicalView.repaint();


                    /*
                    String bt_data = null;
                    try {
                        bt_data = read_output();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (bt_data != null) {
                        float raw_vol = Float.parseFloat(bt_data);
                        int vol = (int) raw_vol;
                        //myPoints.add(new Point(numReadings, vol));
                        //lineGraph.clearLine();
                        //for (int i=0;i<myPoints.size();i++) lineGraph.addPoint(new Point(numReadings, vol));
                        numReadings++;
                    }
                    */
                }
            }
        };
        thread.start();
    }

    private void updatePlotArray(int y) {
        for (int i=0;i<59;i++) {
            myPoints[i] = new Point(-60+i, myPoints[i+1].getY());
        }
        myPoints[59] = new Point(-1, y);
        lineGraph.clearLine();
        for (int i=0;i<60;i++) lineGraph.addPoint(myPoints[i]);
    }

    private String read_output() throws IOException {
        if (mBufferedReader != null) {
            String s;
            s = mBufferedReader.readLine();
            //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            return s;
        } else {
            Toast.makeText(getApplicationContext(), "Lost Connection", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    protected void onStart() {
        super.onStart();
        graphicalView = lineGraph.getView(this);
        setContentView(graphicalView);
    }


}
