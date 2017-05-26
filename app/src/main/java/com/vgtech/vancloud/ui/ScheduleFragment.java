package com.vgtech.vancloud.ui;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.vgtech.common.PrfUtils;
import com.vgtech.common.URLAddr;
import com.vgtech.common.api.JsonDataFactory;
import com.vgtech.common.api.RootData;
import com.vgtech.common.network.NetworkPath;
import com.vgtech.common.network.android.HttpListener;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.api.Counselor;
import com.vgtech.vancloud.api.Timer;
import com.vgtech.vancloud.api.WorkCalendar;
import com.vgtech.vancloud.ui.adapter.CalendarTitleGridAdapter;
import com.vgtech.vancloud.ui.adapter.TimerAdapter;
import com.vgtech.vancloud.ui.view.calendar.CalendarFragment;
import com.vgtech.vancloud.ui.view.calendar.OnDateSelectListener;
import com.vgtech.vancloud.utils.ActivityUtils;
import com.vgtech.vancloud.utils.CalendarUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by vic on 2017/5/11.
 */
public class ScheduleFragment extends BaseFragment implements HttpListener<String>, OnDateSelectListener, View.OnClickListener {
    @Override
    protected int initLayoutId() {
        return R.layout.fragment_schedule;
    }

    ScreenSlidePagerAdapter screenSlidePagerAdapter;
    ViewPager viewPager;
    private TextView tv_current_month;
    private TimerAdapter mTimerAdapter;
    private int mMonth;

    public View getHeadLayout() {
        View headerView = getActivity().getLayoutInflater().inflate(R.layout.schedule_header_layout, null);
        tv_current_month = (TextView) headerView.findViewById(R.id.tv_current_month);
        GridView titleGridView = (GridView) headerView.findViewById(R.id.title_gridview);
        titleGridView.setAdapter(new CalendarTitleGridAdapter(getActivity()));
        viewPager = (ViewPager) headerView.findViewById(R.id.viewpager);
        screenSlidePagerAdapter = new ScreenSlidePagerAdapter(
                getActivity().getSupportFragmentManager(), this);
        viewPager.setAdapter(screenSlidePagerAdapter);
        viewPager.setCurrentItem(500);
        tv_current_month.setText(Calendar.getInstance().get(
                Calendar.YEAR) + "年" + (Calendar.getInstance().get(
                Calendar.MONTH) + 1) + "月");
        mMonth = Calendar.getInstance().get(
                Calendar.MONTH) + 1;
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                final Calendar calendar = CalendarUtils.getSelectCalendar(position);
                tv_current_month.setText(calendar.get(
                        Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月");
                mMonth = calendar.get(
                        Calendar.MONTH) + 1;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int index = viewPager.getCurrentItem();
                        CalendarFragment calendarFragment = (CalendarFragment) screenSlidePagerAdapter.instantiateItem(viewPager, index);
                        calendarFragment.refresh();
                    }
                }, 500);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        return headerView;
    }


    @Override
    public void onSelected(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int m = calendar.get(
                Calendar.MONTH) + 1;
        if (m != mMonth || (date.getTime() < System.currentTimeMillis() - 24 * 60 * 60 * 1000)) {
            return;
        }
        loadTimerByDate(date);
    }

    private void loadTimerByDate(Date date) {
        mTimerAdapter.clear();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String counselorID = getArguments().getString("counselorID");
//        Map<String, String> params = new HashMap<>();
//        params.put("counselorID", counselorID);
//        params.put("reservationDate", format.format(date));
        Uri uri = Uri.parse(URLAddr.URL_RESERVATION_SELECTWORKSHIFTS).buildUpon()
                .appendQueryParameter("counselorID", counselor.counselorID)
                .appendQueryParameter("reservationDate", format.format(date))
                .appendQueryParameter("mbrID", PrfUtils.getMbrId(getActivity())).build();
        NetworkPath path = new NetworkPath(uri.toString());
        getApplication().getNetworkManager().load(1, path, this, true);
    }

    @Override
    public void onSelected(String date) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                getActivity().onBackPressed();
                break;
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private OnDateSelectListener dateSelectListener;

        public ScreenSlidePagerAdapter(FragmentManager fragmentManager, OnDateSelectListener listener) {
            super(fragmentManager);
            dateSelectListener = listener;
        }

        @Override
        public Fragment getItem(int position) {
            CalendarFragment calendarFragment = CalendarFragment.create(position);
            calendarFragment.setDateSelectListener(dateSelectListener);
            return calendarFragment;
        }

        @Override
        public int getCount() {
            return 1000;
        }
    }

    private ListView listView;
    private Counselor counselor;

    @Override
    protected void initView(View view) {
        String counselorStr = getArguments().getString("counselor");
        try {
            counselor = JsonDataFactory.getData(Counselor.class, new JSONObject(counselorStr));
        } catch (Exception e) {
            e.printStackTrace();
        }
        TextView titleTv = (TextView) view.findViewById(android.R.id.title);
        titleTv.setText("预约咨询");
        View backView = view.findViewById(R.id.btn_back);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(this);
        listView = (ListView) view.findViewById(android.R.id.list);
        View headerView = getHeadLayout();
        listView.addHeaderView(headerView);
        mTimerAdapter = new TimerAdapter(getActivity(), new WorkCalendarClickListener() {

            @Override
            public void onWorkCalendarClick(WorkCalendar workCalendar) {
                MainActivity mainActivity = (MainActivity) getActivity();
                ConsultationActivity fragment = new ConsultationActivity();
                Bundle bundle = new Bundle();
                bundle.putString("counselor",counselor.getJson().toString());
                bundle.putString("workCalendar",workCalendar.getJson().toString());
                fragment.setArguments(bundle);
                mainActivity.controller.pushFragment(fragment);
            }
        });
        listView.setAdapter(mTimerAdapter);
        int index = viewPager.getCurrentItem();
        CalendarFragment calendarFragment = (CalendarFragment) screenSlidePagerAdapter.instantiateItem(viewPager, index);
        calendarFragment.refresh();
        loadTimerByDate(new Date());
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
        try {
            mTimerAdapter.clear();
            List<WorkCalendar> list = JsonDataFactory.getDataArray(WorkCalendar.class, rootData.getJson().getJSONObject("data").getJSONArray("workCalendarList"));
            List<Timer> timers = new ArrayList<>();
            Timer timer = null;
            for (int i = 0; i < list.size(); i++) {
                WorkCalendar workCalendar = list.get(i);
                if (i % 2 == 0) {
                    timer = new Timer();
                    timer.setFisrtTime(workCalendar);
                    timers.add(timer);
                } else {
                    timer.setSecondTime(workCalendar);
                }
            }
            mTimerAdapter.addAllData(timers);
            mTimerAdapter.notifyDataSetChanged();
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

    public interface WorkCalendarClickListener {
        void onWorkCalendarClick(WorkCalendar workCalendar);
    }
}
