package com.reine.postjfx.enums;

import com.reine.postjfx.entity.property.HeaderProperty;
import com.reine.postjfx.entity.property.ParamProperty;
import com.reine.postjfx.utils.HttpUtils;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 请求方法枚举
 *
 * @author reine
 */
public enum RequestMethodEnum {
    GET("GET") {
        @Override
        public CompletableFuture<HttpResponse<String>> http(String url, List<ParamProperty> params, List<HeaderProperty> headers, String body) {
            return HttpUtils.get(url, params, headers);
        }
    }, POST("POST") {
        @Override
        public CompletableFuture<HttpResponse<String>> http(String url, List<ParamProperty> params, List<HeaderProperty> headers, String body) {
            return HttpUtils.post(url, params, headers, body);
        }
    }, PUT("PUT") {
        @Override
        public CompletableFuture<HttpResponse<String>> http(String url, List<ParamProperty> params, List<HeaderProperty> headers, String body) {
            return HttpUtils.put(url, params, headers, body);
        }
    }, DELETE("DELETE") {
        @Override
        public CompletableFuture<HttpResponse<String>> http(String url, List<ParamProperty> params, List<HeaderProperty> headers, String body) {
            return HttpUtils.delete(url, params, headers);
        }
    };
    private final String name;

    RequestMethodEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract CompletableFuture<HttpResponse<String>> http(String url, List<ParamProperty> params, List<HeaderProperty> headers, String body);

}
