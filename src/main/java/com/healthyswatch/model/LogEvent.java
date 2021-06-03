package com.healthyswatch.model;

import lombok.Data;

@Data
public class LogEvent {

    private final long time;
    private final String source;
    private final String message;

}