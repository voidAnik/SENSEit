package com.example.senseit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    //Defined variables
    RelativeLayout light_card, proxy_card, accelerometer_card, gyro_card;
    TextView light_value, proxy_value, accelerometer_value, gyro_value;

    //Sensor related variables
    SensorManager sensorManager;
    Sensor light_sensor, proximity_sensor, accelerometer_sensor, gyroscope_sensor;
    SensorValue sensor_values;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensor_values = new SensorValue();

        // Custom Binding
        light_card = findViewById(R.id.light_card);
        proxy_card = findViewById(R.id.proxy_card);
        accelerometer_card = findViewById(R.id.accelerometer_card);
        gyro_card = findViewById(R.id.gyro_card);

        light_value = findViewById(R.id.light_value);
        proxy_value = findViewById(R.id.proxy_value);
        accelerometer_value = findViewById(R.id.accelerometer_value);
        gyro_value = findViewById(R.id.gyro_value);

        // Sensor binding
        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        sensor_values = new SensorValue();

        if(sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)!=null) {
            light_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }else{
            light_value.setText(R.string.not_available_light);
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)!=null) {
            proximity_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }else{
            proxy_value.setText(R.string.not_available_proxy);
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null) {
            accelerometer_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }else{
            accelerometer_value.setText(R.string.not_available_accelerometer);
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!=null) {
            gyroscope_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }else{
            gyro_value.setText(R.string.not_available_gyro);
        }

        Intent serviceIntent = new Intent(this, ForegroundProcess.class);
        serviceIntent.putExtra("sensor_values", sensor_values);
        serviceIntent.putExtra("bool", true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        //sensorManager.unregisterListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        sensorManager.registerListener(this, light_sensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, proximity_sensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, accelerometer_sensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope_sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*sensorManager.registerListener(this, light_sensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, proximity_sensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, accelerometer_sensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope_sensor, SensorManager.SENSOR_DELAY_NORMAL);*/
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_LIGHT){
            light_value.setText(String.valueOf(event.values[0]));
            /*sensor_values.light_value = event.values[0];*/
        }
        if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){
            proxy_value.setText(String.valueOf(event.values[0]));
            /*sensor_values.proxy_value = Double.parseDouble(String.format("%.2f",event.values[0]));*/
        }
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            accelerometer_value.setText("X:" + String.format("%.2f",event.values[0]) + "  Y:" + String.format("%.2f",event.values[1]) + "  Z:" + String.format("%.2f",event.values[2]) );//+ "(m/s^2)"
            /*sensor_values.accelerometer_value[0] = Double.parseDouble(String.format("%.2f",event.values[0]));
            sensor_values.accelerometer_value[1] = Double.parseDouble(String.format("%.2f",event.values[1]));
            sensor_values.accelerometer_value[2] = Double.parseDouble(String.format("%.2f",event.values[2]));*/
        }
        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            gyro_value.setText("X:" + String.format("%.2f",event.values[0]) + "  Y:" + String.format("%.2f",event.values[1]) + "  Z:" + String.format("%.2f",event.values[2]));//+ " (rad/s)"
            /*sensor_values.gyro_value[0] = Double.parseDouble(String.format("%.2f",event.values[0]));
            sensor_values.gyro_value[1] = Double.parseDouble(String.format("%.2f",event.values[1]));
            sensor_values.gyro_value[2] = Double.parseDouble(String.format("%.2f",event.values[2]));*/
        }
        /*Intent serviceIntent = new Intent(this, ForegroundProcess.class);
        serviceIntent.putExtra("sensor_values", sensor_values);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        }*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.stop_fg:
                Intent serviceIntent = new Intent(this, ForegroundProcess.class);
                serviceIntent.putExtra("sensor_values", sensor_values);
                serviceIntent.putExtra("bool", false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent);
                }
                return true;
            case R.id.exit:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to exit?")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                sensorManager.unregisterListener(MainActivity.this);
                                sensorManager.unregisterListener(MainActivity.this);
                                sensorManager.unregisterListener(MainActivity.this);
                                sensorManager.unregisterListener(MainActivity.this);
                                finishAffinity();
                            }
                        }).show();

        }
        return true;
    }
}