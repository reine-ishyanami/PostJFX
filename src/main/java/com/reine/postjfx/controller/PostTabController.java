package com.reine.postjfx.controller;

import com.reine.postjfx.App;
import com.reine.postjfx.component.EditableTab;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;

/**
 * @author reine
 */
public class PostTabController extends TabPane {

    @FXML
    private Tab addPostPageTab;

    @FXML
    private EditableTab initTab;

    public PostTabController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("post-tab.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    /**
     * 添加一个tab
     */
    @FXML
    void addPostPage() {
        try {
            EditableTab tab = new EditableTab();
            tab.setTitle("New Tab");
            tab.setContent(new PostPageController());
            ObservableList<Tab> tabs = this.getTabs();
            Tab addTabButton = tabs.remove(tabs.size() - 1);
            tabs.addAll(tab, addTabButton);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void initialize() {
        initTab.setTitle("New Tab");
        this.setStyle("-fx-open-tab-animation: GROW; -fx-close-tab-animation: GROW;");
    }
}
