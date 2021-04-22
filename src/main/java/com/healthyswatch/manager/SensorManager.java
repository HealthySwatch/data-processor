package com.healthyswatch.manager;

import com.healthyswatch.sensor.Sensor;
import com.healthyswatch.sensor.SensorData;
import com.healthyswatch.sensor.SensorProcessor;

public interface SensorManager {

    boolean registerSensor(Sensor<?> sensor);

    <R extends SensorData, T extends Sensor<R>> void registerSensorProcessor(Class<T> sensorClass, SensorProcessor<R, T> sensorProcessor);

    void tickSensors();

}