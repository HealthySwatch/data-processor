package com.healthyswatch.model;

import com.healthyswatch.sensor.SensorData;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class Report {

    private final long startAt;
    private final long endAt;

    private final List<LogEvent> events = new ArrayList<>();
    private final Map<String, SensorData> sensors = new HashMap<>();

}
