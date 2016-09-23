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
     * You coding error, please check code!
     */
    int ERROR = -1;
    /**
     * WebSocket EOF
     */
    int DISCONNECT_EOF = 1 << 1;
    /**
     * WebSocket SSL error!
     */
    int DISCONNECT_SSL_ERROR = 1 << 2;
    /**
     * Got close op!
     */
    int GOT_CLOSE_OPT = 1 << 3;

    /**
     * Ping message is too lang
     */
    int PING_MESSAGE_IS_TOO_LANG = 1 << 4;

    /**
     * PONG message i empty
     */
    int PONG_MESSAGE_IS_EMPTY = 1 << 5;

    /**
     * PONG message out of time
     */
    int PONG_MESSAGE_OUT_OF_TIME = PONG_MESSAGE_IS_EMPTY + 1;

    /**
     * onConnect
     */
    void onConnect();

    /**
     * on TEXT message
     *
     * @param message {@link String}
     */
    void onMessage(String message);

    /**
     * on Binary message
     *
     * @param data {@link byte}
     */
    void onMessage(byte[] data);

    /**
     * and heartbeat callback
     *
     * @param ping  ping message {@link String}
     * @param pong  pong message {@link String}
     * @param error pong exception {@link ProtocolError}
     * @return isHeartbeat alive
     */
    boolean onHeartbeat(String ping, String pong, ProtocolError error);

    /**
     * on Disconnect info call back
     *
     * @param code   int
     * @param reason {@link String}
     * @param error  {@link ProtocolError}
     */
    void onDisconnect(int code, String reason, ProtocolError error);

    /**
     * on Error info call back
     *
     * @param error {@link Exception}
     */
    void onError(Exception error);
}
