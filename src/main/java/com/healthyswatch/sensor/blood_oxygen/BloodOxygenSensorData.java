package com.healthyswatch.sensor.blood_oxygen;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.healthyswatch.sensor.SensorData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BloodOxygenSensorData implements SensorData {

    private final long time;
    private final float oxygenPercent;

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(oxygenPercent);
    }
}
