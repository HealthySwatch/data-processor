package com.healthyswatch.encryption;

import com.healthyswatch.model.EncryptionProfile;
import com.healthyswatch.repository.EncryptionRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
public class EncryptionRepositoryTest implements EncryptionRepository {

    private final String userPassword;
    private EncryptionProfile profile;

    @Override
    public String getUserPassword() {
        return userPassword;
    }

    @Override
    public EncryptionProfile getCurrentProfile() {
        return profile;
    }

    @Override
    public void setCurrentProfile(EncryptionProfile profile) {
        this.profile = profile;
    }
}
