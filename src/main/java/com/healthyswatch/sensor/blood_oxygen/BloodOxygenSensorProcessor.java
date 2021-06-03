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
        long now = data.getTime();
        if (data.getOxygenPercent() < alertThreshold) {
            // using a counter to prevent false-positive
            // this means it requires 4 detections in a row to trigger logging
            // processing is done every 10 seconds
            this.counter = Math.min(counter + 1, 10);
            if (this.counter == 4) {
                core.getTrackingRepository().addEvent(new LogEvent(
                        now,
                        "blood-oxygen-sensor: " + sensor.name(),
                        core.getTranslationRegistry().text("sensor.blood_oxygen.alert.below", data.getOxygenPercent() * 100, alertThreshold * 100)
                ));
            }
        } else {
            this.counter = Math.max(counter - 1, 0);
        }
    }
}
