package cn.jarkata.tools.clazz;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JarUtilsTest {

    @Test
    public void testJar() throws IOException {
        List<Class<?>> classList = JarUtils.readClassFromLocation(
                "/Users/data/jarkata-tools/src/test/resources/jarkata-facade-1.0-SNAPSHOT.jar");
        System.out.println(classList);
        Assert.assertEquals(4, classList.size());
    }

    @Test
    public void readClasses() throws IOException {
        List<Class<?>> classList = JarUtils.readClasses(
                new File("/Users/data/jarkata-tools/src/test/resources/jarkata-facade-1.0-SNAPSHOT.jar"));
        for (Class<?> aClass : classList) {
            System.out.println(aClass);
        }
    }

    @Test
    public void unpackJar() throws IOException {
        JarUtils.unpackJar(new File("/Users/data/code/gitcode/jarkata-tools/src/test/resources/jarkata-facade-1.0-SNAPSHOT.jar"));
    }
}