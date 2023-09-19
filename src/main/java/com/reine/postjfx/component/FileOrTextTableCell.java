package com.reine.postjfx.component;

import com.reine.postjfx.entity.ParamProperty;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;

import java.util.Optional;

/**
 * 文本与文件输入切换
 *
 * @author reine
 */
public class FileOrTextTableCell extends TableCell<ParamProperty, String> {

    private final TextField valueTextField = new TextField();

    public FileOrTextTableCell() {
        valueTextField.setOnAction(event -> commitEdit(valueTextField.getText()));
        valueTextField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ESCAPE)) cancelEdit();
        });
    }

    @Override
    public void startEdit() {
        super.startEdit();
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        ParamProperty row = getTableRow().getItem();
        switch (row.getParamTypeEnum()) {
            case TEXT -> {
                valueTextField.setText(row.getValue());
                setGraphic(valueTextField);
                valueTextField.requestFocus();
            }
            case FILE -> {
                FileChooser fileChooser = new FileChooser();
                Optional.ofNullable(fileChooser.showOpenDialog(null))
                        .ifPresent(file -> commitEdit(file.getAbsolutePath()));
            }
        }
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) setText(null);
        else setText(item);
    }

    @Override
    public void commitEdit(String newValue) {
        super.commitEdit(newValue);
        setContentDisplay(ContentDisplay.TEXT_ONLY);
        setGraphic(null);
        setText(newValue);
        getTableView().getItems().get(getIndex()).setValue(newValue);
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setContentDisplay(ContentDisplay.TEXT_ONLY);
        setGraphic(null);
    }
}
