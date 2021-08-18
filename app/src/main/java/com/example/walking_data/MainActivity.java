package com.example.walking_data;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.walking_data.databinding.ActivityMainBinding;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity implements SensorEventListener {

    private TextView mTextView;
    private ActivityMainBinding binding;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer, mGyroscope, mHeartrate;

    private Button button_standing;
    private Button button_walking;
    private Button button_running;
    private Button button_jumping;
    private Button button_stop;

    private FileWriter mFileWriter;

    public String filename ;

    private static List<Float> ax, ay, az;
    private static List<Float> gx, gy, gz;
    private static List<Float> hr;

    private static final int TIME_STAMP = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ax = new ArrayList<>();
        ay = new ArrayList<>();
        az = new ArrayList<>();
        gx = new ArrayList<>();
        gy = new ArrayList<>();
        gz = new ArrayList<>();
        hr = new ArrayList<>();

        filename = "walking_data";

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //Log.d("sensorlist", mSensorManager.getSensorList(Sensor.TYPE_ALL).toString());
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mHeartrate = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

        button_walking = findViewById(R.id.start_collect_walking);
        button_stop = findViewById(R.id.stopall);

        button_walking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectdata();
            }
        });

        button_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSensor();
            }
        });


    }

    public void collectdata()  {

        mSensorManager.registerListener(this, mAccelerometer, 2000000000);
        mSensorManager.registerListener(this, mGyroscope, 2000000000);
        mSensorManager.registerListener(this, mHeartrate, 0);
    }

    public void stopSensor(){
        mSensorManager.unregisterListener(this);
    }





    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            Log.d("sensor_ac_time" ,  getCurrentTimeStamp());
            ax.add(event.values[0]);
            ay.add(event.values[1]);
            az.add(event.values[2]);
            Log.d("sensor_ac_data" ,  event.values[0] + "/" + event.values[1]  + "/"  + event.values[2]);

        }else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            Log.d("sensor_gyr_time" ,  getCurrentTimeStamp());
            gx.add(event.values[0]);
            gy.add(event.values[1]);
            gz.add(event.values[2]);
            Log.d("sensor_gyr_data" ,  event.values[0] + "/" + event.values[1]  + "/"  + event.values[2]);
        }else if(sensor.getType() == Sensor.TYPE_HEART_RATE) {
            Log.d("sensor_hr_time" ,  getCurrentTimeStamp());
            hr.add(event.values[0]);
            hr.add(event.values[0]);
            hr.add(event.values[0]);
            hr.add(event.values[0]);
            hr.add(event.values[0]);
            hr.add(event.values[0]);
            hr.add(event.values[0]);
            hr.add(event.values[0]);
            hr.add(event.values[0]);
            hr.add(event.values[0]);
        }

        try {
            writedown();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void writedown() throws IOException {

        if (ax.size() >= TIME_STAMP && ay.size() >= TIME_STAMP && az.size() >= TIME_STAMP
                && gx.size() >= TIME_STAMP && gy.size() >= TIME_STAMP && gz.size() >= TIME_STAMP
                && hr.size() >= TIME_STAMP) {

            Log.d("sensor_ac_size", ax.size() + "/" + ay.size() + "/" + az.size());
            Log.d("sensor_gy_size", gx.size() + "/" + gy.size() + "/" + gz.size());
            Log.d("sensor_hr_size", String.valueOf(hr.size()));
            String[] data = new String[9];
            for (int i = 0; i < 50; i++) {
                int j = 0;
                data[j++] = getCurrentTimeStamp();
                data[j++] = String.valueOf(hr.get(i));
                data[j++] = String.valueOf(ax.get(i));
                data[j++] = String.valueOf(ay.get(i));
                data[j++] = String.valueOf(az.get(i));
                data[j++] = String.valueOf(gx.get(i));
                data[j++] = String.valueOf(gy.get(i));
                data[j++] = String.valueOf(gz.get(i));
                data[j] = "0";
                writecsv(filename, data);
            }

            ax.clear();
            ay.clear();
            az.clear();
            gx.clear();
            gy.clear();
            gz.clear();
            hr.clear();
        }
    }


    private void writecsv(String fileName, String[] data) throws IOException {
        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        //String fileName = "activity.csv";
        String filePath = baseDir + File.separator + fileName;
        File f = new File(filePath);
        CSVWriter writer;
        // File exist
        if(f.exists()&&!f.isDirectory()){
            mFileWriter = new FileWriter(filePath, true);
            writer = new CSVWriter(mFileWriter);
        }else{
            writer = new CSVWriter(new FileWriter(filePath));
        }

        writer.writeNext(data);
        writer.close();
        Log.d("file_created:", getCurrentTimeStamp());
    }


    public String getCurrentTimeStamp() {
        return new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}