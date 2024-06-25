package io.github.jagodevreede.sdkman.api.http;

import java.io.NotActiveException;

import static io.github.jagodevreede.sdkman.api.OsHelper.getPlatformName;

public class SdkManUriComposer {

    private String BASE_URL = "";
    private boolean useJson = false;

    public SdkManUriComposer(String baseUrl, boolean useJson) {
        this.BASE_URL = baseUrl;
        this.useJson = useJson;
    }

    public String getVersionsUrlForCandidate(String candidate) {
        if (useJson) {
            return BASE_URL + "/versions/" + candidate;
        }
        return BASE_URL + "/candidates/" + candidate + "/" + getPlatformName() + "/versions/list?installed=";
    }

    // SdkMan provides an own download uri for fetching the binaries
    public String getDownloadUrlFor(String identifier, String version) throws NotActiveException {
        if (useJson) {
            throw new NotActiveException("JSON api not yet active for downloads");
        } else {
            return "/broker/download/" + identifier + "/" + version + "/" + getPlatformName();
        }
    }
}