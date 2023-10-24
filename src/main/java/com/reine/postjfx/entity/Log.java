package com.reine.postjfx.entity;

import javafx.collections.ObservableList;

import java.time.LocalDateTime;

/**
 * @author reine
 * 日志记录类
 * @param dateTime 请求时间
 * @param method 请求方法
 * @param url 请求url
 * @param params 请求参数
 * @param headers 请求头
 * @param body 请求体
 */
public record Log(LocalDateTime dateTime,
                  String method,
                  String url,
                  ObservableList<ParamProperty> params,
                  ObservableList<HeaderProperty> headers,
                  String body
) {
}