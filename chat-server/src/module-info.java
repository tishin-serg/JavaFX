module chat.server {

    requires javafx.fxml;
    requires javafx.controls;
    requires network;
    requires sqlite.jdbc;
    requires java.sql;
    requires common;

    opens ru.geekbrains.java_two.chat.server.core;
    opens ru.geekbrains.java_two.chat.server.gui;
    opens ru.geekbrains.java_two.chat.server.resources;
    exports ru.geekbrains.java_two.chat.server.core;
}