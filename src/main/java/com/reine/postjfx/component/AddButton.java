package com.reine.postjfx.component;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * 表格中添加一行按钮
 * @author reine
 */
public class AddButton<T, S> extends TableCell<T, S> {

    private final TableView<T> tableView;

    private final Class<T> itemType;

    public AddButton(TableView<T> tableView, Class<T> itemType) {
        super();
        this.tableView = tableView;
        this.itemType = itemType;
    }


    @Override
    protected void updateItem(S item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            HBox root = new HBox();
            root.setAlignment(Pos.CENTER);
            ImageView imageView = new ImageView("/image/add.png");
            imageView.setFitWidth(10);
            imageView.setFitHeight(10);
            root.getChildren().add(imageView);
            this.setGraphic(root);
            root.setOnMouseClicked(event -> {
                try {
                    tableView.getItems().add(itemType.getDeclaredConstructor().newInstance());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            this.setGraphic(null);
        }
    }
}
