package com.healthyswatch.sensor.heart_rate;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.healthyswatch.sensor.SensorData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class HeartRateSensorData implements SensorData {

    private final long time;
    private final int beatsPerMinute;

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(beatsPerMinute);
    }
}
