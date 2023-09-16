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
        ParamProperty row = getTableRow().getItem();
        switch (row.getParamTypeEnum()) {
            case TEXT -> {
                valueTextField.setText(row.getValue());
                setGraphic(valueTextField);
            }
            case FILE -> {
                FileChooser fileChooser = new FileChooser();
                Optional.ofNullable(fileChooser.showOpenDialog(null))
                        .ifPresent(file -> commitEdit(file.getAbsolutePath()));
            }
        }
    }

    @Override
    public void commitEdit(String newValue) {
        super.commitEdit(newValue);
        setGraphic(null);
        setText(newValue);
        getTableView().getItems().get(getIndex()).setValue(newValue);
    }


}
