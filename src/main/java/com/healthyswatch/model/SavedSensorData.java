package com.healthyswatch.model;

import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SavedSensorData {

    private final String sensor;
    private final long time;
    private final JsonElement data;

}