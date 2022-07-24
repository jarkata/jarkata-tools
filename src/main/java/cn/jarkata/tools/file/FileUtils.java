package cn.jarkata.tools.file;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
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


}
