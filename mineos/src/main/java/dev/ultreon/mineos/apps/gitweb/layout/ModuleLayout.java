package dev.ultreon.mineos.apps.gitweb.layout;

import dev.ultreon.devices.impl.app.Layout;
import dev.ultreon.mineos.apps.gitweb.module.ModuleEntry;
import dev.ultreon.mineos.apps.gitweb.component.GitWebFrame;

public class ModuleLayout extends Layout {
    public ModuleEntry entry;
    private final GitWebFrame frame;

    public ModuleLayout(int left, int top, int width, GitWebFrame frame, ModuleEntry entry) {
        super(left, top, width, entry.getModule().calculateHeight(entry.getData(), width));
        this.entry = entry;
        this.frame = frame;
    }

    public void modify() {
        //this.components.clear();
        entry.getModule().modify(frame, this, width, entry.getData());
    }

    @Override
    public void init() {
        super.init();
        entry.getModule().generate(frame, this, width, entry.getData());
    }

    public void _tick() {
        //DebugLog.log("TICKING " + entry.getModule());
        entry.getModule().tick(frame, this, width, entry.getData());
    }
}
