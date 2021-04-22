package com.healthyswatch;

import com.healthyswatch.manager.SensorManager;
import com.healthyswatch.manager.impl.SensorManagerImpl;
import com.healthyswatch.extension.DiallerExtension;
import com.healthyswatch.repository.EncryptionRepository;
import com.healthyswatch.repository.TrackingRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Getter
public class HSWCore {

    private final SensorManager sensorManager;

    private final EncryptionRepository encryptionRepository;
    private final TrackingRepository trackingRepository;
    private final DiallerExtension diallerExtension;

    private final ScheduledExecutorService executorService;

    private ScheduledFuture<?> tickingTask;

    public HSWCore(EncryptionRepository encryptionRepository, TrackingRepository trackingRepository, DiallerExtension diallerExtension) {
        this.sensorManager = new SensorManagerImpl();
        this.executorService = Executors.newSingleThreadScheduledExecutor();


        this.encryptionRepository = encryptionRepository;
        this.trackingRepository = trackingRepository;
        this.diallerExtension = diallerExtension;
    }

    public void start() {
        if (tickingTask != null) {
            throw new IllegalStateException("Core is already started");
        }
        this.tickingTask = executorService.scheduleAtFixedRate(sensorManager::tickSensors, 10, 10, TimeUnit.SECONDS);
    }

    public void stop() {
        if (tickingTask == null) {
            throw new IllegalStateException("Core is not started");
        }
        this.tickingTask.cancel(false);
        this.tickingTask = null;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    private static class Builder {
        private EncryptionRepository encryptionRepository;
        private TrackingRepository trackingRepository;
        private DiallerExtension diallerExtension = DiallerExtension.EMPTY;

        public HSWCore build() {
            return new HSWCore(encryptionRepository, trackingRepository, diallerExtension);
        }
    }

}
