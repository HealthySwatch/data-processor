package com.healthyswatch.sensor.blood_oxygen;

import com.healthyswatch.sensor.Sensor;

public interface BloodOxygenSensor extends Sensor<BloodOxygenSensorData> {

    @Override
    default Class<BloodOxygenSensor> type() {
        return BloodOxygenSensor.class;
    }

    float getAlertThreshold();

}
