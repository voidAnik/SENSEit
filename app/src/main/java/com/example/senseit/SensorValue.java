package com.example.senseit;

import java.io.Serializable;

public class SensorValue implements Serializable {
    public double light_value = 0.00;
    public double proxy_value = 0.00;
    public double[] accelerometer_value = {0.00, 0.00, 0.00};
    public double[] gyro_value = {0.00, 0.00, 0.00};

    public SensorValue() {
    }

    public SensorValue(double light_value, double proxy_value, double[] accelerometer_value, double[] gyro_value) {
        this.light_value = light_value;
        this.proxy_value = proxy_value;
        this.accelerometer_value = accelerometer_value;
        this.gyro_value = gyro_value;
    }
}
