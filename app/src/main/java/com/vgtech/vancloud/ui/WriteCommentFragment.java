package com.vgtech.vancloud.ui;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.vgtech.common.URLAddr;
import com.vgtech.common.api.RootData;
import com.vgtech.common.network.NetworkPath;
import com.vgtech.common.network.android.HttpListener;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.utils.ActivityUtils;
import com.vgtech.vancloud.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vic on 2017/5/21.
 */
public class WriteCommentFragment extends BaseFragment implements HttpListener<String> {
    @Override
    protected int initLayoutId() {
        return R.layout.fragment_writecomment;
    }

    private View mView;

    @Override
    protected void initView(View view) {
        mView = view;
        View backView = view.findViewById(R.id.btn_back);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener((v) -> getActivity().onBackPressed());
        TextView titleTv = (TextView) view.findViewById(android.R.id.title);
        titleTv.setText("评论结论");
        view.findViewById(R.id.btn_save).setOnClickListener((v) -> doSubmit(0));
        view.findViewById(R.id.btn_submit).setOnClickListener((v) -> doSubmit(1));

        String remark = getArguments().getString("remark");
        String conclusion = getArguments().getString("conclusion");
        TextView et_pinglun = (TextView) mView.findViewById(R.id.et_pinglun);
        TextView et_jielun = (TextView) mView.findViewById(R.id.et_jielun);
        et_pinglun.setText(remark);
        et_jielun.setText(conclusion);
    }

    private void doSubmit(int updateType) {
        TextView et_pinglun = (TextView) mView.findViewById(R.id.et_pinglun);
        TextView et_jielun = (TextView) mView.findViewById(R.id.et_jielun);
        Map<String, String> params = new HashMap<>();
        String reserID = getArguments().getString("reserID");
        params.put("reserID", reserID);
        params.put("updateType", updateType + "");
        params.put("remark", et_pinglun.getText().toString());
        params.put("conclusion", et_jielun.getText().toString());
        NetworkPath path = new NetworkPath(URLAddr.URL_RESERVATION_COUNSELORSAVERESERVATIONINFO, params, getActivity());
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

    @Override
    public void dataLoaded(int callbackId, NetworkPath path, RootData rootData) {
        boolean safe = ActivityUtils.prehandleNetworkData(getActivity(), rootData);
        if (!safe) {
            return;
        }
        ToastUtils.show(getActivity(), rootData.getMsg());
        getActivity().sendBroadcast(new Intent("submit_comment"));
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(String response) {

    }
}
