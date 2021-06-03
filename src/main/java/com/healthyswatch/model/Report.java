package com.healthyswatch.model;

import com.google.gson.JsonElement;
import lombok.Data;

import java.util.Collection;
import java.util.Map;

@Data
public class Report {

    private final long startAt;
    private final long endAt;

    private final Collection<LogEvent> events;
    private final Map<String, Collection<JsonElement>> samples;

}
