package com.vgtech.vancloud.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.vgtech.common.image.Bimp;
import com.vgtech.common.image.ImageUtility;
import com.vgtech.common.utils.FileUtils;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.ui.view.clip.ClipImageLayout;

/**
 * Created by zhangshaofang on 2015/10/8.
 */
public class ClipActivity extends BaseActivity {
    private ClipImageLayout mClipImageLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.btn_back).setVisibility(View.VISIBLE);
        setTitle("图片裁剪");
        mClipImageLayout = (ClipImageLayout) findViewById(R.id.id_clipImageLayout);
        TextView rightTv = (TextView) findViewById(R.id.tv_right);
        rightTv.setVisibility(View.VISIBLE);
        rightTv.setText(getString(R.string.ok));
        rightTv.setOnClickListener(this);
        try {
            String path = getIntent().getStringExtra("path");
            Bitmap bm = Bimp.revitionImageSize(path);
            bm = ImageUtility.checkFileDegree(path, bm);
            mClipImageLayout.setImageBmp(bm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_right:
                Bitmap bitmap = mClipImageLayout.clip();
                String newStr = String.valueOf(System.currentTimeMillis());
                String path = FileUtils.saveBitmap(this, bitmap, "" + newStr);
                Intent intent = new Intent();
                intent.putExtra("path", path);
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.clip_layout;
    }
}
