package cn.jarkata.tools.command;

public class CmdResult {
    private final int code;
    private final String message;

    public CmdResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "CmdResult{" + "code=" + code + ", message='" + message + '\'' + '}';
    }
}
