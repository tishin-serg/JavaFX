package ru.geekbrains.java_two.network;

import java.net.Socket;

public interface SocketThreadListener {

    void onSocketStart(SocketThread thread, Socket socket);
    void onSocketStop(SocketThread thread);
    void onSocketReady(SocketThread thread, Socket socket); // чтобы не попасть в null pointer в промежуток между созданием
    // сокета и созданием стримов ввода вывода
    void onReceiveString(SocketThread thread, Socket socket, String msg);
    void onSocketException(SocketThread thread, Exception exception);
}
