package com.vgtech.vancloud.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.inject.Inject;
import com.vgtech.common.PrfUtils;
import com.vgtech.common.URLAddr;
import com.vgtech.common.api.RootData;
import com.vgtech.common.network.NetworkPath;
import com.vgtech.common.network.android.HttpListener;
import com.vgtech.vancloud.PrfConstants;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.ui.chat.MessagesFragment;
import com.vgtech.vancloud.ui.chat.controllers.Controller;
import com.vgtech.vancloud.ui.chat.models.ChatGroup;
import com.vgtech.vancloud.ui.fragment.ReportListFragment;
import com.vgtech.vancloud.utils.ActivityUtils;
import com.vgtech.vancloud.utils.ImgUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboguice.RoboGuice;

/**
 * Created by vic on 2017/3/11.
 */
public class MeFragment extends BaseFragment implements View.OnClickListener, HttpListener<String> {
    @Override
    protected int initLayoutId() {
        return R.layout.fragment_me;
    }

    private View mView;
    private View me_msg_new;

    @Override
    protected void initView(View view) {
        mView = view;
        TextView titleTv = (TextView) view.findViewById(android.R.id.title);
        titleTv.setText("个人中心");
        me_msg_new = view.findViewById(R.id.me_msg_new);
        view.findViewById(R.id.btn_messagge).setOnClickListener(this);
        view.findViewById(R.id.btn_attention).setOnClickListener(this);
        view.findViewById(R.id.btn_fav).setOnClickListener(this);
        view.findViewById(R.id.btn_report_manager).setOnClickListener(this);
        view.findViewById(R.id.btn_my_scale).setOnClickListener(this);
        view.findViewById(R.id.btn_my_prepare).setOnClickListener(this);
        view.findViewById(R.id.btn_product).setOnClickListener(this);
        view.findViewById(R.id.btn_phone_setting).setOnClickListener(this);
        view.findViewById(R.id.btn_sns_setting).setOnClickListener(this);
        initUser();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("msg_new");
        intentFilter.addAction("msg_new_gone");
        getActivity().registerReceiver(receiver, intentFilter);
        view.findViewById(R.id.btn_logout).setOnClickListener((v) -> {
            PrfUtils.logout(getActivity());
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });
    }

    private void loadUserInfo() {
        Map<String, String> params = new HashMap<>();
        NetworkPath path = new NetworkPath(URLAddr.URL_MY_HOME, params, getActivity());
        getApplication().getNetworkManager().load(1, path, this, true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
            receiver = null;
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("msg_new")) {
                me_msg_new.setVisibility(View.VISIBLE);
            } else if (action.equals("msg_new_gone")) {
                me_msg_new.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        initUser();
        updateAppNum(getActivity());
    }

    public void updateAppNum(final Context context) {
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
                if (num > 0) {
                    me_msg_new.setVisibility(View.VISIBLE);
                } else {
                    me_msg_new.setVisibility(View.GONE);
                }
            }
        }.execute();
    }

    private void initUser() {
        SimpleDraweeView iv_icon = (SimpleDraweeView) mView.findViewById(R.id.iv_icon);
        try {
            JSONObject proFile = new JSONObject(PrfUtils.getPrfparams(getActivity(), PrfConstants.PARAM_PROFILE));
            ImgUtils.setUserImg(iv_icon, proFile.getString("headImg"));
            TextView nameTv = (TextView) mView.findViewById(R.id.tv_name);
            nameTv.setText(proFile.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        iv_icon.setOnClickListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RoboGuice.getInjector(getActivity()).injectMembersWithoutViews(this);
    }

    @Inject
    public Controller controller;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_icon: {
                Intent intent = new Intent(getActivity(), SelfInfoActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.btn_messagge:
                controller.pushFragment(new MessagesFragment());
                break;
            case R.id.btn_attention:
                break;
            case R.id.btn_fav:
                break;
            case R.id.btn_report_manager: {
                MainActivity mainActivity = (MainActivity) getActivity();
                ReportListFragment framgent = new ReportListFragment();
                mainActivity.controller.pushFragment(framgent);
            }
            break;
            case R.id.btn_my_scale: {
                MainActivity mainActivity = (MainActivity) getActivity();
                MyScaleListActivity framgent = new MyScaleListActivity();
                mainActivity.controller.pushFragment(framgent);
            }
            break;
            case R.id.btn_my_prepare: {
                MainActivity mainActivity = (MainActivity) getActivity();
                AppointMentListActivity framgent = new AppointMentListActivity();
                mainActivity.controller.pushFragment(framgent);
            }
            break;
            case R.id.btn_product: {
                MainActivity mainActivity = (MainActivity) getActivity();
                MyProductListActivity framgent = new MyProductListActivity();
                mainActivity.controller.pushFragment(framgent);

            }
            break;
            case R.id.btn_phone_setting: {
                MainActivity mainActivity = (MainActivity) getActivity();
                SettingFragment framgent = new SettingFragment();
                mainActivity.controller.pushFragment(framgent);
            }
            break;
            case R.id.btn_sns_setting: {
                Intent intent = new Intent(getActivity(), CommunityActivity.class);
                startActivity(intent);
            }
            break;
        }
    }

    @Override
    protected void initData() {
        loadUserInfo();
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
            String mbrType = rootData.getJson().getJSONObject("data").getString("mbrType");
            PrfUtils.savePrfparams(getActivity(), PrfUtils.MBRTYPE, mbrType);
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
