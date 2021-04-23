package com.healthyswatch.repository;

import com.healthyswatch.model.LogEvent;
import com.healthyswatch.model.RemoteTrackingSettings;
import com.healthyswatch.model.Report;
import com.healthyswatch.model.SavedSensorData;
import com.healthyswatch.sensor.Sensor;
import com.healthyswatch.sensor.SensorData;

import java.util.Collection;

public interface TrackingRepository {

    void addEvent(LogEvent event);

    <T extends SensorData> void addSample(Sensor<T> sensor, T sensorData);

    void addReport(Report report);

    Collection<LogEvent> getEvents(long startAt, long endAt);

    Collection<SavedSensorData> getSamples(long startAt, long endAt);

    Collection<Report> getReports();

    Collection<Report> getNotSynchronizedReports();

    long getLastSynchronizeTime();

    RemoteTrackingSettings getRemoteTrackingSettings();
}
