package com.healthyswatch.tracking;

import com.healthyswatch.model.LogEvent;
import com.healthyswatch.model.RemoteTrackingSettings;
import com.healthyswatch.model.Report;
import com.healthyswatch.model.SavedSensorData;
import com.healthyswatch.repository.TrackingRepository;
import com.healthyswatch.sensor.Sensor;
import com.healthyswatch.sensor.SensorData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TrackingRepositoryTest implements TrackingRepository {

    private final List<LogEvent> events = new ArrayList<>();
    private final List<Report> reports = new ArrayList<>();
    private final List<SavedSensorData> samples = new ArrayList<>();
    private final RemoteTrackingSettings trackingSettings = new RemoteTrackingSettings();

    private long lastSynchronizationTime;

    @Override
    public void addEvent(LogEvent event) {
        this.events.add(event);
    }

    @Override
    public <T extends SensorData> void addSample(Sensor<T> sensor, T sensorData) {
        this.samples.add(new SavedSensorData(sensor.getSensorType().getSimpleName(), sensorData.getTime(), sensorData.serialize()));
    }

    @Override
    public void addReport(Report report) {
        this.reports.add(report);
    }

    @Override
    public Collection<LogEvent> getEvents(long startAt, long endAt) {
        return this.events.stream().filter(e -> e.getTime() >= startAt && e.getTime() < endAt).collect(Collectors.toList());
    }

    @Override
    public Collection<SavedSensorData> getSamples(long startAt, long endAt) {
        return this.samples.stream().filter(e -> e.getTime() >= startAt && e.getTime() < endAt).collect(Collectors.toList());
    }

    @Override
    public Collection<Report> getReports() {
        return this.reports;
    }

    @Override
    public Collection<Report> getNotSynchronizedReports() {
        return this.reports;
    }

    @Override
    public long getLastSynchronizeTime() {
        return lastSynchronizationTime;
    }

    @Override
    public RemoteTrackingSettings getRemoteTrackingSettings() {
        return trackingSettings;
    }
}
