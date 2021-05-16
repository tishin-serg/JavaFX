package ru.geekbrains.java_two.chat.server.core;

import ru.geekbrains.java_two.chat.common.Library;
import ru.geekbrains.java_two.network.ServerSocketThread;
import ru.geekbrains.java_two.network.ServerSocketThreadListener;
import ru.geekbrains.java_two.network.SocketThread;
import ru.geekbrains.java_two.network.SocketThreadListener;

import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class ChatServer implements ServerSocketThreadListener, SocketThreadListener {

    private final ChatServerListener chatServerListener;
    private final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss: ");
    private final Vector<SocketThread> clients;

    private ServerSocketThread thread;

    public ChatServer(ChatServerListener chatServerListener) {
        this.chatServerListener = chatServerListener;
        this.clients = new Vector<>();
    }

    public void start(int port) {
        if (thread != null && thread.isAlive()) {
            putLog("Server already started");
        } else {
            thread = new ServerSocketThread(this, "Thread of server", port, 2000);
        }
    }

    public void stop() {
        if (thread == null || !thread.isAlive()) {
            putLog("Server is not running");
        } else {
            thread.interrupt();
//            for (SocketThread socketThread : socketThreads) {
//                socketThread.close();
//            }
        }
    }

    private void putLog(String msg) {
        msg = DATE_FORMAT.format(System.currentTimeMillis()) + Thread.currentThread().getName() + ": " + msg;
        System.out.println(msg);
        chatServerListener.onChatServerMessage(msg + "\n");
    }

    @Override
    public void onServerStart(ServerSocketThread thread) {
        putLog("Server started");
        SqlClient.connect();
    }

    @Override
    public void onServerStop(ServerSocketThread thread) {
        putLog("Server stopped");
        SqlClient.disconnect();
        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).close();
        }
    }

    // работа с сообщениями

    private void handleAuthMessage(ClientThread client, String msg) {
        String[] msgForSplit = msg.split(Library.DELIMITER);
        String prefix = msgForSplit[0];
        switch (prefix) {
            case Library.TYPE_BCAST_CLIENT:
                sendToAllAuthorizedClients(Library.getTypeBroadcast(client.getNickname(), msgForSplit[1]));
                break;
            case Library.TYPE_PRIVATE_CLIENT:
                String recipient = msgForSplit[3];
                String message = msgForSplit[4];
                String sender = client.getNickname();
                sendToPerson(Library.getTypePrivateClient(sender, recipient, message), client , recipient); // отправка получателю
            break;
            default:
                client.msgFormatError(msg);
        }
    }

    private void handleNonAuthMessage(ClientThread client, String msg) {
        String[] arr = msg.split(Library.DELIMITER);
        if (arr.length != 3 || !arr[0].equals(Library.AUTH_REQUEST)) {
            client.msgFormatError(msg);
            return;
        }
        String login = arr[1];
        String password = arr[2];
        String nickname = SqlClient.getNickname(login, password);
        if (nickname == null) {
            putLog("Invalid login attempt: " + login);
            client.authFail();
            return;
        } else {
            ClientThread oldClient = findClientByNickname(nickname);
            client.authAccept(nickname);
            if (oldClient == null) {
                sendToAllAuthorizedClients(Library.getTypeBroadcast("Server", nickname + " connected"));
            } else {
                oldClient.reconnect();
                clients.remove(oldClient);
            }
        }
        sendToAllAuthorizedClients(Library.getUserList(getUsers()));
    }

    private void sendToAllAuthorizedClients(String msg) {
        for (int i = 0; i < clients.size(); i++) {
            ClientThread recipient = (ClientThread) clients.get(i); // приводим каждый тред из вектора к клиент треду
            if (!recipient.isAuthorized()) continue;                       // если не авторизован переходим на след итерацию
            recipient.sendMessage(msg); // если авторизован то отправляем всем авторизованным
        }
    }

    private void sendToPerson(String msg, ClientThread sender, String recipientNickname) {
        ClientThread recipient = findClientByNickname(recipientNickname);
        if (recipient == null) {
            sender.sendMessage(Library.getRecipientNotFoundError(recipientNickname));
        } else {
            recipient.sendMessage(msg);
            sender.sendMessage(msg);
        }

    }

    private String getUsers() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < clients.size(); i++) {
            ClientThread client = (ClientThread) clients.get(i);
            if (!client.isAuthorized()) continue;
            sb.append(client.getNickname()).append(Library.DELIMITER);
        }
        return sb.toString();
    }

    private synchronized ClientThread findClientByNickname(String nickname) {
        for (int i = 0; i < clients.size(); i++) {
            ClientThread client = (ClientThread) clients.get(i);
            if (!client.isAuthorized()) continue;
            if (client.getNickname().equals(nickname))
                return client;
        }
        return null;
    }

    // события сервера

    @Override
    public void onServerSocketCreated(ServerSocketThread thread, ServerSocket server) {
        putLog("Server socket created");
    }

    @Override
    public void onServerTimeout(ServerSocketThread thread, ServerSocket server) {

    }

    @Override
    public void onServerException(ServerSocketThread thread, Throwable exception) {
        exception.printStackTrace();
    }

    @Override
    public void onSocketAccepted(ServerSocketThread thread, ServerSocket server, Socket socket) {
        putLog("Client connected");
        String name = "SocketThread " + socket.getInetAddress() + ":" + socket.getPort();
        new ClientThread(name, this, socket);
    }

    // события сокетов

    @Override
    public void onSocketStart(SocketThread thread, Socket socket) {
        putLog("Socket started");
    }

    @Override
    public void onSocketStop(SocketThread thread) {
        putLog("Socket stopped");
        clients.remove(thread);
        ClientThread client = (ClientThread) thread;
        if (client.isAuthorized() && !client.isReconnecting()) {
            sendToAllAuthorizedClients(Library.getTypeBroadcast("Server", client.getNickname() + " disconnected"));
        }
        sendToAllAuthorizedClients(Library.getUserList(getUsers()));
    }

    @Override
    public void onSocketReady(SocketThread thread, Socket socket) {
        clients.add(thread);
        putLog("Socket ready");
    }

    @Override
    public void onReceiveString(SocketThread thread, Socket socket, String msg) {
        ClientThread client = (ClientThread) thread; // апкастим тред
        if (client.isAuthorized()) {
            handleAuthMessage(client, msg);
        } else handleNonAuthMessage(client, msg);
    }

    @Override
    public void onSocketException(SocketThread thread, Exception exception) {
        exception.printStackTrace();
    }

}
