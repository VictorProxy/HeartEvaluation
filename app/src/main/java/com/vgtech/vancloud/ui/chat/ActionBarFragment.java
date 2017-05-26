package com.vgtech.vancloud.ui.chat;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.inject.Inject;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.VanCloudApplication;
import com.vgtech.vancloud.ui.chat.controllers.Controller;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * @author xuanqiang
 */
public class ActionBarFragment extends RoboFragment implements View.OnClickListener {
    @InjectView(R.id.btn_back)
    protected View backButton;
    @InjectView(android.R.id.title)
    protected TextView titleView;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (backButton != null)
            backButton.setOnClickListener(this);
//    if(titleView!=null)
//    titleView.setSelected(true);
    }

    protected View createContentView(final int layoutResID) {
        return controller.createActionBar(layoutResID);
    }

    @Override
    public void onClick(View view) {
        if (view == backButton) {
            getActivity().onBackPressed();
        }
    }

    @Override
    public void onDestroyView() {
        setUserVisibleHint(false);
        getApplication().getNetworkManager().cancle(this);
        super.onDestroyView();
    }

    @Inject
    public Controller controller;

    public VanCloudApplication getApplication() {
        return (VanCloudApplication) getActivity().getApplication();
    }


}
