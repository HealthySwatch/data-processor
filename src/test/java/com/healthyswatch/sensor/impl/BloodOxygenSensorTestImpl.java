package com.healthyswatch.sensor.impl;

import com.healthyswatch.HSWCore;
import com.healthyswatch.sensor.blood_oxygen.BloodOxygenSensor;
import com.healthyswatch.sensor.blood_oxygen.BloodOxygenSensorData;
import com.healthyswatch.sensor.blood_oxygen.BloodOxygenSensorProcessor;
import com.healthyswatch.sensor.common.CommonSensorSamplerProcessor;

public class BloodOxygenSensorTestImpl extends BaseSensorTestImpl<BloodOxygenSensorData> implements BloodOxygenSensor {

    private final float alertThreshold;

    public BloodOxygenSensorTestImpl(HSWCore core, String name, float alertThreshold) {
        super(name);
        this.alertThreshold = alertThreshold;
        this.processors.add(new CommonSensorSamplerProcessor<>(core, this));
        this.processors.add(new BloodOxygenSensorProcessor(core, this, alertThreshold));
    }

    @Override
    public float getAlertThreshold() {
        return alertThreshold;
    }
}
