package com.reine.postjfx.component;

import com.reine.postjfx.entity.ParamProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.util.Optional;

/**
 * @author reine
 */
public class FileOrTextTableCell extends TableCell<ParamProperty, String> {

    private final TextField valueTextField = new TextField();

    public FileOrTextTableCell() {
        valueTextField.setOnAction(event -> commitEdit(valueTextField.getText()));
    }

    @Override
    public void startEdit() {
        super.startEdit();
        setText(null);
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
        if (empty) setText(null);
        else setText(item);
    }

    @Override
    public void commitEdit(String newValue) {
        super.commitEdit(newValue);
        setGraphic(null);
        setText(newValue);
        getTableView().getItems().get(getIndex()).setValue(newValue);
    }
}
