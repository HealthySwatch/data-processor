package com.healthyswatch.repository;

import com.healthyswatch.model.LogEvent;
import com.healthyswatch.model.LogSample;
import com.healthyswatch.model.RemoteTrackingSettings;
import com.healthyswatch.model.Report;

import java.util.Collection;

public interface TrackingRepository {

    void addEvent(LogEvent event);

    void addSample(LogSample sample);

    void addReport(Report report);

    Collection<LogEvent> getEvents(long startAt, long endAt);

    Collection<LogSample> getSamples(long startAt, long endAt);

    Collection<Report> getReports();

    Collection<Report> getNotSynchronizedReports();

    long getLastSynchronizeTime();

    RemoteTrackingSettings getRemoteTrackingSettings();
}
