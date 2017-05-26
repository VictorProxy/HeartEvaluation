package com.vgtech.vancloud.ui.view.calendar;

import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vgtech.vancloud.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarGridViewAdapter extends BaseAdapter {

    private Calendar calStartDate = Calendar.getInstance();// 当前显示的日历
    private Calendar calToday = Calendar.getInstance(); // 今日
    private int iMonthViewCurrentMonth = 0; // 当前视图月
    private Date selectDate = null;


    // 根据改变的日期更新日历
    // 填充日历控件用
    private void UpdateStartDateForMonth() {
        calStartDate.set(Calendar.DATE, 1); // 设置成当月第一天
        iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);// 得到当前日历显示的月

        // 星期一是2 星期天是1 填充剩余天数
        int iDay = 0;
        int iFirstDayOfWeek = Calendar.MONDAY;
        int iStartDay = iFirstDayOfWeek;
        if (iStartDay == Calendar.MONDAY) {
            iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
            if (iDay < 0)
                iDay = 6;
        }
        if (iStartDay == Calendar.SUNDAY) {
            iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
            if (iDay < 0)
                iDay = 6;
        }
        calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);
        calStartDate.add(Calendar.DAY_OF_MONTH, -1);// 周日第一位

    }

    ArrayList<Date> titles;

    private ArrayList<Date> getDates() {

        UpdateStartDateForMonth();

        ArrayList<Date> alArrayList = new ArrayList<Date>();

        for (int i = 1; i <= 42; i++) {
            alArrayList.add(calStartDate.getTime());
            calStartDate.add(Calendar.DAY_OF_MONTH, 1);
        }

        return alArrayList;
    }

    private Activity activity;
    Resources resources;

    // construct
    public CalendarGridViewAdapter(Activity a, Calendar cal) {
        calStartDate = cal;
        activity = a;
        resources = activity.getResources();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                new AsyncTask<Void, Void, ArrayList<Date>>() {
                    @Override
                    protected ArrayList<Date> doInBackground(Void... params) {
                        return getDates();
                    }

                    @Override
                    protected void onPostExecute(ArrayList<Date> dates) {
                        titles = dates;
                        notifyDataSetChanged();
                    }
                }.execute();
            }
        });

        selectDate = null;
    }

    @Override
    public int getCount() {
        return titles == null ? 0 : titles.size();
    }

    @Override
    public Object getItem(int position) {
        return titles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.calendar_item_layout,
                    null);
            holder = new ViewHolder();
            holder.dayTextView = (TextView) convertView.findViewById(R.id.day_text);
            holder.reddotView = convertView.findViewById(R.id.red_dot);
            holder.clickViewBg = convertView.findViewById(R.id.click_view);
            convertView.setTag(R.id.day_text, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.id.day_text);
        }
        Date myDate = (Date) getItem(position);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        String str = format.format(myDate);
        Calendar calCalendar = Calendar.getInstance();
        calCalendar.setTime(myDate);

        final int iMonth = calCalendar.get(Calendar.MONTH);
        final int iDay = calCalendar.get(Calendar.DAY_OF_WEEK);

        if (iMonth == iMonthViewCurrentMonth) {
            holder.clickViewBg.setVisibility(View.VISIBLE);
            holder.dayTextView.setTextColor(resources.getColor(R.color.text_color));
            if (selectDate != null && myDate.equals(selectDate) || selectDate == null && equalsDate(calToday.getTime(), myDate)) {
                holder.clickViewBg.setBackgroundResource(R.drawable.today_ground);
                holder.dayTextView.setTextColor(resources.getColor(R.color.white));
            } else {
                if (isExistData(myDate)) {
                    holder.clickViewBg.setBackgroundResource(R.drawable.ground_bg);
                    holder.dayTextView.setTextColor(resources.getColor(R.color.white));
                } else {
                    holder.clickViewBg.setBackgroundResource(R.drawable.ground_gray);
                    holder.dayTextView.setTextColor(resources.getColor(R.color.black));
                }
            }
        } else {
            holder.clickViewBg.setVisibility(View.GONE);
            holder.dayTextView.setTextColor(resources.getColor(R.color.comment_grey));
        }
        final int day = myDate.getDate(); // 日期
        holder.dayTextView.setText(String.valueOf(day < 10 ? "0" + day : day));
        convertView.setTag(R.id.date, myDate);
        return convertView;
    }

    @SuppressWarnings("deprecation")
    private Boolean equalsDate(Date date1, Date date2) {
        if (date1.getYear() == date2.getYear()
                && date1.getMonth() == date2.getMonth()
                && date1.getDate() == date2.getDate()) {
            return true;
        } else {
            return false;
        }
    }

    private Boolean isExistData(Date myDate) {
        return myDate.getTime() >= System.currentTimeMillis() - 24 * 60 * 60 * 1000;
    }

    class ViewHolder {

        private TextView dayTextView;
        private View reddotView;
        private View clickViewBg;

    }

    public void clickSelect(Date selectDate) {
        Calendar calCalendar = Calendar.getInstance();
        calCalendar.setTime(selectDate);
        final int iMonth = calCalendar.get(Calendar.MONTH);
        if (iMonth != iMonthViewCurrentMonth|| (selectDate.getTime()< System.currentTimeMillis() - 24 * 60 * 60 * 1000))
            return;
        this.selectDate = selectDate;
        notifyDataSetChanged();
    }

    public void refreshAction() {
        notifyDataSetChanged();
    }

}
