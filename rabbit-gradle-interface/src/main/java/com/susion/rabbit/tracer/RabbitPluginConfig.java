package com.susion.rabbit.tracer;

import java.util.ArrayList;
import java.util.List;

/**
 * susionwang at 2020-01-02
 * <p>
 * 读取 rabbit plugin's config， 运行时会使用这些变量
 * <p>
 * 同步于 Gradle Transform Plugin Config
 */
public class RabbitPluginConfig {

    public static final String CLASS_PATH = "com/susion/rabbit/tracer/RabbitPluginConfig";
    public static final String METHOD_INJECT_METHOD_PKGS = "injectMethodMonitorPkgs";
    public static final String METHOD_ADD_PKG_TO_METHOD_PKGS = "addPkgToMethodPkgs";
    public static final String METHOD_ADD_PKG_TO_METHOD_PKGS_PARAMS = "(Ljava/lang/String;)V";

    public static List<String> pageSpeedMonitorPkgs = new ArrayList<>();
    public static List<String> methodMonitorPkgs = new ArrayList<>();

    public static void loadConfig() {
        injectMethodMonitorPkgs();
        injectPageSpeedMonitorPkgs();
    }

    public static void injectMethodMonitorPkgs() {
        //generate code in transform
    }

    public static void injectPageSpeedMonitorPkgs() {
        //generate code in transform
    }

    public static void addPkgToMethodPkgs(String pkg) {
        methodMonitorPkgs.add(pkg);
    }

}
