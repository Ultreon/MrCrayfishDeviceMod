package dev.ultreon.devices.mineos.ultan;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class UltanScript {
    private final ScriptSource source;
    private final Map<String, UtStruct> variables = new LinkedHashMap<>();

    public UltanScript(ScriptSource source) {
        this.source = source;
    }

    public void compile() throws CompileException {
        try {
            new UltanCompiler(this.source.open());
        } catch (IOException e) {
            throw new CompileException();
        }
    }
}
