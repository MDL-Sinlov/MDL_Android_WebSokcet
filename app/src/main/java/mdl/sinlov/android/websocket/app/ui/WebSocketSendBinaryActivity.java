package mdl.sinlov.android.websocket.app.ui;

import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mdl.sinlov.android.log.ALog;
import mdl.sinlov.android.log.ALogPrinter;
import mdl.sinlov.android.websocket.app.R;
import mdl.sinlov.android.websocket.app.utils.HexUtils;
import mdl.sinlov.android.websocket.app.utils.ResourceUtil;
import mdl.sinlov.android.websokcet.WebSocketEngine;
import mdl.sinlov.android.websokcet.WebSocketListener;

public class WebSocketSendBinaryActivity extends MDLTestActivity {

    @BindView(R.id.btn_ws_send_binary)
    Button btnWsSendBinary;
    @BindView(R.id.tv_ws_send_binary_resource)
    TextView tvWsSendBinaryResource;
    @BindView(R.id.tv_ws_send_binary_res)
    TextView tvWsSendBinaryRes;
    private String wsHost;
    private WebSocketEngine wsEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_socket_send_binary);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wsEngine.onWebSocketListener(new WebSocketListener() {
            @Override
            public void onConnect() {
                ALog.d("onConnect" + "server: " + wsHost + " is connect!");
                tvWsSendBinaryRes.setText(ALogPrinter.getLogMessage());
            }

            @Override
            public void onMessage(String message) {
                ALog.w(message);
                if (!TextUtils.isEmpty(message)) {
                    String info = "Message String:\n" + message;
                    tvWsSendBinaryRes.setText(info);
                }
            }

            @Override
            public void onMessage(byte[] data) {
                String message = HexUtils.bytes2HexStr(data);
                if (message.length() > 20) {
                    message = message.substring(0, 19);
                }
                ALog.i(message);
                if (!TextUtils.isEmpty(message)) {
                    String info = "Message byte[]:\n" + message;
                    tvWsSendBinaryRes.setText(info);
                }
            }

            @Override
            public void onDisconnect(int code, String reason, Exception error) {
                ALog.d("onDisconnect" + "server: " + wsHost + " is disconnect!");
                tvWsSendBinaryRes.setText(ALogPrinter.getLogMessage());
            }

            @Override
            public void onError(Exception error) {
                ALog.e("onError: " + error.getMessage());
                tvWsSendBinaryRes.setText(ALogPrinter.getLogMessage());
            }
        });
    }

    private void initData() {
        int ws_server_host = ResourceUtil.getStringId(getApplicationContext(), "ws_server_host");
        wsHost = getString(ws_server_host);
        wsEngine = WebSocketEngine.getInstance();
    }

    @Override
    protected void bindView(Bundle savedInstanceState) {

    }

    @Override
    protected void bindListener() {

    }

    @OnClick(R.id.btn_ws_send_binary)
    public void onClick() {
        byte[] data = getBitmapByteArray();
        wsEngine.send(data);
        String hexStr = HexUtils.bytes2HexStr(data);
        if (hexStr.length() > 20) {
            hexStr = hexStr.substring(0, 19);
        }
        tvWsSendBinaryResource.setText(hexStr);
    }

    private byte[] getBitmapByteArray() {
        Bitmap launcherBitmap = getMipmapXXXDpiRes("test_send");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        launcherBitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        launcherBitmap.recycle();
        byte[] res = output.toByteArray();
        try {
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public Bitmap getMipmapXXXDpiRes(String name) {
        ApplicationInfo appInfo = getApplicationInfo();
        int resID = getResources().getIdentifier(name, "drawable", appInfo.packageName);
        return BitmapFactory.decodeResource(getResources(), resID);
    }
}
