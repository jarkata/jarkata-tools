package cn.jarkata.tools.maven;

import cn.jarkata.tools.command.CmdResult;
import cn.jarkata.tools.command.CommandUtils;

import java.io.IOException;
import java.util.Objects;

public class MavenUtils {

    /**
     * 下载jar文件
     *
     * @param groupId
     * @param artifactId
     * @param version
     * @return 下载成功返回true，下载失败，返回false
     * @throws IOException
     * @throws InterruptedException
     */

    public static boolean download(String groupId, String artifactId, String version) throws Exception {
        String mavenHome = System.getenv("MAVEN_HOME");
        Objects.requireNonNull(mavenHome, "MAVEN_HOME IS NULL");
        String bin = mavenHome + "/bin/mvn";
        String cmd =
                bin + " dependency:get -DgroupId=" + groupId + " -DartifactId=" + artifactId + " -Dversion=" + version;
        String[] split = cmd.split(" ");
        CmdResult cmdResult = CommandUtils.exec(split);
        String message = cmdResult.getMessage();
        int buildSuccess = message.indexOf("BUILD SUCCESS");
        return buildSuccess > 0;

    }
}
