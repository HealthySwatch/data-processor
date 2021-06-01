package com.healthyswatch.sensor.heart_rate;

import com.healthyswatch.HSWCore;
import com.healthyswatch.model.LogEvent;
import com.healthyswatch.sensor.SensorProcessor;
import lombok.RequiredArgsConstructor;

import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class HeartRateSensorProcessor implements SensorProcessor<HeartRateSensorData> {

    private final HSWCore core;
    private final HeartRateSensor sensor;

    private final int minThreshold;
    private final int maxThreshold;

    private final Deque<Integer> abnormalValues = new LinkedList<>();

    /*
    https://www.heart.org/en/healthy-living/fitness/fitness-basics/target-heart-rates
     */

    @Override
    public void process(HeartRateSensorData data) {
        long now = System.currentTimeMillis();
        int value = data.getBeatsPerMinute();
        boolean belowMin = value < minThreshold;
        boolean aboveMax = value > maxThreshold;
        if ((belowMin || aboveMax)) {
            // we store every bad value and count them to prevent false-positive
            // this means it requires 4 detections in a row to trigger logging
            // processing is done every 10 seconds and we store at max 10 abnormal values
            this.abnormalValues.offerLast(value);
            if (this.abnormalValues.size() > 10) {
                this.abnormalValues.removeFirst();
            }
            if (this.abnormalValues.size() == 4) {
                core.getTrackingRepository().addEvent(new LogEvent(
                        now,
                        "heart-rate-sensor: " + sensor.name(),
                        belowMin ? core.getTranslationRegistry().text("sensor.heart_rate.alert.below", value, minThreshold)
                                : core.getTranslationRegistry().text("sensor.heart_rate.alert.above", value, maxThreshold)
                ));
            } else if (this.abnormalValues.size() == 9) {
                String abnormalValues = this.abnormalValues.stream().map(String::valueOf).collect(Collectors.joining(", "));
                this.core.getEmergencyManager().emergency(core.getTranslationRegistry().text("sensor.heart_rate.alert.emergency", abnormalValues));
            }
        } else {
            if (!this.abnormalValues.isEmpty()) {
                this.abnormalValues.removeFirst();
            }
        }
    }

}
