package mdl.sinlov.android.websocket.app;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mdl.sinlov.android.log.ALog;
import mdl.sinlov.android.log.ALogPrinter;
import mdl.sinlov.android.websocket.app.ui.MDLTestActivity;
import mdl.sinlov.android.websocket.app.ui.WebSocketSendBinaryActivity;
import mdl.sinlov.android.websocket.app.ui.WebSocketSendStringActivity;
import mdl.sinlov.android.websocket.app.utils.ResourceUtil;
import mdl.sinlov.android.websokcet.MessageUtils;
import mdl.sinlov.android.websokcet.ProtocolError;
import mdl.sinlov.android.websokcet.WebSocketEngine;
import mdl.sinlov.android.websokcet.WebSocketListener;


public class MainActivity extends MDLTestActivity {

    @BindView(R.id.tv_main_result)
    TextView tvMainResult;
    @BindView(R.id.btn_main_connect)
    Button btnMainConnect;
    @BindView(R.id.btn_main_disconnect)
    Button btnMainDisconnect;
    @BindView(R.id.btn_main_send_message)
    Button btnMainSendMessage;
    @BindView(R.id.btn_main_send_byte)
    Button btnMainSendByte;
    @BindView(R.id.tv_server_info)
    TextView tvServerInfo;
    @BindView(R.id.et_main_server_change)
    EditText etMainServerChange;
    @BindView(R.id.btn_main_change_server)
    Button btnMainChangeServer;
    @BindView(R.id.btn_main_send_ping)
    Button btnMainSendPing;
    private String wsHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindView(Bundle savedInstanceState) {
        ALog.initTag();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
        tvServerInfo.setText(wsHost);
    }

    private void initData() {
        int ws_server_host = ResourceUtil.getStringId(getApplicationContext(), "ws_server_host");
        wsHost = getString(ws_server_host);
        WebSocketEngine.getInstance().initClient("123", wsHost);
    }

    @Override
    protected void onResume() {
        super.onResume();
        WebSocketEngine.getInstance().onWebSocketListener(new MyWebSocketListener());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // this not necessary
        WebSocketEngine.getInstance().disconnect();
    }

    @Override
    protected void bindListener() {

    }

    @OnClick({R.id.btn_main_connect, R.id.btn_main_disconnect, R.id.btn_main_send_message,
            R.id.btn_main_send_byte, R.id.btn_main_change_server, R.id.btn_main_send_ping})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_main_change_server:
                checkChangeServer();
                break;
            case R.id.btn_main_connect:
                WebSocketEngine.getInstance().connect();
                break;
            case R.id.btn_main_disconnect:
                WebSocketEngine.getInstance().disconnect();
                break;
            case R.id.btn_main_send_ping:
                WebSocketEngine.getInstance().ping(String.valueOf(System.currentTimeMillis()));
                break;
            case R.id.btn_main_send_message:
                skip2Activity(WebSocketSendStringActivity.class);
                break;
            case R.id.btn_main_send_byte:
                skip2Activity(WebSocketSendBinaryActivity.class);
                break;
        }
    }

    private void checkChangeServer() {
        String etInput = etMainServerChange.getText().toString().trim();
        if (TextUtils.isEmpty(etInput)) {
            showToast("Please Input You server Like IP:Port");
        } else {
            wsHost = etInput;
            tvServerInfo.setText(wsHost);
            WebSocketEngine.getInstance().changeClient("12345", wsHost, new MyWebSocketListener());
            showToast("Change Server Success: " + wsHost);
        }
    }

    private class MyWebSocketListener implements WebSocketListener {
        @Override
        public void onConnect() {
            ALog.d("onConnect" + "server: " + wsHost + " is connect!");
            tvMainResult.setText(ALogPrinter.getLogMessage());
        }

        @Override
        public void onMessage(String message) {
            ALog.i(message);
            if (!TextUtils.isEmpty(message)) {
                String info = "Message String:\n" + message;
                tvMainResult.setText(info);
            }
        }

        @Override
        public void onMessage(byte[] data) {
            String message = MessageUtils.byteArray2String(data);
            ALog.i(message);
            if (!TextUtils.isEmpty(message)) {
                String info = "Message byte[]:\n" + message;
                tvMainResult.setText(info);
            }
        }

        @Override
        public boolean onHeartbeat(String ping, String pong, ProtocolError error) {
            ALog.d("message ping: " + ping + "\n" +
                    "message pong: " + pong + "\n"
            );
            tvMainResult.setText(ALogPrinter.getLogMessage());
            return false;
        }


        @Override
        public void onDisconnect(int code, String reason, ProtocolError error) {
            ALog.d("onDisconnect" + "server: " + wsHost + " is disconnect!");
            tvMainResult.setText(ALogPrinter.getLogMessage());
        }

        @Override
        public void onError(Exception error) {
            ALog.w("onError: " + error.getMessage());
            tvMainResult.setText(ALogPrinter.getLogMessage());
        }
    }
}
