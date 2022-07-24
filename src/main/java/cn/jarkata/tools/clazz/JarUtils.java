package cn.jarkata.tools.clazz;

import cn.jarkata.tools.file.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
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
     * @param location
     * @return
     * @throws IOException
     */
    public static List<Class<?>> readClassFromLocation(String... location) throws IOException {
        return readClasses(FileUtils.toFiles(location).toArray(new File[0]));
    }


    /**
     * 读取Jar包类所有的class文件
     *
     * @param jarFile Jar文件
     * @return
     * @throws IOException
     */
    public static List<Class<?>> readClasses(File... jarFile) throws IOException {
        Objects.requireNonNull(jarFile, "JarFile Is Null");
        List<Class<?>> classList = new ArrayList<>();
        try (
                URLClassLoader jarkataClassLoader = getClassLoader(FileUtils.toURL(jarFile));
        ) {
            for (File file : jarFile) {
                InputStream inputStream = Files.newInputStream(file.toPath());
                JarInputStream jarInputStream = new JarInputStream(inputStream);
                JarEntry nextJarEntry = null;
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
     * @param urlList
     * @return
     */
    private static URLClassLoader getClassLoader(List<URL> urlList) {
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
