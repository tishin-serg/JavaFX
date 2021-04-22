package chat_v2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Controller {

    @FXML
    TextField tfIPAddress;

    @FXML
    TextField tfPort;

    @FXML
    TextField tfLogin;

    @FXML
    PasswordField tfPassword;

    @FXML
    CheckBox cbAlwaysOnTop;

    @FXML
    TextField tfMessage;

    @FXML
    TextArea taMessageLog;

    @FXML
    Button btnLogin;

    @FXML
    Button btnDisconnect;

    @FXML
    Button btnSend;

    @FXML
    ListView<String> userList;

    @FXML
    ScrollPane spLogMessage;

    @FXML
    ScrollPane spUserList;

    @FXML
    private AnchorPane ap;

    @FXML
    private void sendMessageByClick(ActionEvent event) {
        taMessageLog.appendText(tfMessage.getText() + "\n");
        tfMessage.clear();
    }

    @FXML
    private void sendMessageByKey(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            taMessageLog.appendText(tfMessage.getText() + "\n");
            tfMessage.clear();
        }
    }

    @FXML
    private void setAlwaysOnTop(ActionEvent event) {
        if (cbAlwaysOnTop.isSelected()) {
            ((Stage) taMessageLog.getScene().getWindow()).setAlwaysOnTop(true);
        } else {
            ((Stage) taMessageLog.getScene().getWindow()).setAlwaysOnTop(false);
        }
    }

}
