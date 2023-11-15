package com.reine.postjfx.component;

import com.reine.postjfx.entity.property.HeaderProperty;
import com.reine.postjfx.enums.HeaderTypeEnum;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.util.StringConverter;

/**
 * 请求标头表中的下拉选择框
 *
 * @author reine
 */
public class ComboBoxHeadersTableCell extends TableCell<HeaderProperty, HeaderTypeEnum> {

    private final ComboBox<HeaderTypeEnum> comboBox = new ComboBox<>();

    public ComboBoxHeadersTableCell() {
        comboBox.setPrefWidth(340);
        comboBox.getItems().addAll(HeaderTypeEnum.values());
        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(HeaderTypeEnum object) {
                return object != null ? object.getName() : "";
            }

            @Override
            public HeaderTypeEnum fromString(String string) {
                return HeaderTypeEnum.of(string);
            }
        });

        comboBox.setOnAction(event -> {
            HeaderTypeEnum value = comboBox.getValue();
            getTableRow().getItem().setHeaderTypeEnum(value);
            commitEdit(value);
        });
    }

    @Override
    protected void updateItem(HeaderTypeEnum item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(null);
        if (empty || item == null) setText(null);
        else setText(item.getName());
    }

    @Override
    public void startEdit() {
        super.startEdit();
        HeaderTypeEnum paramTypeEnum = getTableRow().getItem().getHeaderTypeEnum();
        setGraphic(comboBox);
        setText(null);
        comboBox.setValue(paramTypeEnum);
    }

    @Override
    public void commitEdit(HeaderTypeEnum newValue) {
        super.commitEdit(newValue);
        setGraphic(null);
        setText(newValue.getName());
    }
}
