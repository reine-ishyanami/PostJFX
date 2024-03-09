module com.reine.postjfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires org.xerial.sqlitejdbc;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.antdesignicons;
    requires atlantafx.base;

    opens com.reine.postjfx to javafx.fxml;
    opens com.reine.postjfx.enums to javafx.fxml;
    opens com.reine.postjfx.controller to javafx.fxml;
    opens com.reine.postjfx.entity.property to javafx.base;
    opens com.reine.postjfx.entity.record to javafx.base;
    exports com.reine.postjfx;
    exports com.reine.postjfx.controller;
    exports com.reine.postjfx.enums;
    exports com.reine.postjfx.component;
    exports com.reine.postjfx.entity.property;
    exports com.reine.postjfx.entity.record;

}