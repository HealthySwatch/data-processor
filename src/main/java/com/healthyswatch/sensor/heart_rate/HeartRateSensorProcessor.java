package com.healthyswatch.sensor.heart_rate;

import com.healthyswatch.HSWCore;
import com.healthyswatch.model.LogEvent;
import com.healthyswatch.sensor.SensorProcessor;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class HeartRateSensorProcessor implements SensorProcessor<HeartRateSensorData, HeartRateSensor> {

    private final HSWCore core;
    private final Map<HeartRateSensor, Integer> counters = new HashMap<>();

    /*
    https://www.heart.org/en/healthy-living/fitness/fitness-basics/target-heart-rates
     */

    @Override
    public void process(HeartRateSensor sensor, HeartRateSensorData data) {
        long now = System.currentTimeMillis();
        int minThreshold = sensor.getMinThreshold();
        int maxThreshold = sensor.getMaxThreshold();
        boolean belowMin = data.getBeatsPerMinute() < minThreshold;
        boolean aboveMax = data.getBeatsPerMinute() > maxThreshold;
        if ((belowMin || aboveMax)) {
            int count = counters.merge(sensor, 1, (v, a) -> Math.min(v + a, 10));
            if (count == 5) {
                core.getTrackingRepository().addEvent(new LogEvent(
                        now,
                        "heart-rate-sensor: " + sensor.getName(),
                        belowMin ? String.format("Measured heart rate is below defined threshold: %d < %d", data.getBeatsPerMinute(), minThreshold)
                                : String.format("Measured heart rate is above defined threshold: %d > %d", data.getBeatsPerMinute(), maxThreshold)
                ));
            }
        } else {
            counters.merge(sensor, -1, (v, a) -> Math.max(v + a, -1));
        }
    }

}
