package com.reine.postjfx.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 请求参数类型枚举
 * @author reine
 */
public enum ParamTypeEnum {
    TEXT("文本"), FILE("文件");
    private final String name;

    ParamTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private static final Map<String, ParamTypeEnum> PARAM_TYPE_ENUM_MAP =
            Arrays.stream(ParamTypeEnum.values()).collect(Collectors.toMap(ParamTypeEnum::getName, Function.identity()));

    public static ParamTypeEnum of(String name){
        return PARAM_TYPE_ENUM_MAP.get(name);
    }

}
