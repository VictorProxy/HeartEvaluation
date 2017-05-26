package com.vgtech.vancloud.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.vgtech.vancloud.R;

/**
 * Created by zhangshaofang on 2015/11/24.
 */
public class WebActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    protected WebView mWebView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title = getIntent().getStringExtra("title");
        setTitle(title);
        findViewById(R.id.btn_back).setVisibility(View.VISIBLE);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.my_swiperefreshlayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());
        String url = getIntent().getDataString();
        mWebView.loadUrl(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.reload();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    finish();
                }
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.web;
    }

    @Override
    public void onRefresh() {
        mWebView.reload();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mWebView.loadUrl(url);
            return true;
        }

        @Override
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

    public class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }
    }
}
