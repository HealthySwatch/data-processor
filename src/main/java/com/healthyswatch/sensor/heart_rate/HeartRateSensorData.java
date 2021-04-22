package com.healthyswatch.sensor.heart_rate;

import com.healthyswatch.sensor.SensorData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class HeartRateSensorData implements SensorData {

    private final long time;
    private final int beatsPerMinute;

}
