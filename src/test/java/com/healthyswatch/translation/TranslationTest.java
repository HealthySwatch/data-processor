package com.healthyswatch.translation;

import com.healthyswatch.manager.TranslationRegistry;
import com.healthyswatch.manager.impl.TranslationRegistryImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TranslationTest {

    @BeforeAll
    public static void initTests() {
        Locale.setDefault(Locale.ROOT);
    }

    @Test
    public void testTextEnglish() {
        TranslationRegistry registry = new TranslationRegistryImpl(Locale.US);
        assertEquals("This is a simple english text", registry.text("test.translation.text"));
        assertEquals("This is an english text with an integer argument '421'", registry.text("test.translation.text_1_arg", 421));
        assertEquals("This is an english text with an integer argument '2033' and a formatted decimal argument '3.14'", registry.text("test.translation.text_2_args", 2033, Math.PI));
    }

    @Test
    public void testTextFrench() {
        TranslationRegistry registry = new TranslationRegistryImpl(Locale.FRANCE);
        assertEquals("Ceci est un texte français simple.", registry.text("test.translation.text"));
        assertEquals("Ceci est un texte français avec un paramètre entier '421'.", registry.text("test.translation.text_1_arg", 421));
        assertEquals("Ceci est un texte français avec un paramètre entier '2033' et un paramètre décimal formatté '3.14'.", registry.text("test.translation.text_2_args", 2033, Math.PI));
    }

    @Test
    public void testTextLangFallback() {
        TranslationRegistry registry = new TranslationRegistryImpl(Locale.SIMPLIFIED_CHINESE);
        assertEquals("This is some wonderfull piece of text :)", registry.text("test.translation.another_text"));
    }

    @Test
    public void testUndefinedText() {
        TranslationRegistry registry = new TranslationRegistryImpl(Locale.US);
        assertEquals("test.translation.text_undefined", registry.text("test.translation.text_undefined"));
    }

}
