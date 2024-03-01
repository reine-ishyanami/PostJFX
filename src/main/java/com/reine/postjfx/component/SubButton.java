package com.reine.postjfx.component;

import javafx.scene.control.TableCell;
import org.kordamp.ikonli.antdesignicons.AntDesignIconsOutlined;
import org.kordamp.ikonli.javafx.FontIcon;

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
        if (empty) {
            this.setGraphic(null);
        } else {
            FontIcon icon = new FontIcon(AntDesignIconsOutlined.MINUS);
            icon.setPickOnBounds(true);
            this.setGraphic(icon);
            icon.setOnMouseClicked(event -> {
                event.consume();
                getTableView().getItems().remove(getIndex());
            });
        }
    }
}
