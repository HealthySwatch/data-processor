package com.healthyswatch.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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

}
