package com.vgtech.vancloud.ui;

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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangshaofang on 2017/5/17.
 */

public class UpdatePhoneFragment extends BaseFragment implements HttpListener<String> {
    @Override
    protected int initLayoutId() {
        return R.layout.fragment_input_phone;
    }

    private TextView et_code;

    @Override
    protected void initView(View view) {
        TextView titleTv = (TextView) view.findViewById(android.R.id.title);
        titleTv.setText("更换手机号");
        View backView = view.findViewById(R.id.btn_back);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener((v) -> getActivity().onBackPressed());
        et_code = (TextView) view.findViewById(R.id.et_code);
        view.findViewById(R.id.btn_next).setOnClickListener((v) -> doNext());
    }

    private void doNext() {
        String code = et_code.getText().toString();
        if (TextUtils.isEmpty(code)) {
            ToastUtils.show(getActivity(), "请填写验证码");
            return;
        }
        showLoadingDialog("");
        VanCloudApplication application = getApplication();
        Map<String, String> params = new HashMap<>();
        String phone = getArguments().getString("phone");
        params.put("mobile", phone);
        params.put("verificationCode", code);
        NetworkPath path = new NetworkPath(URLAddr.URL_MOBILE_VERIFICATIONCODE, params, getActivity());
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
                String mobile = path.getPostValues().get("mobile");
                PrfUtils.savePrfparams(getActivity(), PrfUtils.USERNAME, mobile);
                ToastUtils.show(getActivity(), rootData.getMsg());
                getActivity().onBackPressed();
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
