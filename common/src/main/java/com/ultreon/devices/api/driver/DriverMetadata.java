package com.ultreon.devices.api.driver;

import com.jab125.version.SemanticVersion;

public record DriverMetadata(String name, String description, String author, SemanticVersion version) {

}
