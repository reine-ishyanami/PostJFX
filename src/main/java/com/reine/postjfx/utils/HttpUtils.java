package com.reine.postjfx.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
     * @param header
     * @return
     */
    public static CompletableFuture<HttpResponse<String>> get(String url, Map<String, Object> params, Map<String, String> header) {
        StringBuilder queryString = new StringBuilder();
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (!queryString.isEmpty()) queryString.append("&");
                queryString.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        String[] headerArray;
        if (header != null) {
            List<String> list = new ArrayList<>();
            for (Map.Entry<String, String> entry : header.entrySet()) {
                list.add(entry.getKey());
                list.add(entry.getValue());
            }
            headerArray = list.toArray(new String[0]);
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
