package com.healthyswatch.repository;

import com.healthyswatch.model.EncryptionProfile;

public interface EncryptionRepository {

    String getUserPassword();

    EncryptionProfile getCurrentProfile();

    void setCurrentProfile(EncryptionProfile profile);

}
