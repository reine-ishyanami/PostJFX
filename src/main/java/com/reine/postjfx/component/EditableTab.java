package com.reine.postjfx.component;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;

import java.util.Objects;

/**
 * 可编辑选项卡标题的选项卡
 * @author reine
 */
public class EditableTab extends Tab {

    private final Node updateTabName;

    private final TextField titleTextField = new TextField();

    private final StackPane graphic;

    public EditableTab() {
        ImageView editImage = new ImageView(new Image(
                Objects.requireNonNull(getClass().getResource("/image/edit.png")).toString()
        ));
        editImage.setFitWidth(15);
        editImage.setFitHeight(15);
        editImage.setPickOnBounds(true);
        this.updateTabName = editImage;
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
        updateTabName.setOnMouseClicked(event -> {
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

