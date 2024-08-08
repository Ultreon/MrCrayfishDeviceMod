package dev.ultreon.devices.impl.bios;

public class Executable {
    private final ExecMeta meta;
    private final ExecutableResource resource;

    public Executable(ExecMeta meta, ExecutableResource resource) {
        this.meta = meta;
        this.resource = resource;
    }

    public void execute(UserspaceClassLoader classLoader, String[] args) throws Throwable {
        Class<?> clazz = classLoader.loadClass(meta.launcher());
        clazz.getDeclaredMethod("main", String[].class).invoke(null, new Object[]{args});
    }

    public ExecMeta getMeta() {
        return meta;
    }

    public byte[] getClass(String name) throws ClassNotFoundException {
        return resource.getClass(name);
    }
}
