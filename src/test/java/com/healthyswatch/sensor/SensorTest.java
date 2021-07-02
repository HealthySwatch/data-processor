package com.healthyswatch.sensor;

import com.healthyswatch.HSWCore;
import com.healthyswatch.encryption.EncryptionRepositoryTest;
import com.healthyswatch.manager.EmergencyManager;
import com.healthyswatch.model.LogEvent;
import com.healthyswatch.model.LogSample;
import com.healthyswatch.model.Report;
import com.healthyswatch.sensor.blood_oxygen.BloodOxygenSensorData;
import com.healthyswatch.sensor.heart_rate.HeartRateSensorData;
import com.healthyswatch.sensor.impl.BloodOxygenSensorTestImpl;
import com.healthyswatch.sensor.impl.HearthRateSensorTestImpl;
import com.healthyswatch.tracking.TrackingRepositoryTest;
import com.healthyswatch.tracking.TrackingTests;
import com.healthyswatch.utils.Base58;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Order(10)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SensorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensorTest.class);
    private static HSWCore CORE;

    @BeforeAll
    static void beforeAll() {
        CORE = new HSWCore(
                new EncryptionRepositoryTest("John Smith.", "MyP4$sw0rd"),
                new TrackingRepositoryTest(),
                EmergencyManager.EMPTY,
                Locale.FRENCH
        );
    }

    @Test
    @Order(0)
    public void ensureCanStart() {
        CORE.start();
    }

    @Test
    @Order(1)
    public void ensureCannotStartTwice() {
        assertThrows(IllegalStateException.class, CORE::start);
    }

    @Test
    @Order(2)
    public void ensureCanStop() {
        CORE.stop();
    }

    @Test
    @Order(3)
    public void ensureCannotStopTwice() {
        assertThrows(IllegalStateException.class, CORE::stop);
    }

    @Test
    @Order(4)
    public void tickSensors() {
        long start = Instant.now().getEpochSecond() - TimeUnit.HOURS.toSeconds(1), now, diff = 10;
        HearthRateSensorTestImpl hearthRateSensor = new HearthRateSensorTestImpl(CORE, "test-sensor", 85, 145);
        BloodOxygenSensorTestImpl bloodOxygenSensor = new BloodOxygenSensorTestImpl(CORE, "test-sensor", 0.95F);

        now = start;
        hearthRateSensor.getDataQueue().offer(new HeartRateSensorData(now += diff, 100));
        hearthRateSensor.getDataQueue().offer(new HeartRateSensorData(now += diff, 100));
        hearthRateSensor.getDataQueue().offer(new HeartRateSensorData(now += diff, 100));
        hearthRateSensor.getDataQueue().offer(new HeartRateSensorData(now += diff, 80));
        hearthRateSensor.getDataQueue().offer(new HeartRateSensorData(now += diff, 80));
        hearthRateSensor.getDataQueue().offer(new HeartRateSensorData(now += diff, 80));
        hearthRateSensor.getDataQueue().offer(new HeartRateSensorData(now += diff, 80));
        hearthRateSensor.getDataQueue().offer(new HeartRateSensorData(now += diff, 80));
        hearthRateSensor.getDataQueue().offer(new HeartRateSensorData(now += diff, 90));
        hearthRateSensor.getDataQueue().offer(new HeartRateSensorData(now += diff, 80));
        hearthRateSensor.getDataQueue().offer(new HeartRateSensorData(now += diff, 80));
        hearthRateSensor.getDataQueue().offer(new HeartRateSensorData(now += diff, 80));
        hearthRateSensor.getDataQueue().offer(new HeartRateSensorData(now += diff, 80));
        hearthRateSensor.getDataQueue().offer(new HeartRateSensorData(now += diff, 80));
        hearthRateSensor.getDataQueue().offer(new HeartRateSensorData(now += diff, 100));

        now = start;
        bloodOxygenSensor.getDataQueue().offer(new BloodOxygenSensorData(now += diff, 0.98F));
        bloodOxygenSensor.getDataQueue().offer(new BloodOxygenSensorData(now += diff, 0.96F));
        bloodOxygenSensor.getDataQueue().offer(new BloodOxygenSensorData(now += diff, 0.95F));
        bloodOxygenSensor.getDataQueue().offer(new BloodOxygenSensorData(now += diff, 0.94F));
        bloodOxygenSensor.getDataQueue().offer(new BloodOxygenSensorData(now += diff, 0.93F));
        bloodOxygenSensor.getDataQueue().offer(new BloodOxygenSensorData(now += diff, 0.93F));
        bloodOxygenSensor.getDataQueue().offer(new BloodOxygenSensorData(now += diff, 0.92F));
        bloodOxygenSensor.getDataQueue().offer(new BloodOxygenSensorData(now += diff, 0.91F));
        bloodOxygenSensor.getDataQueue().offer(new BloodOxygenSensorData(now += diff, 0.93F));
        bloodOxygenSensor.getDataQueue().offer(new BloodOxygenSensorData(now += diff, 0.91F));
        bloodOxygenSensor.getDataQueue().offer(new BloodOxygenSensorData(now += diff, 0.95F));
        bloodOxygenSensor.getDataQueue().offer(new BloodOxygenSensorData(now += diff, 0.96F));
        bloodOxygenSensor.getDataQueue().offer(new BloodOxygenSensorData(now += diff, 0.94F));
        bloodOxygenSensor.getDataQueue().offer(new BloodOxygenSensorData(now += diff, 0.95F));
        bloodOxygenSensor.getDataQueue().offer(new BloodOxygenSensorData(now += diff, 0.94F));

        CORE.getSensorManager().registerSensor(hearthRateSensor);
        CORE.getSensorManager().registerSensor(bloodOxygenSensor);

        // take manual control over sensors ticking
        assertNull(CORE.getSensorsTickingTask());
        assertEquals(0, CORE.getTrackingRepository().getSamples(start, now).size());
        assertEquals(0, CORE.getTrackingRepository().getEvents(start, now).size());

        for (int i = 0; i < 15; i++) {
            CORE.getSensorManager().tickSensors();
        }
        now += diff;

        Collection<LogSample> samples = CORE.getTrackingRepository().getSamples(start, now);
        Collection<LogEvent> events = CORE.getTrackingRepository().getEvents(start, now);
        LOGGER.info("samples: {}", samples);
        assertEquals(23, samples.size()); // with duplicate filtering, there is only 23 logged samples
        LOGGER.info("events: {}", events);
        assertEquals(3, events.size());
    }

    @Test
    @Order(5)
    public void dailyReport() {
        assertEquals(0, CORE.getTrackingRepository().getReports().size());
        CORE.getTrackingRepository().getRemoteTrackingSettings().setEnabled(false); // TODO: true to debug
        CORE.getTrackingManager().tickTracking();
        LOGGER.info("reports = {}", CORE.getTrackingRepository().getReports());
        assertEquals(1, CORE.getTrackingRepository().getReports().size());
        Report report = CORE.getTrackingRepository().getReports().stream().findAny().get();
        assertEquals(3, report.getEvents().size());
        assertEquals(2, report.getSamples().size());
        assertEquals(23, report.getSamples().values().stream().mapToLong(Collection::size).sum());
    }

    @Test @Disabled
    @Order(6)
    public void printDebug() {
        String shareToken = CORE.getTrackingRepository().getRemoteTrackingSettings().getShareToken();
        String encryptionKey = Base58.encode(CORE.getEncryptionRepository().getCurrentProfile().getRandomPassword().getBytes());
        System.out.println("http://127.0.0.1:8000/viewer/" + shareToken + "#" + encryptionKey);
    }

}
