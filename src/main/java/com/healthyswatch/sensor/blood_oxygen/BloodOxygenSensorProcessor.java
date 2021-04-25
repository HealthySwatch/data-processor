package com.healthyswatch.sensor.blood_oxygen;

import com.healthyswatch.HSWCore;
import com.healthyswatch.model.LogEvent;
import com.healthyswatch.sensor.SensorProcessor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BloodOxygenSensorProcessor implements SensorProcessor<BloodOxygenSensorData> {

    private final HSWCore core;
    private final BloodOxygenSensor sensor;

    private final float alertThreshold;

    private int counter;

    /*
    According to https://www.healthline.com/health/normal-blood-oxygen-level#oxygen-levels ,
    blood oxygen level:
        95% - 100% : normal
        < 95% : low

        but, with COPD or other lung diseases normal could be 88% to 92%
     */
    @Override
    public void process(BloodOxygenSensorData data) {
        long now = System.currentTimeMillis();
        if (data.getOxygenPercent() < alertThreshold) {
            this.counter = Math.min(counter + 1, 10);
            if (this.counter == 5) {
                core.getTrackingRepository().addEvent(new LogEvent(
                        now,
                        "blood-oxygen-sensor: " + sensor.name(),
                        String.format("Measured blood oxygen percent is below defined threshold: %.2f < %.2f", data.getOxygenPercent() * 100, alertThreshold * 100)
                ));
            }
        } else {
            this.counter = Math.max(counter - 1, 0);
        }
    }
}
