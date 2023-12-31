package com.reine.postjfx.entity.property;

import com.reine.postjfx.enums.HeaderTypeEnum;
import javafx.beans.property.SimpleStringProperty;

/**
 * @author reine
 */
public class HeaderProperty {

    private HeaderTypeEnum headerTypeEnum;

    private final SimpleStringProperty value = new SimpleStringProperty();

    public HeaderProperty() {
    }

    public HeaderProperty(HeaderTypeEnum headerTypeEnum, String value) {
        this.headerTypeEnum = headerTypeEnum;
        this.value.set(value);
    }

    public HeaderTypeEnum getHeaderTypeEnum() {
        return headerTypeEnum;
    }

    public void setHeaderTypeEnum(HeaderTypeEnum headerTypeEnum) {
        this.headerTypeEnum = headerTypeEnum;
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
