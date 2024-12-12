package com.ultreon.devices.api.event;

import com.ultreon.devices.api.print.IPrint;
import com.ultreon.devices.block.entity.PrinterBlockEntity;
import org.jetbrains.annotations.NotNull;

public class PrinterPrintedEvent implements PrinterEvent {
    private final PrinterBlockEntity printer;
    private final IPrint print;

    public PrinterPrintedEvent(@NotNull PrinterBlockEntity printer,
                               @NotNull IPrint print) {
        this.printer = printer;
        this.print = print;
    }

    @Override
    public @NotNull PrinterBlockEntity getPrinter() {
        return printer;
    }

    public @NotNull IPrint getPrint() {
        return print;
    }
}
