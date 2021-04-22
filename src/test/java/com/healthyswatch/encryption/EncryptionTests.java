package com.healthyswatch.encryption;

import com.healthyswatch.manager.EncryptionManager;
import com.healthyswatch.manager.impl.EncryptionManagerImpl;
import com.healthyswatch.repository.EncryptionRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EncryptionTests {

    @SneakyThrows
    @Test
    public void testBasicEncryptionDecryption() {
        String input = "this text is wonderfull!";
        EncryptionRepository encryptionRepository = new EncryptionRepositoryTest(null);
        EncryptionManager manager = new EncryptionManagerImpl(encryptionRepository);
        assertEquals(input, manager.decode(manager.encode(input)));
    }

    @SneakyThrows
    @Test
    public void testBasicEncryptionDecryptionWithPassword() {
        String input = "this one is not THAT great :/";
        EncryptionRepository encryptionRepository = new EncryptionRepositoryTest("azerty123");
        EncryptionManager manager = new EncryptionManagerImpl(encryptionRepository);
        assertEquals(input, manager.decode(manager.encode(input)));
    }

    @SneakyThrows
    @Test
    public void testMultipleEncryptionDecryption() {
        String[] inputs = {"this text is wonderfull!", "this one is not THAT great :/", "you know what is great ? this segway, to our sponsor !"};
        String[] outputs = new String[inputs.length];
        EncryptionRepository encryptionRepository = new EncryptionRepositoryTest(null);
        EncryptionManager manager = new EncryptionManagerImpl(encryptionRepository);
        for (int i = 0; i < inputs.length; i++) {
            outputs[i] = manager.encode(inputs[i]);
        }
        for (int i = 0; i < inputs.length; i++) {
            assertEquals(inputs[i], manager.decode(outputs[i]));
        }
    }

}
