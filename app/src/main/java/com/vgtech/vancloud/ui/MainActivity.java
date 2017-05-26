package com.vgtech.vancloud.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.vgtech.common.PrfUtils;
import com.vgtech.common.URLAddr;
import com.vgtech.common.adapter.TabViewPagerAdapter;
import com.vgtech.common.api.RootData;
import com.vgtech.common.network.NetworkPath;
import com.vgtech.common.network.android.HttpListener;
import com.vgtech.common.view.TabPageIndicator;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.VanCloudApplication;
import com.vgtech.vancloud.api.Profile;
import com.vgtech.vancloud.ui.chat.OnEvent;
import com.vgtech.vancloud.ui.chat.controllers.Controller;
import com.vgtech.vancloud.ui.chat.controllers.XmppController;
import com.vgtech.vancloud.ui.chat.models.ChatGroup;
import com.vgtech.vancloud.ui.fragment.CounselorListFragment;
import com.vgtech.vancloud.ui.fragment.HomeFragment;
import com.vgtech.vancloud.ui.fragment.ProductFragment;
import com.vgtech.vancloud.ui.fragment.ScaleFragment;
import com.vgtech.vancloud.utils.ActivityUtils;
import com.vgtech.vancloud.utils.NoticeUtils;

import org.jivesoftware.smack.SmackAndroid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboguice.RoboGuice;
import roboguice.event.Observes;
import roboguice.inject.RoboInjector;
import roboguice.util.RoboContext;

/**
 * Created by vic on 2017/1/13.
 */
public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, RoboContext {
    private ViewPager mViewPager;
    private TabPageIndicator tabPageIndicator;
    List<Fragment> fragmentList;
    @Inject
    public Controller controller;
    @Inject
    XmppController xmpp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final RoboInjector injector = RoboGuice.getInjector(this);
        injector.injectMembersWithoutViews(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTabBar();
        SmackAndroid smackAndroid = SmackAndroid.init(this);
        xmpp.init();
    }


    public TabPageIndicator getTabPageIndicator() {
        return tabPageIndicator;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (xmpp != null && !xmpp.isConnected()) {
            xmpp.startXmpp();
        }
        updateAppNum(this);
    }
    public  void updateAppNum(final Context context) {
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                List<ChatGroup> groups = ChatGroup.findAllbyChat(PrfUtils.getUserId(context));
                int num = 0;
                for (ChatGroup group : groups) {
                    num += group.unreadNum;
                }
                return num;
            }

            @Override
            protected void onPostExecute(Integer num) {
                if (num > 99) {
                    num = 99;
                }
                updateTabNums(4,num);
            }
        }.execute();
    }
    private void initTabBar() {
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        fragmentList = new ArrayList<>();
        fragmentList.add(new HomeFragment());
        fragmentList.add(new ScaleFragment());
        fragmentList.add(new CounselorListFragment());
        fragmentList.add(new ProductFragment());
        fragmentList.add(new MeFragment());
        TabViewPagerAdapter fragmentViewPagerAdapter = new TabViewPagerAdapter(getSupportFragmentManager(), fragmentList);
        mViewPager.setAdapter(fragmentViewPagerAdapter);
        tabPageIndicator = (TabPageIndicator) findViewById(R.id.tabindicator);
        tabPageIndicator.removeAllViews();
        tabPageIndicator.setViewPager(mViewPager);
        tabPageIndicator.addTab(getIndicator(R.drawable.icon_tab_home, R.string.tab_home));
        tabPageIndicator.addTab(getIndicator(R.drawable.icon_tab_evaluation, R.string.tab_evaluation));
        tabPageIndicator.addTab(getIndicator(R.drawable.icon_tab_consultation, R.string.tab_consultation));
        tabPageIndicator.addTab(getIndicator(R.drawable.icon_tab_train, R.string.tab_train));
        tabPageIndicator.addTab(getIndicator(R.drawable.icon_tab_me, R.string.tab_me));
        tabPageIndicator.setCurrentTab(0);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setOffscreenPageLimit(fragmentList.size());
    }

    private View getIndicator(int iconResId, int textResId) {
        View tabIndicator = getLayoutInflater().inflate(R.layout.below_button_layout, null);
        ImageView icon = (ImageView) tabIndicator.findViewById(R.id.button_ico);
        icon.setImageResource(iconResId);
        TextView tv = (TextView) tabIndicator.findViewById(R.id.button_text);
        ColorStateList csl = (ColorStateList) getResources().getColorStateList(R.color.bottom_text_background);
        tv.setTextColor(csl);
        tv.setText(textResId);
        return tabIndicator;
    }

    @SuppressWarnings("UnusedDeclaration")
    void handleEvent(@Observes OnEvent event) {
        eventHandler.sendMessage(eventHandler.obtainMessage(0, event));
    }

    //region event handler
    private Handler eventHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            OnEvent event = (OnEvent) msg.obj;
            assert event != null;
            if (event.type == OnEvent.EventType.NEW) {
                controller.updateMessagesBarNum(ChatGroup.findAll(PrfUtils.getUserId(MainActivity.this)));
            }
        }
    };

    /**
     * 更新底部标签数字
     *
     * @param num   数字
     */
    public void updateTabNums(int index, int num) {

        if (tabPageIndicator != null) {
            View view = tabPageIndicator.getChildAt(4);
            TextView numTextView = (TextView) view.findViewById(R.id.num);
            if (num == 0) {
                sendBroadcast(new Intent("msg_new_gone"));
                numTextView.setVisibility(View.GONE);
            } else {
                sendBroadcast(new Intent("msg_new"));
                numTextView.setVisibility(View.VISIBLE);
//                numTextView.setText("" + (num < 100 ? num : "N"));
            }
        }
    }

    public Fragment getWebFragment(String url) {
        WebFragment webFragment = new WebFragment();
        Bundle bundle = new Bundle();
        bundle.putString(WebFragment.WEB_URL, url);
        webFragment.setArguments(bundle);
        return webFragment;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void finish() {
        if (xmpp != null)
            xmpp.stopXmpp();
        controller = null;
        xmpp = null;
        mViewPager = null;
        fragmentList = null;
        super.finish();
    }

    @Override
    public void onPageSelected(int position) {
        tabPageIndicator.setCurrentTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    protected HashMap<Key<?>, Object> scopedObjects = new HashMap<Key<?>, Object>();

    @Override
    public Map<Key<?>, Object> getScopedObjectMap() {
        return scopedObjects;
    }

}
