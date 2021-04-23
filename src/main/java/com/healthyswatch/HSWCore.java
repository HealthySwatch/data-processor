package com.healthyswatch;

import com.healthyswatch.extension.DiallerExtension;
import com.healthyswatch.manager.EncryptionManager;
import com.healthyswatch.manager.SensorManager;
import com.healthyswatch.manager.TrackingManager;
import com.healthyswatch.manager.impl.EncryptionManagerImpl;
import com.healthyswatch.manager.impl.SensorManagerImpl;
import com.healthyswatch.manager.impl.TrackingManagerImpl;
import com.healthyswatch.repository.EncryptionRepository;
import com.healthyswatch.repository.TrackingRepository;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Getter
public class HSWCore {

    private final SensorManager sensorManager;
    private final EncryptionManager encryptionManager;
    private final TrackingManager trackingManager;

    private final EncryptionRepository encryptionRepository;
    private final TrackingRepository trackingRepository;
    private final ScheduledExecutorService executorService;

    private ScheduledFuture<?> sensorsTickingTask;
    private ScheduledFuture<?> synchronizationTask;

    @Setter
    private DiallerExtension diallerExtension;

    public HSWCore(EncryptionRepository encryptionRepository, TrackingRepository trackingRepository, DiallerExtension diallerExtension) {
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.sensorManager = new SensorManagerImpl();
        this.encryptionManager = new EncryptionManagerImpl(encryptionRepository);
        this.trackingManager = new TrackingManagerImpl("https://localhost:8000/api", trackingRepository, encryptionRepository, encryptionManager);

        this.encryptionRepository = encryptionRepository;
        this.trackingRepository = trackingRepository;
        this.diallerExtension = diallerExtension;
    }

    public void start() {
        if (sensorsTickingTask != null) {
            throw new IllegalStateException("Core is already started");
        }
        LocalTime syncTime = LocalTime.of(22, 30);
        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();
        long delay;
        if (nowTime.isBefore(syncTime)) {
            delay = nowTime.until(syncTime, ChronoUnit.SECONDS);
        } else {
            LocalDateTime nowDateTime = LocalDateTime.of(nowDate, nowTime);
            LocalDateTime nextSyncDateTime = LocalDateTime.of(nowDate.plusDays(1), syncTime);
            delay = nowDateTime.until(nextSyncDateTime, ChronoUnit.SECONDS);
        }
        this.sensorsTickingTask = executorService.scheduleAtFixedRate(sensorManager::tickSensors, 10, 10, TimeUnit.SECONDS);
        this.synchronizationTask = executorService.scheduleAtFixedRate(trackingManager::tickTracking, delay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
    }

    public void stop() {
        if (sensorsTickingTask == null) {
            throw new IllegalStateException("Core is not started");
        }
        this.sensorsTickingTask.cancel(false);
        this.sensorsTickingTask = null;
        this.synchronizationTask.cancel(false);
        this.synchronizationTask = null;
    }

}
