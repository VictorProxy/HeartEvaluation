package com.vgtech.vancloud.ui.chat.controllers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.inject.Inject;
import com.vgtech.common.api.UserAccount;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.ui.LoginActivity;
import com.vgtech.vancloud.ui.MainActivity;
import com.vgtech.vancloud.ui.chat.UsersMessagesFragment;
import com.vgtech.vancloud.ui.chat.models.ChatGroup;

import java.util.Collection;

import roboguice.inject.ContextScopedProvider;
import roboguice.inject.ContextSingleton;
import roboguice.util.Ln;

/**
 * @author xuanqiang
 */
@SuppressWarnings("UnusedDeclaration")
@ContextSingleton
public class Controller {

    public void logoutDialog() {
        new AlertDialog.Builder(context).setTitle(context.getString(R.string.app_logout))
                .setPositiveButton(context.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        logout();
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public void logout() {
        UserAccount account = prefController.getAccount();
//    account.uid = null;
//    if(!account.remember){
//      account.logname = null;
//      account.pwd = null;
//      account.getuiClientId = null;
//    }
        prefController.storageAccount(account);
        stopPush();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        getActivity().finish();
    }

    public View createActionBar(final int layoutResID) {
        return createActionBar(R.layout.actionbar, layoutResID);
    }

    public View createActionBar(final int actionBarResID, final int layoutResID) {
        View actionBar = getActivity().getLayoutInflater().inflate(actionBarResID, null);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.setClickable(true);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        assert actionBar != null;
        linearLayout.addView(actionBar, layoutParams);

        View content = getActivity().getLayoutInflater().inflate(layoutResID, null);
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        assert content != null;
        linearLayout.addView(content, layoutParams);

        return linearLayout;
    }

    public void pushFragment(final Fragment fragment) {
        pushFragment(fragment, null);
    }

    public void pushFragment(final Fragment fragment, final String tag) {
        pushFragment(fragment, true, tag);
    }

    public void pushFragment(final Fragment fragment, final boolean isAdd, final String tag) {
        FragmentTransaction ft = ftAnimations();
        if (isAdd) {
            Fragment frag = fragment();
            if (frag != null) {
                frag.setUserVisibleHint(false);
            }
            ftAdd(ft, fragment, tag);
        } else {
            ft.replace(R.id.container, fragment, tag);
        }
        ft.addToBackStack(null).commitAllowingStateLoss();
    }

    public FragmentTransaction ftAdd(final FragmentTransaction ft, final Fragment fragment, final String tag) {
        return ft.add(R.id.container, fragment, tag);
    }

    @SuppressLint("CommitTransaction")
    public FragmentTransaction ft() {
        return fm().beginTransaction();
    }

    public FragmentTransaction ftAnimationsExcludeEnter() {
        return ft().setCustomAnimations(0, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out);
    }

    public FragmentTransaction ftAnimations() {
        return ft().setCustomAnimations(R.anim.push_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out);
    }

    public FragmentTransaction ftFadeAnimations() {
        return ft().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void replaceFragment(final Fragment fragment) {
        replaceFragment(R.id.container, fragment);
    }

    public void replaceFragment(final int resId, final Fragment fragment) {
        ft().replace(resId, fragment).commitAllowingStateLoss();
    }

    public void removeFragmentByHandler(final int resId) {
        if (fmProvider != null) {
            removeFragmentByHandler(fmProvider.get(context).findFragmentById(resId));
        }
    }

    public void removeFragmentByHandler(final Fragment fragment) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    removeFragment(fragment);
                } catch (Exception ignored) {
                }
            }
        });
    }

    public void removeFragment(final int resId) {
        removeFragment(fm().findFragmentById(resId));
    }

    public void removeFragment(final Fragment fragment) {
        if (fragment != null) {
            ft().remove(fragment).commitAllowingStateLoss();
        }
    }

    public FragmentManager fm() {
        return fmProvider.get(context);
    }

    public Fragment fragment() {
        return fm().findFragmentById(R.id.container);
    }

    public void setFragmentUserVisibleHint(boolean isVisibleToUser) {
        Fragment fragment = fragment();
        if (fragment != null) {
            fragment.setUserVisibleHint(isVisibleToUser);
        }
    }

    public boolean isFastDoubleClick() {
        return isFastDoubleClick(500);
    }

    private long lastClickTime;

    public boolean isFastDoubleClick(long duration) {
        long time = System.currentTimeMillis();
        long _duration = time - lastClickTime;
        if (0 < _duration && _duration < duration) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

//  public void setLocale(final Locale locale){
//    UserAccount account = account();
//    Configuration config = context.getResources().getConfiguration();
//    if(locale != null) {
//      config.locale = locale;
//    }else {
//      if(Strings.isEmpty(account.lang)) {
//        config.locale = Locale.getDefault();
//      }else{
//        config.locale = account.locale;
//      }
//    }
//    if(!config.locale.equals(account.locale)){
//      account.locale = config.locale;
//      pref().storageAccount(account);
//    }
//    context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
//  }

    public void navigationToMessageFragment() {
        if (context instanceof MainActivity) {
            fm().popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//            MainActivity activity = (MainActivity) getActivity();
//            activity.getTabHost().setCurrentTab(0);
        }
    }

    public void pushUserMessagesFragment(UsersMessagesFragment fragment) {
        if (fm().getFragments() != null)
            for (Fragment frag : fm().getFragments()) {
                if (frag instanceof UsersMessagesFragment) {
                    ft().remove(frag).commitAllowingStateLoss();
                }
            }
        pushFragment(fragment);
    }

    public Activity getActivity() {
        return ((Activity) context);
    }

//  public void startPush(){
//    UserAccount account = prefController.getAccount();
//    if(Strings.notEmpty(account.uid) && (Strings.isEmpty(account.receivePush) || account.receivePush.equals("yes"))){
//      enablePushService();
//    }else {
//      disablePushService();
//    }
//  }

    public void stopPush() {
    }

//  @SuppressWarnings("ConstantConditions")
//  public synchronized void enablePushService(){
//    UserAccount account = prefController.getAccount();
//    account.receivePush = "yes";
//    prefController.storageAccount(account);
//    try {
//      PushManager.getInstance().turnOnPush(context.getApplicationContext());
//    } catch (Exception e) {
//      Ln.e(e);
//    }
//  }

//  public void disablePushService(){
//    UserAccount account = prefController.getAccount();
//    account.receivePush = "no";
//    prefController.storageAccount(account);
//    stopPush();
//  }

    public String getBaiduApiKey() {
        return getMetaValue("api_key");
    }

    @SuppressWarnings("ConstantConditions")
    public String getMetaValue(String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Ln.e(e);
        }
        return apiKey;
    }

    public void textViewTopDrawable(final TextView textView, int resId) {
        textView.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(resId), null, null);
    }

    public void textViewLeftDrawable(final TextView textView, int resId) {
        textView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(resId), null, null, null);
    }

    public void textViewRightDrawable(final TextView textView, int resId) {
        textView.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(resId), null);
    }

    public void updateMessagesBarNum(final Collection<ChatGroup> groups) {
        int num = 0;
        for (ChatGroup group : groups) {
            num += group.unreadNum;
        }
        updateBarNum(0, num);
    }

    @SuppressWarnings("ConstantConditions")
    public void updateBarNum(final int index, final int num) {
        if (context instanceof MainActivity) {
            MainActivity homeActivity = (MainActivity) context;
            homeActivity.updateTabNums(index, num);
        }
    }

//  @SuppressWarnings("ConstantConditions")
//  public void selectMyTabbar() {
//    if(context instanceof MainActivity) {
//      MainActivity homeActivity = (MainActivity) context;
//      View com.vgtech.personaledition.view = homeActivity.getTabHost().getTabWidget().getChildAt(1);
//      if (homeActivity.getTabHost().getCurrentTab() == 1) {
//        com.vgtech.personaledition.view.setSelected(true);
//      }
//    }
//  }

    public int getPixels(final float dp,Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float density = dm.density;
        return (int) (dp * density);
    }

    public PreferencesController pref() {
        return prefController;
    }

    public UserAccount account() {
        return pref().getAccount();
    }
    @Inject
    public Context context;
    @Inject
    ContextScopedProvider<FragmentManager> fmProvider;
    @Inject
    private PreferencesController prefController;
    @Inject
    InputMethodManager imManager;

}
