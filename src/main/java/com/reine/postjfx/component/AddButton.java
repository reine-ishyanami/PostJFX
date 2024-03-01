package com.reine.postjfx.component;

import javafx.scene.control.TableCell;
import org.kordamp.ikonli.antdesignicons.AntDesignIconsOutlined;
import org.kordamp.ikonli.javafx.FontIcon;

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
            FontIcon icon = new FontIcon(AntDesignIconsOutlined.PLUS);
            icon.setPickOnBounds(true);
            this.setGraphic(icon);
            icon.setOnMouseClicked(event -> {
                event.consume();
                try {
                    Class<?> rowType = getTableRow().getItem().getClass();
                    getTableView().getItems().add((T) rowType.getDeclaredConstructor().newInstance());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
