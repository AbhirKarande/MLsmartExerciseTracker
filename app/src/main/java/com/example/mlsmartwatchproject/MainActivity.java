package com.example.mlsmartwatchproject;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.SparseInstance;
import static weka.core.SerializationHelper.read;

public class MainActivity extends Activity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "MainActivity";
    public GoogleApiClient mApiClient;
    private SensorManager sensorManager;
    Sensor accelerometer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(MainActivity.this)
                .addOnConnectionFailedListener(MainActivity.this)
                .build();
        mApiClient.connect();
        Log.d(TAG, "onCreate: Initializing Sensor Services");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//        Classifier cls = null;
//        try {
//            cls = (Classifier) read(getAssets().open("J48ExerciseRecognition.model"));
//            ArrayList<Double> xAcceleration = new ArrayList<>();
//            ArrayList<Double> yAcceleration = new ArrayList<>();
//            ArrayList<Double> zAcceleration = new ArrayList<>();
//        }catch(Exception e){
//            System.out.println(e);
//            Toast.makeText()
//        }
        Log.d(TAG, "onCreate: Registered accelerometer listener");

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent(MainActivity.this, ActivityRecognizedService.class);
        PendingIntent pendingIntent = PendingIntent.getService(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 2000, pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.d(TAG, "onSensorChanged: X: " + sensorEvent.values[0] + " Y: " + sensorEvent.values[1] + " Z: " + sensorEvent.values[2]);

    }
}