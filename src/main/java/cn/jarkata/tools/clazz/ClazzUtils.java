package cn.jarkata.tools.clazz;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 类文件相关操作的工具类
 */
public class ClazzUtils {


    public static String trimClassName(String className) {
        Objects.requireNonNull(className,"className is null");
        int index = className.lastIndexOf(".cl");
        className = className.substring(0, index);
        return className.replaceAll("/", ".");
    }


    public static List<Method> getMethods(Class<?> clazz) {
        return Arrays.asList(clazz.getDeclaredMethods());
    }

    public static List<Field> getFields(Class<?> clazz) {
        return Arrays.asList(clazz.getDeclaredFields());
    }


}
