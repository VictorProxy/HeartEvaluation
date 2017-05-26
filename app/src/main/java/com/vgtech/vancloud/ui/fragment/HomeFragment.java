package com.vgtech.vancloud.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.android.volley.VolleyError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.inject.Inject;
import com.vgtech.common.URLAddr;
import com.vgtech.common.api.JsonDataFactory;
import com.vgtech.common.api.RootData;
import com.vgtech.common.network.NetworkPath;
import com.vgtech.common.network.android.HttpListener;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.api.ADInfo;
import com.vgtech.vancloud.api.Announcement;
import com.vgtech.vancloud.api.Counselor;
import com.vgtech.vancloud.api.Product;
import com.vgtech.vancloud.api.Scale;
import com.vgtech.vancloud.ui.BaseFragment;
import com.vgtech.vancloud.ui.ConsultationActivity;
import com.vgtech.vancloud.ui.CounselorInfoFragment;
import com.vgtech.vancloud.ui.MainActivity;
import com.vgtech.vancloud.ui.ProductInfoActivity;
import com.vgtech.vancloud.ui.ScaleInfoActivity;
import com.vgtech.vancloud.ui.SearchActivity;
import com.vgtech.vancloud.ui.WebActivity;
import com.vgtech.vancloud.ui.adapter.ProductAdapter;
import com.vgtech.vancloud.ui.adapter.ScaleAdapter;
import com.vgtech.vancloud.ui.chat.controllers.Controller;
import com.vgtech.vancloud.ui.view.NoScrollListview;
import com.vgtech.vancloud.ui.view.cycle.CycleViewPager;
import com.vgtech.vancloud.ui.view.cycle.ViewFactory;
import com.vgtech.vancloud.utils.ActivityUtils;
import com.vgtech.vancloud.utils.ImgUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import roboguice.RoboGuice;

