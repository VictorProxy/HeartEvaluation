package com.vgtech.vancloud.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.vgtech.common.FileCacheUtils;
import com.vgtech.common.PrfUtils;
import com.vgtech.common.URLAddr;
import com.vgtech.common.api.JsonDataFactory;
import com.vgtech.common.api.RootData;
import com.vgtech.common.network.NetworkPath;
import com.vgtech.common.network.android.HttpListener;
import com.vgtech.common.view.ActionSheetDialog;
import com.vgtech.vancloud.PrfConstants;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.VanCloudApplication;
import com.vgtech.vancloud.api.Community;
import com.vgtech.vancloud.ui.common.publish.internal.PicSelectActivity;
import com.vgtech.vancloud.utils.ActivityUtils;
import com.vgtech.vancloud.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vic on 2017/5/15.
 */
public class CommunityActivity extends BaseActivity implements HttpListener<String> {
    @Override
    protected int getContentView() {
        return R.layout.activity_community;
    }

    private TextView tv_com_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("社区设置");
        findViewById(R.id.btn_back).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_sns_setting).setOnClickListener(this);
        tv_com_name = (TextView) findViewById(R.id.tv_com_name);
        initData();
    }

    private List<Community> mCommunityList;
    private String mComcode;

    private void initData() {
        try {
            JSONObject proFile = new JSONObject(PrfUtils.getPrfparams(this, PrfConstants.PARAM_PROFILE));
            mComcode = proFile.getString("comCode");
            JSONArray jsonArray = new JSONArray(PrfUtils.getPrfparams(this, PrfConstants.PARAM_COMMUNITYLIST));
            mCommunityList = JsonDataFactory.getDataArray(Community.class, jsonArray);
            String comName = null;
            if (!TextUtils.isEmpty(mComcode)) {
                for (Community com : mCommunityList) {
                    if (mComcode.equals(com.comCode)) {
                        comName = com.communityName;
                    }
                }
            }
            if (!TextUtils.isEmpty(comName)) {
                tv_com_name.setText(comName);
            } else {
                tv_com_name.setText("选择社区");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sns_setting:
                ActionSheetDialog sheetDialog = new ActionSheetDialog(this)
                        .builder()
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true);
                for (Community com : mCommunityList) {
                    sheetDialog.addSheetItem(com.communityName, ActionSheetDialog.SheetItemColor.Blue,
                            new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    Community c = mCommunityList.get(which);
                                    tv_com_name.setText(c.communityName);
                                    uploadCommunity(c.comCode);
                                }
                            });
                }
                sheetDialog.show();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    private void uploadCommunity(String communityID) {
        showLoadingDialog("");
        Map<String, String> params = new HashMap<>();
        params.put("comCode", communityID);
        NetworkPath path = new NetworkPath(URLAddr.URL_MY_PROFILESAVE, params, this);
        VanCloudApplication vancloudApplication = (VanCloudApplication) getApplication();
        vancloudApplication.getNetworkManager().load(1, path, this);
    }

    @Override
    public void dataLoaded(int callbackId, NetworkPath path, RootData rootData) {
        dismisLoadingDialog();
        boolean safe = ActivityUtils.prehandleNetworkData(this, rootData);
        if (!safe) {
            return;
        }
        try {
            ToastUtils.show(this,rootData.getMsg());
            JSONObject proFile = new JSONObject(PrfUtils.getPrfparams(this, PrfConstants.PARAM_PROFILE));
            proFile.put("comCode", path.getPostValues().get("comCode"));
            PrfUtils.savePrfparams(this, PrfConstants.PARAM_PROFILE, proFile.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(String response) {

    }
}
