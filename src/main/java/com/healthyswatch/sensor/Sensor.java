package com.healthyswatch.sensor;

import java.util.Collection;

public interface Sensor<T extends SensorData> {

    String name();

    Class<? extends Sensor<T>> type();

    Collection<SensorProcessor<T>> processors();

    T read();

    T value();

}