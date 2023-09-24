package com.reine.postjfx.utils;

import com.reine.postjfx.entity.HeaderProperty;
import com.reine.postjfx.entity.ParamProperty;
import com.reine.postjfx.enums.HeaderTypeEnum;
import com.reine.postjfx.enums.ParamTypeEnum;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * 用于发送Http请求的工具类
 *
 * @author reine
 */
public class HttpUtils {

    private static final HttpClient client = HttpClient.newHttpClient();

    private static final String defaultUserAgent = "PostJFX/1.1.0";

    private static final List<HeaderProperty> defaultHeader = List.of(
            new HeaderProperty(HeaderTypeEnum.USER_AGENT, defaultUserAgent),
            new HeaderProperty(HeaderTypeEnum.ACCEPT, "*/*")
    );
    private static final List<HeaderProperty> defaultHeaderWithBody = List.of(
            new HeaderProperty(HeaderTypeEnum.USER_AGENT, defaultUserAgent),
            new HeaderProperty(HeaderTypeEnum.ACCEPT, "*/*"),
            new HeaderProperty(HeaderTypeEnum.CONTENT_TYPE, "application/json")
    );
    private static final List<HeaderProperty> defaultHeaderWithFileParam = List.of(
            new HeaderProperty(HeaderTypeEnum.USER_AGENT, defaultUserAgent),
            new HeaderProperty(HeaderTypeEnum.ACCEPT, "*/*"),
            new HeaderProperty(HeaderTypeEnum.CONTENT_TYPE, "multipart/form-data; boundary=boundary")
    );

    /**
     * get请求
     *
     * @param url
     * @param params
     * @param headers
     * @return
     */
    public static CompletableFuture<HttpResponse<String>> get(String url, List<ParamProperty> params, List<HeaderProperty> headers) {
        String queryString = Optional.ofNullable(handleGetOrDeleteParam(params)).orElse("");
        String[] headerArray = handleGetOrDeleteHeader(headers);
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
        String[] headerArray = handlePostOrPutHeader(params, headers, body);
        // 如果含有文件
        if (params.stream().anyMatch(paramProperty -> paramProperty.getParamTypeEnum().equals(ParamTypeEnum.FILE))) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .headers(headerArray)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(handlePostOrPutParam(params)))
                    .build();
            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            // 不包含文件
        } else {
            String queryString = Optional.ofNullable(handleGetOrDeleteParam(params)).orElse("");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("%s?%s", url, queryString)))
                    .headers(headerArray)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        }
    }


    /**
     * put请求
     *
     * @param url
     * @param params
     * @param headers
     * @param body
     * @return
     */
    public static CompletableFuture<HttpResponse<String>> put(String url, List<ParamProperty> params, List<HeaderProperty> headers, String body) {
        String[] headerArray = handlePostOrPutHeader(params, headers, body);
        // 如果含有文件
        if (params.stream().anyMatch(paramProperty -> paramProperty.getParamTypeEnum().equals(ParamTypeEnum.FILE))) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .headers(headerArray)
                    .PUT(HttpRequest.BodyPublishers.ofByteArray(handlePostOrPutParam(params)))
                    .build();
            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            // 不包含文件
        } else {
            String queryString = Optional.ofNullable(handleGetOrDeleteParam(params)).orElse("");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("%s?%s", url, queryString)))
                    .headers(headerArray)
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        }
    }

    /**
     * delete请求
     *
     * @param url
     * @param params
     * @param headers
     * @return
     */
    public static CompletableFuture<HttpResponse<String>> delete(String url, List<ParamProperty> params, List<HeaderProperty> headers) {
        String queryString = Optional.ofNullable(handleGetOrDeleteParam(params)).orElse("");
        String[] headerArray = handleGetOrDeleteHeader(headers);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s?%s", url, queryString)))
                .headers(headerArray)
                .DELETE()
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * 解析Get请求或Delete请求的请求参数
     *
     * @param params
     * @return
     */
    private static String handleGetOrDeleteParam(List<ParamProperty> params) {
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
    private static String[] handleGetOrDeleteHeader(List<HeaderProperty> headers) {
        if (!headers.isEmpty()) return convertHeaderListToArray(headers);
        else return convertHeaderListToArray(defaultHeader);
    }

    /**
     * 解析Post请求的请求头
     *
     * @param params
     * @param headers
     * @param body
     * @return
     */
    private static String[] handlePostOrPutHeader(List<ParamProperty> params, List<HeaderProperty> headers, String body) {
        if (!body.isEmpty()) return convertHeaderListToArray(defaultHeaderWithBody);
        if (!headers.isEmpty()) return handleGetOrDeleteHeader(headers);
        if (params.stream().anyMatch(paramProperty -> paramProperty.getParamTypeEnum().equals(ParamTypeEnum.FILE)))
            return convertHeaderListToArray(defaultHeaderWithFileParam);
        return convertHeaderListToArray(defaultHeaderWithBody);
    }

    /**
     * 将list类型的headers转换为string[]
     * @param headerPropertyList
     * @return
     */
    private static String[] convertHeaderListToArray(List<HeaderProperty> headerPropertyList) {
        if (headerPropertyList.stream().noneMatch(headerProperty -> headerProperty.getHeaderTypeEnum().equals(HeaderTypeEnum.USER_AGENT)))
            headerPropertyList.add(new HeaderProperty(HeaderTypeEnum.USER_AGENT, defaultUserAgent));
        return headerPropertyList.stream()
                .flatMap(headerProperty -> Stream.of(headerProperty.getHeaderTypeEnum().getName(), headerProperty.getValue()))
                .toArray(String[]::new);
    }


    /**
     * 创建文件上传请求体
     *
     * @param params 请求参数
     * @return
     */
    private static byte[] handlePostOrPutParam(List<ParamProperty> params) {
        // 定义boundary
        String boundary = "boundary";

        ByteArrayOutputStream requestBodyStream = new ByteArrayOutputStream();

        // 添加文本参数
        params.stream().filter(paramProperty -> !paramProperty.isFileParam()).forEach(paramProperty -> {
            String fieldName = paramProperty.getKey();
            String fieldValue = paramProperty.getValue();
            try {
                String textPart = "--" + boundary + "\r\n"
                        + "Content-Disposition: form-data; name=\"" + fieldName + "\"\r\n\r\n"
                        + fieldValue + "\r\n";
                requestBodyStream.write(textPart.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // 添加文件参数
        params.stream().filter(ParamProperty::isFileParam).forEach(paramProperty -> {
            String fieldName = paramProperty.getKey();
            Path filePath = Paths.get(paramProperty.getValue());
            try {
                String fileName = filePath.getFileName().toString();
                String filePart = "--" + boundary + "\r\n"
                        + "Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"\r\n"
                        + "Content-Type: application/octet-stream\r\n\r\n";
                requestBodyStream.write(filePart.getBytes(StandardCharsets.UTF_8));
                Files.copy(filePath, requestBodyStream);
                requestBodyStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        try {
            requestBodyStream.write(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return requestBodyStream.toByteArray();
    }


}
