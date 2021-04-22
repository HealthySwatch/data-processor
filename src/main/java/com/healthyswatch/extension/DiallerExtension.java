package com.healthyswatch.extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface DiallerExtension {

    DiallerExtension EMPTY = number -> {
        Logger logger = LoggerFactory.getLogger(DiallerExtension.class);
        logger.warn("using empty implementation to dial {}", number);
    };

    void dial(String phoneNumber) throws Exception;

}
