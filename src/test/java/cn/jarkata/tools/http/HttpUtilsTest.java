package cn.jarkata.tools.http;

import org.junit.Test;

public class HttpUtilsTest {

    @Test
    public void postJson() {
        long start = System.currentTimeMillis();
        try {
            String json = HttpUtils.postJson("http://www.xxx.com", "4234", 2);
            System.out.println(json);
        } finally {
            System.out.println(System.currentTimeMillis() - start);
        }

    }
}