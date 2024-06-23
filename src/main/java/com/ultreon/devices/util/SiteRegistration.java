package com.ultreon.devices.util;

import com.ultreon.devices.Devices;
import com.ultreon.devices.programs.gitweb.component.GitWebFrame;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;

import static com.ultreon.devices.util.SiteRegistration.Type.*;

public final class SiteRegistration {
    private static final Marker MARKER = MarkerFactory.getMarker("SITE_REGISTER");
    private final String registrant;
    private final String string;
    private final Type type;
    private final String site;

    public SiteRegistration(String registrant, String string, Type type, String site) {
        this.registrant = registrant;
        this.string = string;
        this.type = type;
        this.site = site;
    }

    public SiteRegistration(String registrant, String string, String type, String site) {
        this(registrant, string, Type.of(type), site);
    }

    public static String getURL(String website) {
        Matcher matcher = GitWebFrame.PATTERN_LINK.matcher(website);
        if (!matcher.matches()) {
            Devices.LOGGER.error("No Match Found For " + website + "!");
            return "https://raw.githubusercontent.com/Ultreon/gitweb-sites/main/";
        }
        String domain = matcher.group("domain");
        String extension = matcher.group("extension");
        String directory = matcher.group("directory");
        String url = "https://raw.githubusercontent.com/Ultreon/gitweb-sites/main/";
        for (SiteRegistration siteRegistration : Devices.SITE_REGISTRATIONS) {
            if (siteRegistration.type == ALL) {
                url = siteRegistration.site;
            } else if (siteRegistration.type == EXTENSION && siteRegistration.string.equals(extension)) {
                url = siteRegistration.site;
            } else if (siteRegistration.type == DOMAIN && domain != null) {
                Matcher domainMatcher = GitWebFrame.PATTERN_LINK.matcher(siteRegistration.string);
                if (domainMatcher.matches() && domainMatcher.group("domain").equals(domain) && domainMatcher.group("extension").equals(extension)) {
                    url = siteRegistration.site;
                }
            } else if (siteRegistration.type == DIRECTORY && directory != null) { //TODO: Implement cascading priorities
                Matcher directoryMatcher = GitWebFrame.PATTERN_LINK.matcher(siteRegistration.string);
                if (directoryMatcher.matches() && directoryMatcher.group("domain").equals(domain) && directoryMatcher.group("extension").equals(extension) && directoryMatcher.group("directory").equals(directory)) {
                    url = siteRegistration.site;
                }
            }
        }
        Devices.LOGGER.info(MARKER, "Registered Sites: " + Devices.SITE_REGISTRATIONS.size() + ", " + "URL: " + url);
        return url;
    }

    public String registrant() {
        return registrant;
    }

    public String string() {
        return string;
    }

    public Type type() {
        return type;
    }

    public String site() {
        return site;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SiteRegistration) obj;
        return Objects.equals(this.registrant, that.registrant) &&
                Objects.equals(this.string, that.string) &&
                Objects.equals(this.type, that.type) &&
                Objects.equals(this.site, that.site);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registrant, string, type, site);
    }

    @Override
    public String toString() {
        return "SiteRegistration[" +
                "registrant=" + registrant + ", " +
                "string=" + string + ", " +
                "type=" + type + ", " +
                "site=" + site + ']';
    }


    public enum Type {
        ALL, EXTENSION, DOMAIN, DIRECTORY;

        public static Type of(String type) {
            if (type.equals("*")) {
                return ALL;
            } else {
                return Type.valueOf(type.toUpperCase(Locale.ROOT));
            }
        }

        public static Type compare(Type a, Type b) {
            return a.ordinal() > b.ordinal() ? b : a;
        }
    }
}
