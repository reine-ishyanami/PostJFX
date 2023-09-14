module com.reine.postjfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;

    opens com.reine.postjfx to javafx.fxml;
    opens com.reine.postjfx.enums to javafx.fxml;
    opens com.reine.postjfx.controller to javafx.fxml;
    opens com.reine.postjfx.entity to javafx.base;
    exports com.reine.postjfx;
    exports com.reine.postjfx.controller;
}