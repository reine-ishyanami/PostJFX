package com.reine.postjfx.component;

import com.reine.postjfx.entity.ParamProperty;
import com.reine.postjfx.enums.ParamTypeEnum;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.util.StringConverter;

/**
 * @author reine
 */
public class CustomComboBoxTableCell extends TableCell<ParamProperty, ParamTypeEnum> {

    private final ComboBox<ParamTypeEnum> comboBox = new ComboBox<>();

    public CustomComboBoxTableCell() {
        comboBox.getItems().addAll(ParamTypeEnum.values());
        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(ParamTypeEnum object) {
                return object != null ? object.getName() : "";
            }

            @Override
            public ParamTypeEnum fromString(String string) {
                return ParamTypeEnum.valueOf(string);
            }
        });

        comboBox.setOnAction(event -> {
            ParamTypeEnum value = comboBox.getValue();
            getTableRow().getItem().setParamTypeEnum(value);
            commitEdit(value);
        });
    }

    @Override
    protected void updateItem(ParamTypeEnum item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(null);
        if (empty) setText(null);
        else setText(item.getName());
    }

    @Override
    public void startEdit() {
        super.startEdit();
        ParamTypeEnum paramTypeEnum = getTableRow().getItem().getParamTypeEnum();
        setGraphic(comboBox);
        setText(null);
        comboBox.setValue(paramTypeEnum);
    }

    @Override
    public void commitEdit(ParamTypeEnum newValue) {
        super.commitEdit(newValue);
        setGraphic(null);
        setText(newValue.getName());
    }
}
