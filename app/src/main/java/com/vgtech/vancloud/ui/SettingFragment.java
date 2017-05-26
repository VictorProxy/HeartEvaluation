package com.vgtech.vancloud.ui;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.vgtech.common.PrfUtils;
import com.vgtech.vancloud.R;

/**
 * Created by zhangshaofang on 2017/5/17.
 */

public class SettingFragment extends BaseFragment {
    @Override
    protected int initLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void initView(View view) {
        TextView titleTv = (TextView) view.findViewById(android.R.id.title);
        titleTv.setText("手机设置");
        View backView = view.findViewById(R.id.btn_back);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener((v) -> getActivity().onBackPressed());
        TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
        tv_phone.setText(PrfUtils.getPrfparams(getActivity(), PrfUtils.USERNAME));
        view.findViewById(R.id.btn_update_phone).setOnClickListener((v) -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            InputPhoneFragment framgent = new InputPhoneFragment();
            mainActivity.controller.pushFragment(framgent);
        });
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
}