/**
 * Created by vic on 2017/3/8.
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener, HttpListener<String>, AdapterView.OnItemClickListener {
    private FrameLayout bannerView;
    private List<ADInfo> mAdInfos;
    private List<View> views = new ArrayList<View>();
    private CycleViewPager cycleViewPager;
    private View mView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RoboGuice.getInjector(getActivity()).injectMembersWithoutViews(this);
    }

    @Override
    protected int initLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(View view) {
        mView = view;
        bannerView = (FrameLayout) view.findViewById(R.id.banner_view);
        initCycleView();
        view.findViewById(R.id.btn_icon_links_cp).setOnClickListener(this);
        view.findViewById(R.id.btn_icon_links_zx).setOnClickListener(this);
        view.findViewById(R.id.btn_icon_links_xy).setOnClickListener(this);
        view.findViewById(R.id.btn_icon_links_ly).setOnClickListener(this);
        view.findViewById(R.id.btn_search).setOnClickListener(this);
    }

    private void initCycleView() {
        cycleViewPager = new CycleViewPager(bannerView, getActivity());
    }

    private void initCycleView(List<ADInfo> adInfos) {
        if (!adInfos.isEmpty()) {
            bannerView.setVisibility(View.VISIBLE);
        }
        mAdInfos = adInfos;
        // 将最后一个ImageView添加进来
        views.clear();
        if (mAdInfos.size() > 1)
            views.add(ViewFactory.generaItemView(getActivity(), mAdInfos.get(mAdInfos.size() - 1), cycleViewPager));
        for (int i = 0; i < mAdInfos.size(); i++) {
            views.add(ViewFactory.generaItemView(getActivity(), mAdInfos.get(i), cycleViewPager));
        }
        // 将第一个ImageView添加进来
        if (mAdInfos.size() > 1)
            views.add(ViewFactory.generaItemView(getActivity(), mAdInfos.get(0), cycleViewPager));
        // 设置循环，在调用setData方法前调�?
        if (adInfos.size() > 1)
            cycleViewPager.setCycle(true);
        // 在加载数据前设置是否循环
        cycleViewPager.setData(views, mAdInfos, mAdCycleViewListener);
        //设置轮播
        if (adInfos.size() > 1)
            cycleViewPager.setWheel(true);
        // 设置轮播时间，默�?000ms
        if (adInfos.size() > 1)
            cycleViewPager.setTime(3000);
        //设置圆点指示图标组居中显示，默认靠右
        cycleViewPager.setIndicatorCenter();
    }

    private CycleViewPager.ImageCycleViewListener mAdCycleViewListener = new CycleViewPager.ImageCycleViewListener() {

        @Override
        public void onImageClick(ADInfo info, int position, View imageView) {
            position = position - 1;
            ADInfo adInfo = mAdInfos.get(position);
            Intent intent = new Intent(getActivity(), WebActivity.class);
            intent.putExtra("title", adInfo.adTitle);
            intent.setData(Uri.parse(adInfo.url));
            startActivity(intent);
        }

    };


    @Override
    protected void initData() {
        loadData();
    }

    private void loadData() {
        Map<String, String> params = new HashMap<>();
        NetworkPath path = new NetworkPath(URLAddr.URL_INDEX, params, getActivity());
        getApplication().getNetworkManager().load(1, path, this, true);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected boolean onBackPressed() {
        return false;
    }

    @Inject
    public Controller controller;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_more_cp:
                ((MainActivity) getActivity()).getTabPageIndicator().setCurrentTab(1);
                break;
            case R.id.btn_more_tj:
                ((MainActivity) getActivity()).getTabPageIndicator().setCurrentTab(3);
                break;
            case R.id.btn_search:
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.counselor_item1:
            case R.id.counselor_item2:
                Counselor counselor = (Counselor) v.getTag();
                CounselorInfoFragment fragment = new CounselorInfoFragment();
                Bundle bundle = new Bundle();
                bundle.putString("counselor", counselor.getJson().toString());
                fragment.setArguments(bundle);
                controller.pushFragment(fragment);
                break;
            case R.id.btn_icon_links_cp:
                ((MainActivity) getActivity()).getTabPageIndicator().setCurrentTab(1);
                break;
            case R.id.btn_icon_links_zx:
                ((MainActivity) getActivity()).getTabPageIndicator().setCurrentTab(2);
                break;
            case R.id.btn_icon_links_xy:
                ((MainActivity) getActivity()).getTabPageIndicator().setCurrentTab(3);
                break;
            case R.id.btn_icon_links_ly:
                ((MainActivity) getActivity()).getTabPageIndicator().setCurrentTab(3);
                break;
        }
    }

    @Override
    public void dataLoaded(int callbackId, NetworkPath path, RootData rootData) {
        boolean safe = ActivityUtils.prehandleNetworkData(getActivity(), rootData);
        if (!safe) {
            return;
        }
        try {
            JSONObject jsonObject = rootData.getJson().getJSONObject("data");

            List<ADInfo> adInfos = JsonDataFactory.getDataArray(ADInfo.class, jsonObject.getJSONArray("IndexADList"));
            initCycleView(adInfos);

            List<Counselor> counselors = JsonDataFactory.getDataArray(Counselor.class, jsonObject.getJSONArray("IndexCounselor"));
            initCounselorView(counselors);
            List<Scale> scales = JsonDataFactory.getDataArray(Scale.class, jsonObject.getJSONArray("ScaleList"));
            initScaleView(scales);
            List<Product> products = JsonDataFactory.getDataArray(Product.class, jsonObject.getJSONArray("ProductList"));
            initProductView(products);
            List<Announcement> announcements = JsonDataFactory.getDataArray(Announcement.class, jsonObject.getJSONArray("IndexAnnouncementList"));
            initAnnouncementView(announcements);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initProductView(List<Product> products) {
        if (!products.isEmpty()) {
            mView.findViewById(R.id.tj_title).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.btn_more_tj).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.btn_more_tj).setOnClickListener(this);
            NoScrollListview listView = (NoScrollListview) mView.findViewById(R.id.tj_list);
            listView.setVisibility(View.VISIBLE);
            listView.setItemClick(true);
            listView.setOnItemClickListener(this);
            ProductAdapter scaleAdapter = new ProductAdapter(getActivity());
            listView.setAdapter(scaleAdapter);
            scaleAdapter.addAllData(products);
        }
    }

    private void initScaleView(List<Scale> scales) {
        if (!scales.isEmpty()) {
            mView.findViewById(R.id.cp_title).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.btn_more_cp).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.btn_more_cp).setOnClickListener(this);
            NoScrollListview listView = (NoScrollListview) mView.findViewById(R.id.cp_list);
            listView.setItemClick(true);
            listView.setOnItemClickListener(this);
            listView.setVisibility(View.VISIBLE);
            ScaleAdapter scaleAdapter = new ScaleAdapter(getActivity());
            listView.setAdapter(scaleAdapter);
            scaleAdapter.addAllData(scales);
        }
    }

    private void initAnnouncementView(List<Announcement> announcements) {
        if (!announcements.isEmpty()) {
            mView.findViewById(R.id.counselor_tip_view).setVisibility(View.VISIBLE);
            ViewFlipper viewFlipper = (ViewFlipper) mView.findViewById(R.id.flipper_scrollTitle);
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            for (int i = 0; i < announcements.size(); i++) {
                Announcement a = announcements.get(i);
                View itemView = inflater.inflate(R.layout.announcement_item, null);
                TextView counselor_tip = (TextView) itemView.findViewById(R.id.counselor_tip);
                counselor_tip.setText(a.title);
                viewFlipper.addView(itemView);
            }
//            if (announcements.size() > 1) {
            viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_right_in));
            viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_left_out));
            viewFlipper.startFlipping();
//            }
        }
    }

    private void initCounselorView(List<Counselor> counselors) {
        if (!counselors.isEmpty()) {
            mView.findViewById(R.id.view_counselor).setVisibility(View.VISIBLE);
            for (int i = 0; i < counselors.size(); i++) {
                Counselor counselor = counselors.get(i);
                if (i == 0) {
                    SimpleDraweeView usericon = (SimpleDraweeView) mView.findViewById(R.id.usericon1);
                    ImgUtils.setUserImg(usericon, counselor.photoPath);
                    TextView nameTv = (TextView) mView.findViewById(R.id.tv_name1);
                    nameTv.setText(counselor.name);
                    RatingBar rating = (RatingBar) mView.findViewById(R.id.rating1);
                    rating.setRating(counselor.getDegree());
                    View counselorItem = mView.findViewById(R.id.counselor_item1);
                    counselorItem.setVisibility(View.VISIBLE);
                    counselorItem.setOnClickListener(this);
                    counselorItem.setTag(counselor);
                } else if (i == 1) {
                    SimpleDraweeView usericon = (SimpleDraweeView) mView.findViewById(R.id.usericon2);
                    ImgUtils.setUserImg(usericon, counselor.photoPath);
                    TextView nameTv = (TextView) mView.findViewById(R.id.tv_name2);
                    nameTv.setText(counselor.name);
                    RatingBar rating = (RatingBar) mView.findViewById(R.id.rating2);
                    rating.setRating(counselor.getDegree());
                    View counselorItem = mView.findViewById(R.id.counselor_item2);
                    counselorItem.setVisibility(View.VISIBLE);
                    counselorItem.setOnClickListener(this);
                    counselorItem.setTag(counselor);
                    break;
                }
            }
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(String response) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Object obj = parent.getItemAtPosition(position);
        if (obj instanceof Scale) {
            Scale scale = (Scale) obj;
            MainActivity mainActivity = (MainActivity) getActivity();
            ScaleInfoActivity fragment = new ScaleInfoActivity();
            Bundle bundle = new Bundle();
            bundle.putString("scale", scale.getJson().toString());
            fragment.setArguments(bundle);
            mainActivity.controller.pushFragment(fragment);
        } else if (obj instanceof Product) {
            Product product = (Product) obj;
            Intent intent = new Intent(getActivity(), ProductInfoActivity.class);
            intent.putExtra("productID", product.productID);
            startActivity(intent);
        }
    }
}
