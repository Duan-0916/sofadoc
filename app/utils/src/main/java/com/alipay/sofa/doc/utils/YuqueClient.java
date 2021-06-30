package com.alipay.sofa.doc.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author <a href="mailto:zhanggeng.zg@antfin.com">GengZhang</a>
 */
public class YuqueClient {
    /**
     * Logger for HttpUtils
     **/
    private static final Logger LOGGER = LoggerFactory.getLogger(YuqueClient.class);
    private final String baseUrl;
    private final String xAuthToken;

    public YuqueClient(String baseUrl, String xAuthToken) {
        if (StringUtils.isEmpty(baseUrl) || StringUtils.isEmpty(xAuthToken)) {
            throw new IllegalArgumentException("baseUrl and xAuthToken can not null");
        }
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.xAuthToken = xAuthToken;
    }

    private String buildUrl(String url) {
        return baseUrl + (url.startsWith("/") ? url : "/" + url);
    }

    public String post(String url, Map<String, String> head, String json) {
        String response;
        try {
            CloseableHttpClient httpclient = HttpClientBuilder.create().build();
            url = buildUrl(url);
            HttpPost httpost = new HttpPost(url);
            RequestConfig defaultConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
            httpost.setConfig(defaultConfig);

            if (head != null) {
                for (Map.Entry<String, String> entry : head.entrySet()) {
                    httpost.setHeader(entry.getKey(), entry.getValue()); // 服务端需要token
                }
            }
            httpost.setHeader("X-Auth-Token", xAuthToken);

            StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
            entity.setContentType("application/json");
            httpost.setEntity(entity);
            HttpResponse httpResponse = httpclient.execute(httpost);
            HttpEntity responseEntity = httpResponse.getEntity();
            response = EntityUtils.toString(responseEntity);

        } catch (Exception e) {
            LOGGER.error("call " + url + " error!", e);
            response = "";
        }
        return response;
    }

    public String put(String url, Map<String, String> head, String json) {
        String response;
        try {
            CloseableHttpClient httpclient = HttpClientBuilder.create().build();
            url = buildUrl(url);
            HttpPut httpPut = new HttpPut(url);
            RequestConfig defaultConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
            httpPut.setConfig(defaultConfig);
            if (head != null) {
                for (Map.Entry<String, String> entry : head.entrySet()) {
                    httpPut.setHeader(entry.getKey(), entry.getValue()); // 服务端需要token
                }
            }
            httpPut.setHeader("X-Auth-Token", xAuthToken);

            StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
            entity.setContentType("application/json");
            httpPut.setEntity(entity);
            HttpResponse httpResponse = httpclient.execute(httpPut);
            HttpEntity responseEntity = httpResponse.getEntity();
            response = EntityUtils.toString(responseEntity);
        } catch (Exception e) {
            LOGGER.error("call " + url + " error!", e);
            response = "";
        }
        return response;
    }

    public String get(String url) {
        String response;
        try {
            CloseableHttpClient httpclient = HttpClientBuilder.create().build();
            url = buildUrl(url);
            HttpGet httpGet = new HttpGet(url);
            RequestConfig defaultConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
            httpGet.setConfig(defaultConfig);
            httpGet.setHeader("X-Auth-Token", xAuthToken);

            HttpResponse httpResponse = httpclient.execute(httpGet);
            HttpEntity responseEntity = httpResponse.getEntity();
            response = EntityUtils.toString(responseEntity);
        } catch (Exception e) {
            LOGGER.error("get " + url + " error!", e);
            response = "";
        }
        return response;
    }

    public String delete(String url) {
        String response;
        try {
            CloseableHttpClient httpclient = HttpClientBuilder.create().build();
            url = buildUrl(url);
            HttpDelete httpDelete = new HttpDelete(url);
            RequestConfig defaultConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
            httpDelete.setConfig(defaultConfig);
            httpDelete.setHeader("X-Auth-Token", xAuthToken);

            HttpResponse httpResponse = httpclient.execute(httpDelete);
            HttpEntity responseEntity = httpResponse.getEntity();
            //LOGGER.info("response status: " + httpResponse.getStatusLine() + " " + url);
            response = EntityUtils.toString(responseEntity);
        } catch (Exception e) {
            LOGGER.error("delete " + url + " error!", e);
            response = "";
        }
        return response;
    }
}
