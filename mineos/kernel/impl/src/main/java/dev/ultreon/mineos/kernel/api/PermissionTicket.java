package dev.ultreon.mineos.kernel.api;

public interface PermissionTicket {
    boolean hasPermission(Permission permission);
}
