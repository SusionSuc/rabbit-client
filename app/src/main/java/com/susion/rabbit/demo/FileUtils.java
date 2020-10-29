package com.susion.rabbit.demo;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by wanlipeng on 2018/7/31 下午5:05
 */
public class FileUtils {
    private FileUtils() {
    }

    /**
     * 把指定内容写入到文件中
     *
     * @param file    需要写入的文件
     * @param content 写入的内容
     * @throws IOException
     */
    public static void writeFile(@NonNull File file, @NonNull String content, boolean append) throws IOException {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        file.getParentFile().mkdirs();
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file, append);
            outputStream.write(content.getBytes());
            outputStream.flush();
        } finally {
            IoUtil.safeClose(outputStream);
        }
    }

    /**
     * 把指定内容写入到文件中
     *
     * @param file    需要写入的文件
     * @param content 写入的内容
     * @throws IOException
     */
//    public static void writeFile(@NonNull File file, @NonNull JSONObject content, boolean append) throws IOException {
//        if (content == null) {
//            return;
//        }
//        file.getParentFile().mkdirs();
//        BufferedWriter writer = null;
//        try {
//            writer = new BufferedWriter(new FileWriter(file));
//            JSONWriter.writeTo(content, writer);
//        } catch (Throwable t) {
//        } finally {
//            IoUtil.safeClose(writer);
//        }
//    }

    /**
     * 把指定内容写入到文件中
     *
     * @param file    需要写入的文件
     * @param content 写入的内容
     * @throws IOException
     */
