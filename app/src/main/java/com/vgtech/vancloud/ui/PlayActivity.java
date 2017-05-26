package com.vgtech.vancloud.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.vgtech.vancloud.R;
import com.vgtech.vancloud.utils.videoplayer.Session;
import com.vgtech.vancloud.utils.videoplayer.VideoPlayer;

public class PlayActivity extends BaseActivity {

    /**
     * ------------------组件-------------------
     **/

    VideoPlayer videoPlayer;

    /**
     * ------------------数据-------------------
     **/

    Session session = Session.getSession();

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.btn_back).setVisibility(View.VISIBLE);
        session.initialize(this);

        videoPlayer = (VideoPlayer) findViewById(R.id.videoPlayer);

        String name = getIntent().getStringExtra("name");
        String url = getIntent().getStringExtra("url");
        setTitle(name);
        // 注意调用的先后顺序
        videoPlayer.setData(name,
                url);
//		videoPlayer.setData("我的歌声里",
//				"http://test-android.oss-cn-shenzhen.aliyuncs.com/video/111.mp4");// 如果本视频已经不存在，请自行想办法
        videoPlayer.setPlayerActivity(this);
        videoPlayer.backFromFullScreen();

        videoPlayer.setBackClickListener(backClickListener);
        videoPlayer.setDownloadClickListener(downloadClickListener);
        videoPlayer.setFavoriteClickListener(favoriteClickListener);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_course_detail;
    }

    @Override
    protected void onDestroy() {
        videoPlayer.onDestroy();
        super.onDestroy();
    }

    OnClickListener backClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (videoPlayer.isFullScreen()) {
                videoPlayer.backFromFullScreen();
            } else {
                onBackPressed();
            }
        }
    };

    OnClickListener favoriteClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (videoPlayer.isFavorite()) {
                Toast.makeText(PlayActivity.this, "取消收藏成功！", Toast.LENGTH_LONG).show();
                videoPlayer.setFavorite(false);
            } else {
                Toast.makeText(PlayActivity.this, "收藏成功！", Toast.LENGTH_LONG).show();
                videoPlayer.setFavorite(true);
            }
        }
    };

    OnClickListener downloadClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(PlayActivity.this, "已经开始下载……", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onPause() {
        videoPlayer.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoPlayer.onResume();
    }

    @Override
    public void onBackPressed() {
        if (videoPlayer.isFullScreen()) {
            videoPlayer.backFromFullScreen();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        videoPlayer.onConfigurationChanged(newConfig);
    }
}
