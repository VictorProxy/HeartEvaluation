package com.vgtech.vancloud.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.vgtech.common.URLAddr;
import com.vgtech.common.api.JsonDataFactory;
import com.vgtech.common.api.RootData;
import com.vgtech.common.network.NetworkPath;
import com.vgtech.common.network.android.HttpListener;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.api.Counselor;
import com.vgtech.vancloud.api.WorkCalendar;
import com.vgtech.vancloud.models.Staff;
import com.vgtech.vancloud.utils.ActivityUtils;
import com.vgtech.vancloud.utils.ToastUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vic on 2017/5/12.
 */
public class ConsultationConfirmActivity extends BaseFragment implements View.OnClickListener, HttpListener<String> {

    @Override
    protected int initLayoutId() {
        return R.layout.activity_consultationconfirm;
    }

    private Counselor counselor;

    @Override
    protected void initView(View view) {
        Bundle bundle = getArguments();
        String counselorStr = bundle.getString("counselor");
        try {
            counselor = JsonDataFactory.getData(Counselor.class, new JSONObject(counselorStr));
        } catch (Exception e) {
            e.printStackTrace();
        }
        TextView titleTv = (TextView) view.findViewById(android.R.id.title);
        titleTv.setText("预约咨询");
        view.findViewById(R.id.btn_back).setVisibility(View.VISIBLE);
        view.findViewById(R.id.btn_back).setOnClickListener(this);
        view.findViewById(R.id.btn_submit).setOnClickListener(this);
        view.findViewById(R.id.btn_consultation).setOnClickListener(this);
        SimpleDraweeView userIcon = (SimpleDraweeView) view.findViewById(R.id.iv_icon);
        userIcon.setImageURI(counselor.photoPath);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_name.setText(counselor.name);
        TextView tv_job = (TextView) view.findViewById(R.id.tv_job);
        tv_job.setText(counselor.getLevel());

        TextView et_ask_type = (TextView) view.findViewById(R.id.et_ask_type);
        TextView et_ask_times = (TextView) view.findViewById(R.id.et_ask_times);
        TextView et_name = (TextView) view.findViewById(R.id.et_name);
        TextView et_phone = (TextView) view.findViewById(R.id.et_phone);
        TextView et_age = (TextView) view.findViewById(R.id.et_age);
        TextView et_sex = (TextView) view.findViewById(R.id.et_sex);
        TextView et_description = (TextView) view.findViewById(R.id.et_description);
        TextView et_amount = (TextView) view.findViewById(R.id.et_amount);
        String reserType = bundle.getString("reserType");
        if ("1".equals(reserType)) {
            et_ask_type.setText("在线咨询");
        } else if ("2".equals(reserType)) {
            et_ask_type.setText("电话咨询");
        } else if ("3".equals(reserType)) {
            et_ask_type.setText("面对面咨询");
        }
        String reserNum = bundle.getString("reserNum");
        et_ask_times.setText(reserNum);
        String mbrName = bundle.getString("mbrName");
        et_name.setText(mbrName);
        String mbrMobile = bundle.getString("mbrMobile");
        et_phone.setText(mbrMobile);
        String mbrAge = bundle.getString("mbrAge");
        et_age.setText(mbrAge);
        String mbrGender = bundle.getString("mbrGender");
        if ("0".equals(mbrGender)) {
            et_sex.setText("女");
        } else if ("1".equals(mbrGender)) {
            et_sex.setText("男");
        }
        String mbrNotes = bundle.getString("mbrNotes");
        et_description.setText("问题描述："+mbrNotes);
        String reserPrice = bundle.getString("reserPrice");
        et_amount.setText(reserPrice);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit: {
                doSubmit();
            }
            break;
            case R.id.btn_back:
                getActivity().onBackPressed();
                break;
            case R.id.btn_consultation:
                sendXmppChat(counselor.mbrID, counselor.name, counselor.photoPath);
                break;
        }
    }

    private void doSubmit() {
        Bundle bundle = getArguments();
        String reserType = bundle.getString("reserType");
        String reserNum = bundle.getString("reserNum");
        String mbrName = bundle.getString("mbrName");
        String mbrMobile = bundle.getString("mbrMobile");
        String mbrAge = bundle.getString("mbrAge");
        String mbrGender = bundle.getString("mbrGender");
        String mbrNotes = bundle.getString("mbrNotes");
        String calendarID = bundle.getString("calendarID");
        //TODO submit
        Map<String, String> params = new HashMap<>();
        params.put("reserType", reserType);
        params.put("reserNum", reserNum);
        params.put("mbrName", mbrName);
        params.put("mbrMobile", mbrMobile);
        params.put("mbrAge", mbrAge);
        params.put("mbrGender", mbrGender);
        params.put("mbrNotes", mbrNotes);
        if (!TextUtils.isEmpty(calendarID))
            params.put("calendarID", calendarID);
        NetworkPath path = new NetworkPath(URLAddr.URL_RESERVATION_COUNSELORRESERVATIONSAVE, params, getActivity());
        getApplication().getNetworkManager().load(1, path, this);
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


    public void sendXmppChat(String userId, String name, String photo) {
        List<Staff> contactses = new ArrayList<Staff>();
        Staff staff = new Staff(userId, userId, name, photo);
        contactses.add(staff);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.xmpp.chat(contactses, null);
    }

    @Override
    public void dataLoaded(int callbackId, NetworkPath path, RootData rootData) {
        boolean safe = ActivityUtils.prehandleNetworkData(getActivity(), rootData);
        if (!safe) {
            return;
        }
        ToastUtils.show(getActivity(), "预约成功");
        getActivity().sendBroadcast(new Intent("finish"));
        getActivity().onBackPressed();
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(String response) {

    }
}
