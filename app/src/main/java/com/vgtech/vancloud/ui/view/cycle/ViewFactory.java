package com.vgtech.vancloud.ui.view.cycle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.api.ADInfo;

public class ViewFactory {

    public static View generaItemView(final Context context, final ADInfo adInfo, final CycleViewPager cycleViewPager) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.view_banner, null);
        SimpleDraweeView imageView = (SimpleDraweeView) view.findViewById(R.id.background);
        imageView.setImageURI(adInfo.adPic);
        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = (int) (width/3f);
        imageView.setAspectRatio(3f);
        TextView titleTv = (TextView) view.findViewById(R.id.tv_title);
        titleTv.setText(adInfo.adTitle);
        cycleViewPager.getView().setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        return view;
    }
}
