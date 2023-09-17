package com.reine.postjfx.component;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * 表格中添加一行按钮
 *
 * @author reine
 */
public class AddButton<T> extends TableCell<T, Void> {

    public AddButton() {
        super();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            this.setGraphic(null);
        } else {
            HBox root = new HBox();
            root.setAlignment(Pos.CENTER);
            ImageView imageView = new ImageView("/image/add.png");
            imageView.setFitWidth(10);
            imageView.setFitHeight(10);
            root.getChildren().add(imageView);
            this.setGraphic(root);
            root.setOnMouseClicked(event -> {
                try {
                    Class<?> rowType = getTableRow().getItem().getClass();
                    getTableView().getItems().add((T) rowType.cast(rowType.getDeclaredConstructor().newInstance()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
