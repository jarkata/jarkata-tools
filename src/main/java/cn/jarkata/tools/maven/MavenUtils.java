package cn.jarkata.tools.maven;

import cn.jarkata.tools.command.CmdResult;
import cn.jarkata.tools.command.CommandUtils;

import java.io.IOException;

public class MavenUtils {

    public static void download(String groupId, String artifactId, String version) throws IOException,
            InterruptedException {
        String mavenHome = System.getenv("MAVEN_HOME");
        String bin = mavenHome + "/bin/mvn";
        String cmd =
                bin + " dependency:get -DgroupId=" + groupId + " -DartifactId=" + artifactId + " -Dversion=" + version;
        String[] split = cmd.split(" ");

        CmdResult cmdResult = CommandUtils.exec(split);
        System.out.println(cmdResult);
    }
}
