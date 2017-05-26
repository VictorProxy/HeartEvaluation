package com.vgtech.vancloud.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.vgtech.common.PrfUtils;
import com.vgtech.common.URLAddr;
import com.vgtech.common.api.JsonDataFactory;
import com.vgtech.common.api.RootData;
import com.vgtech.common.network.NetworkPath;
import com.vgtech.common.network.android.HttpListener;
import com.vgtech.common.view.AlertDialog;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.api.Scale;
import com.vgtech.vancloud.api.ScaleInfo;
import com.vgtech.vancloud.models.Staff;
import com.vgtech.vancloud.utils.ActivityUtils;
import com.vgtech.vancloud.utils.ToastUtils;
import com.vgtech.vancloud.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vic on 2017/3/11.
 */
public class ScaleInfoActivity extends BaseFragment implements HttpListener<String>, View.OnClickListener {
    private Scale scale;
    private View mView;

    @Override
    protected void initView(View view) {
        mView = view;
        String scaleStr = getArguments().getString("scale");
        try {
            TextView titleTv = (TextView) view.findViewById(android.R.id.title);
            titleTv.setText("测评详情");
            view.findViewById(R.id.btn_back).setVisibility(View.VISIBLE);
            view.findViewById(R.id.btn_back).setOnClickListener(this);
            scale = JsonDataFactory.getData(Scale.class, new JSONObject(scaleStr));
//            scale.scaleID = "140";
            Map<String, String> params = new HashMap<>();
            params.put("scaleID", scale.scaleID);
            NetworkPath path = new NetworkPath(URLAddr.URL_SCALE_DETAIL, params, getActivity());
            getApplication().getNetworkManager().load(1, path, this, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            case R.id.btn_fav:
                doAttention(scaleInfo.scaleID, scaleInfo.isMyPraise == 0);
                break;
            case R.id.btn_ask:
                //TODO
//                sendXmppChat(counselor.mbrID, counselor.name, counselor.photoPath);
                break;
            case R.id.btn_test:
                if ("0".equals(scaleInfo.isOpen)) {
                    new AlertDialog(getActivity()).builder().setTitle("提示")
                            .setMsg("该量表需要预约")
                            .setPositiveButton("预约", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    yuyue();
                                }
                            }).setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
                } else {
                    testBegain();
                }
                break;
            case R.id.btn_back:
                getActivity().onBackPressed();
                break;
        }
    }

    private void yuyue() {
        Map<String, String> params = new HashMap<>();
        params.put("mbrID", PrfUtils.getMbrId(getActivity()));
        params.put("scaleID", scaleInfo.scaleID);
        NetworkPath path = new NetworkPath(URLAddr.URL_RESERVATION_TESTRESERVATIONSAVE, params, getActivity());
        getApplication().getNetworkManager().load(5, path, this);
    }

    public void sendXmppChat(String userId, String name, String photo) {
        List<Staff> contactses = new ArrayList<Staff>();
        Staff staff = new Staff(userId, userId, name, photo);
        contactses.add(staff);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.xmpp.chat(contactses, null);
    }

    private void doAttention(String counselorID, boolean isAtt) {
        Map<String, String> params = new HashMap<>();
        params.put("mbrID", PrfUtils.getMbrId(getActivity()));
        params.put("objectID", counselorID);
        params.put("objectType", "SCALE");
        params.put("doType", isAtt ? "ATTENTION" : "CANCEL");
        NetworkPath path = new NetworkPath(URLAddr.URL_ATTENTION_DOATTENTION, params, getActivity());
        getApplication().getNetworkManager().load(3, path, this);
    }

    private void testBegain() {
        showLoadingDialog("");
        Map<String, String> params = new HashMap<>();
        params.put("mbrID", PrfUtils.getMbrId(getActivity()));
        params.put("scaleID", scale.scaleID);
        NetworkPath path = new NetworkPath(URLAddr.URL_TEST_TESTBEGIN, params, getActivity());
        getApplication().getNetworkManager().load(2, path, this);
    }

    @Override
    protected int initLayoutId() {
        return R.layout.activity_scaleinfo;
    }

    private ScaleInfo scaleInfo;

    @Override
    public void dataLoaded(int callbackId, NetworkPath path, RootData rootData) {
        dismisLoadingDialog();
        boolean safe = ActivityUtils.prehandleNetworkData(getActivity(), rootData);
        if (!safe) {
            return;
        }
        try {
            switch (callbackId) {
                case 1:
                    JSONObject jsonObject = rootData.getJson().getJSONObject("data");
                    scaleInfo = JsonDataFactory.getData(ScaleInfo.class, jsonObject.getJSONObject("scale"));
                    SimpleDraweeView imageView = (SimpleDraweeView) mView.findViewById(R.id.background);
                    imageView.setImageURI(scaleInfo.picPath);
                    TextView tv_subtitle = (TextView) mView.findViewById(R.id.tv_subtitle);
                    tv_subtitle.setText(scaleInfo.name);
                    TextView tv_type = (TextView) mView.findViewById(R.id.tv_type);
                    tv_type.setText(scaleInfo.categoryID);
                    TextView tv_count = (TextView) mView.findViewById(R.id.tv_count);
                    tv_count.setText("" + scaleInfo.questionNum);
                    TextView tv_time = (TextView) mView.findViewById(R.id.tv_time);
                    tv_time.setText(scaleInfo.testTime + "分钟");
                    TextView tv_desc = (TextView) mView.findViewById(R.id.tv_desc);
                    tv_desc.setText(scaleInfo.memo);
                    mView.findViewById(R.id.btn_fav).setOnClickListener(this);
                    mView.findViewById(R.id.btn_ask).setOnClickListener(this);
                    mView.findViewById(R.id.btn_test).setOnClickListener(this);
                    mView.findViewById(R.id.view_action).setVisibility(View.VISIBLE);
                    mView.findViewById(R.id.view_content).setVisibility(View.VISIBLE);
                    break;
                case 2:
                    Intent intent = new Intent(getActivity(), QuestionInfoActivity.class);
                    intent.putExtra("testBegin", rootData.getJson().toString());
                    startActivity(intent);
                    break;
                case 5:
                    ToastUtils.show(getActivity(),rootData.getMsg());
                    MainActivity mainActivity = (MainActivity) getActivity();
                    MyScaleListActivity fragment = new MyScaleListActivity();
                    Bundle bundle = new Bundle();
                    bundle.putString("from","scaleInfo");
                    fragment.setArguments(bundle);
                    mainActivity.controller.pushFragment(fragment);
                    break;
            }
        } catch (Exception e) {
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
