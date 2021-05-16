package ru.geekbrains.java_two.chat.server.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import ru.geekbrains.java_two.chat.server.core.ChatServer;
import ru.geekbrains.java_two.chat.server.core.ChatServerListener;

public class ControllerServerGUI implements EventHandler<ActionEvent>, ChatServerListener {

    private final ChatServer chatServer = new ChatServer(this);

    @FXML
    Button btnStart;

    @FXML
    Button btnStop;

    @FXML
    TextArea log;

    @Override
    public void handle(ActionEvent event) {
        Object src = event.getSource();
        if (src.equals(btnStart)) {
            chatServer.start(8189);
        } else if (src.equals(btnStop)) {
            chatServer.stop();
        } else {
            throw new RuntimeException("Unexpected source: " + src);
        }
    }

    @Override
    public void onChatServerMessage(String msg) {
        log.appendText(msg);
    }
}
