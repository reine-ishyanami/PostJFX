package com.reine.postjfx.enums;

/**
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
