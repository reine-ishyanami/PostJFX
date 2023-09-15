package com.reine.postjfx.enums;


import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 请求头枚举
 * @author reine
 */
public enum HeaderTypeEnum {
    ACCEPT("Accept"),
    ACCEPT_CHARSET("Accept-Charset"),
    ACCEPT_ENCODING("Accept-Encoding"),
    ACCEPT_LANGUAGE("Accept-Language"),
    ACCESS_CONTROL_REQUEST_HEADERS("Access-Control-Request-Headers"),
    ACCESS_CONTROL_REQUEST_METHOD("Access-Control-Request-Method"),
    AUTHORIZATION("Authorization"),
    CACHE_CONTROL("Cache-Control"),
    CONTENT_MD5("Content-MD5"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TRANSFER_ENCODING("Content-Transfer-Encoding"),
    CONTENT_TYPE("Content-Type"),
    COOKIE("Cookie"),
    COOKIE_2("Cookie 2"),
    DATE("Date"),
    EXPECT("Expect"),
    FROM("From"),
    HOST("Host"),
    IF_MATCH("If-Match"),
    IF_MODIFIED_SINCE("If-Modified-Since"),
    IF_NONE_MATCH("If-None-Match"),
    IF_RANGE("If-Range"),
    IF_UNMODIFIED_SINCE("If-Unmodified-Since"),
    KEEP_ALIVE("Keep-Alive"),
    MAX_FORWARDS("Max-Forwards"),
    ORIGIN("Origin"),
    PRAGMA("Pragma"),
    PROXY_AUTHORIZATION("Proxy-Authorization"),
    RANGE("Range"),
    REFERER("Referer"),
    TE("TE"),
    Trailer("Trailer"),
    TRANSFER_ENCODING("Transfer-Encoding"),
    UPGRADE("Upgrade"),
    USER_AGENT("User-Agent"),
    VIA("Via"),
    WARNING("Warning"),
    X_REQUESTED_WITH("X-Requested-With"),
    X_DO_NOT_TRACK("X-Do-Not-Track"),
    DNT("DNT"),
    X_API_KEY("x-api-key");

    private final String name;

    HeaderTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private static final Map<String, HeaderTypeEnum> HEADER_TYPE_ENUM_MAP =
            Arrays.stream(HeaderTypeEnum.values()).collect(Collectors.toMap(HeaderTypeEnum::getName, Function.identity()));

}
