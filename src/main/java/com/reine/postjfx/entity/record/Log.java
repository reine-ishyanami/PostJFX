package com.reine.postjfx.entity.record;

import com.reine.postjfx.entity.property.HeaderProperty;
import com.reine.postjfx.entity.property.ParamProperty;
import javafx.collections.ObservableList;

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
public record Log(String dateTime,
                  String method,
                  String url,
                  ObservableList<ParamProperty> params,
                  ObservableList<HeaderProperty> headers,
                  String body
) {
}
