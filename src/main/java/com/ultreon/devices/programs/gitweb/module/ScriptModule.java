package com.ultreon.devices.programs.gitweb.module;

//public class ScriptModule extends Module {
//    @Override
//    public String[] getRequiredData() {
//        return new String[]{"script", "runtime"};
//    }
//
//    @Override
//    public String[] getOptionalData() {
//        return new String[0];
//    }
//
//    @Override
//    public int calculateHeight(Map<String, String> data, int width) {
//        return 1;
//    }
//
//    @Override
//    public void generate(GitWebFrame frame, Layout layout, int width, Map<String, String> data) {
//        String script = data.get("script").replaceAll("scriptModule:newLine", "\n").replaceAll("scriptModule:equals", "=");
//        if (data.get("runtime").equals("apoint")) {
//            initAPointRuntime(frame);
//            System.out.println(script);
//            frame.aPointRuntime.parse(script);
//        } else if (data.get("runtime").equals("ulang")) {
//            //
//        }
//    }
//
//    public static void main(String[] args) {
//        String script = """
//                VAL str paragraph
//                VAL str text
//                VAL str "Hello World!"
//                MODIFY
//                """;
//        System.out.println(script.replaceAll("\n", "scriptModule:newLine").replaceAll("=", "scriptModule:equals"));
//    }
//
//    private void initAPointRuntime(GitWebFrame frame) {
//        if (frame.aPointRuntime == null) {
//            APointRuntime runtime = APoint.createRuntime();
//            runtime.removeCommand("METHOD:STATIC");
//            runtime.removeCommand("METHOD");
//            runtime.removeCommand("FIELD:STATIC");
//            runtime.addCommand("RANDOM", (params) -> {
//                runtime.setPointer(runtime.getOut(), Math.random());
//            });
//            runtime.addCommand("MODIFY", (params) -> {
//                String id = (String) runtime.getVals().get(0);
//                String key = (String) runtime.getVals().get(1);
//                String value = (String) runtime.getVals().get(2);
//                for (Component component : frame.layout.components) {
//                    if (component instanceof ModuleLayout) {
//                        ModuleLayout moduleLayout = (ModuleLayout) component;
//                        if (id.equals(moduleLayout.entry.getId())) {
//                            HashMap<String, String> newData = new HashMap<>(moduleLayout.entry.getData());
//                            newData.put(key, value);
//                            moduleLayout.entry.setData(newData);
//                            moduleLayout.modify();
//                        }
//                    }
//                }
//            });
//            frame.aPointRuntime = runtime;
//            //runtime.removeCommand("");
//        }
//    }
//
//    public static void setTimeout(Runnable runnable, int delay){
//        new Thread(() -> {
//            try {
//                Thread.sleep(delay);
//                runnable.run();
//            }
//            catch (Exception e){
//                System.err.println(e);
//            }
//        }).start();
//    }
//
//    @Override
//    public void tick(GitWebFrame frame, Layout layout, int width, Map<String, String> data) {
//
//    }
//
//    @Override
//    public void modify(GitWebFrame frame, ModuleLayout layout, int width, Map<String, String> data) {
//        this.generate(frame, layout, width, data);
//    }
//}
