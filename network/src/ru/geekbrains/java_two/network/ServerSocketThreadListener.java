package ru.geekbrains.java_two.network;

import java.net.ServerSocket;
import java.net.Socket;

public interface ServerSocketThreadListener {

    void onServerStart(ServerSocketThread thread); // сервер запустился (в параметры передаем в каком именно треде)

    void onServerStop(ServerSocketThread thread);  // сервер стопнулся

    void onServerSocketCreated(ServerSocketThread thread, ServerSocket server); // СерверСокет создался (в каком треде и какой
    // именно создался внутри треда) Начинаем слушать порт

    void onServerTimeout(ServerSocketThread thread, ServerSocket server); // Когда произошёл таймаут (в каком треде и в каком
    // сервере)

    void onServerException(ServerSocketThread thread, Throwable exception);

    void onSocketAccepted(ServerSocketThread thread, ServerSocket server, Socket socket); // Когда сервер принял сокет клиента
}
