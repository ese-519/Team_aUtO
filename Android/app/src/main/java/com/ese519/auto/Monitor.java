package com.ese519.auto;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import org.achartengine.GraphicalView;
import java.io.BufferedReader;
import java.io.IOException;

public class Monitor extends Activity {

    private static GraphicalView graphicalView;
    private LineGraph lineGraph = new LineGraph();
    private static Thread thread_graph;
    private static Thread thread_bt;

    Handler handler = new Handler();
    Runnable alarmRunnable = new Runnable() {
        @Override
        public void run() {
            check_alarm();
            handler.postDelayed(alarmRunnable,1000);
        }
    };

    private BluetoothSocket mSocket;
    private BufferedReader mBufferedReader;
    private float cur_level = 0;
    private float cur_hourly = 0;
    private int numPoints = 60;
    private boolean reached10 = false;
    private Point [] ratePoints = new Point [numPoints]; // previous minute
    private Point [] levelPoints = new Point[numPoints];

    private MediaPlayer mediaPlayer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        // init
        mSocket = MainActivity.getmSocket();
        mBufferedReader = MainActivity.getmBufferedReader();
        mediaPlayer = new MediaPlayer();

        // init points array
        for (int i=0;i<numPoints;i++) {
            ratePoints[i] = new Point(-numPoints+i+1, 0);
            levelPoints[i] = new Point(-numPoints+i+1, 0);
        }


        // start alam check
        //ßhandler.postDelayed(alarmRunnable,1000);

        // start data collection
        thread_bt = new Thread() {
            public void run() {
                while (true) {
                    try {
                        read_output();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        };

        thread_graph = new Thread() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // get newest data
                    int raw_rate = (int) cur_hourly;
                    int raw_level = (int) cur_level;
                    updatePlotArray(raw_rate, raw_level);

                    // repaint graph
                    graphicalView.repaint();

                    // get point and update counter debugger!!!!
                    /*
                    int cur_y = MockData.getDataFromReceiver(1).getY();
                    totalUO += cur_y;
                    updatePlotArray(cur_y, totalUO);
                    graphicalView.repaint();
                    */

                }
            }
        };
        thread_graph.start();
        thread_bt.start();
    }

    private void check_alarm() {
        if (reached10) {
            if (cur_hourly < 5) {
                sound_alarm();
            }
        }
        if (cur_hourly >= 10) reached10 = true;

    }

    private void sound_alarm() {
        // play alarm
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        try {
            mediaPlayer.setDataSource(this, notification);
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updatePlotArray(int y_hourly, int y_level) {
        for (int i=0;i<59;i++) {
            ratePoints[i] = new Point(-numPoints+i+1, ratePoints[i+1].getY());
            levelPoints[i] = new Point(-numPoints+i+1, levelPoints[i+1].getY());
        }
        ratePoints[numPoints-1] = new Point(0, y_hourly);
        levelPoints[numPoints-1] = new Point(0, y_level);
        lineGraph.clearLine();
        for (int i=0;i<numPoints;i++) {
            lineGraph.addPoint_rate(ratePoints[i]);
            lineGraph.addPoint_level(levelPoints[i]);
        }
    }

    private void parseOutput(String s) {
        // output in form : "cur_level hourly" in mL and mL/hr
        String [] temp = s.split(" ");
        if (temp.length == 2) {
            cur_level = Float.parseFloat(temp[0]);
            cur_hourly = Float.parseFloat(temp[1]);
        }
    }

    private String read_output() throws IOException {
        String s = null;
        if (mBufferedReader != null) {
            s = mBufferedReader.readLine();
            parseOutput(s);
        }
        return s;
    }

    protected void onStart() {
        super.onStart();
        graphicalView = lineGraph.getView(this);
        setContentView(graphicalView);
    }




}
