package com.reine.postjfx.utils;

import com.reine.postjfx.entity.HeaderProperty;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
    public static CompletableFuture<HttpResponse<String>> get(String url, Map<String, Object> params, List<HeaderProperty> headers) {
        StringBuilder queryString = new StringBuilder();
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (!queryString.isEmpty()) queryString.append("&");
                queryString.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        String[] headerArray;
        if (headers != null) {
            headerArray = headers.stream().map(headerProperty -> {
                String first = headerProperty.getHeaderTypeEnum().getName();
                String second = headerProperty.getValue();
                return new String[]{first, second};
            }).reduce((a, b) -> Stream.concat(Arrays.stream(a), Arrays.stream(b)).toArray(String[]::new)).orElse(new String[]{});
        } else {
            headerArray = defaultHeader;
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s?%s", url, queryString)))
                .headers(headerArray)
                .GET()
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}
