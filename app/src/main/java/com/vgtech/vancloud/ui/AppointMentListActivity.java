package com.vgtech.vancloud.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.vgtech.common.PrfUtils;
import com.vgtech.common.adapter.TabViewPagerAdapter;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.ui.fragment.AppointMentListFragment;
import com.vgtech.vancloud.ui.fragment.AppointMentMbrListFragment;
import com.vgtech.vancloud.ui.view.ViewPagerIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vic on 2017/5/13.
 */
public class AppointMentListActivity extends BaseFragment implements View.OnClickListener{
    private ViewPager mVpger;
    private ViewPagerIndicator mIndicator;
    private List<String> mTabs;
    private TabViewPagerAdapter mPagerAdapter;
    private List<Fragment> mDatas;
    @Override
    protected int initLayoutId() {
        return R.layout.activity_appointmentlist;
    }

    @Override
    protected void initView(View view) {
        TextView titleTv = (TextView) view.findViewById(android.R.id.title);
        titleTv.setText("我的预约");
        View backView = view.findViewById(R.id.btn_back);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(this);
        mIndicator = (ViewPagerIndicator) view.findViewById(R.id.indicator_vp);
        mVpger = (ViewPager) view.findViewById(R.id.vp_menu);
        initData();
    }

    protected void initData() {
        mTabs = new ArrayList<>();
        mDatas = new ArrayList<>();

        mTabs.add("最新");
        mTabs.add("进行中");
        mTabs.add("已完成");
        mDatas.add(generaFragment("0"));
        mDatas.add(generaFragment("1"));
        mDatas.add(generaFragment("2"));
        mIndicator.setTabItemTitles(mTabs);
        mIndicator.setVisibility(View.VISIBLE);
        mPagerAdapter = new TabViewPagerAdapter(getActivity().getSupportFragmentManager(), mDatas);
        mVpger.setAdapter(mPagerAdapter);
        mIndicator.setViewPager(mVpger, 0);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected boolean onBackPressed() {
        return false;
    }

    private Fragment generaFragment(String categoryID) {
        Fragment fragment = null;
        if("1".equals(PrfUtils.getPrfparams(getActivity(),PrfUtils.MBRTYPE)))
        {
            fragment  = new AppointMentMbrListFragment();
        }else {
            fragment  = new AppointMentListFragment();
        }

        Bundle bundle = new Bundle();
        bundle.putString("showType", categoryID);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                getActivity().onBackPressed();
                break;
        }
    }
}
