package cn.jarkata.tools.clazz;

import cn.jarkata.commons.utils.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JarUtilsTest {

    @Test
    public void testJar() throws IOException {
        File file = FileUtils.getFile("jarkata-facade-1.0-SNAPSHOT.jar");
        String absolutePath = file.getAbsolutePath();
        List<Class<?>> classList = JarUtils.readClassFromLocation(absolutePath);
        System.out.println(classList);
        Assert.assertEquals(4, classList.size());
    }

    @Test
    public void readClasses() throws IOException {
        File file = FileUtils.getFile("jarkata-facade-1.0-SNAPSHOT.jar");
        List<Class<?>> classList = JarUtils.readClasses(file);
        for (Class<?> aClass : classList) {
            System.out.println(aClass);
        }
    }

    @Test
    public void unpackJar() throws IOException {
        File file = FileUtils.getFile("jarkata-facade-1.0-SNAPSHOT.jar");
        JarUtils.unpackJar(file);
    }
}