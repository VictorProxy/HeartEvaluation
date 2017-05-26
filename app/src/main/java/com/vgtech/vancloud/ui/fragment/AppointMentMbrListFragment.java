package com.vgtech.vancloud.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.vgtech.common.PrfUtils;
import com.vgtech.common.URLAddr;
import com.vgtech.common.api.JsonDataFactory;
import com.vgtech.common.api.RootData;
import com.vgtech.common.network.NetworkPath;
import com.vgtech.common.network.android.HttpListener;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.api.Reservation;
import com.vgtech.vancloud.api.ReservationMbr;
import com.vgtech.vancloud.ui.BaseFragment;
import com.vgtech.vancloud.ui.MainActivity;
import com.vgtech.vancloud.ui.ReservationItemActivity;
import com.vgtech.vancloud.ui.ReservationMbrItemActivity;
import com.vgtech.vancloud.ui.adapter.ReservationAdapter;
import com.vgtech.vancloud.ui.adapter.ReservationMbrAdapter;
import com.vgtech.vancloud.utils.ActivityUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vic on 2017/3/9.
 */
public class AppointMentMbrListFragment extends BaseFragment implements HttpListener<String>, PullToRefreshBase.OnRefreshListener2, AdapterView.OnItemClickListener {
    @Override
    protected int initLayoutId() {
        return R.layout.fragment_scalelist;
    }

    private ReservationMbrAdapter scaleAdapter;
    PullToRefreshListView mListView;

    @Override
    protected void initView(View view) {
        mListView = (PullToRefreshListView) view.findViewById(R.id.cp_list);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        mListView.setOnRefreshListener(this);
        mListView.setClickable(true);
        mListView.setOnItemClickListener(this);
        scaleAdapter = new ReservationMbrAdapter(getActivity());
        mListView.setAdapter(scaleAdapter);
    }

    private String showType;

    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            showType = bundle.getString("showType");
            loadData(mNextId);
        }
    }

    private void loadData(int nextId) {
        Map<String, String> params = new HashMap<>();
        params.put("showType", showType);
        params.put("currentPage", String.valueOf(nextId));
        params.put("showCount", "8");
        NetworkPath path = new NetworkPath(URLAddr.URL_RESERVATION_COUNRESERVATIONLIST4COUNSELOR, params, getActivity());
        getApplication().getNetworkManager().load(1, path, this, true);
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
            List<ReservationMbr> scales = JsonDataFactory.getDataArray(ReservationMbr.class, jsonObject.getJSONArray("reservationList"));
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object obj = parent.getItemAtPosition(position);
        if (obj instanceof ReservationMbr) {
            ReservationMbr reservation = (ReservationMbr) obj;
            Bundle bundle = new Bundle();
            bundle.putString("reserID", reservation.reserID);
            MainActivity mainActivity = (MainActivity) getActivity();
            Fragment fragment = new ReservationMbrItemActivity();
            fragment.setArguments(bundle);
            mainActivity.controller.pushFragment(fragment);
        }
    }
}
