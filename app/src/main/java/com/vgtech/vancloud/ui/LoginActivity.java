package com.vgtech.vancloud.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.vgtech.common.PrfUtils;
import com.vgtech.common.URLAddr;
import com.vgtech.common.api.RootData;
import com.vgtech.common.api.UserAccount;
import com.vgtech.common.network.NetworkManager;
import com.vgtech.common.network.NetworkPath;
import com.vgtech.common.network.android.HttpListener;
import com.vgtech.vancloud.PrfConstants;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.VanCloudApplication;
import com.vgtech.vancloud.ui.chat.controllers.Controller;
import com.vgtech.vancloud.utils.ActivityUtils;
import com.vgtech.vancloud.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import roboguice.RoboGuice;
import roboguice.inject.RoboInjector;
import roboguice.util.RoboContext;

/**
 * Created by vic on 2017/1/13.
 */
public class LoginActivity extends BaseActivity implements HttpListener<String>, RoboContext {
    @Inject
    Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final RoboInjector injector = RoboGuice.getInjector(this);
        injector.injectMembersWithoutViews(this);
        super.onCreate(savedInstanceState);
        TextView titleTv = (TextView) findViewById(android.R.id.title);
        titleTv.setText("登录");
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_forget_password).setOnClickListener(this);
        findViewById(R.id.btn_register).setOnClickListener(this);
        findViewById(R.id.btn_wx_login).setOnClickListener(this);
        findViewById(R.id.btn_alipy_login).setOnClickListener(this);
        String username = PrfUtils.getPrfparams(this, PrfUtils.USERNAME);
        String pwd = PrfUtils.getPrfparams(this, PrfUtils.PASSWORD);
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
            TextView usernameTv = (TextView) findViewById(R.id.et_username);
            TextView passwordTv = (TextView) findViewById(R.id.et_password);
            usernameTv.setText(username);
            passwordTv.setText(pwd);
        }
        String token = PrfUtils.getPrfparams(this, PrfUtils.TOKEN);
        String openfire = PrfUtils.getPrfparams(this, PrfUtils.OPENFIRE);
        String profileJson = PrfUtils.getPrfparams(this, PrfConstants.PARAM_PROFILE);
        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(openfire)) {
            try {
                JSONObject proFile = new JSONObject(profileJson);
                UserAccount userAccount = new UserAccount();
                userAccount.user_id = proFile.getString("mbrID");
                userAccount.user_name = proFile.getString("name");
                userAccount.photo = proFile.getString("headImg");
                String[] openFileAdd = openfire.split(":");
                userAccount.xmpp_host = openFileAdd[0];
                userAccount.xmpp_port = Integer.parseInt(openFileAdd[1]);
                controller.pref().storageAccount(userAccount);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                doLogin();
                break;
            case R.id.btn_register: {
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.btn_forget_password: {
                Intent intent = new Intent(this, FindPwdActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.btn_wx_login:
                break;
            case R.id.btn_alipy_login:
                break;
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    private NetworkManager mNetworkManager;

    private void doLogin() {
        TextView usernameTv = (TextView) findViewById(R.id.et_username);
        TextView passwordTv = (TextView) findViewById(R.id.et_password);
        String usename = usernameTv.getText().toString();
        String password = passwordTv.getText().toString();
        if (TextUtils.isEmpty(usename)) {
            ToastUtils.show(this, usernameTv.getHint() + "不能为空");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtils.show(this, passwordTv.getHint() + "不能为空");
            return;
        }
        showLoadingDialog("");


        VanCloudApplication application = (VanCloudApplication) getApplication();
        mNetworkManager = application.getNetworkManager();
        Uri uri = Uri.parse(URLAddr.URL_LOGIN).buildUpon().appendQueryParameter("userName", usename).appendQueryParameter("password", password).build();
        NetworkPath path = new NetworkPath(uri.toString());
        mNetworkManager.load(1, path, this);
    }

    private void loadMyprofile() {
        Map<String, String> params = new HashMap<>();
        NetworkPath path = new NetworkPath(URLAddr.URL_MY_PROFILE, params, this);
        VanCloudApplication vancloudApplication = (VanCloudApplication) getApplication();
        vancloudApplication.getNetworkManager().load(2, path, this);
    }

    private String mOpenFile;

    @Override
    public void dataLoaded(int callbackId, NetworkPath path, RootData rootData) {
        boolean safe = ActivityUtils.prehandleNetworkData(this, rootData);
        if (!safe) {
            dismisLoadingDialog();
            return;
        }
        switch (callbackId) {
            case 1:
                try {
                    JSONObject jsonObject = rootData.getJson().getJSONObject("data");
                    String mbrID = jsonObject.getString("mbrID");
                    String token = jsonObject.getString("token");
                    String host = jsonObject.getString("host");
                    mOpenFile = jsonObject.getString("openFire");
                    ToastUtils.show(this, rootData.getMsg());
                    Uri uri = Uri.parse(path.getUrl());
                    String username = uri.getQueryParameter("userName");
                    String password = uri.getQueryParameter("password");
                    SharedPreferences preferences = PrfUtils.getSharePreferences(this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(PrfUtils.OPENFIRE, mOpenFile);
                    editor.putString(PrfUtils.USERNAME, username);
                    editor.putString(PrfUtils.IMG_HOST, host);
                    editor.putString(PrfUtils.PASSWORD, password);
                    editor.putString(PrfUtils.MBRID, mbrID);
                    editor.putString(PrfUtils.TOKEN, token);
                    editor.putBoolean(PrfUtils.LOGIN_TICKET, true);
                    editor.commit();
                    loadMyprofile();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                try {
                    dismisLoadingDialog();
                    JSONObject jsonObject = rootData.getJson().getJSONObject("data");
                    JSONObject proFile = jsonObject.getJSONObject("profile");
                    JSONArray communityList = jsonObject.getJSONArray("communityList");
                    PrfUtils.savePrfparams(this, PrfConstants.PARAM_PROFILE, proFile.toString());
                    PrfUtils.savePrfparams(this, PrfConstants.PARAM_COMMUNITYLIST, communityList.toString());
                    UserAccount userAccount = new UserAccount();
                    userAccount.user_id = proFile.getString("mbrID");
//                    userAccount.user_id = "613578361146445824735893790648176640";
//                    userAccount.user_id = "606695182489882624735893790648176640";
                    userAccount.user_name = proFile.getString("name");
                    userAccount.photo = proFile.getString("headImg");
                    String[] openFileAdd = mOpenFile.split(":");
//                    userAccount.xmpp_host = "192.168.3.244";
//                    userAccount.xmpp_port = 5222;
                    userAccount.xmpp_host = openFileAdd[0];
                    userAccount.xmpp_port = Integer.parseInt(openFileAdd[1]);
                    controller.pref().storageAccount(userAccount);
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void finish() {
        if (mNetworkManager != null)
            mNetworkManager.cancle(this);
        super.finish();
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(String response) {

    }

    protected HashMap<Key<?>, Object> scopedObjects = new HashMap<Key<?>, Object>();

    @Override
    public Map<Key<?>, Object> getScopedObjectMap() {
        return scopedObjects;
    }
}
