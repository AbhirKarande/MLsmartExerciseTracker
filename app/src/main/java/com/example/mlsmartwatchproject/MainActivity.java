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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.SparseInstance;
import weka.classifiers.trees.J48;
import static weka.core.SerializationHelper.read;

public class MainActivity extends Activity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "MainActivity";
    public GoogleApiClient mApiClient;
    private SensorManager sensorManager;
    Sensor accelerometer;
//    ArrayList<Float> x = new ArrayList<Float>();
    private final int window_size = 4;
    private final int features_selected = 4;
    private float[] x = new float[window_size*300];
    private int index = 0;
    private boolean full = false;
    private String[][] data = new String[2][features_selected+1];
    private String[] features = {"mean_x","median_x","median_y","median_z","label"};
    private Classifier cls = null;

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
        sensorManager.registerListener(MainActivity.this, accelerometer, 10000);

        try {
            cls = (Classifier) read(getAssets().open("J48ExcerciseRecognition.model"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        data[0] = features;
        
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
        //Log.d(TAG, "onSensorChanged: X: " + sensorEvent.values[0] + " Y: " + sensorEvent.values[1] + " Z: " + sensorEvent.values[2]);
//        x.add(sensorEvent.values[0]);
        if(index == window_size*100-1)
        	full = true;
        x[index*3] = sensorEvent.values[0];
        x[index*3+1] = sensorEvent.values[1];
        x[index*3+2] = sensorEvent.values[2];
        index = (index+1)%(window_size*100);
        if(full&&index%100 == 1) {
        	data[1][0] = String.valueOf((x[0]+x[3]+x[6]+x[9])/4);
        	data[1][1] = String.valueOf(findMedian(0));
        	data[1][2] = String.valueOf(findMedian(1));
        	data[1][3] = String.valueOf(findMedian(2));
        	data[1][4] = "?";
            String arffData = null;
            try {
                arffData = MyWekaUtils.csvToArff(data, new int[]{0,1,2,3});
            } catch (Exception e) {
                e.printStackTrace();
            }
            StringReader strReader = new StringReader(arffData);
            Instances unlabeled = null;
            try {
                unlabeled = new Instances(strReader);
            } catch (IOException e) {
                e.printStackTrace();
            }
            unlabeled.setClassIndex(unlabeled.numAttributes() - 1);
            double clsLabel = 0;
            try {
                clsLabel = cls.classifyInstance(unlabeled.instance(0));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG, "Classification: " + clsLabel);
        }
    }
    
    private float findMedian(int i) {
		PriorityQueue<Float> p = new PriorityQueue<>();
		for(int j = 0; j < 400; j++) {
			if(p.size()==201) {
				p.poll();
			}
			p.add(x[i+j*3]);
		}
		float a = p.poll();
		float b = p.poll();
		return (a+b)/2;
    }

}