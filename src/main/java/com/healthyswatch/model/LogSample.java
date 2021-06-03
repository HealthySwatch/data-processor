package com.healthyswatch.model;

import com.google.gson.JsonElement;
import lombok.Data;

@Data
public class LogSample {

    private final String type;
    private final long time;
    private final JsonElement data;

}
