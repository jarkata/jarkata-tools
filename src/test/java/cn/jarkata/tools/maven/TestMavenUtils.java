package cn.jarkata.tools.maven;

import org.junit.Test;

public class TestMavenUtils {

    @Test
    public void testMaven() throws Exception {
//        System.setProperty("MAVEN_HOME", "/Users/kart/program/apache-maven/");

        String mavenHome = System.getenv("MAVEN_HOME");
        String bin = mavenHome + "/bin/mvn";
        String targetFilePath = "/Users/data/code/gitcode/jarkata-tools/src/test/logback-classic-1.2.11.jar";
//        String str = (" install:install-file -Dfile=" + targetFilePath + "-DpomFile=" + tmpPomPath);
//        String str = (" dependency:get -DgroupId=ch.qos.logback -DartifactId=logback-classic -Dversion=1.2.11"  );
        String str = (" org.apache.maven.plugins:maven-dependency-plugin:2.8:get -DgroupId=ch.qos.logback  " +
                "-DartifactId=logback-classic -Dversion=1.2.11");
        boolean download = MavenUtils.download("ch.qos.logback", "logback-classic", "1.2.11");
        System.out.println(download);
    }


}
