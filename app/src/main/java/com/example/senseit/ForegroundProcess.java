package com.example.senseit;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.example.senseit.Globe.CHANNEL_ID;

public class ForegroundProcess extends Service{
    SensorValue sensor_values;
    PendingIntent pendingIntent;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensor_values =(SensorValue) intent.getSerializableExtra("sensor_values");
        Intent notifyIntent = new Intent(this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, 0);
        //NotificationCompat.Builder nBuilder =
        Notification notification = createNotification();

        startForeground(1, notification);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //startForeground(1, createNotification());
    }

    private Notification createNotification() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
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
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
        return notification;
    }

    @Override
    public void onDestroy() {
        //super.onDestroy();
        //stopForeground(true);
        //startForeground(1, createNotification());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
