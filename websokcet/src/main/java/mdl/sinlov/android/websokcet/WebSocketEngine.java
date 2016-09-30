package mdl.sinlov.android.websokcet;


import android.text.TextUtils;

import org.apache.http.message.BasicNameValuePair;

import java.net.URI;
import java.util.Collections;
import java.util.List;

/**
 * engine of WebSocket
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
public class WebSocketEngine {

    public static boolean DEBUG = false;

    private static WebSocketEngine instance;
    private final String ERROR_NOT_INIT_CLIENT = "You are not init WebSocket Client";

    private WebSocketClient client;

    private WebSocketEngine() {
    }

    public static synchronized WebSocketEngine getInstance() {
        if (instance == null) {
            instance = new WebSocketEngine();
        }
        return instance;
    }

    public void onWebSocketListener(WebSocketListener listener) {
        if (null != client) {
            this.client.setmListener(listener);
        } else {
            new RuntimeException(ERROR_NOT_INIT_CLIENT).printStackTrace();
        }
    }

    public WebSocketClient initClient(String sessionId, String serverUrl) {
        List<BasicNameValuePair> extraHeaders = Collections.singletonList(
                new BasicNameValuePair("Cookie", "session=" + sessionId));
        WebSocketClient client = new WebSocketClient(URI.create(serverUrl), extraHeaders);
        this.client = client;
        return client;
    }

    public WebSocketClient initClient(String sessionId, String serverUrl, WebSocketListener listener) {

        List<BasicNameValuePair> extraHeaders = Collections.singletonList(
                new BasicNameValuePair("Cookie", "session=" + sessionId));

        WebSocketClient client = new WebSocketClient(URI.create(serverUrl), listener, extraHeaders);
        this.client = client;
        return client;
    }

    public WebSocketClient changeClient(String sessionId, String serverUrl, WebSocketListener listener) {
        List<BasicNameValuePair> extraHeaders = Collections.singletonList(
                new BasicNameValuePair("Cookie", "session=" + sessionId));

        this.client = null;
        WebSocketClient client = new WebSocketClient(URI.create(serverUrl), listener, extraHeaders);
        this.client = client;
        return client;
    }

    public void connect() {
        if (null != client) {
            client.connect();
        } else {
            new RuntimeException(ERROR_NOT_INIT_CLIENT).printStackTrace();
        }
    }

    public void disconnect() {
        if (null != client) {
            client.disconnect();
        } else {
            new RuntimeException(ERROR_NOT_INIT_CLIENT).printStackTrace();
        }
    }

    public void send(String content) {
        if (null != client) {
            client.send(content);
        } else {
            new RuntimeException(ERROR_NOT_INIT_CLIENT).printStackTrace();
        }
    }

    public void send(byte[] data) {
        if (null != client) {
            client.send(data);
        } else {
            new RuntimeException(ERROR_NOT_INIT_CLIENT).printStackTrace();
        }
    }

    /**
     * just in beta
     *
     * @param message ping message
     */
    public void ping(String message) {
        if (null != client) {
            client.ping(message);
        } else {
            new RuntimeException(ERROR_NOT_INIT_CLIENT).printStackTrace();
        }
    }

    public void setVersion(String version) {
        if (TextUtils.isEmpty(version)) {
            new RuntimeException("your version is bad at " + version).printStackTrace();
        } else {
            if (null != client) {
                client.setWebSocketVersion(version);
            } else {
                new RuntimeException(ERROR_NOT_INIT_CLIENT).printStackTrace();
            }
        }
    }
}
