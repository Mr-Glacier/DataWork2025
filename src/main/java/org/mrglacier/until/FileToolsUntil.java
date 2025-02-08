package org.mrglacier.until;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Mr-Glacier
 * @version 1.0
 * @apiNote 文件类操作工具类
 * @since 2025/2/7 21:27
 */
public class FileToolsUntil {

    /**
     * [文件工具]
     * 创建文件夹
     *
     * @param dirPath 文件夹路径
     * @return 返回值：true：创建成功，false：创建失败
     */
    public static boolean methodCreatFolder(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            return file.mkdirs();
        } else {
            return true;
        }
    }

    /**
     * [文件工具]
     * 删除文件夹 ,删除文件夹下所有文件
     * 如果路径输入的是文件，则删除文件，如果路径输入的是文件夹，则删除文件夹下所有文件，并删除文件夹
     *
     * @param dirPath 文件夹路径
     */
    public static boolean methodDeleteFolder(String dirPath) {
        File file = new File(dirPath);
        if (file.exists() && file.isDirectory()) {
            // 删除当前目录下的所有文件和子目录
            File[] contents = file.listFiles();
            if (contents != null) {
                for (File f : contents) {
                    if (f.isDirectory()) {
                        if (!methodDeleteFolder(f.getAbsolutePath())) {
                            // 如果子目录删除失败，则直接返回false
                            return false;
                        }
                    } else {
                        if (!f.delete()) {
                            // 如果文件删除失败，则直接返回false
                            return false;
                        }
                    }
                }
            }
            // 尝试删除当前目录
            return file.delete();
        } else if (file.exists()) {
            // 直接尝试删除文件
            return file.delete();
        }
        // 目录不存在异常抛出
        throw new RuntimeException("目录不存在");
    }


    /**
     * [文件工具]
     * 重命名文件夹 或 文件
     *
     * @param oldPath 原文件路径
     * @param newPath 新文件路径
     * @return 返回值：true：重命名成功，false：重命名失败
     */
    public static boolean methodRenameFolder(String oldPath, String newPath) {
        try {
            Path oldFilePath = Paths.get(oldPath);
            Path newFilePath = Paths.get(newPath);
            if (!Files.exists(oldFilePath)) {
                throw new RuntimeException(oldPath + "-->指定的文件或目录不存在");
            }
            // 使用标准复制选项，如果新路径已经存在则替换它
            Files.move(oldFilePath, newFilePath, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (NoSuchFileException e) {
            System.err.println("No such file or directory: " + e.getMessage());
        } catch (FileAlreadyExistsException e) {
            System.err.println("File already exists: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("An I/O error occurred: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
        return false;
    }


    /**
     * [文件工具]
     * 读取文件内容 AS String , UTF-8编码
     *
     * @param filePath 文件路径
     * @return 文件内容
     */
    public static String methodReadFileAsString(String filePath) {
        // 检查文件路径是否为null或空
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path is null or empty.");
        }
        // 检查文件是否存在
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("File does not exist: " + filePath);
        }
        try {
            // 尝试读取所有字节并以UTF-8编码返回为字符串
            return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            System.err.println("The file does not exist: " + filePath);
        } catch (IOException e) {
            System.err.println("An I/O error occurred while reading the file: " + e.getMessage());
        } catch (SecurityException e) {
            System.err.println("Permission denied to read the file: " + filePath);
        }
        return null;
    }

    /**
     * [文件工具]
     * 读取文件内容 AS String , 指定字符编码
     *
     * @param filePath 文件路径
     * @param charset  字符编码 "UTF-8", "GB2312"等
     * @return 文件内容
     */
    public static String methodReadFileAsString(String filePath, String charset) {
        // 检查文件路径是否为null或空
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path is null or empty.");
        }
        // 检查文件是否存在
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("File does not exist: " + filePath);
        }
        try {
            return new String(Files.readAllBytes(path), charset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * [文件工具]
     * 按行读取文件内容，并返回一个包含每行的字符串列表。
     *
     * @param filePath 文件路径
     * @return 读取文件内容
     */
    public static List<String> methodReadFileByLine(String filePath) {
        List<String> results = new ArrayList<>();
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path is null or empty.");
        }
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("File does not exist: " + filePath);
        }
        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(results::add);
            return results;
        } catch (IOException e) {
            System.err.println("Error occurred while reading the file: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


    /**
     * [文件工具]
     * 写入文件内容 ,覆盖写入
     *
     * @param filePath 文件路径
     * @param content  文件内容
     * @return 写入文件是否成功
     */
    public static boolean methodWriteFile(String filePath, String content) {
        try {
            Files.write(Paths.get(filePath), content.getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * [文件工具]
     * 写入文件内容 ,追加写入
     *
     * @param filePath 文件路径
     * @param content  文件内容
     * @param isAppend 是否追加写入
     * @return 是否写入成功
     */
    public static boolean methodWriteFile(String filePath, String content, boolean isAppend) {
        try {
            Path path = Paths.get(filePath);
            if (!isAppend) {
                Files.write(path, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } else {
                Files.write(path, content.getBytes(StandardCharsets.UTF_8));
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * [文件工具]
     * 写入文件内容, 使用缓冲区
     *
     * @param filePath 文件路径
     * @param content  文件内容
     */
    public void methodWriteFileBuffer(String filePath, String content) {
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(Files.newOutputStream(Paths.get(filePath)), 331074)) {
            bufferedOutputStream.write(content.getBytes(StandardCharsets.UTF_8));
            bufferedOutputStream.flush();
        } catch (IOException ex) {
            System.err.println("Error occurred while writing to the file: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    /**
     * [文件工具]
     * 写入文件内容, 使用缓冲区 ,是否追加写入
     *
     * @param filePath 文件路径
     * @param content  文件内容
     * @param append   是否追加写入
     */
    public void methodWriteFileBuffer(String filePath, String content, boolean append) {
        OpenOption[] options = append ?
                new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND} :
                new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};

        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(Files.newOutputStream(Paths.get(filePath), options), 331074)) {
            bufferedOutputStream.write(content.getBytes(StandardCharsets.UTF_8));
            bufferedOutputStream.flush();
        } catch (IOException ex) {
            System.err.println("Error occurred while writing to the file: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }


}
