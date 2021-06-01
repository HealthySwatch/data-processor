package com.healthyswatch.encryption;

import com.healthyswatch.manager.EncryptionManager;
import com.healthyswatch.manager.impl.EncryptionManagerImpl;
import com.healthyswatch.model.EncryptionProfile;
import com.healthyswatch.repository.EncryptionRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EncryptionTests {

    @SneakyThrows
    @Test
    public void testSingleEncryptionThenDecryption() {
        String input = "this text is wonderfull!";
        EncryptionRepository encryptionRepository = new EncryptionRepositoryTest(null);
        EncryptionManager manager = new EncryptionManagerImpl(encryptionRepository);
        assertEquals(input, manager.decode(manager.encode(input), encryptionRepository.getCurrentProfile()));
    }

    @SneakyThrows
    @Test
    public void testSingleEncryptionThenDecryptionWithPassword() {
        String input = "this one is not THAT great :/";
        EncryptionRepository encryptionRepository = new EncryptionRepositoryTest("azerty123");
        EncryptionManager manager = new EncryptionManagerImpl(encryptionRepository);
        assertEquals(input, manager.decode(manager.encode(input), encryptionRepository.getCurrentProfile()));
    }

    @SneakyThrows
    @Test
    public void testSameConsecutiveEncryption() {
        String input = "you know what is great ? this segway, to our sponsor !";
        EncryptionRepository encryptionRepository = new EncryptionRepositoryTest(null);
        EncryptionManager manager = new EncryptionManagerImpl(encryptionRepository);
        assertNotEquals(manager.encode(input), manager.encode(input));
    }

    @SneakyThrows
    @Test
    public void testMultipleEncryptionThenMultipleDecryption() {
        String[] inputs = {"this text is wonderfull!", "this one is not THAT great :/", "you know what is great ? this segway, to our sponsor !"};
        EncryptionResult[] outputs = new EncryptionResult[inputs.length];
        EncryptionRepository encryptionRepository = new EncryptionRepositoryTest(null);
        EncryptionManager manager = new EncryptionManagerImpl(encryptionRepository);
        for (int i = 0; i < inputs.length; i++) {
            outputs[i] = new EncryptionResult(manager.encode(inputs[i]), encryptionRepository.getCurrentProfile());
        }
        for (int i = 0; i < inputs.length; i++) {
            assertEquals(inputs[i], manager.decode(outputs[i].output, outputs[i].profile));
        }
    }

    @AllArgsConstructor
    private static class EncryptionResult {
        String output;
        EncryptionProfile profile;
    }

}
