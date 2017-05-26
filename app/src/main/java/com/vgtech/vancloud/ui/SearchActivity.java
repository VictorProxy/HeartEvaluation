package com.vgtech.vancloud.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.vgtech.common.URLAddr;
import com.vgtech.common.api.JsonDataFactory;
import com.vgtech.common.api.RootData;
import com.vgtech.common.network.NetworkPath;
import com.vgtech.common.network.android.HttpListener;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.api.Announcement;
import com.vgtech.vancloud.api.Counselor;
import com.vgtech.vancloud.api.Product;
import com.vgtech.vancloud.api.Scale;
import com.vgtech.vancloud.ui.BaseActivity;
import com.vgtech.vancloud.ui.BaseFragment;
import com.vgtech.vancloud.ui.adapter.CounselorAdapter;
import com.vgtech.vancloud.ui.adapter.ProductAdapter;
import com.vgtech.vancloud.ui.adapter.ScaleAdapter;
import com.vgtech.vancloud.utils.ActivityUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vic on 2017/3/10.
 */
public class SearchActivity extends BaseActivity implements HttpListener<String> {
    private EditText mSearchTv;
    private View mWaitView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_search;
    }

    protected void initView() {
        mWaitView = findViewById(R.id.progressBar);
        ListView zj_list = (ListView) findViewById(R.id.zj_list);
        ListView cp_list = (ListView) findViewById(R.id.cp_list);
        ListView tj_list = (ListView) findViewById(R.id.tj_list);
        counselorAdapter = new CounselorAdapter(this);
        scaleAdapter = new ScaleAdapter(this);
        productAdapter = new ProductAdapter(this);
        zj_list.setAdapter(counselorAdapter);
        cp_list.setAdapter(scaleAdapter);
        tj_list.setAdapter(productAdapter);
        mSearchTv = (EditText) findViewById(R.id.et_search);
        View clearBtn = findViewById(R.id.btn_clear);
        clearBtn.setOnClickListener(this);
        mSearchTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_clear:
                CharSequence keyword = mSearchTv.getText();
                if (TextUtils.isEmpty(keyword)) {
                    InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    mInputMethodManager.hideSoftInputFromWindow(
                            mSearchTv.getWindowToken(), 0);
                    finish();
                } else {
                    mSearchTv.setText("");
                }
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    private CounselorAdapter counselorAdapter;
    private ScaleAdapter scaleAdapter;
    private ProductAdapter productAdapter;

    private void adapterClear() {
        counselorAdapter.clear();
        scaleAdapter.clear();
        productAdapter.clear();
    }

    private void search(String s) {
        if (!TextUtils.isEmpty(s)) {
            adapterClear();
            mWaitView.setVisibility(View.VISIBLE);
            Map<String, String> params = new HashMap<>();
            params.put("keywords", s);
            NetworkPath path = new NetworkPath(URLAddr.URL_INDEXSEARCH, params, this);
            getAppliction().getNetworkManager().load(1, path, this, true);
        }

    }

    @Override
    public void dataLoaded(int callbackId, NetworkPath path, RootData rootData) {
        mWaitView.setVisibility(View.GONE);
        boolean safe = ActivityUtils.prehandleNetworkData(this, rootData);
        if (!safe) {
            return;
        }
        try {
            adapterClear();
            JSONObject jsonObject = rootData.getJson().getJSONObject("data");
            List<Counselor> counselors = JsonDataFactory.getDataArray(Counselor.class, jsonObject.getJSONArray("IndexCounselor"));
            List<Scale> scales = JsonDataFactory.getDataArray(Scale.class, jsonObject.getJSONArray("ScaleList"));
            List<Product> products = JsonDataFactory.getDataArray(Product.class, jsonObject.getJSONArray("ProductList"));
            counselorAdapter.addAllData(counselors);
            scaleAdapter.addAllData(scales);
            productAdapter.addAllData(products);
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
