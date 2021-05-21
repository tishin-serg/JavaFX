package ru.geekbrains.java_two.chat.common;

public class Library {

    /*
    /auth_request
    /auth_accept±nickname
    /auth_denied
    /broadcast±time±src±msg
    /msg_format_error±msg
    /user_list±user1±user2
    /client_bcast±msg
    /client_private±recipient±msg
    /client_change_nick±nick

    */

    public static final String DELIMITER = "±";
    public static final String AUTH_REQUEST = "/auth_request";
    public static final String AUTH_ACCEPT = "/auth_accept";
    public static final String AUTH_DENIED = "/auth_denied";
    public static final String MSG_FORMAT_ERROR = "/msg_format_error";
    public static final String TYPE_BROADCAST = "/broadcast";
    public static final String USER_LIST = "/user_list";
    public static final String TYPE_BCAST_CLIENT = "/client_bcast";
    public static final String TYPE_PRIVATE_CLIENT = "/client_private";
    public static final String RECIPIENT_NOT_FOUND_ERROR = "/recipient_not_found_error";
    public static final String LOGIN_AS_GUEST = "/login_as_guest";
    public static final String DISCONNECT_ON_TIMEOUT = "/disconnect_on_timeout";
    public static final String CLIENT_CHANGE_NICK = "/client_change_nick";

    public static String getAuthRequest(String login, String password) {
        return AUTH_REQUEST + DELIMITER + login + DELIMITER + password;
    }

    public static String getAuthAccept(String nickname) {
        return AUTH_ACCEPT + DELIMITER + nickname;
    }

    public static String getAuthDenied() {
        return AUTH_DENIED;
    }

    public static String getMsgFormatError(String msg) {
        return MSG_FORMAT_ERROR + DELIMITER + msg;
    }

    public static String getTypeBroadcast(String src, String msg) {
        return TYPE_BROADCAST + DELIMITER + System.currentTimeMillis() + DELIMITER + src + DELIMITER + msg;
    }

    public static String getUserList(String users) {
        return USER_LIST + DELIMITER + users;
    }

    public static String getTypeClientBcast(String msg) {
        return TYPE_BCAST_CLIENT + DELIMITER + msg;
    }

    public static String getTypePrivateClient(String sender, String recipient, String msg) {
        return TYPE_PRIVATE_CLIENT + DELIMITER + System.currentTimeMillis() + DELIMITER + sender + DELIMITER + recipient + DELIMITER + msg;
    }

    public static String getRecipientNotFoundError(String recipient) {
        return RECIPIENT_NOT_FOUND_ERROR + DELIMITER + recipient;
    }

    public static String getLoginAsGuest() {
        return LOGIN_AS_GUEST + DELIMITER;
    }

    public static String getDisconnectOnTimeout() {
        return DISCONNECT_ON_TIMEOUT + DELIMITER + System.currentTimeMillis();
    }

    public static String getClientChangeNick(String nick, String login) {
        return CLIENT_CHANGE_NICK + DELIMITER + nick + DELIMITER + login;
    }
}
