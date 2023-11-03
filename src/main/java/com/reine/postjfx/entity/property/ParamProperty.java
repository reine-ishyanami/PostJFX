package com.reine.postjfx.entity.property;

import com.reine.postjfx.enums.ParamTypeEnum;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * @author reine
 */
public class ParamProperty {

    private final SimpleStringProperty key = new SimpleStringProperty();

    private final SimpleStringProperty value = new SimpleStringProperty();

    private final SimpleBooleanProperty fileParam = new SimpleBooleanProperty(false);

    private ParamTypeEnum paramTypeEnum = ParamTypeEnum.TEXT;

    public ParamProperty() {
    }

    public ParamProperty(String key, String value, ParamTypeEnum paramTypeEnum) {
        this.key.set(key);
        this.value.set(value);
        this.paramTypeEnum = paramTypeEnum;
        setFileParam(paramTypeEnum.equals(ParamTypeEnum.FILE));
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
        setFileParam(paramTypeEnum.equals(ParamTypeEnum.FILE));
    }

    public boolean isFileParam() {
        return fileParam.get();
    }

    public SimpleBooleanProperty fileParamProperty() {
        return fileParam;
    }

    public void setFileParam(boolean fileParam) {
        this.fileParam.set(fileParam);
    }
}
