package com.jab125.updater;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Properties;

public class Updater {
    public static void main(String[] args) throws IOException {
        String apiKey = Objects.requireNonNull(System.getenv("JAB125_COM_API_KEY"), "Api Key Cannot Be Null!");
        String type = Objects.requireNonNull(System.getenv("STORER_TYPE"), "Storer Type Cannot Be Null!");
        String requestStr;
        if (type.equals("build")) {
            var build = Objects.requireNonNull(System.getenv("GITHUB_BUILD_NUMBER"));
            requestStr = build + "\\" + apiKey + "\\" + type;
        } else {
            var props = new Properties();
            props.load(Files.newInputStream(new File("gradle.properties").toPath()));
            String version = props.getProperty("mod_version");
            requestStr = version + "\\" + apiKey + "\\" + type;
        }


        var builder = HttpRequest.newBuilder(URI.create("https://jab125.com/updater.php"));
        builder.POST(HttpRequest.BodyPublishers.ofString(requestStr));
        var request = builder.build();
        try {
            var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
