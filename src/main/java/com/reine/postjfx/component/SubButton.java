package com.reine.postjfx.component;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * 表格中删除本行按钮
 *
 * @author reine
 */
public class SubButton<T, S> extends TableCell<T, S> {

    private final TableView<T> tableView;

    public SubButton(TableView<T> tableView) {
        super();
        this.tableView = tableView;
    }

    @Override
    protected void updateItem(S item, boolean empty) {
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
                if (tableView.getItems().size() > 1)
                    tableView.getItems().remove(this.getIndex());
            });
        } else {
            this.setGraphic(null);
        }
    }
}
