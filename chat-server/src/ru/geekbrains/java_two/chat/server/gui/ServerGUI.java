package ru.geekbrains.java_two.chat.server.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ServerGUI extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/ru/geekbrains/java_two/chat/server/resources/ServerGUI.fxml"));
        stage.setTitle("ServerGUI");
        stage.setScene(new Scene(root, 600, 300));
        stage.sizeToScene();
        stage.setResizable(false);
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
    }


}
