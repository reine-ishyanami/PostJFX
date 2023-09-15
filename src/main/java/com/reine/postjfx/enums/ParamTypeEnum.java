package com.reine.postjfx.enums;

/**
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
}
