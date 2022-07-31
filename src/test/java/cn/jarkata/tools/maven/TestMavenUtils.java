package cn.jarkata.tools.maven;

import org.apache.maven.cli.MavenCli;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class TestMavenUtils {

    @Test
    public void testMaven() throws IOException, InterruptedException {
//        System.setProperty("MAVEN_HOME", "/Users/kart/program/apache-maven/");

        String mavenHome = System.getenv("MAVEN_HOME");
        String bin = mavenHome + "/bin/mvn";
        String targetFilePath = "/Users/data/code/gitcode/jarkata-tools/src/test/logback-classic-1.2.11.jar";
//        String str = (" install:install-file -Dfile=" + targetFilePath + "-DpomFile=" + tmpPomPath);
//        String str = (" dependency:get -DgroupId=ch.qos.logback -DartifactId=logback-classic -Dversion=1.2.11"  );
        String str = (" org.apache.maven.plugins:maven-dependency-plugin:2.8:get -DgroupId=ch.qos.logback  " +
                "-DartifactId=logback-classic -Dversion=1.2.11");
        MavenUtils.download("ch.qos.logback", "logback-classic", "1.2.11");
    }


    @Test
    public void mavenCli() {
        String tmpPomPath = "/Users/data/code/gitcode/jarkata-tools/src/test/";
        System.setProperty("maven.multiModuleProjectDirectory", tmpPomPath);
        String cmd = "dependency:get -DgroupId=cn.jarkata -DartifactId=jarkata-commons -Dversion=1.0.1";
        String[] split = cmd.split(" ");
        MavenCli mavenCli = new MavenCli();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int pomPath = mavenCli.doMain(split, tmpPomPath, new PrintStream(outputStream), new PrintStream(outputStream));
        System.out.println(pomPath);
        System.out.println(outputStream.toString());
    }
}
