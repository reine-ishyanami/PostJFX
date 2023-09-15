package com.reine.postjfx.utils;

import com.reine.postjfx.entity.HeaderProperty;
import com.reine.postjfx.entity.ParamProperty;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * @author reine
 */
public class HttpUtils {

    private static final HttpClient client = HttpClient.newHttpClient();

    private static final String[] defaultHeader = new String[]{"Content-Type", "application/json"};


    /**
     * get请求
     *
     * @param url
     * @param params
     * @param headers
     * @return
     */
    public static CompletableFuture<HttpResponse<String>> get(String url, List<ParamProperty> params, List<HeaderProperty> headers) {
        String queryString = handleQueryParam(params);
        String[] headerArray = handleRequestHeader(headers);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s?%s", url, queryString)))
                .headers(headerArray)
                .GET()
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * 解析请求参数
     *
     * @param params
     * @return
     */
    private static String handleQueryParam(List<ParamProperty> params) {
        if (params != null)
            return params.stream().map(paramProperty -> {
                String key = paramProperty.getKey();
                String value = paramProperty.getValue();
                return String.format("%s=%s", key, value);
            }).reduce((a, b) -> String.format("%s&%s", a, b)).orElse("");
        else return "";
    }

    /**
     * 解析请求头
     *
     * @param headers
     * @return
     */
    private static String[] handleRequestHeader(List<HeaderProperty> headers) {
        if (headers != null)
            return
                    headers.stream()
                            .map(headerProperty -> {
                                String first = headerProperty.getHeaderTypeEnum().getName();
                                String second = headerProperty.getValue();
                                return new String[]{first, second};
                            })
                            .reduce((a, b) -> Stream.concat(Arrays.stream(a), Arrays.stream(b)).toArray(String[]::new))
                            .orElse(new String[]{});
        else return defaultHeader;
    }


}
