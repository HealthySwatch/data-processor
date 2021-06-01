package com.healthyswatch.manager.impl;

import com.healthyswatch.manager.TranslationRegistry;
import com.healthyswatch.utils.UTF8ResourceBundleControl;

import java.util.Locale;
import java.util.ResourceBundle;

public class TranslationRegistryImpl implements TranslationRegistry {

    private Locale locale;
    private ResourceBundle resourceBundle;

    public TranslationRegistryImpl(Locale locale) {
        this.load(locale);
    }

    @Override
    public void load(Locale locale) {
        this.locale = locale;
        this.resourceBundle = ResourceBundle.getBundle("com/healthyswatch/lang/messages", locale, UTF8ResourceBundleControl.get());
    }

    @Override
    public String text(String key) {
        return resourceBundle.getString(key);
    }

    @Override
    public String text(String key, Object... args) {
        return String.format(text(key), args);
    }
}
