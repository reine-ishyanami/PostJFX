package com.reine.postjfx.controller;

import com.reine.postjfx.App;
import com.reine.postjfx.component.EditableTab;
import com.reine.postjfx.entity.record.Log;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
            tab.setText("新标签页");
            tab.setOnCloseRequest(this::onCloseRequestCheck);
            // 设置新标签页内容
            tab.setContent(new PostPageController());
            insertNewTab(tab);
            // 每次点击按钮，清空缓存
            if (!errorX.isEmpty()) errorX.clear();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 初始化一个具有初始数据的新标签页
     *
     * @param log 日志记录
     */
    public void addPostPageWithData(Log log) {
        try {
            EditableTab tab = new EditableTab();
            tab.setText("新标签页");
            tab.setOnCloseRequest(this::onCloseRequestCheck);
            // 设置新标签页内容
            tab.setContent(new PostPageController(log));
            insertNewTab(tab);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 插入新标签页
     *
     * @param tab 要增加的新标签页
     */
    private void insertNewTab(EditableTab tab) {
        ObservableList<Tab> tabs = this.getTabs();
        if (tabs.get(tabs.size() - 1).equals(btnTab)) tabs.add(tabs.size() - 1, tab);
        else tabs.add(tab);
        double maxX = addTabButton.localToScene(addTabButton.getBoundsInLocal()).getMaxX();
        if (maxX > 750.0) {
            errorX.add(maxX);
            tabs.remove(btnTab);
        }
    }

    /**
     * 缓存溢出标签页的位置信息
     */
    private final Set<Double> errorX = new HashSet<>();


    @FXML
    void initialize() {// 默认触发一次按钮，新建一个标签页
        addTabButton.fire();
    }

    @FXML
    private Tab btnTab;

    /**
     * 检查该选项卡是否是最后一个选项卡，如果是，则不能关闭
     */
    @FXML
    void onCloseRequestCheck(Event event) {
        if (this.getTabs().size() <= 2) event.consume();
        // 删除时默认添加增加标签页
        if (!getTabs().get(getTabs().size() - 1).equals(btnTab)) getTabs().add(btnTab);
        double maxX = addTabButton.localToScene(addTabButton.getBoundsInLocal()).getMaxX();
        // 如果添加完增加标签页后，其偏移量大于指定数值，则代表此时标签页仍然是溢出状态，移除掉增加标签页的按钮
        if (maxX > 900.0 || errorX.contains(maxX)) getTabs().remove(btnTab);
    }
}
