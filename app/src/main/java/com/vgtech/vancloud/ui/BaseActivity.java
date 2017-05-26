package com.vgtech.vancloud.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.vgtech.common.view.IphoneDialog;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.VanCloudApplication;

/**
 * Created by vic on 2017/3/8.
 */
public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        View btnView = findViewById(R.id.btn_back);
        if (btnView != null)
            btnView.setOnClickListener(this);
    }

    public void setTitle(String title) {
        TextView titleTv = (TextView) findViewById(android.R.id.title);
        titleTv.setText(title);
    }
    protected IphoneDialog iphoneDialog = null;
    /**
     * @param contentStr
     */
    public void showLoadingDialog(String contentStr) {
        if (iphoneDialog == null) {
            iphoneDialog = new IphoneDialog(this);
        }
        iphoneDialog.setMessage(contentStr);
        iphoneDialog.show(true);
    }

    public void showLoadingDialog(Context mContext, String contentStr, boolean ifCandismiss) {
        if (iphoneDialog == null) {
            iphoneDialog = new IphoneDialog(mContext);
        }
        iphoneDialog.setMessage(contentStr);
        iphoneDialog.show(ifCandismiss);
    }


    /**
     *
     */
    public void dismisLoadingDialog() {
        if (iphoneDialog != null && iphoneDialog.isShowing()) {
            iphoneDialog.dismiss();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
        }
    }
    public VanCloudApplication getAppliction() {
        return (VanCloudApplication) getApplication();
    }

    @Override
    public void finish() {
        super.finish();
        getAppliction().getNetworkManager().cancle(this);
    }

    protected abstract int getContentView();
}
