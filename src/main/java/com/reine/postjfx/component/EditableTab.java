package com.reine.postjfx.component;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;

/**
 * @author reine
 */
public class EditableTab extends Tab {

    private final Label titleLabel;

    private final TextField titleTextField;

    private final StackPane graphic;

    public EditableTab() {
        graphic = new StackPane();
        graphic.setAlignment(Pos.CENTER);
        graphic.setPrefWidth(100);
        titleLabel = new Label();
        titleTextField = new TextField();
        graphic.getChildren().addAll(titleLabel, titleTextField);
        titleTextField.setVisible(false);
        this.setGraphic(graphic);
        listen();
    }

    public void setTitle(String title){
        titleLabel.setText(title);
        titleTextField.setText(title);
    }

    private void listen() {
        // 标题栏双击事件
        graphic.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                titleLabel.setVisible(false);
                titleTextField.setVisible(true);
            }
        });

        titleTextField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                titleLabel.setText(titleTextField.getText());
                titleLabel.setVisible(true);
                titleTextField.setVisible(false);
            }
        });

        titleTextField.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                titleLabel.setText(titleTextField.getText());
                titleLabel.setVisible(true);
                titleTextField.setVisible(false);
            }
        });
    }

}

