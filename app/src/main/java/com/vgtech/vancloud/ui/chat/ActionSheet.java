package com.vgtech.vancloud.ui.chat;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Rect;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vgtech.vancloud.R;

/**
 * @author xuanqiang
 * @date 13-8-3
 */
@SuppressWarnings("ConstantConditions")
public class ActionSheet {
  private Dialog dialog;
  private View containerView;
  private LinearLayout contentView;
  private TextView titleView;
  private TextView cancelView;
  private Activity activity;
  private float density;
  private OnClickListener cancelListener;

  public ActionSheet(final Activity activity){
    density = activity.getResources().getDisplayMetrics().density;
    containerView = LayoutInflater.from(activity).inflate(R.layout.actionsheet_container,null);
    containerView.setBackgroundColor(activity.getResources().getColor(R.color.transparent_black));
    contentView = (LinearLayout)containerView.findViewById(R.id.actionsheet_container_content);
    this.activity = activity;
    titleView = new TextView(activity);
    titleView.setTextColor(activity.getResources().getColor(android.R.color.white));
    titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
    titleView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(int)(50 * density)));
    titleView.setGravity(Gravity.CENTER);
    contentView.addView(titleView);

    cancelView = new TextView(activity);
    cancelView.setTextColor(activity.getResources().getColor(android.R.color.black));
    cancelView.setTextSize(TypedValue.COMPLEX_UNIT_SP,17);
    cancelView.setSingleLine();
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(int)(44 * density));
    cancelView.setLayoutParams(lp);
    cancelView.setGravity(Gravity.CENTER);
    cancelView.setBackgroundResource(R.drawable.actionsheet_cancel_rounded);
    cancelView.setText(activity.getString(R.string.cancel));
    cancelView.setPadding(0,0,0,(int)(1*density));
    lp.setMargins(0,0,0,(int)(5*density));

    cancelListener = new OnClickListener(){
      @Override
      public void onClick(View v){
        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.actionsheet_exit);
        animation.setAnimationListener(new Animation.AnimationListener(){
          @Override
          public void onAnimationStart(Animation animation){
            containerView.setBackgroundColor(activity.getResources().getColor(android.R.color.transparent));
          }
          @Override
          public void onAnimationEnd(Animation animation){
            new Handler().post(new Runnable(){
              @Override
              public void run(){
                dialog.dismiss();
              }
            });
          }
          @Override
          public void onAnimationRepeat(Animation animation){}
        });
        contentView.startAnimation(animation);
      }
    };
  }

  public void addAction(final String action, final OnClickListener listener){
    TextView actionView = new TextView(activity);
    actionView.setTextColor(activity.getResources().getColor(android.R.color.white));
    actionView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
    actionView.setSingleLine();
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(int)(44 * density));
    actionView.setLayoutParams(lp);
    actionView.setGravity(Gravity.CENTER);
    actionView.setBackgroundResource(R.drawable.actionsheet_button_rounded);
    actionView.setText(action);
    actionView.setPadding(0,0,0,(int)(2*density));
    lp.setMargins(0,0,0,(int)(20*density));
    actionView.setOnClickListener(new OnClickListener(){
      @Override
      public void onClick(View v){
        listener.onClick(v);
        cancelListener.onClick(v);
      }
    });
    contentView.addView(actionView);
  }

  public void setTitle(final String title){
    titleView.setText(title);
  }

  public void show(){
    cancelView.setOnClickListener(cancelListener);
    containerView.setOnClickListener(cancelListener);
    contentView.addView(cancelView);
    DisplayMetrics metrics = new DisplayMetrics();
    Rect rect = new Rect();
    WindowManager windowManager = activity.getWindowManager();
    windowManager.getDefaultDisplay().getMetrics(metrics);
    activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
    dialog = new Dialog(activity, R.style.ActionSheetStyle);
    dialog.setContentView(containerView);
    dialog.getWindow().setGravity(Gravity.BOTTOM);
    dialog.getWindow().getAttributes().width = metrics.widthPixels;
    dialog.getWindow().getAttributes().height = metrics.heightPixels - rect.top;
    contentView.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.actionsheet_enter));
    dialog.show();
  }
}
