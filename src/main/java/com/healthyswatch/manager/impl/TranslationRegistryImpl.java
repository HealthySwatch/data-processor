package com.healthyswatch.manager.impl;

import com.healthyswatch.manager.TranslationRegistry;
import com.healthyswatch.utils.UTF8ResourceBundleControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class TranslationRegistryImpl implements TranslationRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(TranslationRegistryImpl.class);

    private Locale locale;
    private ResourceBundle resourceBundle;

    public TranslationRegistryImpl(Locale locale) {
        this.load(locale);
    }

    @Override
    public Locale requestedLocale() {
        return locale;
    }

    @Override
    public Locale loadedLocale() {
        return resourceBundle.getLocale();
    }

    @Override
    public void load(Locale locale) {
        this.locale = locale;
        this.resourceBundle = ResourceBundle.getBundle("com/healthyswatch/lang/messages", locale, TranslationRegistryImpl.class.getClassLoader(), UTF8ResourceBundleControl.get());
        String loadedLocale = resourceBundle.getLocale().toString();
        LOGGER.info("Requested load of locale {}, loaded {}", locale, loadedLocale.isEmpty() ? "default" : loadedLocale);
    }

    @Override
    public String text(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException exception) {
            return key;
        }
    }

    @Override
    public String text(String key, Object... args) {
        try {
            return String.format(Locale.ROOT, text(key), args);
        } catch (MissingResourceException exception) {
            return key;
        }
    }
}
