package com.healthyswatch.tracking;

import com.healthyswatch.encryption.EncryptionRepositoryTest;
import com.healthyswatch.manager.EncryptionManager;
import com.healthyswatch.manager.TrackingManager;
import com.healthyswatch.manager.impl.EncryptionManagerImpl;
import com.healthyswatch.manager.impl.TrackingManagerImpl;
import com.healthyswatch.model.LogEvent;
import com.healthyswatch.repository.EncryptionRepository;
import com.healthyswatch.repository.TrackingRepository;
import com.healthyswatch.utils.Base58;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

public class TrackingTests {

    static {
        try {
            initNoSSL();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    private static void initNoSSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext context;
        context = SSLContext.getInstance("TLSv1.2");
        TrustManager[] trustManager = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                    public void checkClientTrusted(X509Certificate[] certificate, String str) {}
                    public void checkServerTrusted(X509Certificate[] certificate, String str) {}
                }
        };
        context.init(null, trustManager, new SecureRandom());

        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
    }

    //@Test
    public void testttt() {
        long now = System.currentTimeMillis();
        EncryptionRepository encryptionRepository = new EncryptionRepositoryTest("tOtO");
        EncryptionManager encryptionManager = new EncryptionManagerImpl(encryptionRepository);

        TrackingRepository trackingRepository = new TrackingRepositoryTest();
        TrackingManager trackingManager = new TrackingManagerImpl("https://localhost:8000/api", trackingRepository, encryptionRepository, encryptionManager);

        trackingRepository.addEvent(new LogEvent(now - TimeUnit.HOURS.toMillis(3), "TestSensor", "Hey you should move ur fat ass"));
        trackingManager.tickTracking();

        trackingRepository.addEvent(new LogEvent(now - TimeUnit.HOURS.toMillis(2), "TestSensor", "Hey you should move ur fat ass"));
        trackingManager.tickTracking();

        trackingRepository.addEvent(new LogEvent(now - TimeUnit.HOURS.toMillis(1), "TestSensor", "Hey you should move ur fat ass"));
        trackingManager.tickTracking();
        System.out.println(trackingRepository.getRemoteTrackingSettings());
        System.out.println("random password: " + Base58.encode(encryptionRepository.getCurrentProfile().getRandomPassword().getBytes()));
        System.out.println("user password: " + encryptionRepository.getCurrentProfile().getUserPassword());
    }

}
