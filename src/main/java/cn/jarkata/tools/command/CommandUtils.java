package cn.jarkata.tools.command;

import cn.jarkata.commons.utils.FileUtils;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class CommandUtils {

    public static CmdResult exec(String... cmd) throws Exception {
        ByteArrayOutputStream outputStream = null;
        ProcessBuilder builder;
        try {
            builder = new ProcessBuilder(cmd);
            Process process = builder.start();
            outputStream = new ByteArrayOutputStream();
            FileUtils.copy(process.getInputStream(), outputStream);
            FileUtils.copy(process.getErrorStream(), outputStream);
            int value = process.waitFor();
            return new CmdResult(value, outputStream.toString(StandardCharsets.UTF_8.name()));
        } finally {
            if (Objects.nonNull(outputStream)) {
                outputStream.close();
            }

        }
    }

}