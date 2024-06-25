package io.github.jagodevreede.sdkman.api.domain.json;

// Representation of Json objects at https://state.sdkman.io/versions/java
public record SdkCandidate(
        String candidate,
        String version,
        String vendor,
        String platform,
        String url,
        boolean available) {
}
