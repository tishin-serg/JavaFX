module chat.client {

    requires javafx.fxml;
    requires javafx.controls;
    requires network;
    requires common;
    requires chat.server;

    opens ru.geekbrains.java_two.chat.client;
}