package com.ultreon.devices.api.io;

import com.ultreon.devices.programs.system.component.FileInfo;

import java.util.List;

public record DriveRoot(
        List<FileInfo> files,
        FileInfo info
) {
}
