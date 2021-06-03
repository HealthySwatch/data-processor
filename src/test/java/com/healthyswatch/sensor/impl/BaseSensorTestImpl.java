package com.healthyswatch.sensor.impl;

import com.healthyswatch.sensor.Sensor;
import com.healthyswatch.sensor.SensorData;
import com.healthyswatch.sensor.SensorProcessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public abstract class BaseSensorTestImpl<T extends SensorData> implements Sensor<T> {

    protected final String name;
    protected final Collection<SensorProcessor<T>> processors = new ArrayList<>();
    protected final Queue<T> dataQueue = new LinkedList<>();

    public BaseSensorTestImpl(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Collection<SensorProcessor<T>> processors() {
        return processors;
    }

    @Override
    public T read() {
        return dataQueue.poll();
    }

    @Override
    public T value() {
        return dataQueue.peek();
    }

    public Queue<T> getDataQueue() {
        return this.dataQueue;
    }
}
