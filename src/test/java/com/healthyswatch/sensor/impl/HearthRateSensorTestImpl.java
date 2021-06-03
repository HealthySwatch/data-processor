package com.healthyswatch.sensor.impl;

import com.healthyswatch.HSWCore;
import com.healthyswatch.sensor.common.CommonSensorSamplerProcessor;
import com.healthyswatch.sensor.heart_rate.HeartRateSensor;
import com.healthyswatch.sensor.heart_rate.HeartRateSensorData;
import com.healthyswatch.sensor.heart_rate.HeartRateSensorProcessor;

public class HearthRateSensorTestImpl extends BaseSensorTestImpl<HeartRateSensorData> implements HeartRateSensor {

    private final int minThreshold;
    private final int maxThreshold;

    public HearthRateSensorTestImpl(HSWCore core, String name, int minThreshold, int maxThreshold) {
        super(name);
        this.minThreshold = minThreshold;
        this.maxThreshold = maxThreshold;
        this.processors.add(new CommonSensorSamplerProcessor<>(core, this));
        this.processors.add(new HeartRateSensorProcessor(core, this, minThreshold, maxThreshold));
    }

    @Override
    public int getMinThreshold() {
        return minThreshold;
    }

    @Override
    public int getMaxThreshold() {
        return maxThreshold;
    }
}
