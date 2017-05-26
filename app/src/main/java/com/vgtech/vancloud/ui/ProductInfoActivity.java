package com.vgtech.vancloud.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.vgtech.common.URLAddr;
import com.vgtech.common.api.JsonDataFactory;
import com.vgtech.common.api.RootData;
import com.vgtech.common.network.NetworkPath;
import com.vgtech.common.network.android.HttpListener;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.api.Product;
import com.vgtech.vancloud.utils.ActivityUtils;
import com.vgtech.vancloud.utils.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vic on 2017/3/12.
 */
public class ProductInfoActivity extends BaseActivity implements HttpListener<String> {
    private Player player;
    private SeekBar musicProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("训练详情");
        findViewById(R.id.btn_back).setVisibility(View.VISIBLE);
        musicProgress = (SeekBar) findViewById(R.id.music_progress);
        player = new Player(musicProgress);
        musicProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
        String productID = getIntent().getStringExtra("productID");
        loadProductInfo(productID);
    }

    private void loadProductInfo(String productId) {
        Map<String, String> params = new HashMap<>();
        params.put("productID", productId);
        NetworkPath path = new NetworkPath(URLAddr.URL_PRODUCT_DETAIL, params, this);
        getAppliction().getNetworkManager().load(1, path, this, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play:
                if ("1".equals(product.fileType)) {
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            player.playUrl(product.filePath);
                        }
                    }).start();
                } else if ("4".equals(product.fileType)) {
//                    Intent intent = new Intent(this, PlayActivity.class);
//                    intent.putExtra("url", product.filePath);
//                    intent.putExtra("name", product.productName);
//                    startActivity(intent);
                    Intent intent = new Intent(this, WebActivity.class);
                    intent.setData(Uri.parse(product.filePath));
                    intent.putExtra("title", product.productName);
                    startActivity(intent);

                }

                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_productinfo;
    }

    private Product product;

    @Override
    public void dataLoaded(int callbackId, NetworkPath path, RootData rootData) {
        boolean safe = ActivityUtils.prehandleNetworkData(this, rootData);
        if (!safe) {
            return;
        }
        switch (callbackId) {
            case 1:
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                findViewById(R.id.view_content).setVisibility(View.VISIBLE);
                try {
                    product = JsonDataFactory.getData(Product.class, rootData.getJson().getJSONObject("data").getJSONObject("product"));
                    TextView tv_subtitle = (TextView) findViewById(R.id.tv_subtitle);
                    tv_subtitle.setText(product.productName);
                    SimpleDraweeView icon = (SimpleDraweeView) findViewById(R.id.icon);
                    icon.setImageURI(product.picPath);
                    TextView tv_desc = (TextView) findViewById(R.id.tv_desc);
                    tv_desc.setText(Html.fromHtml(product.intro));
                    if ("1".equals(product.fileType)) {
                        View btn_play = findViewById(R.id.btn_play);
                        btn_play.setVisibility(View.VISIBLE);
                        btn_play.setOnClickListener(this);
                        musicProgress.setVisibility(View.VISIBLE);
                    } else if ("4".equals(product.fileType)) {
                        View btn_play = findViewById(R.id.btn_play);
                        btn_play.setVisibility(View.VISIBLE);
                        btn_play.setOnClickListener(this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
        int progress;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
            this.progress = progress * player.mediaPlayer.getDuration()
                    / seekBar.getMax();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
            player.mediaPlayer.seekTo(progress);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
            player = null;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(String response) {

    }
}
