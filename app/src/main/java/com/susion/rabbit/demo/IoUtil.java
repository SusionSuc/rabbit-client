package com.susion.rabbit.demo;


import java.io.Closeable;
import java.io.File;
import java.util.zip.ZipFile;

/**
 * 提供 APM 全局使用的 IO 工具类
 * @author liaoxudong
 */
public class IoUtil {

    private IoUtil() {
    }

    public static void safeClose(Closeable stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (Throwable ignored) {

        }
    }

    public static void safeClose(ZipFile zipFile) {
        if (zipFile == null) {
            return;
        }
        try {
            zipFile.close();
        } catch (Throwable ignored) {
            // ignore
        }
    }

    public static boolean mkdir(String path) {
        File dir = new File(path);
        return dir.mkdirs();
    }

}
