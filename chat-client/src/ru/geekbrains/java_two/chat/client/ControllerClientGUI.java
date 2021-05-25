package ru.geekbrains.java_two.chat.client;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ru.geekbrains.java_two.chat.common.Library;
import ru.geekbrains.java_two.network.SocketThread;
import ru.geekbrains.java_two.network.SocketThreadListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;

public class ControllerClientGUI implements EventHandler<ActionEvent>, SocketThreadListener {

    private final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss: ");
    private SocketThread socketThread;
    private String nickname;

    @FXML
    private GridPane topPanel;
    @FXML
    private HBox bottomPanel;
    @FXML
    private TextField tfIPAddress;
    @FXML
    private TextField tfPort;
    @FXML
    private TextField tfLogin;
    @FXML
    private PasswordField tfPassword;
    @FXML
    private CheckBox cbAlwaysOnTop;
    @FXML
    private CheckBox cbLoginAsGuest;
    @FXML
    private TextField tfMessage;
    @FXML
    private TextArea taMessageLog;
    @FXML
    private Button btnLogin;
    @FXML
    private Button btnDisconnect;
    @FXML
    private Button btnSend;
    @FXML
    private ListView<String> userListView;
    @FXML
    private Button btnChangeNickname;
    @FXML
    private TextField tfChangeNickname;

