package com.healthyswatch.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface EmergencyManager {

    EmergencyManager EMPTY = text -> {
        Logger logger = LoggerFactory.getLogger(EmergencyManager.class);
        logger.warn("using empty emergency implementation with text: {}", text);
    };

    void emergency(String text);

}
