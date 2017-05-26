package com.vgtech.vancloud.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.vgtech.vancloud.R;
import com.vgtech.vancloud.models.Staff;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vic on 2017/1/16.
 */
public class WebFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String WEB_URL = "WEB_URL";
    protected WebView mWebView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.web, null);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.my_swiperefreshlayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mWebView = (WebView) view.findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLoadsImagesAutomatically(true);
        mWebView.setWebViewClient(new MyWebViewClient());
        webSettings.setAppCacheEnabled(false);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.addJavascriptInterface(new JavaScriptObject(getActivity()), "jsObj");
        String url = getArguments().getString(WEB_URL);
        mWebView.loadUrl(url);
        return view;
    }

    @Override
    public void onRefresh() {
        mWebView.reload();
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_test:
//                sendXmppChat("798","Name798","http://img.hyylkj.com/repository/default.jpg");
////                sendXmppChat("799","Name799","http://img.hyylkj.com/repository/default.jpg");
//                break;
//        }
//    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mWebView.loadUrl(url);
            return true;
        }

        public void onPageFinished(WebView view, String url) {
            view.getSettings().setJavaScriptEnabled(true);
            swipeRefreshLayout.setRefreshing(false);
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, 1000);
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    public class JavaScriptObject {
        Context mContxt;

        public JavaScriptObject(Context mContxt) {
            this.mContxt = mContxt;
        }

        /**
         * @param userId 用户唯一标识
         * @param name   用户姓名（显示用）
         * @param photo  用户头像（显示用）
         */
        @JavascriptInterface //sdk17版本以上加上注解
        public void sendChat(String userId, String name, String photo) {
            sendXmppChat(userId, name, photo);
        }
    }

    public void sendXmppChat(String userId, String name, String photo) {
        List<Staff> contactses = new ArrayList<Staff>();
        Staff staff = new Staff(userId, userId, name, photo);
        contactses.add(staff);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.xmpp.chat(contactses, null);
    }
}
