package com.healthyswatch.repository;

import com.healthyswatch.model.LogEvent;
import com.healthyswatch.model.Report;
import com.healthyswatch.sensor.Sensor;
import com.healthyswatch.sensor.SensorData;

public interface TrackingRepository {

    void addEvent(LogEvent event);

    <T extends SensorData> void addSample(Sensor<T> sensor, T sensorData);

    Report createReport(long startAt, long endAt);

}
