package com.healthyswatch.sensor;

public interface SensorProcessor<T extends SensorData> {

    void process(T data);

}
