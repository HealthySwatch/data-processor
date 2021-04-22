package com.healthyswatch.sensor.heart_rate;

import com.healthyswatch.sensor.Sensor;

public interface HeartRateSensor extends Sensor<HeartRateSensorData> {

    @Override
    default Class<HeartRateSensor> getSensorType() {
        return HeartRateSensor.class;
    }

    int getMinThreshold();

    int getMaxThreshold();

}
