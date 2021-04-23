package com.healthyswatch.model;

import lombok.Data;

@Data
public class RemoteTrackingSettings {

    private boolean enabled = true;
    private boolean shareEnabled;
    private String apiToken;
    private String shareToken;

    public boolean isInitialized() {
        return apiToken != null && shareToken != null;
    }

}
