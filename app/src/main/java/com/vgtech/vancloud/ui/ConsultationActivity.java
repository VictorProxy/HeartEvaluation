package com.vgtech.vancloud.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.vgtech.common.api.JsonDataFactory;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.api.Counselor;
import com.vgtech.vancloud.api.WorkCalendar;
import com.vgtech.vancloud.models.Staff;
import com.vgtech.vancloud.utils.ToastUtils;
import com.vgtech.vancloud.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vic on 2017/5/11.
 */
public class ConsultationActivity extends BaseFragment implements View.OnClickListener {

    @Override
    protected int initLayoutId() {
        return R.layout.activity_consultation;
    }

    private Counselor counselor;
    private WorkCalendar workCalendar;
    private TextView et_name;
    private TextView et_phone;
    private TextView et_age;
    private TextView et_description;
    private View mOnlineIv, mPhoneIv, mFaceIv;
    private TextView tv_count;
    private int mSex = -1;
    private CheckBox cb_agree;

    @Override
    protected void initView(View view) {
        String counselorStr = getArguments().getString("counselor");
        String workCalendarStr = getArguments().getString("workCalendar");
        try {
            counselor = JsonDataFactory.getData(Counselor.class, new JSONObject(counselorStr));
            if (!TextUtils.isEmpty(workCalendarStr))
                workCalendar = JsonDataFactory.getData(WorkCalendar.class, new JSONObject(workCalendarStr));
        } catch (Exception e) {
            e.printStackTrace();
        }
        TextView titleTv = (TextView) view.findViewById(android.R.id.title);
        titleTv.setText("预约咨询");
        view.findViewById(R.id.btn_back).setVisibility(View.VISIBLE);
        view.findViewById(R.id.btn_back).setOnClickListener(this);
        view.findViewById(R.id.btn_consultation).setOnClickListener(this);
        view.findViewById(R.id.btn_minus).setOnClickListener(this);
        view.findViewById(R.id.btn_add).setOnClickListener(this);
        view.findViewById(R.id.btn_submit).setOnClickListener(this);
        tv_count = (TextView) view.findViewById(R.id.tv_count);
        et_name = (TextView) view.findViewById(R.id.et_name);
        et_phone = (TextView) view.findViewById(R.id.et_phone);
        et_age = (TextView) view.findViewById(R.id.et_age);
        cb_agree = (CheckBox) view.findViewById(R.id.cb_agree);
        et_description = (TextView) view.findViewById(R.id.et_description);
        SimpleDraweeView userIcon = (SimpleDraweeView) view.findViewById(R.id.iv_icon);
        userIcon.setImageURI(counselor.photoPath);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_name.setText(counselor.name);
        TextView tv_job = (TextView) view.findViewById(R.id.tv_job);
        tv_job.setText(counselor.getLevel());
        RadioGroup sexGroup = (RadioGroup) view.findViewById(R.id.rg_sex);
        sexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_boy:
                        mSex = 1;
                        break;
                    case R.id.rb_girl:
                        mSex = 0;
                        break;
                }
            }
        });
        mOnlineIv = view.findViewById(R.id.iv_online_price);
        mPhoneIv = view.findViewById(R.id.iv_phone_price);
        mFaceIv = view.findViewById(R.id.iv_face_price);
        TextView tv_online_price = (TextView) view.findViewById(R.id.tv_online_price);
        TextView tv_phone_price = (TextView) view.findViewById(R.id.tv_phone_price);
        TextView tv_face_price = (TextView) view.findViewById(R.id.tv_face_price);
        tv_online_price.setText("在线咨询\n" + counselor.onlinePrice + "元/次");
        tv_phone_price.setText("电话咨询\n" + counselor.phonePrice + "元/次");
        tv_face_price.setText("面对面咨询\n" + counselor.facePrice + "元/次");
        view.findViewById(R.id.btn_online_price).setOnClickListener(this);
        view.findViewById(R.id.btn_phone_price).setOnClickListener(this);
        view.findViewById(R.id.btn_face_price).setOnClickListener(this);
        onClick(view.findViewById(R.id.btn_online_price));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("finish");
        getActivity().registerReceiver(finishReceiver, intentFilter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (finishReceiver != null) {
            getActivity().unregisterReceiver(finishReceiver);
            finishReceiver = null;
        }
    }

    private View mLastSelectedView;
    private View mLastIcon;

    private int reserType = 1;
    private String reserPrice;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit: {
                doSubmit();
            }
            break;
            case R.id.btn_minus: {
                int count = Integer.parseInt(tv_count.getText().toString());
                if (count > 1) {
                    count--;
                }
                tv_count.setText(String.valueOf(count));
            }
            break;
            case R.id.btn_add: {
                int count = Integer.parseInt(tv_count.getText().toString());
                count++;
                tv_count.setText(String.valueOf(count));
            }
            break;
            case R.id.btn_online_price:
                reserType = 1;
                reserPrice = counselor.onlinePrice;
                if (mLastSelectedView != null)
                    mLastSelectedView.setSelected(false);
                if (mLastIcon != null)
                    mLastIcon.setVisibility(View.GONE);
                v.setSelected(true);
                mOnlineIv.setVisibility(View.VISIBLE);
                mLastIcon = mOnlineIv;
                mLastSelectedView = v;

                break;
            case R.id.btn_phone_price:
                reserType = 2;
                reserPrice = counselor.phonePrice;
                if (mLastSelectedView != null)
                    mLastSelectedView.setSelected(false);
                if (mLastIcon != null)
                    mLastIcon.setVisibility(View.GONE);
                v.setSelected(true);
                mPhoneIv.setVisibility(View.VISIBLE);
                mLastIcon = mPhoneIv;
                mLastSelectedView = v;
                break;
            case R.id.btn_face_price:
                reserType = 3;
                reserPrice = counselor.facePrice;
                if (mLastSelectedView != null)
                    mLastSelectedView.setSelected(false);
                if (mLastIcon != null)
                    mLastIcon.setVisibility(View.GONE);
                v.setSelected(true);
                mFaceIv.setVisibility(View.VISIBLE);
                mLastIcon = mFaceIv;
                mLastSelectedView = v;
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
        String reserNum = tv_count.getText().toString();
        String mbrName = et_name.getText().toString();
        String mbrMobile = et_phone.getText().toString();
        String mbrAge = et_age.getText().toString();
        String mbrNotes = et_description.getText().toString();
        if (TextUtils.isEmpty(mbrName)) {
            ToastUtils.show(getActivity(), "请填写称呼");
            return;
        }
        if (TextUtils.isEmpty(mbrMobile)) {
            ToastUtils.show(getActivity(), "请填写手机号码");
            return;
        }
        if (!Utils.isPhoneNum(mbrMobile)) {
            ToastUtils.show(getActivity(), "手机号码不合法");
        }
        if (TextUtils.isEmpty(mbrAge)) {
            ToastUtils.show(getActivity(), "请填写年龄");
            return;
        }
        if (mSex == -1) {
            ToastUtils.show(getActivity(), "请选择性别");
            return;
        }
        if (TextUtils.isEmpty(mbrNotes)) {
            ToastUtils.show(getActivity(), "请填写问题描述");
            return;
        }
        if (!cb_agree.isChecked()) {
            ToastUtils.show(getActivity(), "请同意相关条款协议");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("counselor", counselor.getJson().toString());
        bundle.putString("reserType", String.valueOf(reserType));
        bundle.putString("reserNum", reserNum);
        bundle.putString("mbrName", mbrName);
        bundle.putString("mbrMobile", mbrMobile);
        bundle.putString("mbrAge", mbrAge);
        bundle.putString("mbrGender", String.valueOf(mSex));
        bundle.putString("mbrNotes", mbrNotes);
        bundle.putString("reserPrice", reserPrice);
        if (workCalendar != null)
            bundle.putString("calendarID", workCalendar.calendarID);
        ConsultationConfirmActivity consulationConfirm = new ConsultationConfirmActivity();
        consulationConfirm.setArguments(bundle);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.controller.pushFragment(consulationConfirm);
    }

    private BroadcastReceiver finishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("finish")) {
                getActivity().onBackPressed();
            }
        }
    };

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
}
