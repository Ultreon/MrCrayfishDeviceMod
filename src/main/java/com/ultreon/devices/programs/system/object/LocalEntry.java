package com.ultreon.devices.programs.system.object;

import com.ultreon.devices.object.AppInfo;

import java.util.Objects;

/**
 * @author MrCrayfish
 */
public final class LocalEntry implements AppEntry {
    private final AppInfo info;

    /**
     *
     */
    public LocalEntry(AppInfo info) {
        this.info = info;
    }

    @Override
    public String id() {
        return info.getId().toString();
    }

    @Override
    public String name() {
        return info.getName();
    }

    @Override
    public String author() {
        return info.getAuthor();
    }

    @Override
    public String description() {
        return info.getDescription();
    }

    @Override
    public String version() {
        return info.getVersion();
    }

    @Override
    public AppInfo.Icon icon() {
        return info.getIcon();
    }

    @Override
    public String[] screenshots() {
        return info.getScreenshots();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AppEntry) {
            return ((AppEntry) obj).id().equals(id());
        }
        return false;
    }

    public AppInfo info() {
        return info;
    }

    @Override
    public int hashCode() {
        return Objects.hash(info);
    }

    @Override
    public String toString() {
        return "LocalEntry[" +
                "info=" + info + ']';
    }

}
