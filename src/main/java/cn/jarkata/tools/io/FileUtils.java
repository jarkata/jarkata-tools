package cn.jarkata.tools.io;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FileUtils {

    /**
     * 将所有的文件对象转换为List
     *
     * @param file 文件集合
     * @return URL集合
     */
    public static List<URL> toURL(File... file) {
        return Arrays.stream(file).map(mapper -> {
            try {
                return mapper.toURI().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }


    public static List<File> toFiles(String... locations) {
        return Arrays.stream(locations).map(File::new).collect(Collectors.toList());
    }


    /**
     * 将input输入流复制至output数据流
     *
     * @param inputStream  输入流
     * @param outputStream 输出流
     */
    public static void copy(InputStream inputStream, OutputStream outputStream) {
        try (
                BufferedInputStream bis = new BufferedInputStream(inputStream);
                BufferedOutputStream fos = new BufferedOutputStream(outputStream)
        ) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 删除文件
     *
     * @param files 文件集
     * @throws IOException IO异常
     */
    public static void clearFile(File... files) throws IOException {
        Objects.requireNonNull(files, "files is null");
        for (File file : files) {
            if (file.isDirectory()) {
                clearFile(file);
                continue;
            }
            boolean deleteIfExists = Files.deleteIfExists(file.toPath());
            if (!deleteIfExists) {
                throw new IOException("delete failed");
            }
        }
    }


}
