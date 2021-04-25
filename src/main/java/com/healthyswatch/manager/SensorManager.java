package com.healthyswatch.manager;

import com.healthyswatch.sensor.Sensor;

public interface SensorManager {

    boolean registerSensor(Sensor<?> sensor);

    void tickSensors();

}