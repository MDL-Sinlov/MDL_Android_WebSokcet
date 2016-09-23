package mdl.sinlov.android.websokcet;

import java.io.IOException;

/**
 * Protocol Error of WebSocket
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
 * Created by sinlov on 16/9/23.
 */
public class ProtocolError extends IOException {

    /**
     * Default heartbeat time is 10s
     */
    public static final long DEFAULT_HEARTBEAT_TIME = 10000;

    /**
     * max ping message size
     */
    public static final int MAX_PING_MESSAGE_SIZE = 125;

    public ProtocolError(String detailMessage) {
        super(detailMessage);
    }
}
