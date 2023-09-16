package com.reine.postjfx.utils;

import com.reine.postjfx.entity.HeaderProperty;
import com.reine.postjfx.entity.ParamProperty;
import com.reine.postjfx.enums.ParamTypeEnum;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * 用于发送Http请求的工具类
 *
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
        String queryString = Optional.ofNullable(handleGetOrDeleteQueryParam(params)).orElse("");
        String[] headerArray = Optional.ofNullable(handleGetHeader(headers)).orElse(defaultHeader);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s?%s", url, queryString)))
                .headers(headerArray)
                .GET()
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * post请求
     *
     * @param url
     * @param params
     * @param headers
     * @param body
     * @return
     */
    public static CompletableFuture<HttpResponse<String>> post(String url, List<ParamProperty> params, List<HeaderProperty> headers, String body) {
        String boundary = UUID.randomUUID().toString();
        String[] headerArray = Optional.ofNullable(handlePostHeader(params, headers, boundary)).orElse(defaultHeader);
        // 如果含有文件
        if (params.stream().anyMatch(paramProperty -> paramProperty.getParamTypeEnum().equals(ParamTypeEnum.FILE))) {
            try {
                MultipartBodyBuilder builder = new MultipartBodyBuilder(boundary);
                params.forEach(paramProperty -> {
                    switch (paramProperty.getParamTypeEnum()) {
                        case TEXT -> builder.addTextPart(paramProperty.getKey(), paramProperty.getValue());
                        case FILE ->
                                builder.addFilePart(paramProperty.getKey(), paramProperty.getValue(), Paths.get(paramProperty.getValue()));
                    }
                });
                HttpRequest request = null;
                request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers(headerArray)
                        .POST(HttpRequest.BodyPublishers.ofByteArray(builder.build()))
                        .build();
                return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // 不包含文件
        } else {
            String queryString = Optional.ofNullable(handleGetOrDeleteQueryParam(params)).orElse("");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("%s?%s", url, queryString)))
                    .headers(headerArray)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        }

    }

    /**
     * 解析Get请求或Delete请求的请求参数
     *
     * @param params
     * @return
     */
    private static String handleGetOrDeleteQueryParam(List<ParamProperty> params) {
        if (params != null)
            return params.stream().map(paramProperty -> {
                String key = paramProperty.getKey();
                String value = paramProperty.getValue();
                return String.format("%s=%s", key, value);
            }).reduce((a, b) -> String.format("%s&%s", a, b)).orElse("");
        else return null;
    }

    /**
     * 解析Get请求的请求头
     *
     * @param headers
     * @return
     */
    private static String[] handleGetHeader(List<HeaderProperty> headers) {
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
        else return null;
    }

    /**
     * 解析Post请求的请求头
     *
     * @param params
     * @param headers
     * @return
     */
    private static String[] handlePostHeader(List<ParamProperty> params, List<HeaderProperty> headers, String boundary) {
        if (headers != null) {
            return handleGetHeader(headers);
        } else {
            if (params.stream().anyMatch(paramProperty -> paramProperty.getParamTypeEnum().equals(ParamTypeEnum.FILE))) {
                return new String[]{"Content-Type", "multipart/form-data; boundary=" + boundary};
            } else return null;
        }
    }


}
