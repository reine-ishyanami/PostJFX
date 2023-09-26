package com.reine.postjfx.controller;

import com.reine.postjfx.App;
import com.reine.postjfx.component.EditableTab;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;

/**
 * 多选项卡
 *
 * @author reine
 */
public class PostTabController extends TabPane {

    @FXML
    private Tab addPostPageTab;

    @FXML
    private Button addTabButton;

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
            tab.setTitle("新标签页");
            tab.setOnCloseRequest(this::onCloseRequestCheck);
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
        this.setStyle("-fx-open-tab-animation: GROW; -fx-close-tab-animation: GROW;");
        // 默认触发一次按钮，新建一个标签页
        addTabButton.fire();
    }

    /**
     * 检查该选项卡是否是最后一个选项卡，如果是，则不能关闭
     */
    @FXML
    void onCloseRequestCheck(Event event) {
        if (this.getTabs().size() <= 2) event.consume();
    }
}