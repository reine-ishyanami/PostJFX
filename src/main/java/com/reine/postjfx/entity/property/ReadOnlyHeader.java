package com.reine.postjfx.entity.property;

/**
 * @author reine
 * 响应完成后提取对应的请求头与响应头信息
 */
public class ReadOnlyHeader {
    private String key;
    private String value;

    public ReadOnlyHeader() {
    }

    public ReadOnlyHeader(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
