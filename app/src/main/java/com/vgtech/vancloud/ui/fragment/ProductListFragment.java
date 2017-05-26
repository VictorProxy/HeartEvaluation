package com.vgtech.vancloud.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.vgtech.common.URLAddr;
import com.vgtech.common.api.JsonDataFactory;
import com.vgtech.common.api.RootData;
import com.vgtech.common.network.NetworkPath;
import com.vgtech.common.network.android.HttpListener;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.api.Product;
import com.vgtech.vancloud.api.Scale;
import com.vgtech.vancloud.event.KeywordEvent;
import com.vgtech.vancloud.ui.BaseFragment;
import com.vgtech.vancloud.ui.ProductInfoActivity;
import com.vgtech.vancloud.ui.ScaleInfoActivity;
import com.vgtech.vancloud.ui.adapter.ProductAdapter;
import com.vgtech.vancloud.ui.adapter.ScaleAdapter;
import com.vgtech.vancloud.utils.ActivityUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by vic on 2017/3/9.
 */
public class ProductListFragment extends BaseFragment implements HttpListener<String>, PullToRefreshBase.OnRefreshListener2, AdapterView.OnItemClickListener {
    @Override
    protected int initLayoutId() {
        return R.layout.fragment_scalelist;
    }

    private ProductAdapter scaleAdapter;
    PullToRefreshListView mListView;

    @Override
    protected void initView(View view) {
        mListView = (PullToRefreshListView) view.findViewById(R.id.cp_list);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        mListView.setOnRefreshListener(this);
        mListView.setClickable(false);
        mListView.setPressed(false);
        mListView.setOnItemClickListener(this);
        scaleAdapter = new ProductAdapter(getActivity());
        mListView.setAdapter(scaleAdapter);
        EventBus.getDefault().register(this);
    }
    public void onEvent(KeywordEvent event) {
        keywords = event.getKeyword();
        mNextId = 1;
        loadData(mNextId);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    private String categoryID;
    private String fileType;

    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            categoryID = bundle.getString("categoryID");
            fileType = bundle.getString("fileType");
            loadData(mNextId);
        }
    }
    private String keywords;
    private void loadData(int nextId) {
        Map<String, String> params = new HashMap<>();
        if(!TextUtils.isEmpty(keywords))
        params.put("keywords",keywords);
        params.put("categoryID", categoryID);
        params.put("fileType", fileType);
        params.put("currentPage", String.valueOf(nextId));
        params.put("showCount", "8");
        NetworkPath path = new NetworkPath(URLAddr.URL_PRODUCT_INDEX, params, getActivity());
        getApplication().getNetworkManager().load(1, path, this, true);
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
        mListView.onRefreshComplete();
        if (!safe) {
            return;
        }
        try {
            JSONObject jsonObject = rootData.getJson().getJSONObject("data");
            List<Product> scales = JsonDataFactory.getDataArray(Product.class, jsonObject.getJSONArray("ProductList"));
            String currentPage = path.getPostValues().get("currentPage");
            if ("1".equals(currentPage))
                scaleAdapter.clear();
            scaleAdapter.addAllDataAndNorify(scales);
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

    private final int FLAG_RELOAD = 0X002;
    private final int FLAG_LOAD_ADD = 0X003;
    private int mNextId = 1;
    private int mFlag = FLAG_RELOAD;

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mFlag = FLAG_RELOAD;
        mNextId = 1;
        loadData(mNextId);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        mFlag = FLAG_LOAD_ADD;
        mNextId += 1;
        loadData(mNextId);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object obj = parent.getItemAtPosition(position);
        if (obj instanceof Product) {
            Product product = (Product) obj;
            Intent intent = new Intent(getActivity(), ProductInfoActivity.class);
            intent.putExtra("productID", product.productID);
            startActivity(intent);
        }
    }
}
