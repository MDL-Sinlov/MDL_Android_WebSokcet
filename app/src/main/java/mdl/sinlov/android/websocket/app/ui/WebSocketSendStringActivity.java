package mdl.sinlov.android.websocket.app.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import mdl.sinlov.android.log.ALog;
import mdl.sinlov.android.log.ALogPrinter;
import mdl.sinlov.android.websocket.app.R;
import mdl.sinlov.android.websocket.app.utils.ResourceUtil;
import mdl.sinlov.android.websokcet.MessageUtils;
import mdl.sinlov.android.websokcet.WebSocketEngine;
import mdl.sinlov.android.websokcet.WebSocketListener;

public class WebSocketSendStringActivity extends MDLTestActivity {

    @BindView(R.id.et_ws_send_string)
    EditText etWsSendString;
    @BindView(R.id.btn_ws_send_string)
    Button btnWsSendString;
    @BindView(R.id.tv_ws_send_string_res)
    TextView tvWsSendStringRes;
    @BindView(R.id.tv_ws_send_string_resource)
    TextView tvWsSendStringResource;
    private String wsHost;
    private WebSocketEngine wsEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_web_socket_send_string);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initData();
    }

    private void initData() {
        int ws_server_host = ResourceUtil.getStringId(getApplicationContext(), "ws_server_host");
        wsHost = getString(ws_server_host);
        wsEngine = WebSocketEngine.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wsEngine.onWebSocketListener(new WebSocketListener() {
            @Override
            public void onConnect() {
                ALog.d("onConnect" + "server: " + wsHost + " is connect!");
                tvWsSendStringRes.setText(ALogPrinter.getLogMessage());
            }

            @Override
            public void onMessage(String message) {
                ALog.i(message);
                if (!TextUtils.isEmpty(message)) {
                    String info = "Message String:\n" + message;
                    tvWsSendStringRes.setText(info);
                }
            }

            @Override
            public void onMessage(byte[] data) {
                String message = MessageUtils.byteArray2String(data);
                ALog.i(message);
                if (!TextUtils.isEmpty(message)) {
                    String info = "Message byte[]:\n" + message;
                    tvWsSendStringRes.setText(info);
                }
            }

            @Override
            public void onDisconnect(int code, String reason, Exception error) {
                ALog.d("onDisconnect" + "server: " + wsHost + " is disconnect!");
                tvWsSendStringRes.setText(ALogPrinter.getLogMessage());
            }

            @Override
            public void onError(Exception error) {
                ALog.w("onError: " + error.getMessage());
                tvWsSendStringRes.setText(ALogPrinter.getLogMessage());
            }
        });
    }

    @Override
    protected void bindListener() {
        btnWsSendString.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trim = etWsSendString.getText().toString().trim();
                if (TextUtils.isEmpty(trim)) {
                    tvWsSendStringResource.setText("Your input is empty!");
                } else {
                    tvWsSendStringResource.setText(trim);
                    wsEngine.send(trim);
                }
            }
        });
    }
}
