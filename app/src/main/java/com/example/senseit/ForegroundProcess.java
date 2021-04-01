package com.example.senseit;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;

import java.util.Arrays;

import static com.example.senseit.Globe.CHANNEL_ID;

public class ForegroundProcess extends Service implements SensorEventListener {
    SensorValue sensor_values;
    public Boolean aBoolean;
    PendingIntent pendingIntent;

    //Sensor variables
    SensorManager sensorManager;
    Sensor light_sensor, proximity_sensor, accelerometer_sensor, gyroscope_sensor;
    private static final int NOTIFICATION_ID = 101;

    //Database related variables
    DatabaseHelper databaseHelper;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sensorManager.registerListener(this, light_sensor, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, proximity_sensor, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, accelerometer_sensor, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, gyroscope_sensor, SensorManager.SENSOR_DELAY_FASTEST);

        sensor_values =(SensorValue) intent.getSerializableExtra("sensor_values");
        aBoolean = intent.getBooleanExtra("bool", true);
        Intent notifyIntent = new Intent(this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, 0);
        //NotificationCompat.Builder nBuilder =
        Notification notification = createNotification();




        if(aBoolean) {
            startForeground(NOTIFICATION_ID, notification);
        }
        else {
            stopForeground(true); // Stops foreground service for a button click on mainActivity
            sensorManager.unregisterListener(this);
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        if(sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)!=null) {
            light_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)!=null) {
            proximity_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null) {
            accelerometer_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!=null) {
            gyroscope_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }


        //Database related
        databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        final Handler handler = new Handler(); // handler to save the sensor data to database every 5 minute
        final int delay =300000; // 1000 milliseconds == 1 second
        handler.postDelayed(new Runnable() {
            public void run() {
                save_data(sensor_values);
                //Toast.makeText(ForegroundProcess.this, "Saved data", Toast.LENGTH_SHORT).show();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.senseit_logo)
                .setContentTitle(getString(R.string.sensing_title) + ":")
                .setContentText("Click expand to see the live values")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Light sensor value: " + sensor_values.light_value
                                + "\nProximity sensor value: " + sensor_values.proxy_value
                                +"\nAccelerometer sensor values: X:" +sensor_values.accelerometer_value[0]+" Y:"+sensor_values.accelerometer_value[1]+" Z:"+sensor_values.accelerometer_value[2]
                                +"\nGyroscope sensor values: X:" +sensor_values.gyro_value[0]+" Y:"+sensor_values.accelerometer_value[1]+" Z:"+sensor_values.accelerometer_value[2]))
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*stopForeground(true);
        sensorManager.unregisterListener(this);*/
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_LIGHT){
            sensor_values.light_value = event.values[0];
        }
        if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){
            sensor_values.proxy_value = Double.parseDouble(String.format("%.2f",event.values[0]));
        }
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            sensor_values.accelerometer_value[0] = Double.parseDouble(String.format("%.2f",event.values[0]));
            sensor_values.accelerometer_value[1] = Double.parseDouble(String.format("%.2f",event.values[1]));
            sensor_values.accelerometer_value[2] = Double.parseDouble(String.format("%.2f",event.values[2]));
        }
        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            sensor_values.gyro_value[0] = Double.parseDouble(String.format("%.2f",event.values[0]));
            sensor_values.gyro_value[1] = Double.parseDouble(String.format("%.2f",event.values[1]));
            sensor_values.gyro_value[2] = Double.parseDouble(String.format("%.2f",event.values[2]));
        }

        if(aBoolean) {
            startForeground(NOTIFICATION_ID, createNotification());
        }
        else {
            stopForeground(true);
            sensorManager.unregisterListener(this);
        }
    }

    private void save_data(SensorValue sensor_values) {
        long[] row_ids = databaseHelper.insertData(sensor_values);

        //Toast.makeText(this, ""+ Arrays.toString(row_ids), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
