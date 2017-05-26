package com.vgtech.vancloud.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.vgtech.common.config.ImageOptions;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.api.Scale;
import com.vgtech.vancloud.api.Timer;
import com.vgtech.vancloud.api.WorkCalendar;
import com.vgtech.vancloud.ui.ConsultationActivity;
import com.vgtech.vancloud.ui.MainActivity;
import com.vgtech.vancloud.ui.ScheduleFragment;

/**
 * Created by vic on 2017/3/8.
 */
public class TimerAdapter extends BaseSimpleAdapter<Timer> implements View.OnClickListener {
    private ScheduleFragment.WorkCalendarClickListener mListener;

    public TimerAdapter(Context context, ScheduleFragment.WorkCalendarClickListener listener) {
        super(context);
        mListener = listener;
    }

    @Override
    public int getItemResource(int viewType) {
        return R.layout.timer_item;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        Timer timer = getItem(position);
        TextView tv_first = holder.getView(R.id.tv_first);
        TextView tv_second = holder.getView(R.id.tv_second);
        tv_first.setText(timer.getFirstTime());
        if (!TextUtils.isEmpty(timer.getSecondTime())) {
            tv_second.setText(timer.getSecondTime());
            tv_second.setVisibility(View.VISIBLE);
        } else {
            tv_second.setVisibility(View.INVISIBLE);
        }
        tv_first.setTag(timer.getFTime());
        tv_second.setTag(timer.getSTime());
        tv_first.setOnClickListener(this);
        tv_second.setOnClickListener(this);
        if ("1".equals(timer.getFTime().status)) {
            tv_first.setEnabled(true);
        } else {
            tv_first.setEnabled(false);
        }
        if (timer.getSTime() != null && "1".equals(timer.getSTime().status)) {
            tv_second.setEnabled(true);
        } else {
            tv_second.setEnabled(false);
        }

        return convertView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_first: {
                WorkCalendar workCalendar = (WorkCalendar) v.getTag();
                mListener.onWorkCalendarClick(workCalendar);
            }
            break;
            case R.id.tv_second:
                WorkCalendar workCalendar = (WorkCalendar) v.getTag();
                mListener.onWorkCalendarClick(workCalendar);
                break;
        }
    }
}
