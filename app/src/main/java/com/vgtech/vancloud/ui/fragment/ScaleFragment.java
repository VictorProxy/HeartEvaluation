package com.vgtech.vancloud.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.vgtech.common.URLAddr;
import com.vgtech.common.adapter.TabViewPagerAdapter;
import com.vgtech.common.api.JsonDataFactory;
import com.vgtech.common.api.RootData;
import com.vgtech.common.network.NetworkPath;
import com.vgtech.common.network.android.HttpListener;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.api.ScaleType;
import com.vgtech.vancloud.ui.BaseFragment;
import com.vgtech.vancloud.ui.adapter.ViewPagerAdapter;
import com.vgtech.vancloud.ui.view.ViewPagerIndicator;
import com.vgtech.vancloud.utils.ActivityUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vic on 2017/3/9.
 */
public class ScaleFragment extends BaseFragment implements HttpListener<String> {
    private ViewPager mVpger;
    private ViewPagerIndicator mIndicator;
    private List<String> mTabs;
    private TabViewPagerAdapter mPagerAdapter;
    private List<Fragment> mDatas;

    @Override
    protected int initLayoutId() {
        return R.layout.fragment_scale;
    }

    @Override
    protected void initView(View view) {
        TextView titleTv = (TextView) view.findViewById(android.R.id.title);
        titleTv.setText("测评列表");
        mIndicator = (ViewPagerIndicator) view.findViewById(R.id.indicator_vp);
        mVpger = (ViewPager) view.findViewById(R.id.vp_menu);

    }

    @Override
    protected void initData() {
        mTabs = new ArrayList<>();
        mDatas = new ArrayList<>();

        Map<String, String> params = new HashMap<>();
        NetworkPath path = new NetworkPath(URLAddr.URL_SCALE_SCALETYPELIST, params, getActivity());
        getApplication().getNetworkManager().load(1, path, this);

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
        try {
            JSONObject jsonObject = rootData.getJson().getJSONObject("data");
            List<ScaleType> scaleTypes = JsonDataFactory.getDataArray(ScaleType.class, jsonObject.getJSONArray("scaleTypeList"));
            for (ScaleType scaleType : scaleTypes) {
                mTabs.add(scaleType.name);
                ScaleListFragment fragment = new ScaleListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("categoryID",scaleType.categoryID);
                fragment.setArguments(bundle);
                mDatas.add(fragment);
            }
            mIndicator.setTabItemTitles(mTabs);
            mIndicator.setVisibility(View.VISIBLE);
            mPagerAdapter = new TabViewPagerAdapter(getActivity().getSupportFragmentManager(), mDatas);
            mVpger.setAdapter(mPagerAdapter);
            mIndicator.setViewPager(mVpger, 0);

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
}