    @FXML
    private void sendMessageByEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            sendMessage();
        }
    }

    @Override
    public void handle(ActionEvent event) {
        Object src = event.getSource();
        if (src.equals(btnSend)) {
            sendMessage();
        } else if (src.equals(btnLogin)) {
            connect();
        } else if (src.equals(btnDisconnect)) {
            disconnect();
        } else if (src.equals(btnChangeNickname)) {
            changeNickname();
        } else {
            showErrorAlert("Unexpected source: " + src);
            throw new RuntimeException("Unexpected source: " + src);
        }
    }

    @FXML
    private void changeNickname() {
        if (tfChangeNickname.getText().equals("")) return;
        socketThread.sendMessage(Library.getClientChangeNick(tfChangeNickname.getText(), tfLogin.getText()));
        tfChangeNickname.clear();
    }

    @FXML
    private void setAlwaysOnTop(ActionEvent event) {
        if (cbAlwaysOnTop.isSelected()) {
            ((Stage) taMessageLog.getScene().getWindow()).setAlwaysOnTop(true);
        } else if (!cbAlwaysOnTop.isSelected()) {
            ((Stage) taMessageLog.getScene().getWindow()).setAlwaysOnTop(false);
        } else {
            throw new RuntimeException("Unknown event: " + event);
        }
    }

    public void writeInLog() throws IOException {
        File file1 = new File("/Users/mac/IdeaProjects/HelloJavaFX/src/chat_v2/resources/LogMessage.txt");
        FileWriter fileWriter = new FileWriter(file1, false);
        fileWriter.write(taMessageLog.getText());
        fileWriter.flush();
    }

    private void sendMessage() {
        String msg = tfMessage.getText();
        if ("".equals(msg)) return;
        tfMessage.clear();

        // запилить приват мод и глобал мод

        String[] msgForSplit = msg.split(" ");
        String prefix = msgForSplit[0];

        switch (prefix) {
            case Library.TYPE_PRIVATE_CLIENT:
                String sender = nickname;
                String recipient = msgForSplit[1];
                String message =
                        msg.substring(Library.TYPE_PRIVATE_CLIENT.length() +
                                Library.DELIMITER.length() + recipient.length() + Library.DELIMITER.length());
                socketThread.sendMessage(Library.getTypePrivateClient(sender, recipient, message));
                break;
            default:
                socketThread.sendMessage(Library.getTypeClientBcast(msg));
        }
    }

    private void putLog(String msg) {
        if ("".equals(msg)) return;
        // msg = DATE_FORMAT.format(System.currentTimeMillis()) + Thread.currentThread().getName() + ": " + msg;
        taMessageLog.appendText(msg + "\n");
    }

    @FXML
    private void connect() {
        try {
            Socket socket = new Socket(tfIPAddress.getText(), Integer.parseInt(tfPort.getText()));
            socketThread = new SocketThread("Client", this, socket);
        } catch (IOException e) {
            showErrorAlert(e.getMessage());
        }
    }

    @FXML
    public void disconnect() {
        socketThread.close();
    }

    @Override
    public void onSocketStart(SocketThread thread, Socket socket) {
        putLog("Connection started");
    }

    @Override
    public void onSocketStop(SocketThread thread) {
        btnLogin.setDisable(false);
        btnDisconnect.setDisable(true);
        btnSend.setDisable(true);
        btnChangeNickname.setDisable(true);
        tfMessage.setDisable(true);
        tfChangeNickname.setDisable(true);
        cbLoginAsGuest.setDisable(false);
        putLog("Connection interrupted");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                userListView.getItems().clear();
            }
        });

    }

    @Override
    public void onSocketReady(SocketThread thread, Socket socket) {
        if (tfLogin.getText().isEmpty()) {
            showErrorAlert("Login field can't be empty");
            try {
                thread.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        String login = tfLogin.getText();
        String password = tfPassword.getText();
        btnLogin.setDisable(true);
        btnDisconnect.setDisable(false);
        btnSend.setDisable(false);
        btnChangeNickname.setDisable(false);
        tfMessage.setDisable(false);
        tfChangeNickname.setDisable(false);
        cbLoginAsGuest.setDisable(false);
        if (!cbLoginAsGuest.isSelected()) {
            thread.sendMessage(Library.getAuthRequest(login, password)); // запрос авторизации на сервер
        } else {
            thread.sendMessage(Library.getLoginAsGuest());
            tfMessage.setEditable(false);
        }
    }

    @Override
    public void onReceiveString(SocketThread thread, Socket socket, String msg) {
        handleMessage(msg);
    }

    @Override
    public void onSocketException(SocketThread thread, Exception exception) {
        showErrorAlert(exception.getMessage());
    }

    private void handleMessage(String msg) {
        String[] msgForSplit = msg.split(Library.DELIMITER);
        String prefix = msgForSplit[0];
        switch (prefix) {
            case Library.AUTH_ACCEPT:
                nickname = msgForSplit[1];
                putLog("Welcome! Your nick is " + msgForSplit[1]);
                break;
            case Library.AUTH_DENIED:
                showErrorAlert("Authorization failed");
                break;
            case Library.MSG_FORMAT_ERROR:
                putLog(msg);
                socketThread.close();
                break;
            case Library.TYPE_BROADCAST:
                //return TYPE_BROADCAST + DELIMITER + System.currentTimeMillis() + DELIMITER + src + DELIMITER + msg;
                putLog(DATE_FORMAT.format(Long.parseLong(msgForSplit[1])) + msgForSplit[2] + ": " + msgForSplit[3]);
                break;
            case Library.TYPE_BCAST_CLIENT:
                putLog(msg);
                break;
            case Library.TYPE_PRIVATE_CLIENT:
                putLog(DATE_FORMAT.format(Long.parseLong(msgForSplit[1])) + msgForSplit[2] + " to " + msgForSplit[3] +
                        ": " + msgForSplit[4]);
                break;
            case Library.USER_LIST:
                msg = msg.substring(Library.USER_LIST.length() + Library.DELIMITER.length());
                String[] usersArray = msg.split(Library.DELIMITER);
                Arrays.sort(usersArray);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        userListView.getItems().setAll(usersArray);
                    }
                });
                selectionListener(userListView);
                break;
            case Library.RECIPIENT_NOT_FOUND_ERROR:
                msg = msg.substring(Library.RECIPIENT_NOT_FOUND_ERROR.length() + Library.DELIMITER.length());
                putLog("Recipient " + msg + " was not found");
                break;
            case Library.DISCONNECT_ON_TIMEOUT:
                putLog(DATE_FORMAT.format(Long.parseLong(msgForSplit[1])) + " You have been disconnected after " +
                        "120 s timeout authorization");
                break;
            case Library.LOGIN_AS_GUEST:
                putLog(DATE_FORMAT.format(System.currentTimeMillis()) + " Welcome! You connected as a guest. " +
                        "Please reconnect with login and password or you will be disconnected after timeout 120 s ");
                break;
            default:
                throw new RuntimeException("Unknown message type: " + msg);
        }
    }

    private void selectionListener(ListView<String> userListView) {
        MultipleSelectionModel<String> selectionModel = userListView.getSelectionModel();
        selectionModel.selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                String prefix = "/client_private ";
                if (!t1.equals(nickname) && !tfMessage.getText().startsWith(prefix)) {
                    tfMessage.insertText(0, prefix + t1 + " ");
                }
            }
        });
    }

    /*private void updateUserList(String msg) {
        msg = msg.substring(Library.USER_LIST.length() + Library.DELIMITER.length());
        String[] usersArray = msg.split(Library.DELIMITER);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Arrays.sort(usersArray);
                userListView.getItems().setAll(usersArray);
            }
        });
    }*/

    private void showErrorAlert(String msg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(msg);
                alert.setHeaderText(null);
                alert.showAndWait();
            }
        });
    }


}