//    public static void writeFileWithErrFilter(@NonNull File file, @NonNull JSONObject content, boolean append) throws IOException {
//        if (content == null) {
//            return;
//        }
//        file.getParentFile().mkdirs();
//        BufferedWriter writer = null;
//        try {
//            writer = new BufferedWriter(new FileWriter(file));
//            JSONWriter.writeTo(content, writer);
//        } catch (Throwable t) {
//            try {
//                content.put("err_write", t.toString());
//                CrashBody.putInJson(content, CrashBody.FILTERS, "err_write", t.getLocalizedMessage());
//            } catch (JSONException e) {
//            }
//            Ensure.getInstance().ensureNotReachHereForce(EnsureImpl.NPTH_CATCH, t);
//        } finally {
//            IoUtil.safeClose(writer);
//        }
//    }

    /**
     * 给定文件路径，删除指定文件
     *
     * @param fileName
     * @return 不存在，文件名为空，返回false
     */
    public static boolean deleteFile(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return false;
        }
        return deleteFile(new File(fileName));
    }

    /**
     * 删除文件夹
     *
     * @param parentFile
     * @return
     */
    public static boolean deleteFile(@NonNull File parentFile) {
        if (!parentFile.exists()) {
            return true;
        }
        if (!parentFile.canWrite()) {
            return false;
        }
        if (parentFile.isFile()) {
            return parentFile.delete();
        }
        boolean result = true;
        if (parentFile.isDirectory()) {
            File[] files = parentFile.listFiles();
            for (int i = 0; files != null && i < files.length; i++) {
                if (files[i].isFile()) {
                    if (files[i].canWrite()) {
                        result &= files[i].delete();
                    } else {
                        result = false;
                    }
                } else {
                    result &= deleteFile(files[i]);
                }
            }
            // 最后把根目录删除
            result &= parentFile.delete();
        }
        return result;
    }

    /**
     * 从文件中读取字符串
     *
     * @param fileName 指定文件名
     * @return 返回文件内容
     * @throws IOException
     */
    public static String readFile(String fileName, String lineAppend) throws IOException {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }
        File file = new File(fileName);
        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
                if (lineAppend != null) {
                    sb.append(lineAppend);
                }
            }
        } finally {
            IoUtil.safeClose(bufferedReader);
        }
        return sb.toString();
    }

    /**
     * 从文件中读取字符串
     *
     * @param fileName 指定文件名
     * @return 返回文件内容
     * @throws IOException
     */
    public static JSONArray readFileArray(String fileName) throws IOException {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }
        File file = new File(fileName);
        JSONArray array = new JSONArray();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                array.put(line);
            }
        } finally {
            IoUtil.safeClose(bufferedReader);
        }
        return array;
    }

    public static boolean isEmpty(File dir) {
        String[] listFiles = dir.list();
        return listFiles == null || listFiles.length == 0;
    }

    public static boolean isEmpty(JSONArray array) {
        return array == null || array.length() == 0;
    }

    public static String readFile(String fileName) throws IOException {
        return readFile(fileName, null);
    }


    public static JSONArray toJsonArray(String string, String split) {
        JSONArray array = new JSONArray();
        if (string != null && split != null) {
            String[] arrayString = string.split(split);
            for (String s : arrayString) {
                array.put(s);
            }
        }

        return array;
    }

    /**
     * 读取属性文件，将结果返回记录到map中
     *
     * @param file 属性文件的路径
     * @return
     */
    @Nullable
    public static Map<String, String> readPropertiesFile(File file) {
        FileInputStream fis = null;
        try {
            Properties properties = new Properties();
            fis = new FileInputStream(file);
            properties.load(fis);
            Set<String> keys = properties.stringPropertyNames();
            Map<String, String> map = new HashMap<>();
            for (String key : keys) {
                map.put(key, properties.getProperty(key));
            }
            return map;
        } catch (IOException e) {
//            NpthLog.w(e);
            return null;
        } finally {
            IoUtil.safeClose(fis);
        }
    }

    @Nullable
    public static String[] readProcFile(String file) {
        String procFileContents;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file)), 1000);
            procFileContents = reader.readLine();
            int rightIndex = procFileContents.indexOf(")");
            if (rightIndex > 0) {
                procFileContents = procFileContents.substring(rightIndex + 2);
            }
            return procFileContents.split(" ");
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            IoUtil.safeClose(reader);
        }
        return null;
    }

    /**
     * 判断是否为空
     *
     * @param files
     * @return
     */
    public static boolean isEmpty(File[] files) {
        return files == null || files.length == 0;
    }

    public static void writeListMap(File file, ArrayList<HashMap<String, String>> list, String split, String kvSplit) {
        if (file == null || list == null || TextUtils.isEmpty(split) || TextUtils.isEmpty(kvSplit)) {
            return;
        }
        FileOutputStream writer = null;
        try {
            writer = new FileOutputStream(file);

            StringBuilder stringBuilder = new StringBuilder();
            HashMap<String, String> map;
            for (int i = 0; i < list.size(); i++) {
                map = list.get(i);
                if (map == null || map.size() == 0) {
                    continue;
                }
                for (String key : map.keySet()) {
                    stringBuilder.append(key).append(kvSplit).append(map.get(key)).append(split);
                }
                stringBuilder.append("\n");
            }

            writer.write(stringBuilder.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtil.safeClose(writer);
        }
    }

    public static ArrayList<HashMap<String, String>> readListMap(File file, String split, String kvSplit) {
        if (file == null || !file.exists() || TextUtils.isEmpty(split) || TextUtils.isEmpty(kvSplit)) {
            return null;
        }

        ArrayList<HashMap<String, String>> list = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            HashMap<String, String> map;
            while ((line = reader.readLine()) != null) {
                if ("\n".equals(line)) {
                    continue;
                }
                String[] lineSplit = line.split(split);
                if (lineSplit != null) {
                    map = new HashMap<>();
                    for (int i = 0; i < lineSplit.length; i++) {
                        String[] kv = lineSplit[i].split(kvSplit);
                        if (kv != null && kv.length == 2) {
                            map.put(kv[0], kv[1]);
                        }
                    }
                    if (map.size() > 0) {
                        if (list == null) {
                            list = new ArrayList<>();
                        }
                        list.add(map);
                    }
                }
            }
        } catch (FileNotFoundException e) {
//            NpthLog.w(e);
        } catch (IOException e) {
//            NpthLog.w(e);
        } finally {
            IoUtil.safeClose(reader);
        }
        return list;
    }

    public static void copy(File from, File to) {
        if (from == null || to == null) {
            return;
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            to.getParentFile().mkdirs();
            fis = new FileInputStream(from);
            fos = new FileOutputStream(to);
            byte[] buf = new byte[1024 * 8];
            int len;
            while ((len = fis.read(buf)) > 0) {
                fos.write(buf, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoUtil.safeClose(fis);
            IoUtil.safeClose(fos);
        }
    }

    /**
     * @param inputFileName 输入一个文件夹
     * @param zipFileName   输出一个压缩文件夹，打包后文件名字
     * @throws Exception
     */
    public static void zip(String inputFileName, String zipFileName) throws Exception {
        zip(zipFileName, new File(inputFileName));
    }

    public static void zip(List<String> inputFileNames, String zipFileName) throws Exception {
        ZipOutputStream out = null;
        try {
            out = new ZipOutputStream(new FileOutputStream(
                    zipFileName));
            for (String inputFileName : inputFileNames) {
                File inputFile = new File(inputFileName);
                zip(out, inputFile, inputFile.getName());
            }
        } finally {
            IoUtil.safeClose(out);
        }
    }

    private static void zip(String zipFileName, File inputFile) throws Exception {
        ZipOutputStream out = null;
        try {
            new File(zipFileName).getParentFile().mkdirs();
            out = new ZipOutputStream(new FileOutputStream(
                    zipFileName));
            zip(out, inputFile, "");
        } finally {
            IoUtil.safeClose(out);
        }
    }

    public static void zip(OutputStream wrapOs, File... inputFiles) throws IOException {
        ZipOutputStream out = null;
        try {
            out = new ZipOutputStream(wrapOs);
            out.putNextEntry(new ZipEntry("/"));
            for (File inputFile : inputFiles) {
                zip(out, inputFile);
            }
        } finally {
            IoUtil.safeClose(out);
        }
    }
    private static void zip(ZipOutputStream out, File f) throws IOException {
        if (!f.exists()) {
            return;
        }
        if (f.isDirectory()) {  //判断是否为目录
            File[] fl = f.listFiles();
            for (int i = 0; i < fl.length; i++) {
                zip(out, fl[i], fl[i].getName());
            }
        } else {                //压缩目录中的所有文件
            FileInputStream in = null;
            try {
                in = new FileInputStream(f);
                int n;
                byte[] buffer = new byte[4096];
                while (-1 != (n = in.read(buffer))) {
                    out.write(buffer, 0, n);
                }
            } finally {
                IoUtil.safeClose(in);
            }
        }
    }

    private static void zip(ZipOutputStream out, File f, String base) throws IOException {
        if (!f.exists()) {
            return;
        }
        if (f.isDirectory()) {  //判断是否为目录
            File[] fl = f.listFiles();
            out.putNextEntry(new ZipEntry(base + "/"));
            base = base.length() == 0 ? "" : base + "/";
            for (int i = 0; i < fl.length; i++) {
                zip(out, fl[i], base + fl[i].getName());
            }
        } else {                //压缩目录中的所有文件
            out.putNextEntry(new ZipEntry(base));
            FileInputStream in = null;
            try {
                in = new FileInputStream(f);
                int n;
                byte[] buffer = new byte[4096];
                while (-1 != (n = in.read(buffer))) {
                    out.write(buffer, 0, n);
                }
            } finally {
                IoUtil.safeClose(in);
            }
        }
    }
}
