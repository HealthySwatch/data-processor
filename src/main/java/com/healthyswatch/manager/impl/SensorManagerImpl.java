package com.healthyswatch.manager.impl;

import com.healthyswatch.manager.SensorManager;
import com.healthyswatch.sensor.Sensor;
import com.healthyswatch.sensor.SensorData;
import com.healthyswatch.sensor.SensorProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SensorManagerImpl implements SensorManager {

    private final Logger logger = LoggerFactory.getLogger(SensorManager.class);

    private final Collection<Sensor<?>> sensors;
    private final ReadWriteLock tickingLock;

    public SensorManagerImpl() {
        this.sensors = new HashSet<>();
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
    public void tickSensors() {
        this.tickingLock.readLock().lock();
        try {
            for (Sensor<?> a : sensors) {
                Sensor<SensorData> sensor = (Sensor<SensorData>) a;
                SensorData readData = sensor.read();
                if (readData != null) {
                    Collection<SensorProcessor<SensorData>> processors = sensor.processors();
                    if (processors != null && !processors.isEmpty()) {
                        for (SensorProcessor<SensorData> processor : processors) {
                            processor.process(readData);
                        }
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
