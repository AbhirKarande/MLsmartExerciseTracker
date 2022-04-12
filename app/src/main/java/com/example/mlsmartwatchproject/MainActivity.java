package com.example.mlsmartwatchproject;
import android.app.Activity;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener{

    private TextView mTextView;
    private static final String TAG = "MainActivity";
    private TextView mTextViewAccelerometerX;
    private TextView mTextViewAccelerometerY;
    private TextView mTextViewAccelerometerZ;
    private TextView mTextViewAccelerometerDetect;
    private TextView mTextViewGyroscope;
    private TextView mTextViewGyroscopeDetect;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener(){
            @Override
            public void onLayoutInflated(WatchViewStub stub){
                mTextViewAccelerometer = (TextView) stub.findViewById(R.id.accelerometer)
            }
        })
        mTextView = (TextView) findViewById(R.id.text);

        // Enables Always-on
        setAmbientEnabled();
    }
}