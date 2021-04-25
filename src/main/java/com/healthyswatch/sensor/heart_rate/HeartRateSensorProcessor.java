package com.healthyswatch.sensor.heart_rate;

import com.healthyswatch.HSWCore;
import com.healthyswatch.model.LogEvent;
import com.healthyswatch.sensor.SensorProcessor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HeartRateSensorProcessor implements SensorProcessor<HeartRateSensorData> {

    private final HSWCore core;
    private final HeartRateSensor sensor;

    private final int minThreshold;
    private final int maxThreshold;

    private int counter;

    /*
    https://www.heart.org/en/healthy-living/fitness/fitness-basics/target-heart-rates
     */

    @Override
    public void process(HeartRateSensorData data) {
        long now = System.currentTimeMillis();
        boolean belowMin = data.getBeatsPerMinute() < minThreshold;
        boolean aboveMax = data.getBeatsPerMinute() > maxThreshold;
        if ((belowMin || aboveMax)) {
            this.counter = Math.min(counter + 1, 10);
            if (this.counter == 5) {
                core.getTrackingRepository().addEvent(new LogEvent(
                        now,
                        "heart-rate-sensor: " + sensor.name(),
                        belowMin ? String.format("Measured heart rate is below defined threshold: %d < %d", data.getBeatsPerMinute(), minThreshold)
                                : String.format("Measured heart rate is above defined threshold: %d > %d", data.getBeatsPerMinute(), maxThreshold)
                ));
            }
        } else {
            this.counter = Math.max(counter -1 , 0);
        }
    }

}
