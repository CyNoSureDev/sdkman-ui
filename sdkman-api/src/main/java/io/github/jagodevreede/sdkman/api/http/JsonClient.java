package io.github.jagodevreede.sdkman.api.http;

import io.github.jagodevreede.sdkman.api.domain.json.SdkCandidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

public class JsonClient {
    private static final Logger logger = LoggerFactory.getLogger(JsonClient.class);

    private final HttpClient httpClient;
    private final File cacheLocationFolder;
    private final Duration cacheDuration;
    private List<SdkCandidate> sdkCandidates;

    public JsonClient(HttpClient httpClient, File cacheLocationFolder, Duration cacheDuration) {
        this.cacheDuration = cacheDuration;
        this.cacheLocationFolder = cacheLocationFolder;
        this.httpClient = httpClient;
    }

//    public List<SdkCandidate> getSdkCandidates(String url, boolean offline) {
//        var response = this.getSdkCandidates(url, offline);
//        response.forEach();
//    }

    public String get(String url, boolean offline) throws IOException, InterruptedException {
        var cacheFile = new File(cacheLocationFolder, url.replaceAll("[^a-zA-Z0-9]", "_"));
        var fromCache = loadCandidatesFromCache(cacheFile, offline);
        if (fromCache != null) {
            logger.debug("Loaded from cache: {}", url);
            return fromCache;
        }
        if (offline) {
            String message = "Unable to get " + url + " as you are offline";
            throw new IllegalStateException(message);
        }
        try {
            HttpRequest getRequest = HttpRequest.newBuilder().uri(java.net.URI.create(url)).build();
            var response = httpClient.send(getRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
            try (var cacheOutputStream = new java.io.FileOutputStream(cacheFile)) {
                var bytes = response.body().getBytes();
                cacheOutputStream.write(bytes);
                logger.debug("Loaded and saved to cache: {}", url);
                return new String(bytes, StandardCharsets.UTF_8);
            }
        } catch (ConnectException connectException) {
            logger.warn("Failed to connect, assuming you are offline");
            return get(url, true);
        }
    }

    private String loadCandidatesFromCache(File cacheFile, boolean offline) throws IOException {
        if (!cacheFile.getParentFile().exists()) {
            logger.warn("Cache file {} does not exist --> creating folder.", cacheFile.getParentFile().getAbsolutePath());
            cacheFile.getParentFile().mkdirs();
        }
        if (!cacheFile.exists()) {
            long age = System.currentTimeMillis() - cacheFile.lastModified();
            if (offline || age < cacheDuration.toMillis()) {
                try (var resource = new FileInputStream(cacheFile)) {
                    return new String(resource.readAllBytes(), StandardCharsets.UTF_8);
                }
            } else {
                logger.debug("Cache file outdated : {}", cacheFile.getName());
            }
        }
        return null;
    }

}
