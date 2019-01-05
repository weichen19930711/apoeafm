package com.perficient.library.common.utils;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpUtils {

    private HttpUtils() {
        throw new RuntimeException("HttpUtils cannot be initialized");
    }

    public static String doGet(String url) {
        if (StringUtils.isEmpty(url)) {
            throw new IllegalArgumentException("url cannot be null or empty");
        }

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;

        String result = null;

        try {
            httpClient = HttpClients.createDefault();
            HttpGet httpget = new HttpGet(url);
            response = httpClient.execute(httpget);
            entity = response.getEntity();
            result = EntityUtils.toString(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            EntityUtils.consumeQuietly(entity);
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(httpClient);
        }
        return result;
    }

}
