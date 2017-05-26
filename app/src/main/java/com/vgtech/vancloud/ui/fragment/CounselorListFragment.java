package com.vgtech.vancloud.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.VolleyError;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.vgtech.common.URLAddr;
import com.vgtech.common.api.JsonDataFactory;
import com.vgtech.common.api.RootData;
import com.vgtech.common.network.NetworkPath;
import com.vgtech.common.network.android.HttpListener;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.api.Counselor;
import com.vgtech.vancloud.ui.BaseFragment;
import com.vgtech.vancloud.ui.CounselorInfoFragment;
import com.vgtech.vancloud.ui.adapter.CounselorAdapter;
import com.vgtech.vancloud.ui.chat.controllers.Controller;
import com.vgtech.vancloud.utils.ActivityUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboguice.RoboGuice;

/**
 * Created by vic on 2017/3/11.
 */
public class CounselorListFragment extends BaseFragment implements HttpListener<String>, PullToRefreshBase.OnRefreshListener2, AdapterView.OnItemClickListener {

    private CounselorAdapter scaleAdapter;
    PullToRefreshListView mListView;

    @Override
    protected int initLayoutId() {
        return R.layout.fragment_counselorlist;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RoboGuice.getInjector(getActivity()).injectMembersWithoutViews(this);
    }

    @Override
    protected void initView(View view) {
        mListView = (PullToRefreshListView) view.findViewById(R.id.cp_list);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        mListView.setOnRefreshListener(this);
        mListView.setClickable(false);
        mListView.setPressed(false);
        mListView.setOnItemClickListener(this);
        scaleAdapter = new CounselorAdapter(getActivity());
        mListView.setAdapter(scaleAdapter);
    }

    private void loadData(int nextId) {
        Map<String, String> params = new HashMap<>();
        params.put("currentPage", String.valueOf(nextId));
        params.put("showCount", "12");
        NetworkPath path = new NetworkPath(URLAddr.URL_COUNSELOR_INDEX, params, getActivity());
        getApplication().getNetworkManager().load(1, path, this, true);
    }

    @Override
    protected void initData() {
        loadData(mNextId);
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
        mListView.onRefreshComplete();
        if (!safe) {
            return;
        }
        try {
            JSONObject jsonObject = rootData.getJson().getJSONObject("data");
            List<Counselor> scales = JsonDataFactory.getDataArray(Counselor.class, jsonObject.getJSONArray("IndexCounselor"));
            String currentPage = path.getPostValues().get("currentPage");
            if ("1".equals(currentPage))
                scaleAdapter.clear();
            scaleAdapter.addAllDataAndNorify(scales);
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

    private final int FLAG_RELOAD = 0X002;
    private final int FLAG_LOAD_ADD = 0X003;
    private int mNextId = 1;
    private int mFlag = FLAG_RELOAD;

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mFlag = FLAG_RELOAD;
        mNextId = 1;
        loadData(mNextId);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        mFlag = FLAG_LOAD_ADD;
        mNextId += 1;
        loadData(mNextId);
    }

    @Inject
    public Controller controller;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object obj = parent.getItemAtPosition(position);
        if (obj instanceof Counselor) {
            Counselor counselor = (Counselor) obj;
            CounselorInfoFragment fragment = new CounselorInfoFragment();
            Bundle bundle = new Bundle();
            bundle.putString("counselor", counselor.getJson().toString());
            fragment.setArguments(bundle);
            controller.pushFragment(fragment);
        }
    }
}
