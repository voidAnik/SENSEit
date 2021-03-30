package com.example.senseit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    //Defined variables
    RelativeLayout light_card, proxy_card, accelerometer_card, gyro_card;
    TextView light_value, proxy_value, accelerometer_value, gyro_value;

    //Sensor related variables
    SensorManager sensorManager;
    Sensor light_sensor, proximity_sensor, accelerometer_sensor, gyroscope_sensor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, light_sensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, proximity_sensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, accelerometer_sensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope_sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_LIGHT){
            light_value.setText(event.values[0]+ " (lux)");
        }
        if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){
            proxy_value.setText(event.values[0]+ " (cm)");
        }
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            accelerometer_value.setText("X:" + String.format("%.2f",event.values[0]) + "  Y:" + String.format("%.2f",event.values[1]) );//+ "(m/s^2)"
        }
        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            gyro_value.setText("X:" + String.format("%.2f",event.values[0]) + "  Y:" + String.format("%.2f",event.values[1]));//+ " (rad/s)"
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}