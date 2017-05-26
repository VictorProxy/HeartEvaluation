package com.vgtech.vancloud.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.vgtech.common.URLAddr;
import com.vgtech.common.api.RootData;
import com.vgtech.common.network.NetworkPath;
import com.vgtech.common.network.android.HttpListener;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.VanCloudApplication;
import com.vgtech.vancloud.utils.ActivityUtils;
import com.vgtech.vancloud.utils.ToastUtils;
import com.vgtech.vancloud.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vic on 2017/5/13.
 */
public class RegisterActivity extends BaseActivity implements HttpListener<String> {
    private int mSecond = 60;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mSecond--;
                    if (mSecond > 0) {
                        mGetCodeTv.setText("发送验证码(" + mSecond + ")");
                        mHandler.sendEmptyMessageDelayed(1, 1000);
                    } else {
                        mGetCodeTv.setEnabled(true);
                        mGetCodeTv.setText("发送验证码");
                    }
                    break;
            }
        }
    };

    @Override
    protected int getContentView() {
        return R.layout.activity_register;
    }

    private TextView mGetCodeTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View btnView = findViewById(R.id.btn_back);
        btnView.setVisibility(View.VISIBLE);
        setTitle("快速注册");
        mGetCodeTv = (TextView) findViewById(R.id.btn_get_code);
        mGetCodeTv.setOnClickListener(this);
        findViewById(R.id.btn_register).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_code:
                getCode();
                break;
            case R.id.btn_register:
                doRegister();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    private void getCode() {
        TextView phoneTv = (TextView) findViewById(R.id.et_username);
        String phone = phoneTv.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show(this, "请填写手机号");
            return;
        }
        if (!Utils.isPhoneNum(phone)) {
            ToastUtils.show(this, "手机号不合法");
            return;
        }
        showLoadingDialog("");
        VanCloudApplication application = (VanCloudApplication) getApplication();
        Map<String, String> params = new HashMap<>();
        params.put("mobile", phone);
        NetworkPath path = new NetworkPath(URLAddr.URL_MSG_SEND, params, this);
        application.getNetworkManager().load(1, path, this);
    }

    private void doRegister() {
        TextView phoneTv = (TextView) findViewById(R.id.et_username);
        String phone = phoneTv.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show(this, "请填写手机号");
            return;
        }
        if (!Utils.isPhoneNum(phone)) {
            ToastUtils.show(this, "手机号不合法");
            return;
        }
        TextView et_code = (TextView) findViewById(R.id.et_code);
        String code = et_code.getText().toString();
        if (TextUtils.isEmpty(code)) {
            ToastUtils.show(this, "请填写验证码");
            return;
        }
        TextView et_password = (TextView) findViewById(R.id.et_password);
        String password = et_password.getText().toString();
        if (TextUtils.isEmpty(password)) {
            ToastUtils.show(this, "请填写密码");
            return;
        }
        showLoadingDialog("");
        VanCloudApplication application = (VanCloudApplication) getApplication();
        Map<String, String> params = new HashMap<>();
        params.put("mobile", phone);
        params.put("verificationCode", code);
        params.put("password", password);
        NetworkPath path = new NetworkPath(URLAddr.URL_REG_DOREG, params, this);
        application.getNetworkManager().load(2, path, this);
    }

    private String verificationCode;
    private String mobile;

    @Override
    public void dataLoaded(int callbackId, NetworkPath path, RootData rootData) {
        dismisLoadingDialog();
        boolean safe = ActivityUtils.prehandleNetworkData(this, rootData);
        if (!safe) {
            return;
        }
        switch (callbackId) {
            case 1:
                try {
                    mGetCodeTv.setEnabled(false);
                    mHandler.sendEmptyMessage(1);
                    ToastUtils.show(this, rootData.getMsg());
                    JSONObject jsonObject = rootData.getJson().getJSONObject("data");
                    verificationCode = jsonObject.getString("verificationCode");
                    mobile = jsonObject.getString("mobile");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                ToastUtils.show(this, rootData.getMsg());
                finish();
                break;

        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(String response) {

    }


}
