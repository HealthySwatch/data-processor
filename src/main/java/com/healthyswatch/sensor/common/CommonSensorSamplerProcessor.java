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

    @Override
    public void process(T data) {
        core.getTrackingRepository().addSample(new LogSample(sensor.type().getSimpleName(), data.getTime(), data.serialize()));
    }

}
