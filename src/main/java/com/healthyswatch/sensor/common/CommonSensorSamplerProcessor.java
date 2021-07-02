package com.healthyswatch.sensor.common;

import com.healthyswatch.HSWCore;
import com.healthyswatch.model.LogSample;
import com.healthyswatch.sensor.Sensor;
import com.healthyswatch.sensor.SensorData;
import com.healthyswatch.sensor.SensorProcessor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommonSensorSamplerProcessor<T extends SensorData> implements SensorProcessor<T> {

    private final HSWCore core;
    private final Sensor<T> sensor;

    private LogSample previousSample;
    private boolean logPrevious;

    @Override
    public void process(T data) {
        LogSample sample = new LogSample(sensor.type().getSimpleName(), data.getTime(), data.serialize());
        if (previousSample == null) {
            core.getTrackingRepository().addSample(sample);
            logPrevious = false;
        } else if (!previousSample.getData().equals(sample.getData())) {
            if (logPrevious) {
                core.getTrackingRepository().addSample(previousSample);
            }
            core.getTrackingRepository().addSample(sample);
            logPrevious = false;
        } else {
            logPrevious = true;
        }

        previousSample = sample;
    }

}
