package com.healthyswatch.repository;

import com.healthyswatch.model.EncryptionProfile;

public interface EncryptionRepository {

    String getUsername();

    String getUserPassword();

    EncryptionProfile getCurrentProfile();

    void setCurrentProfile(EncryptionProfile profile);

}
