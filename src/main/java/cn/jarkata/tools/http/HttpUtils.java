package cn.jarkata.tools.http;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class HttpUtils {

    private static final HttpClientConnectionManager clientConnectionManager = new PoolingHttpClientConnectionManager();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(clientConnectionManager::shutdown));
    }

    public static String postJson(String url, String message, int timeoutSecond) {

        HttpEntity responseEntity = null;

        CloseableHttpResponse httpResponse = null;
        try (CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(clientConnectionManager)
                                                         .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
                                                         .build()) {
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeoutSecond * 1000)
                                                       .setConnectTimeout(timeoutSecond * 1000).build();
            StringEntity entity = new StringEntity(message, ContentType.APPLICATION_JSON.withCharset(StandardCharsets.UTF_8));
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);
            httpPost.setEntity(entity);
            httpResponse = httpClient.execute(httpPost);
            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode >= 200 && statusCode < 300) {
                responseEntity = httpResponse.getEntity();
                return EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
            } else {
                throw new HttpResponseException(statusCode, statusLine.getReasonPhrase() + ";URL=" + url);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            closeQuietly(httpResponse, responseEntity);
        }
    }

    public static void closeQuietly(CloseableHttpResponse httpResponse, HttpEntity responseEntity) {
        if (Objects.isNull(httpResponse) || Objects.isNull(responseEntity)) {
            return;
        }
        try {
            EntityUtils.consume(responseEntity);
            httpResponse.close();
        } catch (Exception ignored) {
        }
    }
}
