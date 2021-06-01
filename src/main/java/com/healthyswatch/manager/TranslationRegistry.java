package com.healthyswatch.manager;

import java.util.Locale;

public interface TranslationRegistry {

    void load(Locale locale);

    String text(String key);

    String text(String key, Object ... args);

}
