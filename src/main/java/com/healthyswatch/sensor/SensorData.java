package com.healthyswatch.sensor;

import com.google.gson.JsonElement;

public interface SensorData {

    long getTime();

    JsonElement serialize();

}
