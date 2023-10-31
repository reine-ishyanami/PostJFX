package com.reine.postjfx.component;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;

/**
 * 可编辑选项卡标题的选项卡
 * @author reine
 */
public class EditableTab extends Tab {

    private final Button updateTabName = new Button("改");

    private final TextField titleTextField = new TextField();

    private final StackPane graphic;

    public EditableTab() {
        titleTextField.setPrefWidth(100);
        graphic = new StackPane();
        graphic.setAlignment(Pos.CENTER_LEFT);
        graphic.getChildren().add(updateTabName);
        this.setGraphic(graphic);
        listen();
    }

    private void listen() {

        // 只有当标签页选择时，才可以更改标签页名称
        updateTabName.disableProperty().bind(this.selectedProperty().not());

        // 按钮点击事件
        updateTabName.setOnAction(event -> {
            // 点击按钮，展示输入框
            String text = this.getText();
            this.setText("");
            titleTextField.setText(text);
            // 添加输入框
            graphic.getChildren().add(titleTextField);
            // 强行获取焦点
            titleTextField.requestFocus();
        });

        // 失焦事件
        titleTextField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                this.setText(titleTextField.getText());
                graphic.getChildren().remove(titleTextField);
            }
        });

        // 回车事件
        titleTextField.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                this.setText(titleTextField.getText());
                graphic.getChildren().remove(titleTextField);
            }
        });
    }

}

