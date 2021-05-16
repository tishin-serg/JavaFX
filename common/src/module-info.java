module common {

    requires javafx.fxml;
    requires javafx.controls;

    opens ru.geekbrains.java_two.chat.common;
    exports ru.geekbrains.java_two.chat.common;
}