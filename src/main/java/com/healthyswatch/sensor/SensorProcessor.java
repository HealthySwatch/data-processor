package com.healthyswatch.sensor;

public interface SensorProcessor<R extends SensorData, T extends Sensor<R>> {

    void process(T sensor, R data);

    default void processUnsafe(Sensor<?> sensor, SensorData data) {
        this.process((T)sensor, (R)data);
    }
}
