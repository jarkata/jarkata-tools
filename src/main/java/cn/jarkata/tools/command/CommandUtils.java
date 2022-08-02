package cn.jarkata.tools.command;

import cn.jarkata.tools.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CommandUtils {

    public static CmdResult exec(String... cmd) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(cmd);
        Process process = builder.start();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        FileUtils.copy(process.getInputStream(), outputStream);
        FileUtils.copy(process.getErrorStream(), outputStream);
        int value = process.waitFor();
        return new CmdResult(value, outputStream.toString(StandardCharsets.UTF_8.name()));
    }

}
