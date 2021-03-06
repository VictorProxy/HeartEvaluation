package com.vgtech.vancloud.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.vgtech.common.PrfUtils;
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
 * Created by zhangshaofang on 2017/5/17.
 */

public class InputPhoneFragment extends BaseFragment implements HttpListener<String> {
    @Override
    protected int initLayoutId() {
        return R.layout.fragment_input_phone;
    }

    private TextView phoneTv;

    @Override
    protected void initView(View view) {
        TextView titleTv = (TextView) view.findViewById(android.R.id.title);
        titleTv.setText("新手机号");
        View backView = view.findViewById(R.id.btn_back);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener((v) -> getActivity().onBackPressed());
        phoneTv = (TextView) view.findViewById(R.id.et_phone);
        view.findViewById(R.id.btn_next).setOnClickListener((v) -> doNext());
    }

    private void doNext() {
        String phone = phoneTv.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show(getActivity(), "请填写手机号");
            return;
        }
        if (!Utils.isPhoneNum(phone)) {
            ToastUtils.show(getActivity(), "手机号不合法");
            return;
        }

        showLoadingDialog("");
        VanCloudApplication application = getApplication();
        Map<String, String> params = new HashMap<>();
        params.put("mobile", phone);
        NetworkPath path = new NetworkPath(URLAddr.URL_MOBILE_NEWMOBILE, params, getActivity());
        application.getNetworkManager().load(1, path, this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected boolean onBackPressed() {
        return false;
    }

    @Override
    public void dataLoaded(int callbackId, NetworkPath path, RootData rootData) {
        dismisLoadingDialog();
        boolean safe = ActivityUtils.prehandleNetworkData(getActivity(), rootData);
        if (!safe) {
            return;
        }
        switch (callbackId) {
            case 1:
                ToastUtils.show(getActivity(), rootData.getMsg());
                String mobile = path.getPostValues().get("mobile");
                MainActivity mainActivity = (MainActivity) getActivity();
                UpdatePhoneFragment framgent = new UpdatePhoneFragment();
                Bundle bundle = new Bundle();
                bundle.putString("phone", mobile);
                framgent.setArguments(bundle);
                mainActivity.controller.pushFragment(framgent);
                mainActivity.controller.removeFragment(this);
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
