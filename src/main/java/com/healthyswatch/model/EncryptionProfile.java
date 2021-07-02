package com.healthyswatch.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;
import java.util.Base64;

@RequiredArgsConstructor
@Getter
public class EncryptionProfile {

    private final byte[] cipherIV;
    private final byte[] saltKey;
    private final int secretKeyIteration;
    private final int secretKeyLength;
    private final int cipherAuthTagLength;

    private final String randomPassword;
    private final String userPassword;

    public String getUserPassword() {
        if (userPassword != null) {
            return userPassword;
        }
        return "";
    }

    public JsonElement toAuthData() {
        JsonArray array = new JsonArray();
        array.add(Base64.getEncoder().encodeToString(cipherIV));
        array.add(Base64.getEncoder().encodeToString(saltKey));
        array.add(secretKeyIteration);
        array.add(secretKeyLength);
        array.add(cipherAuthTagLength);
        array.add("aes");
        array.add("gcm");
        return array;
    }

    public EncryptionProfile updateCipherIV() {
        byte[] cipherIV = this.cipherIV.clone();
        new SecureRandom().nextBytes(cipherIV);
        return new EncryptionProfile(
                cipherIV, saltKey.clone(), secretKeyIteration, secretKeyLength,
                cipherAuthTagLength, randomPassword, userPassword
        );
    }

    public EncryptionProfile updateUserPassword(String password) {
        return new EncryptionProfile(
                cipherIV.clone(), saltKey.clone(), secretKeyIteration, secretKeyLength,
                cipherAuthTagLength, randomPassword, password
        );
    }

}
