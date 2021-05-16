package ru.geekbrains.java_two.chat.server.core;

import ru.geekbrains.java_two.chat.common.Library;
import ru.geekbrains.java_two.network.SocketThread;
import ru.geekbrains.java_two.network.SocketThreadListener;

import java.net.Socket;

public class ClientThread extends SocketThread {

    private String nickname;
    private boolean isAuthorized;
    private boolean isReconnecting;
    private boolean isLoginAsGuest;
    private final long timeAfterThreadStarted;

    public ClientThread(String name, SocketThreadListener listener, Socket socket) {
        super(name, listener, socket);
        timeAfterThreadStarted = System.currentTimeMillis();
    }

    public String getNickname() {
        return nickname;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public boolean isReconnecting() {
        return isReconnecting;
    }

    void reconnect() {
        isReconnecting = true;
        close();
    }

    void authAccept(String nickname) {
        isLoginAsGuest = false;
        isAuthorized = true;
        this.nickname = nickname;
        sendMessage(Library.getAuthAccept(nickname)); //отправляем сообщение на клиентскую сторону об авторизации
    }

    void authFail() {
        sendMessage(Library.getAuthDenied());
        close();
    }

    void msgFormatError(String msg) {
        sendMessage(Library.getMsgFormatError(msg));
        close();
    }

    public long getTimeAfterThreadStarted() {
        return timeAfterThreadStarted;
    }

    void loginAsGuest() {
        isLoginAsGuest = true;
        isAuthorized = false;
        this.nickname = "guest";
        sendMessage(Library.getLoginAsGuest());
    }

    public boolean isLoginAsGuest() {
        return isLoginAsGuest;
    }
}
