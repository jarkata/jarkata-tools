package cn.jarkata.tools.clazz;

import cn.jarkata.tools.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

/**
 * Jar包工具类
 *
 * @author jarkata
 */
public class JarUtils {

    private static final Object lock = new Object();
    private static final ConcurrentHashMap<String, URLClassLoader> cache = new ConcurrentHashMap<>();

    /**
     * 从指定的位置读取所有的jar文件
     *
     * @param location 文件位置
     * @return 所有class文件集合
     * @throws IOException 文件操作异常
     */
    public static List<Class<?>> readClassFromLocation(String... location) throws IOException {
        return readClasses(FileUtils.toFiles(location).toArray(new File[0]));
    }

    /**
     * 解压Jar文件
     *
     * @param file jar文件
     * @throws IOException IO异常
     */
    public static void unpackJar(File file) throws IOException {
        Objects.requireNonNull(file, "JarFile is null");
        String fileName = file.getName();
        if (!fileName.endsWith(".jar")) {
            throw new IllegalArgumentException("JarFile is invalid,fileName=" + fileName);
        }
        try (
                FileInputStream fileInputStream = new FileInputStream(file);
                JarInputStream jarInputStream = new JarInputStream(fileInputStream); JarFile jarFile = new JarFile(file)
        ) {
            JarEntry nextJarEntry;
            while (Objects.nonNull(nextJarEntry = jarInputStream.getNextJarEntry())) {
                String absolutePath = file.getAbsolutePath();
                absolutePath = absolutePath.substring(0, absolutePath.length() - 4);
                //判断当前层级的文件是否为目录
                File targetFile = new File(absolutePath, nextJarEntry.getName());
                if (!targetFile.exists()) {
                    boolean mkdir = true;
                    if (nextJarEntry.isDirectory()) {
                        //创建目录
                        mkdir = targetFile.mkdirs();
                    }
                    //目录创建失败，抛出此异常
                    if (!mkdir) {
                        throw new IllegalArgumentException(
                                "File mkdirs Failed,directory=" + targetFile.getAbsolutePath());
                    }
                }
                boolean directory = nextJarEntry.isDirectory();
                //如果是目录，则直接跳过，不做文件复制
                if (directory) {
                    continue;
                }
                InputStream inputStream = jarFile.getInputStream(nextJarEntry);
                Path path = targetFile.toPath();
                Files.deleteIfExists(path);
                try (ByteArrayOutputStream fos = new ByteArrayOutputStream()) {
                    FileUtils.copy(inputStream, fos);
                    Files.write(path, fos.toByteArray(), StandardOpenOption.CREATE_NEW);
                }
            }
        }

    }

    /**
     * 读取Jar包类所有的class文件
     *
     * @param jarFile Jar文件
     * @return Jar文件中所有的class文件
     * @throws IOException IO异常
     */
    public static List<Class<?>> readClasses(File... jarFile) throws IOException {
        Objects.requireNonNull(jarFile, "JarFile Is Null");
        List<Class<?>> classList = new ArrayList<>();
        try (
                URLClassLoader jarkataClassLoader = getClassLoader(FileUtils.toURL(jarFile))
        ) {
            for (File file : jarFile) {
                InputStream inputStream = Files.newInputStream(file.toPath());
                JarInputStream jarInputStream = new JarInputStream(inputStream);
                JarEntry nextJarEntry;
                while (Objects.nonNull((nextJarEntry = jarInputStream.getNextJarEntry()))) {
                    String entryName = nextJarEntry.getName();
                    if (!entryName.endsWith(".class")) {
                        continue;
                    }
                    entryName = ClazzUtils.trimClassName(entryName);
                    Class<?> entryClass = jarkataClassLoader.loadClass(entryName);
                    classList.add(entryClass);

                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return classList;
    }


    /**
     * 获取classloader
     *
     * @param urlList Jar文件路径集合
     * @return ClassLoader
     */
    private static URLClassLoader getClassLoader(List<URL> urlList) {
        Objects.requireNonNull(urlList, "url is null");
        String key = Objects.toString(urlList);
        URLClassLoader urlClassLoader = cache.get(key);
        if (Objects.isNull(urlClassLoader)) {
            synchronized (lock) {
                urlClassLoader = new URLClassLoader(urlList.toArray(new URL[0]));
                cache.put(key, urlClassLoader);
            }
        }
        return urlClassLoader;
    }


}
