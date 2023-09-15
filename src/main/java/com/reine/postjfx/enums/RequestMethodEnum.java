package com.reine.postjfx.enums;

/**
 * 请求方法枚举
 * @author reine
 */
public enum RequestMethodEnum {
    GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE");
    private String name;

    RequestMethodEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
