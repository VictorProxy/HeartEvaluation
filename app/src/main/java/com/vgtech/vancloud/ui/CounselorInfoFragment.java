package com.vgtech.vancloud.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.vgtech.common.PrfUtils;
import com.vgtech.common.URLAddr;
import com.vgtech.common.api.JsonDataFactory;
import com.vgtech.common.api.RootData;
import com.vgtech.common.network.NetworkPath;
import com.vgtech.common.network.android.HttpListener;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.api.Counselor;
import com.vgtech.vancloud.models.Staff;
import com.vgtech.vancloud.utils.ActivityUtils;
import com.vgtech.vancloud.utils.ToastUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vic on 2017/3/11.
 */
public class CounselorInfoFragment extends BaseFragment implements View.OnClickListener, HttpListener<String> {
    @Override
    protected int initLayoutId() {
        return R.layout.fragment_counselor;
    }

    private Counselor counselor;
    private View mView;
    private View mLikeIv;
    private View iv_gz;
    private TextView tv_ask_price;
    @Override
    protected void initView(View view) {
        try {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            mView = view;
            View backView = view.findViewById(R.id.btn_back);
            backView.setVisibility(View.VISIBLE);
            backView.setOnClickListener(this);
            TextView titleTv = (TextView) view.findViewById(android.R.id.title);
            titleTv.setText("咨询中心");
            String jsonStr = getArguments().getString("counselor");
            counselor = JsonDataFactory.getData(Counselor.class, new JSONObject(jsonStr));
            SimpleDraweeView userIcon = (SimpleDraweeView) view.findViewById(R.id.iv_icon);
            userIcon.setImageURI(counselor.photoPath);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_name.setText(counselor.name);
            TextView tv_job = (TextView) view.findViewById(R.id.tv_job);
            tv_job.setText(counselor.getLevel());
            RatingBar rating = (RatingBar) view.findViewById(R.id.rating);
            rating.setRating(counselor.getDegree());
            TextView tv_desc = (TextView) view.findViewById(R.id.tv_desc);
            tv_desc.setText(counselor.introduction);
            view.findViewById(R.id.btn_comment).setOnClickListener(this);
            view.findViewById(R.id.btn_consultation).setOnClickListener(this);
            view.findViewById(R.id.btn_att).setOnClickListener(this);
            view.findViewById(R.id.btn_like).setOnClickListener(this);
            view.findViewById(R.id.btn_ask_service).setOnClickListener(this);
            mLikeIv = view.findViewById(R.id.iv_like);
            iv_gz = view.findViewById(R.id.iv_gz);
            tv_ask_price = (TextView) view.findViewById(R.id.tv_ask_price);
            loadCounselorInfo(counselor.counselorID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCounselorInfo(String counselorID) {
        Map<String, String> params = new HashMap<>();
        params.put("counselorID", counselorID);
        NetworkPath path = new NetworkPath(URLAddr.URL_COUNSELOR_DETAIL, params, getActivity());
        getApplication().getNetworkManager().load(1, path, this, true);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ask_service: {
                MainActivity mainActivity = (MainActivity) getActivity();
                ConsultationActivity fragment = new ConsultationActivity();
                Bundle bundle = new Bundle();
                bundle.putString("counselor",counselor.getJson().toString());
                fragment.setArguments(bundle);
                mainActivity.controller.pushFragment(fragment);
            }
            break;
            case R.id.btn_back:
                getActivity().onBackPressed();
                break;
            case R.id.btn_comment:
                sendXmppChat(counselor.mbrID, counselor.name, counselor.photoPath);
                break;
            case R.id.btn_consultation:
                MainActivity mainActivity = (MainActivity) getActivity();
                ScheduleFragment fragment = new ScheduleFragment();
                Bundle bundle = new Bundle();
                bundle.putString("counselor", counselor.getJson().toString());
                fragment.setArguments(bundle);
                mainActivity.controller.pushFragment(fragment);
                break;
            case R.id.btn_like:
                doPraise(counselor.mbrID, counselor.isMyPraise == 0);
                break;
            case R.id.btn_att:
                doAttention(counselor.mbrID, counselor.isMyPraise == 0);
                break;

        }
    }
    private void doPraise(String counselorID, boolean isLike) {
        Map<String, String> params = new HashMap<>();
        params.put("mbrID", PrfUtils.getMbrId(getActivity()));
        params.put("counselorID", counselorID);
        params.put("doType", isLike ? "PRAISE" : "CANCEL");
        NetworkPath path = new NetworkPath(URLAddr.URL_COUNSELOR_DOPRAISE, params, getActivity());
        getApplication().getNetworkManager().load(2, path, this);
    }

    private void doAttention(String counselorID, boolean isAtt) {
        Map<String, String> params = new HashMap<>();
        params.put("mbrID", PrfUtils.getMbrId(getActivity()));
        params.put("objectID", counselorID);
        params.put("objectType", "COUNSELOR");
        params.put("doType", isAtt ? "ATTENTION" : "CANCEL");
        NetworkPath path = new NetworkPath(URLAddr.URL_ATTENTION_DOATTENTION, params, getActivity());
        getApplication().getNetworkManager().load(3, path, this);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    public void sendXmppChat(String userId, String name, String photo) {
//        userId = "1003";
//        userId = "1013";
//        userId = "613578361146445824735893790648176640";
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
        switch (callbackId) {
            case 1:
                try {
                    counselor = JsonDataFactory.getData(Counselor.class, rootData.getJson().getJSONObject("data").getJSONObject("counselor"));
                    SimpleDraweeView userIcon = (SimpleDraweeView) mView.findViewById(R.id.iv_icon);
                    userIcon.setImageURI(counselor.photoPath);
                    TextView tv_name = (TextView) mView.findViewById(R.id.tv_name);
                    tv_name.setText(counselor.name);
                    TextView tv_job = (TextView) mView.findViewById(R.id.tv_job);
                    tv_job.setText(counselor.getLevel());
                    RatingBar rating = (RatingBar) mView.findViewById(R.id.rating);
                    rating.setRating(counselor.getDegree());
                    TextView tv_desc = (TextView) mView.findViewById(R.id.tv_desc);
                    tv_desc.setText(counselor.introduction);
                    TextView tv_certification = (TextView) mView.findViewById(R.id.tv_certification);
                    TextView tv_real_name = (TextView) mView.findViewById(R.id.tv_real_name);
                    TextView tv_ask_count = (TextView) mView.findViewById(R.id.tv_ask_count);
                    TextView tv_goods_count = (TextView) mView.findViewById(R.id.tv_goods_count);
                    if ("1".equals(counselor.isCertification)) {
                        tv_certification.setText("资质已认证");
                    } else {
                        tv_certification.setText("资质未认证");
                    }
                    if ("1".equals(counselor.isRealName)) {
                        tv_real_name.setText("实名已认证");
                    } else {
                        tv_real_name.setText("实名未认证");
                    }
                    mLikeIv.setSelected(counselor.isMyPraise != 0);
                    iv_gz.setSelected(counselor.isAttention != 0);
                    tv_ask_count.setText(counselor.consultationTimes + "人咨询过");
                    tv_goods_count.setText("获" + counselor.praiseTimes + "赞");
                    tv_ask_price.setText("￥"+counselor.onlinePrice+"(50分钟/次)");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                if ("ATTENTION".equals(path.getPostValues().get("doType"))) {
                    counselor.isAttention = 1;
                } else {
                    counselor.isAttention = 0;
                }
                iv_gz.setSelected(counselor.isAttention != 0);
                ToastUtils.show(getActivity(), rootData.getMsg());
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
