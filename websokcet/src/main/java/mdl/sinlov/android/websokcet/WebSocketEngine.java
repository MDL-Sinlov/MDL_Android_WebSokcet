package mdl.sinlov.android.websokcet;


import org.apache.http.message.BasicNameValuePair;

import java.net.URI;
import java.util.Collections;
import java.util.List;

/**
 * engine of WebSokcet
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

    private static WebSocketEngine instance;

    private WebSocketClient client;

    private WebSocketEngine() {
    }

    public static synchronized WebSocketEngine getInstance() {
        if (instance == null) {
            instance = new WebSocketEngine();
        }
        return instance;
    }


    public WebSocketClient getClient(WebSocketListener listener, String sessionId, String serverUrl) {

        List<BasicNameValuePair> extraHeaders = Collections.singletonList(
                new BasicNameValuePair("Cookie", "session=" + sessionId));

        WebSocketClient client = new WebSocketClient(URI.create(serverUrl), listener, extraHeaders);
        this.client = client;
        return client;
    }

    public void connect() {
        client.connect();
    }

    public void disconnect() {
        client.disconnect();
    }

    public void send(String content) {
        client.send(content);
    }

    public void send(byte[] data) {
        client.send(data);
    }
}
