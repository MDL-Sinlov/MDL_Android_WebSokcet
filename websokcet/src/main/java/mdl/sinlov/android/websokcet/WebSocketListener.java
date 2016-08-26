package mdl.sinlov.android.websokcet;

/**
 * define WebSocketClient default behavior
 * <pre>
 *     sinlov
 *
 *     /\__/\
 *    /`    '\
 *  ≈≈≈ 0  0 ≈≈≈ Hello world!
 *    \  --  /
 *   /        \
 *  /          \
 * |            |
 *  \  ||  ||  /
 *   \_oo__oo_/≡≡≡≡≡≡≡≡o
 *
 * </pre>
 * Created by "sinlov" on 16/8/25.
 */
public interface WebSocketListener {
    /**
     * WebSocket EOF
     */
    int DISCONNECT_EOF = 1 << 1;
    /**
     * WebSocket SSL error!
     */
    int DISCONNECT_SSL_ERROR = 1 << 2;

    void onConnect();

    void onMessage(String message);

    void onMessage(byte[] data);

    void onDisconnect(int code, String reason, Exception error);

    void onError(Exception error);
}
