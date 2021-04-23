package com.healthyswatch.manager.impl;

import com.healthyswatch.manager.SensorManager;
import com.healthyswatch.sensor.Sensor;
import com.healthyswatch.sensor.SensorData;
import com.healthyswatch.sensor.SensorProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SensorManagerImpl implements SensorManager {

    private final Logger logger = LoggerFactory.getLogger(SensorManager.class);

    private final Collection<Sensor<?>> sensors;
    private final Map<Class<? extends Sensor<?>>, Collection<SensorProcessor<?,?>>> processors;
    private final ReadWriteLock tickingLock;

    public SensorManagerImpl() {
        this.sensors = new HashSet<>();
        this.processors = new HashMap<>();
        this.tickingLock = new ReentrantReadWriteLock();
    }

    @Override
    public boolean registerSensor(Sensor<?> sensor) {
        this.tickingLock.writeLock().lock();
        try {
            return this.sensors.add(sensor);
        } finally {
            this.tickingLock.writeLock().unlock();
        }
    }

    @Override
    public <R extends SensorData, T extends Sensor<R>> void registerSensorProcessor(Class<T> sensorType, SensorProcessor<R, T> sensorProcessor) {
        this.tickingLock.writeLock().lock();
        try {
            Collection<SensorProcessor<?,?>> collection = this.processors.computeIfAbsent(sensorType, k -> new ArrayList<>());
            collection.add(sensorProcessor);
        } finally {
            this.tickingLock.writeLock().unlock();
        }
    }

    @Override
    public void tickSensors() {
        this.tickingLock.readLock().lock();
        try {
            for (Sensor<?> sensor : sensors) {
                SensorData readData = sensor.read();
                if (readData != null) {
                    Collection<SensorProcessor<?,?>> processors = this.processors.get(sensor.getSensorType());
                    for (SensorProcessor<?, ?> processor : processors) {
                        processor.processUnsafe(sensor, readData);
                    }
                }
            }
        } catch (Exception exception) {
            logger.error("Exception while ticking sensors", exception);
        } finally {
            this.tickingLock.readLock().unlock();
        }
    }
}
