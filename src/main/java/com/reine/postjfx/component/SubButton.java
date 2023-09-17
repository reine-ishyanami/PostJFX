package com.reine.postjfx.component;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * 表格中删除本行按钮
 *
 * @author reine
 */
public class SubButton<T> extends TableCell<T, Void> {

    public SubButton() {
        super();
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            HBox root = new HBox();
            root.setAlignment(Pos.CENTER);
            ImageView imageView = new ImageView("/image/sub.png");
            imageView.setFitWidth(10);
            imageView.setFitHeight(10);
            root.getChildren().add(imageView);
            this.setGraphic(root);
            root.setOnMouseClicked(event -> {
                if (getTableView().getItems().size() > 1) getTableView().getItems().remove(getIndex());
            });
        } else {
            this.setGraphic(null);
        }
    }
}
