package com.healthyswatch.manager.impl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.healthyswatch.manager.EncryptionManager;
import com.healthyswatch.manager.TrackingManager;
import com.healthyswatch.model.*;
import com.healthyswatch.repository.EncryptionRepository;
import com.healthyswatch.repository.TrackingRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TrackingManagerImpl implements TrackingManager {

    private final Logger logger = LoggerFactory.getLogger(TrackingManager.class);

    private final String baseUrl;
    private final TrackingRepository trackingRepository;
    private final EncryptionRepository encryptionRepository;
    private final EncryptionManager encryptionManager;

    @Override
    public void tickTracking() {
        RemoteTrackingSettings settings = trackingRepository.getRemoteTrackingSettings();
        try {
            createDailyReport();
            if (settings.isEnabled()) {
                if (!settings.isInitialized()) {
                    initTrackingSettings(settings);
                }
                synchronizeReports();
            }
        } catch (Exception e) {
            logger.error("Exception while ticking tracker", e);
        }
    }

    @Override
    public void createDailyReport() {
        long now = Instant.now().getEpochSecond();
        long startAt = now - TimeUnit.DAYS.toSeconds(1);
        Collection<LogEvent> events = trackingRepository.getEvents(startAt, now).stream()
                .map(e -> new LogEvent(e.getTime() - startAt, e.getSource(), e.getMessage()))
                .sorted(Comparator.comparingLong(LogEvent::getTime))
                .collect(Collectors.toList());
        Collection<LogSample> samples = trackingRepository.getSamples(startAt, now).stream()
                .sorted(Comparator.comparingLong(LogSample::getTime))
                .collect(Collectors.toList());
        Map<String, Collection<JsonElement>> samplesByType = new HashMap<>();
        for (LogSample sample : samples) {
            Collection<JsonElement> elements = samplesByType.computeIfAbsent(sample.getType(), k -> new ArrayList<>());
            JsonObject sampleJson = new JsonObject();
            sampleJson.addProperty("time", sample.getTime() - startAt);
            sampleJson.add("data", sample.getData());
            elements.add(sampleJson);
        }
        Report report = new Report(startAt, now, events, samplesByType);
        trackingRepository.addReport(report);
    }

    @Override
    public void synchronizeReports() {
        Gson gson = new Gson();
        Collection<Report> reports = trackingRepository.getNotSynchronizedReports();
        for (Report report : reports) {
            try {
                String json = gson.toJson(report);
                String encryptedJson = encryptionManager.encode(json);
                send(encryptedJson);
            } catch (Exception e) {
                throw new RuntimeException("Unable to synchronise report", e);
            }
        }
    }

    private JsonObject payload(String data) {
        JsonObject payload = new JsonObject();
        EncryptionProfile encryptionProfile = encryptionRepository.getCurrentProfile();
        JsonElement authArray = encryptionProfile.toAuthData();
        payload.addProperty("version", 1);
        payload.add("auth", authArray);
        payload.addProperty("data", data);
        return payload;
    }

    private void initTrackingSettings(RemoteTrackingSettings settings) throws IOException {
        HttpURLConnection pasteRequest = (HttpURLConnection) new URL(baseUrl + "/v1/watch").openConnection();
        pasteRequest.setRequestMethod("POST");
        JsonObject payloadJson = new JsonObject();
        if (encryptionRepository.getUsername() != null) {
            try {
                String data = encryptionManager.encode(encryptionRepository.getUsername());
                payloadJson.addProperty("name", payload(data).toString());
            } catch (Exception e) {
                throw new RuntimeException("Unable to encode username", e);
            }
        }
        pasteRequest.setDoOutput(true);
        pasteRequest.setRequestProperty("X-Requested-With", "JSONHttpRequest");
        pasteRequest.getOutputStream().write(payloadJson.toString().getBytes());

        // Server response
        int responseCode = pasteRequest.getResponseCode();
        if (responseCode != 201) {
            throw new IllegalStateException("Invalid server response code (" + responseCode + ")");
        }

        JsonObject responseJSON = JsonParser.parseReader(new JsonReader(new InputStreamReader(pasteRequest.getInputStream()))).getAsJsonObject();
        settings.setApiToken(responseJSON.get("token").getAsString());
        settings.setShareToken(responseJSON.get("share_id").getAsString());
    }

    private void send(String encryptedData) throws IOException {
        JsonObject payloadJson = payload(encryptedData);

        // POST Request
        HttpURLConnection pasteRequest = (HttpURLConnection) new URL(baseUrl + "/v1/watch/" + trackingRepository.getRemoteTrackingSettings().getApiToken() + "/report").openConnection();
        pasteRequest.setRequestMethod("POST");
        pasteRequest.setDoOutput(true);
        pasteRequest.setRequestProperty("X-Requested-With", "JSONHttpRequest");
        pasteRequest.getOutputStream().write(payloadJson.toString().getBytes());

        // Server response
        int responseCode = pasteRequest.getResponseCode();
        if (responseCode != 201) {
            throw new IllegalStateException("Invalid server response code (" + responseCode + ")");
        }
    }
}
