package com.example.senseit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {
    //Defined variables
    RelativeLayout light_card, proxy_card, accelerometer_card, gyro_card;
    TextView light_value, proxy_value, accelerometer_value, gyro_value;

    //Sensor related variables
    SensorManager sensorManager;
    Sensor light_sensor, proximity_sensor, accelerometer_sensor, gyroscope_sensor;
    SensorValue sensor_values;
    Handler handler;
    Runnable runnable;

    //Customs
    Boolean goingHistory;
    Boolean serviceStopped;

    @SuppressLint("BatteryLife")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // tp turn of the default night mode
        setContentView(R.layout.activity_main);

        // To ignore battery optimization for foreground service
        // To whitelist the application
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }

        // Custom Binding and initializing to OnclickListener
        light_card = findViewById(R.id.light_card);
        light_card.setOnClickListener(this);
        proxy_card = findViewById(R.id.proxy_card);
        proxy_card.setOnClickListener(this);
        accelerometer_card = findViewById(R.id.accelerometer_card);
        accelerometer_card.setOnClickListener(this);
        gyro_card = findViewById(R.id.gyro_card);
        gyro_card.setOnClickListener(this);

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

        //Saving data every 5 minutes when in application
        handler = new Handler(); // handler to save the sensor data to database every 5 minute
        final int delay =300000; // 1000 milliseconds == 1 second
        runnable = new Runnable() {
            public void run() {
                long[] unused = new DatabaseHelper(MainActivity.this).insertData(sensor_values);
                handler.postDelayed(this, delay);
            }
        };
        handler.postDelayed(runnable,delay);


    }

    @Override
    protected void onPause() {
        if(!goingHistory && !serviceStopped) {
            // Starting the foreground services with the live notification when application is minimized
            Intent serviceIntent = new Intent(this, ForegroundProcess.class);
            serviceIntent.putExtra("sensor_values", sensor_values);
            serviceIntent.putExtra("bool", true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            }
            Globe.inApp = false;
        }
        sensorManager.unregisterListener(this);
        super.onPause();

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Registering the sensors
        sensorManager.registerListener(this, light_sensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, proximity_sensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, accelerometer_sensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, gyroscope_sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Foreground service is being killed when application opens
        goingHistory = false;
        serviceStopped = false;
        if(!Globe.inApp) {
            Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
            Intent serviceIntent = new Intent(this, ForegroundProcess.class);
            serviceIntent.putExtra("sensor_values", sensor_values);
            serviceIntent.putExtra("bool", false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            }
        }
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onSensorChanged(SensorEvent event) {

        // Getting values from sensors on every changes
        if(event.sensor.getType() == Sensor.TYPE_LIGHT){
            light_value.setText(String.valueOf(event.values[0]));
        }
        if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){
            proxy_value.setText(String.valueOf(event.values[0]));
        }
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            accelerometer_value.setText("X:" + String.format("%.2f",event.values[0]) + "  Y:" + String.format("%.2f",event.values[1]) + "  Z:" + String.format("%.2f",event.values[2]) );//+ "(m/s^2)"
        }
        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            gyro_value.setText("X:" + String.format("%.2f",event.values[0]) + "  Y:" + String.format("%.2f",event.values[1]) + "  Z:" + String.format("%.2f",event.values[2]));//+ " (rad/s)"

        }
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
        sensorManager.unregisterListener(this); // unregister the sensors on destroy

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.stop_fg: // Menu button to stop the foreground service
                if(!serviceStopped){
                    Toast.makeText(this, Html.fromHtml("<font color='"+ Color.RED +"' >" + "Background Notification Service Stopped!" + "</font>"), Toast.LENGTH_SHORT).show();
                    item.setTitle("START NOTIFY SERVICE");
                    item.setIcon(R.drawable.ic_baseline_circle_24);
                    serviceStopped = true;
                }else {
                    serviceStopped = false;
                    item.setTitle("STOP NOTIFY SERVICE");
                    item.setIcon(R.drawable.ic_baseline_warning_24_red);
                    Toast.makeText(this, Html.fromHtml("<font color='"+ getResources().getColor(R.color.dark_yellow) +"' >" + "Background Notification Service Started!" + "</font>"), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.exit: // To exit the application
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to exit?")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                sensorManager.unregisterListener(MainActivity.this);
                                finishAffinity();
                            }
                        }).show();
                break;

        }
      return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {

        // response to click on cards
        switch (view.getId())
        {
            case R.id.light_card:
                goToHistory(1);
                break;
            case R.id.proxy_card:
                goToHistory(2);
                break;
            case R.id.accelerometer_card:
                goToHistory(3);
                break;
            case R.id.gyro_card:
                goToHistory(4);
                break;
        }

    }

    private void goToHistory(int id) {

        // To history activity
        goingHistory = true;
        Intent history_intent = new Intent(MainActivity.this, HistoryActivity.class);
        history_intent.putExtra("sensor_id", id);
        startActivity(history_intent);
    }


}