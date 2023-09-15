package com.reine.postjfx.entity;

import com.reine.postjfx.enums.ParamTypeEnum;
import javafx.beans.property.SimpleStringProperty;

/**
 * @author reine
 */
public class ParamProperty {

    private final SimpleStringProperty key = new SimpleStringProperty();

    private final SimpleStringProperty value = new SimpleStringProperty();

    private ParamTypeEnum paramTypeEnum = ParamTypeEnum.TEXT;

    public ParamProperty() {
    }

    public ParamProperty(String key, String value, ParamTypeEnum paramTypeEnum) {
        this.key.set(key);
        this.value.set(value);
        this.paramTypeEnum = paramTypeEnum;
    }

    public String getKey() {
        return key.get();
    }

    public SimpleStringProperty keyProperty() {
        return key;
    }

    public void setKey(String key) {
        this.key.set(key);
    }

    public String getValue() {
        return value.get();
    }

    public SimpleStringProperty valueProperty() {
        return value;
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public ParamTypeEnum getParamTypeEnum() {
        return paramTypeEnum;
    }

    public void setParamTypeEnum(ParamTypeEnum paramTypeEnum) {
        this.paramTypeEnum = paramTypeEnum;
    }
}
