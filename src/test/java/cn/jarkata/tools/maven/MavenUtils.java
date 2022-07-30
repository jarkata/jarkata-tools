package cn.jarkata.tools.maven;

import cn.jarkata.tools.file.FileUtils;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class MavenUtils {

    @Test
    public void testMaven() throws IOException, InterruptedException {
//        System.setProperty("MAVEN_HOME", "/Users/kart/program/apache-maven/");

        String mavenHome = System.getenv("MAVEN_HOME");
        String bin = mavenHome + "/bin/mvn";
        String tmpPomPath = "/Users/data/code/gitcode/jarkata-tools/src/test/java/tmp.xml";
        String targetFilePath = "/Users/data/code/gitcode/jarkata-tools/src/test/logback-classic-1.2.11.jar";
//        String str = (" install:install-file -Dfile=" + targetFilePath + "-DpomFile=" + tmpPomPath);
//        String str = (" dependency:get -DgroupId=ch.qos.logback -DartifactId=logback-classic -Dversion=1.2.11"  );
        String str = (" org.apache.maven.plugins:maven-dependency-plugin:2.8:get -DgroupId=ch.qos.logback  " +
                "-DartifactId=logback-classic -Dversion=1.2.11");

        String cmd = bin + " dependency:get -DgroupId=cn.jarkata -DartifactId=jarkata-tools -Dversion=0.0.1";
        String[] split = cmd.split(" ");

        System.out.println(cmd);
        ProcessBuilder builder = new ProcessBuilder(Arrays.asList(split));
        Process process = builder.start();

        InputStream inputStream = process.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        FileUtils.copy(inputStream, outputStream);
        FileUtils.copy(process.getErrorStream(), outputStream);
        System.out.println(new String(outputStream.toByteArray()));
        int waitFor = process.waitFor();
        System.out.println(waitFor);
        int value = process.exitValue();
        System.out.println(value);
    }
}
