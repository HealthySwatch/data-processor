package com.healthyswatch.manager.impl;

import com.google.gson.JsonElement;
import com.healthyswatch.manager.EncryptionManager;
import com.healthyswatch.model.EncryptionProfile;
import com.healthyswatch.repository.EncryptionRepository;
import lombok.RequiredArgsConstructor;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@RequiredArgsConstructor
public class EncryptionManagerImpl implements EncryptionManager {

    private final EncryptionRepository encryptionRepository;

    private byte[] deflate(byte[] input) throws IOException {
        Deflater deflater = new Deflater();
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            deflater.setInput(input);
            deflater.finish();
            byte[] buffer = new byte[1024];
            while (!deflater.finished()) {
                int count = deflater.deflate(buffer);
                stream.write(buffer, 0, count);
            }
            return stream.toByteArray();
        } finally {
            deflater.end();
        }
    }

    private byte[] inflate(byte[] input) throws IOException, DataFormatException {
        Inflater inflater = new Inflater();
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            inflater.setInput(input);
            byte[] buffer = new byte[1024];
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                stream.write(buffer, 0, count);
            }
            return stream.toByteArray();
        } finally {
            inflater.end();
        }
    }

    private EncryptionProfile createEncryptionProfile() throws NoSuchAlgorithmException {
        // Generate password
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(192);
        String randomPassword = Base64.getEncoder().encodeToString(keyGen.generateKey().getEncoded());

        // Generate IV
        byte[] cipherIVBytes = new byte[16];
        new SecureRandom().nextBytes(cipherIVBytes);

        // Generate salt
        byte[] kdfSaltBytes = new byte[8];
        new SecureRandom().nextBytes(kdfSaltBytes);
        int cipherAuthTagLen = 128;
        int secretKeyIteration = 100000, secretKeyLength = 256;
        return new EncryptionProfile(cipherIVBytes, kdfSaltBytes,
                secretKeyIteration, secretKeyLength, cipherAuthTagLen,
                randomPassword, encryptionRepository.getUserPassword());
    }

    private SecretKey configureSecretKey(EncryptionProfile profile) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String randomPassword = profile.getRandomPassword();
        String customPassword = randomPassword + profile.getUserPassword();
        byte[] kdfSaltBytes = profile.getSaltKey();
        int secretKeyIteration = profile.getSecretKeyIteration(), secretKeyLength = profile.getSecretKeyLength();
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec passwordBasedEncryptionKeySpec = new PBEKeySpec(customPassword.toCharArray(), kdfSaltBytes, secretKeyIteration, secretKeyLength);
        return new SecretKeySpec(factory.generateSecret(passwordBasedEncryptionKeySpec).getEncoded(), "AES");
    }

    private Cipher configureCipher(EncryptionProfile profile, SecretKey secretKey, int mode) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        JsonElement gcmTagDataArray = profile.toAuthData();
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(profile.getCipherAuthTagLength(), profile.getCipherIV());
        cipher.init(mode, secretKey, spec);
        cipher.updateAAD(gcmTagDataArray.toString().getBytes());
        return cipher;
    }

    @Override
    public String encode(String input) throws IOException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("No data to paste");
        }

        EncryptionProfile profile = encryptionRepository.getCurrentProfile();

        if (profile == null) {
            profile = createEncryptionProfile();
        } else {
            profile = profile.updateCipherIV();
        }

        encryptionRepository.setCurrentProfile(profile);

        // Compression
        byte[] dataBytes = deflate(input.getBytes());

        // Get secret key for cipher
        SecretKey secret = configureSecretKey(profile);

        // Get cipher
        Cipher cipher = configureCipher(profile, secret, Cipher.ENCRYPT_MODE);

        // Generate cipher text
        byte[] cipherTextBytes = cipher.doFinal(dataBytes);
        return Base64.getEncoder().encodeToString(cipherTextBytes);
    }

    @Override
    public String decode(String input, EncryptionProfile profile) throws IOException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, DataFormatException {
        byte[] cipherTextBytes = Base64.getDecoder().decode(input);

        if (profile == null) {
            throw new IllegalStateException("Unable to decode input if no profile is provided");
        }

        // Get secret key for cipher
        SecretKey secret = configureSecretKey(profile);

        // Get cipher
        Cipher cipher = configureCipher(profile, secret, Cipher.DECRYPT_MODE);

        // Revert cipher text
        byte[] dataBytes = cipher.doFinal(cipherTextBytes);

        // Decompression
        byte[] output = inflate(dataBytes);
        return new String(output);
    }
}
