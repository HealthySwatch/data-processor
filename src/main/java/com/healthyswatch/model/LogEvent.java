package com.healthyswatch.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class LogEvent {

    private final long time;
    private final String source;
    private final String message;

}