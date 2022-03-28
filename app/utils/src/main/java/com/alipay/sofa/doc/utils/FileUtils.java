/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.doc.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * 文件操作工具类<br>
 *
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
public class FileUtils {

    /**
     * 回车符
     */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * 读取文件内容
     *
     * @param file 文件
     * @return 文件内容
     * @throws IOException 发送IO异常
     */
    public static String file2String(File file) throws IOException {
        if (file == null || !file.exists() || !file.isFile() || !file.canRead()) {
            return null;
        }
        FileReader reader = null;
        StringWriter writer = null;
        try {
            reader = new FileReader(file);
            writer = new StringWriter();
            char[] cbuf = new char[1024];
            int len = 0;
            while ((len = reader.read(cbuf)) != -1) {
                writer.write(cbuf, 0, len);
            }
            return writer.toString();
        } finally {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(writer);
        }
    }

    /**
     * 读取类相对路径内容
     *
     * @param clazz        文件
     * @param relativePath 相对路径
     * @param encoding     编码
     * @return 文件内容
     * @throws IOException 发送IO异常
     */
    public static String file2String(Class clazz, String relativePath, String encoding) throws IOException {
        InputStream is = null;
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        try {
            is = clazz.getResourceAsStream(relativePath);
            reader = new InputStreamReader(is, encoding);
            bufferedReader = new BufferedReader(reader);
            StringBuilder context = new StringBuilder();
            String lineText;
            while ((lineText = bufferedReader.readLine()) != null) {
                context.append(lineText).append(LINE_SEPARATOR);
            }
            return context.toString();
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (is != null) {
                is.close();
            }
        }
    }

    /**
     * 读取类相对路径内容
     *
     * @param file 文件
     * @return 文件内容（按行）
     * @throws IOException 读取IO异常
     */
    public static List<String> readLines(File file) throws IOException {
        List<String> lines = new ArrayList<String>();
        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(isr)) {
            String lineText = null;
            while ((lineText = bufferedReader.readLine()) != null) {
                lines.add(lineText);
            }
            return lines;
        }
    }

    /**
     * 写内容到文件
     *
     * @param file 文件
     * @throws IOException 写入IO异常
     */
    public static void writeLines(File file, List<String> lines) throws IOException {
        try (OutputStreamWriter isr = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
             BufferedWriter bufferedWriter = new BufferedWriter(isr)) {
            for (String line : lines) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
        }
    }


    /**
     * 字符流写文件 较快
     *
     * @param file 文件
     * @param data 数据
     * @return 操作是否成功
     * @throws IOException 发送IO异常
     */
    public static boolean string2File(File file, String data) throws IOException {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileWriter writer = null;
        try {
            writer = new FileWriter(file, false);
            writer.write(data);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        return true;
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 文件夹
     * @return 是否删除完成
     */
    public static boolean cleanDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String aChildren : children) {
                    boolean success = cleanDirectory(new File(dir, aChildren));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return dir.delete();
    }

    /**
     * 清楚路径前后的分隔符
     *
     * @param path
     * @return
     */
    public static String trimPath(String path) {
        return trimPath(path, true, true);
    }

    private static String trimPath(String path, boolean left, boolean right) {
        char[] val = path.toCharArray();
        char sbeTrim = '/';
        int idx = 0;
        int len = path.length();
        if (left) {
            while ((idx < len) && (val[idx] == sbeTrim)) {
                idx++;
            }
        }
        if (right) {
            while ((idx < len) && (val[len - 1] == sbeTrim)) {
                len--;
            }
        }
        return ((idx > 0) || (len < path.length())) ? path.substring(idx, len) : path;
    }

    public static String contactPath(String... paths) {
        if (paths.length == 1) {
            return paths[0];
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(trimPath(paths[0], false, true)).append("/");
            for (int i = 1; i < paths.length; i++) {
                sb.append(trimPath(paths[i])).append("/");
            }
            return sb.substring(0, sb.length() - 1);
        }
    }

    /**
     * 解压文件
     *
     * @param file      压缩文件
     * @param targetDir 解压文件输出的目录
     * @throws IOException
     */
    public static void unPacket(Path file, Path targetDir) throws IOException {
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }
        ZipFile zipFile = new ZipFile(file.toFile());
        try {
            try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(file))) {
                ZipEntry zipEntry = null;
                while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                    String entryName = zipEntry.getName();
                    Path entryFile = targetDir.resolve(entryName);
                    if (zipEntry.isDirectory()) {
                        if (!Files.isDirectory(entryFile)) {
                            Files.createDirectories(entryFile);
                        }
                    } else {
                        try (InputStream zipEntryInputStream = zipFile.getInputStream(zipEntry)) {
                            try (OutputStream fileOutputStream = Files.newOutputStream(entryFile, StandardOpenOption.CREATE_NEW)) {
                                byte[] buffer = new byte[4096];
                                int length = 0;
                                while ((length = zipEntryInputStream.read(buffer)) != -1) {
                                    fileOutputStream.write(buffer, 0, length);
                                }
                                fileOutputStream.flush();
                            }
                        }
                    }
                }
            }
        } finally {
            zipFile.close();
        }
    }
}