package mdl.sinlov.android.websokcet;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.BasicNameValuePair;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * Client of websocket
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
/*package*/ class WebSocketClient {
    private static final String TAG = "WebSocketClient";
    public static final int MSG_ERROR = -1;
    public static final int MSG_CONNECT = 1;
    public static final int MSG_DISCONNECT = MSG_CONNECT << 1;
    public static final int MSG_ON_MESSAGE_BYTE = MSG_CONNECT << 2;
    public static final int MSG_ON_MESSAGE_STRING = MSG_CONNECT << 3;
    private final URI mURI;
    private WebSocketListener mListener;
    private Socket mSocket;
    private Thread mThread;
    private HandlerThread mHandlerThread;
    private Handler mHandler = new SafeHandler(this);
    private List<BasicNameValuePair> mExtraHeaders;
    private HeartbeatParser mParser;

    private final Object mSendLock = new Object();

    private static TrustManager[] sTrustManagers;

    public static void setTrustManagers(TrustManager[] tm) {
        sTrustManagers = tm;
    }

    public void setmListener(WebSocketListener mListener) {
        this.mListener = mListener;
    }

    public WebSocketClient(URI uri, List<BasicNameValuePair> extraHeaders) {
        mURI = uri;
        mExtraHeaders = extraHeaders;
        mParser = new HeartbeatParser(this);
        mHandlerThread = new HandlerThread("webSocket-thread");
        mHandlerThread.start();
    }

    public WebSocketClient(URI uri, WebSocketListener listener, List<BasicNameValuePair> extraHeaders) {
        mURI = uri;
        mListener = listener;
        mExtraHeaders = extraHeaders;
        mParser = new HeartbeatParser(this);
        mHandlerThread = new HandlerThread("webSocket-thread");
        mHandlerThread.start();
    }

    private static class SafeHandler extends Handler {
        private static WeakReference<WebSocketClient> wkWebSocketClient;

        public SafeHandler(WebSocketClient webSocketClient) {
            SafeHandler.wkWebSocketClient = new WeakReference<WebSocketClient>(webSocketClient);
        }

        public WeakReference<WebSocketClient> get() {
            return wkWebSocketClient;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            WebSocketClient wr = wkWebSocketClient.get();
            if (null != wr && null != wr.mListener) {
                switch (msg.what) {
                    case MSG_ERROR:
                        if (null != msg.obj) {
                            wr.mListener.onError((Exception) msg.obj);
                        }
                        break;
                    case MSG_CONNECT:
                        wr.mListener.onConnect();
                        break;
                    case MSG_DISCONNECT:
                        if (null != msg.obj) {
                            wr.mListener.onDisconnect(msg.arg1, wr.filterErrorByCode(msg.arg2), (Exception) msg.obj);
                        }
                        break;
                    case MSG_ON_MESSAGE_BYTE:
                        if (null != msg.obj) {
                            wr.mListener.onMessage((byte[]) msg.obj);
                        }
                        break;
                    case MSG_ON_MESSAGE_STRING:
                        if (null != msg.obj) {
                            wr.mListener.onMessage((String) msg.obj);
                        }
                        break;
                    default:
                        wr.mListener.onError(null);
                        break;
                }
            } else {
                if (WebSocketEngine.DEBUG) {
                    Log.w(TAG, "WeakReference is Null");
                }
            }
        }
    }

    private String filterErrorByCode(int code) {
        switch (code) {
            case WebSocketListener.DISCONNECT_EOF:
                return "WebSocket EOF";
            case WebSocketListener.DISCONNECT_SSL_ERROR:
                return " WebSocket SSL error!";
            case WebSocketListener.GOT_CLOSE_OPT:
                return "Got close op!";
        }
        return "UnKnow Error!";
    }

    public void msgError(int code, int arg1, int arg2, Object obj) {
        mHandler.sendMessage(mHandler.obtainMessage(code,
                arg1, arg2,
                obj));
    }

    public void msgContent(int code, Object obj) {
        mHandler.sendMessage(mHandler.obtainMessage(code, obj));
    }

    public void connect() {
        if (mThread != null && mThread.isAlive()) {
            return;
        }
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String secret = createSecret();
                    int port = (mURI.getPort() != -1) ? mURI.getPort() : (mURI.getScheme().equals("wss") ? 443 : 80);

                    String path = TextUtils.isEmpty(mURI.getPath()) ? "/" : mURI.getPath();
                    if (!TextUtils.isEmpty(mURI.getQuery())) {
                        path += "?" + mURI.getQuery();
                    }
                    String originScheme = mURI.getScheme().equals("wss") ? "https" : "http";
                    URI origin = new URI(originScheme, "//" + mURI.getHost(), mURI.getPath(), null);
                    SocketFactory factory = mURI.getScheme().equals("wss") ? getSSLSocketFactory() : SocketFactory.getDefault();
                    mSocket = factory.createSocket(mURI.getHost(), port);
                    PrintWriter out = new PrintWriter(mSocket.getOutputStream());
                    out.print("GET " + path + " HTTP/1.1\r\n");
                    out.print("Upgrade: websocket\r\n");
                    out.print("Connection: Upgrade\r\n");
                    out.print("Host: " + mURI.getHost() + "\r\n");
                    out.print("Origin: " + origin.toString() + "\r\n");
                    out.print("Sec-WebSocket-Key: " + secret + "\r\n");
                    out.print("Sec-WebSocket-Version: 13\r\n");
                    if (mExtraHeaders != null) {
                        for (NameValuePair pair : mExtraHeaders) {
                            out.print(String.format("%s: %s\r\n", pair.getName(), pair.getValue()));
                        }
                    }
                    out.print("\r\n");
                    out.flush();
                    HeartbeatParser.HappyDataInputStream stream = new HeartbeatParser.HappyDataInputStream(mSocket.getInputStream());

                    // Read HTTP response status line.
                    StatusLine statusLine = parseStatusLine(readLine(stream));
                    if (statusLine == null) {
                        throw new HttpException("Received no reply from server.");
                    } else if (statusLine.getStatusCode() != HttpStatus.SC_SWITCHING_PROTOCOLS) {
                        throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
                    }

                    // Read HTTP response headers.
                    String line;
                    boolean validated = false;
                    while (!TextUtils.isEmpty(line = readLine(stream))) {
                        Header header = parseHeader(line);
                        if (header.getName().equals("Sec-WebSocket-Accept")) {
                            String expected = createSecretValidation(secret);
                            String actual = header.getValue().trim();
                            if (WebSocketEngine.DEBUG) {
                                Log.d(TAG, "actual: " + actual + " |line: " + line);
                            }
                            if (!expected.equals(actual)) {
                                throw new HttpException("Bad Sec-WebSocket-Accept header value.");
                            }
                            validated = true;
                        }
                    }

                    if (!validated) {
                        throw new HttpException("No Sec-WebSocket-Accept header.");
                    }

                    mHandler.sendMessage(mHandler.obtainMessage(MSG_CONNECT));
                    // Now decode websocket frames.
                    mParser.start(stream);

                } catch (EOFException ex) {
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_DISCONNECT,
                            WebSocketListener.DISCONNECT_EOF, WebSocketListener.DISCONNECT_EOF,
                            ex));
                } catch (SSLException ex) {
                    // Connection reset by peer
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_DISCONNECT,
                            WebSocketListener.DISCONNECT_SSL_ERROR,
                            WebSocketListener.DISCONNECT_SSL_ERROR,
                            ex));
                } catch (Exception ex) {
                    if (WebSocketEngine.DEBUG) {
                        Log.d(TAG, "Problem", ex);
                    }
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_ERROR, ex));
                }
            }
        });
        mThread.start();
    }

    public void disconnect() {
        if (mSocket != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        mSocket.close();
                        mSocket = null;
                    } catch (IOException ex) {
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_ERROR, ex));
                    }
                }
            });
        }
    }

    public void send(String data) {
        sendFrame(mParser.frame(data));
    }

    public void send(byte[] data) {
        sendFrame(mParser.frame(data));
    }

    private StatusLine parseStatusLine(String line) {
        if (TextUtils.isEmpty(line)) {
            return null;
        }
        return BasicLineParser.parseStatusLine(line, new BasicLineParser());
    }

    private Header parseHeader(String line) {
        return BasicLineParser.parseHeader(line, new BasicLineParser());
    }

    private void parseBody(String line) {
        BasicLineParser.parseRequestLine(line, new BasicLineParser());
    }


    // Can't use BufferedReader because it buffers past the HTTP data.
    private String readLine(HeartbeatParser.HappyDataInputStream reader) throws IOException {
        int readChar = reader.read();
        if (readChar == -1) {
            return null;
        }
        StringBuilder string = new StringBuilder("");
        while (readChar != '\n') {
            if (readChar != '\r') {
                string.append((char) readChar);
            }

            readChar = reader.read();
            if (readChar == -1) {
                return null;
            }
        }
        return string.toString();
    }

    private String createSecret() {
        byte[] nonce = new byte[16];
        for (int i = 0; i < 16; i++) {
            nonce[i] = (byte) (Math.random() * 256);
        }
        return Base64.encodeToString(nonce, Base64.DEFAULT).trim();
    }

    private String createSecretValidation(String secret) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update((secret + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes());
            return Base64.encodeToString(md.digest(), Base64.DEFAULT).trim();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    void sendFrame(final byte[] frame) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (mSendLock) {
                        if (mSocket == null) {
                            msgError(MSG_ERROR, WebSocketListener.ERROR, WebSocketListener.ERROR, new IllegalStateException("Socket not connected"));
//                            throw new IllegalStateException("Socket not connected");
                        } else {
                            OutputStream outputStream = mSocket.getOutputStream();
                            outputStream.write(frame);
                            outputStream.flush();
                        }
                    }
                } catch (IOException ex) {
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_ERROR, ex));
                }
            }
        });
    }


    private SSLSocketFactory getSSLSocketFactory() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, sTrustManagers, null);
        return context.getSocketFactory();
    }

}
