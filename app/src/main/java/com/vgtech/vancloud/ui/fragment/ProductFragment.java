package com.vgtech.vancloud.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.vgtech.common.URLAddr;
import com.vgtech.common.adapter.TabViewPagerAdapter;
import com.vgtech.common.api.RootData;
import com.vgtech.common.network.NetworkPath;
import com.vgtech.common.network.android.HttpListener;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.VanCloudApplication;
import com.vgtech.vancloud.event.KeywordEvent;
import com.vgtech.vancloud.ui.BaseFragment;
import com.vgtech.vancloud.ui.view.ViewPagerIndicator;
import com.vgtech.vancloud.utils.ActivityUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by vic on 2017/3/9.
 */
public class ProductFragment extends BaseFragment implements HttpListener<String> {
    private ViewPager mVpger;
    private ViewPagerIndicator mIndicator;
    private List<String> mTabs;
    private TabViewPagerAdapter mPagerAdapter;
    private List<Fragment> mDatas;

    @Override
    protected int initLayoutId() {
        return R.layout.fragment_product;
    }

    @Override
    protected void initView(View view) {
        TextView titleTv = (TextView) view.findViewById(android.R.id.title);
        titleTv.setText("心理训练");
        mIndicator = (ViewPagerIndicator) view.findViewById(R.id.indicator_vp);
        mVpger = (ViewPager) view.findViewById(R.id.vp_menu);
        EditText et_search = (EditText) view.findViewById(R.id.et_search);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                EventBus.getDefault().post(
                        new KeywordEvent(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void getProductType() {
        VanCloudApplication application = getApplication();
        Map<String, String> params = new HashMap<>();
        NetworkPath path = new NetworkPath(URLAddr.URL_PRODUCT_PRODUCTCATEGORYLIST, params, getActivity());
        application.getNetworkManager().load(2, path, this);
    }

    @Override
    protected void initData() {
        getProductType();
    }

    private ProductListFragment generaFragment(String categoryID, String fileType) {
        ProductListFragment fragment = new ProductListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("categoryID", categoryID);
        bundle.putString("fileType", fileType);
        fragment.setArguments(bundle);
        return fragment;
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
            JSONArray jsonArray = rootData.getJson().getJSONObject("data").getJSONArray("categoryList");
            mTabs = new ArrayList<>();
            mDatas = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String categoryName = jsonObject.getString("categoryName");
                String categoryID = jsonObject.getString("categoryID");
                String fileType = jsonObject.getString("fileType");
                mTabs.add(categoryName);
                mDatas.add(generaFragment(categoryID, fileType));
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
