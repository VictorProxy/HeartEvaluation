package com.vgtech.vancloud.ui;

import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vic on 2017/5/21.
 */
public class FindPwdActivity extends BaseActivity implements HttpListener<String> {
    @Override
    protected int getContentView() {
        return R.layout.activity_findpwd;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View btnView = findViewById(R.id.btn_back);
        btnView.setVisibility(View.VISIBLE);
        setTitle("找回密码");
        findViewById(R.id.btn_register).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                doRegister();
                break;
            default:
                super.onClick(v);
                break;
        }
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
        showLoadingDialog("");
        VanCloudApplication application = (VanCloudApplication) getApplication();
        Map<String, String> params = new HashMap<>();
        params.put("mobile", phone);
        NetworkPath path = new NetworkPath(URLAddr.URL_ACCOUNT_GETBACKPASSWORD, params, this);
        application.getNetworkManager().load(1, path, this);
    }

    @Override
    public void dataLoaded(int callbackId, NetworkPath path, RootData rootData) {
        dismisLoadingDialog();
        boolean safe = ActivityUtils.prehandleNetworkData(this, rootData);
        if (!safe) {
            return;
        }
        switch (callbackId) {
            case 1:
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
