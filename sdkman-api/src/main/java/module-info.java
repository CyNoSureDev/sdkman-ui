module sdkmanapi {
    requires java.net.http;
    requires org.slf4j;
    requires org.apache.commons.compress;
    requires com.sun.jna.platform;
    requires com.google.gson;
    exports io.github.jagodevreede.sdkman.api;
    exports io.github.jagodevreede.sdkman.api.domain;
    exports io.github.jagodevreede.sdkman.api.domain.json;
    exports io.github.jagodevreede.sdkman.api.http;
    exports io.github.jagodevreede.sdkman.api.files;
}