package mdl.sinlov.android.websocket.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mdl.sinlov.android.log.ALog;
import mdl.sinlov.android.websocket.app.ui.MDLTestActivity;
import mdl.sinlov.android.websocket.app.utils.ResourceUtil;


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
    private String wsHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ALog.initTag();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
        tvServerInfo.setText(wsHost);
    }

    private void initData() {
        int ws_server_host = ResourceUtil.getStringId(getApplicationContext(), "ws_server_host");
        wsHost = getString(ws_server_host);
    }

    @Override
    protected void bindListener() {

    }

    @OnClick({R.id.btn_main_connect, R.id.btn_main_disconnect, R.id.btn_main_send_message, R.id.btn_main_send_byte})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_main_connect:
                break;
            case R.id.btn_main_disconnect:
                break;
            case R.id.btn_main_send_message:
                break;
            case R.id.btn_main_send_byte:
                break;
        }
    }
}
