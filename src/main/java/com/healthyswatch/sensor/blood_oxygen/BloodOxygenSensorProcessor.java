package com.healthyswatch.sensor.blood_oxygen;

import com.healthyswatch.HSWCore;
import com.healthyswatch.model.LogEvent;
import com.healthyswatch.sensor.SensorProcessor;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class BloodOxygenSensorProcessor implements SensorProcessor<BloodOxygenSensorData, BloodOxygenSensor> {

    private final HSWCore core;
    private final Map<BloodOxygenSensor, Integer> counters = new HashMap<>();

    /*
    According to https://www.healthline.com/health/normal-blood-oxygen-level#oxygen-levels ,
    blood oxygen level:
        95% - 100% : normal
        < 95% : low

        but, with COPD or other lung diseases normal could be 88% to 92%
     */
    @Override
    public void process(BloodOxygenSensor sensor, BloodOxygenSensorData data) {
        long now = System.currentTimeMillis();
        float threshold = sensor.getAlertThreshold();
        if (data.getOxygenPercent() < threshold) {
            int count = counters.merge(sensor, 1, (v, a) -> Math.min(v + a, 10));
            if (count == 5) {
                core.getTrackingRepository().addEvent(new LogEvent(
                        now,
                        "blood-oxygen-sensor: " + sensor.getName(),
                        String.format("Measured blood oxygen percent is below defined threshold: %.2f < %.2f", data.getOxygenPercent() * 100, threshold * 100)
                ));
            }
        } else {
            counters.merge(sensor, -1, (v, a) -> Math.max(v + a, -1));
        }
    }
}
