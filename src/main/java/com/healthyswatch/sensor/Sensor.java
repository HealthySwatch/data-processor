package com.healthyswatch.sensor;

public interface Sensor<T extends SensorData> {

    String getName();

    Class<? extends Sensor<?>> getSensorType();

    T read();

    T value();

}