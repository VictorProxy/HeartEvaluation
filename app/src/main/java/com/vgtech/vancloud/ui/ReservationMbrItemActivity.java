package com.vgtech.vancloud.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.vgtech.common.URLAddr;
import com.vgtech.common.api.JsonDataFactory;
import com.vgtech.common.api.RootData;
import com.vgtech.common.network.NetworkPath;
import com.vgtech.common.network.android.HttpListener;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.api.Reservation;
import com.vgtech.vancloud.api.ReservationMbr;
import com.vgtech.vancloud.models.Staff;
import com.vgtech.vancloud.utils.ActivityUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vic on 2017/5/12.
 */
public class ReservationMbrItemActivity extends BaseFragment implements HttpListener<String>, View.OnClickListener {
    @Override
    protected int initLayoutId() {
        return R.layout.activity_reservation;
    }

    private View mView;

    @Override
    protected void initView(View view) {
        mView = view;
        view.findViewById(R.id.btn_consultation).setOnClickListener(this);
        View backView = view.findViewById(R.id.btn_back);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(this);
        TextView titleTv = (TextView) view.findViewById(android.R.id.title);
        titleTv.setText("预约详情");
        String reserID = getArguments().getString("reserID");
        loadInfo(reserID);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("submit_comment");
        getActivity().registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
            receiver = null;
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("submit_comment".equals(intent.getAction())) {
                String reserID = getArguments().getString("reserID");
                loadInfo(reserID);
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

    private void loadInfo(String reserID) {
        Map<String, String> params = new HashMap<>();
        params.put("reserID", reserID);
        NetworkPath path = new NetworkPath(URLAddr.URL_RESERVATION_COUNRESERVATIONVIEWINFO, params, getActivity());
        getApplication().getNetworkManager().load(1, path, this);
    }

    private ReservationMbr reservation;

    @Override
    public void dataLoaded(int callbackId, NetworkPath path, RootData rootData) {
        dismisLoadingDialog();
        boolean safe = ActivityUtils.prehandleNetworkData(getActivity(), rootData);
        if (!safe) {
            return;
        }
        switch (callbackId) {
            case 1:
                try {
                    JSONObject dataJson = rootData.getJson().getJSONObject("data");
                    JSONObject reservationJson = dataJson.getJSONObject("reservation");
                    reservation = JsonDataFactory.getData(ReservationMbr.class, reservationJson);
                    SimpleDraweeView userIcon = (SimpleDraweeView) mView.findViewById(R.id.iv_icon);
                    userIcon.setImageURI(reservation.mbrHeadImg);
                    TextView tv_name = (TextView) mView.findViewById(R.id.tv_name);
                    tv_name.setText(reservation.mbrName);
                    TextView tv_job = (TextView) mView.findViewById(R.id.tv_job);
                    String sex = "1".equals(reservation.mbrGender) ? "男" : "女";
                    tv_job.setText(sex);
                    TextView et_ask_type = (TextView) mView.findViewById(R.id.et_ask_type);
                    TextView et_ask_times = (TextView) mView.findViewById(R.id.et_ask_times);
                    TextView et_name = (TextView) mView.findViewById(R.id.et_name);
                    TextView et_phone = (TextView) mView.findViewById(R.id.et_phone);
                    TextView et_age = (TextView) mView.findViewById(R.id.et_age);
                    TextView et_sex = (TextView) mView.findViewById(R.id.et_sex);
                    TextView et_description = (TextView) mView.findViewById(R.id.et_description);
                    TextView et_amount = (TextView) mView.findViewById(R.id.et_amount);
                    String reserType = reservation.reserType;
                    if ("1".equals(reserType)) {
                        et_ask_type.setText("在线咨询");
                    } else if ("2".equals(reserType)) {
                        et_ask_type.setText("电话咨询");
                    } else if ("3".equals(reserType)) {
                        et_ask_type.setText("面对面咨询");
                    }
                    et_ask_times.setText(reservation.reserNum);
                    et_name.setText(reservation.mbrName);
                    et_phone.setText(reservation.mbrMobile);
                    et_age.setText(reservation.mbrAge);
                    String mbrGender = reservation.mbrGender;
                    if ("0".equals(mbrGender)) {
                        et_sex.setText("女");
                    } else if ("1".equals(mbrGender)) {
                        et_sex.setText("男");
                    }
                    et_description.setText("问题描述：" + reservation.mbrNotes);
                    et_amount.setText(reservation.payMoney);
                    ((TextView) mView.findViewById(R.id.et_date)).setText(dataJson.getString("date"));
                    ((TextView) mView.findViewById(R.id.et_time)).setText(dataJson.getString("beginTime") + "--" + dataJson.getString("endTime"));
                    ((TextView) mView.findViewById(R.id.et_status)).setText(reservationJson.getString("reserStatus"));
                    ((TextView) mView.findViewById(R.id.et_paystatus)).setText(reservationJson.getString("payStatus").equals("0") ? "未付款" : "已付款");
                    ((TextView) mView.findViewById(R.id.et_payamount)).setText(reservationJson.getString("payMoney"));

                    View commentView = mView.findViewById(R.id.btn_comment);
                    commentView.setVisibility(View.VISIBLE);
                    commentView.setEnabled("2".equals(reservation.reserStatus));
                    commentView.setOnClickListener(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                sendXmppChat(reservation.mbrID, reservation.mbrName, reservation.mbrHeadImg);
                break;
        }

    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(String response) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                getActivity().onBackPressed();
                break;
            case R.id.btn_consultation:
                if (reservation != null) {
                    showLoadingDialog("");
                    Map<String, String> params = new HashMap<>();
                    params.put("reserID", reservation.reserID);
                    NetworkPath path = new NetworkPath(URLAddr.URL_RESERVATION_COUNSELORSTART, params, getActivity());
                    getApplication().getNetworkManager().load(2, path, this);
                }
                break;
            case R.id.btn_comment:
                Bundle bundle = new Bundle();
                bundle.putString("reserID", reservation.reserID);
                bundle.putString("remark", reservation.remark);
                bundle.putString("conclusion", reservation.conclusion);
                MainActivity mainActivity = (MainActivity) getActivity();
                WriteCommentFragment fragment = new WriteCommentFragment();
                fragment.setArguments(bundle);
                mainActivity.controller.pushFragment(fragment);
                break;
        }
    }

    public void sendXmppChat(String userId, String name, String photo) {
        List<Staff> contactses = new ArrayList<Staff>();
        Staff staff = new Staff(userId, userId, name, photo);
        contactses.add(staff);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.xmpp.chat(contactses, null);
    }
}
