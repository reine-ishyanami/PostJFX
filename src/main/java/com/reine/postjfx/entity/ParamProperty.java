package com.reine.postjfx.entity;

import javafx.beans.property.SimpleStringProperty;

/**
 * @author reine
 */
public class ParamProperty {

    private final SimpleStringProperty key = new SimpleStringProperty();

    private final SimpleStringProperty value = new SimpleStringProperty();

    public ParamProperty() {}

    public ParamProperty(String key, String value) {
        this.key.set(key);
        this.value.set(value);
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
}
