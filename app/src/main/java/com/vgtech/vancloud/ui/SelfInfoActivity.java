package com.vgtech.vancloud.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.vgtech.common.FileCacheUtils;
import com.vgtech.common.PrfUtils;
import com.vgtech.common.URLAddr;
import com.vgtech.common.api.RootData;
import com.vgtech.common.network.NetworkPath;
import com.vgtech.common.network.android.FilePair;
import com.vgtech.common.network.android.HttpListener;
import com.vgtech.common.view.ActionSheetDialog;
import com.vgtech.vancloud.PrfConstants;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.VanCloudApplication;
import com.vgtech.vancloud.ui.common.publish.internal.PicSelectActivity;
import com.vgtech.vancloud.utils.ActivityUtils;
import com.vgtech.vancloud.utils.ImgUtils;
import com.vgtech.vancloud.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vic on 2017/3/12.
 */
public class SelfInfoActivity extends BaseActivity implements HttpListener<String> {
    private int mSex = -1;

    private String profileID;
    private String comCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("完善个人资料");
        findViewById(R.id.btn_back).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_photo).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);
        RadioGroup sexGroup = (RadioGroup) findViewById(R.id.rg_sex);
        sexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_boy:
                        mSex = 1;
                        break;
                    case R.id.rb_girl:
                        mSex = 0;
                        break;
                }
            }
        });
        SimpleDraweeView iv_icon = (SimpleDraweeView) findViewById(R.id.iv_icon);
        try {
            JSONObject proFile = new JSONObject(PrfUtils.getPrfparams(this, PrfConstants.PARAM_PROFILE));
            profileID = proFile.getString("profileID");
            comCode = proFile.getString("comCode");
            ImgUtils.setUserImg(iv_icon, proFile.getString("headImg"));
            RadioButton rb_boy = (RadioButton) findViewById(R.id.rb_boy);
            RadioButton rb_girl = (RadioButton) findViewById(R.id.rb_girl);
            String gender = proFile.getString("gender");
            if ("1".equals(gender)) {
                rb_boy.setChecked(true);
            } else {
                rb_girl.setChecked(true);
            }
            TextView tv_name = (TextView) findViewById(R.id.tv_name);
            String name = proFile.getString("name");
            if (!TextUtils.isEmpty(name))
                tv_name.setText(name);
            TextView tv_age = (TextView) findViewById(R.id.tv_age);
            String age = proFile.getString("age");
            if (!TextUtils.isEmpty(age))
                tv_age.setText(age);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static final int TAKE_PICTURE = 0x000000;
    private static final int FROM_PHOTO = 0x000001;
    private static final int PHOTO_CLIP = 0x000002;
    private String path = "";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_photo: {
                new ActionSheetDialog(this)
                        .builder()
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .addSheetItem(getString(R.string.take), ActionSheetDialog.SheetItemColor.Blue,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        File file = new File(FileCacheUtils.getImageDir(getApplicationContext()), String.valueOf(System.currentTimeMillis())
                                                + ".jpg");
                                        path = file.getPath();
                                        Uri imageUri = Uri.fromFile(file);
                                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                                    }
                                })

                        .addSheetItem(getString(R.string.select_from_photo), ActionSheetDialog.SheetItemColor.Blue,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        Intent intent = new Intent(getApplicationContext(),
                                                PicSelectActivity.class);
                                        intent.putExtra("single", true);
                                        startActivityForResult(intent, FROM_PHOTO);
                                    }
                                }).show();
            }
            break;
            case R.id.btn_save:
                saveUserInfo();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    private void saveUserInfo() {
        TextView nameTv = (TextView) findViewById(R.id.tv_name);
        String name = nameTv.getText().toString();
        if (TextUtils.isEmpty(name)) {
            ToastUtils.show(this, "请填写姓名");
            return;
        }
        if (mSex == -1) {
            ToastUtils.show(this, "请选择性别");
            return;
        }
        TextView tv_age = (TextView) findViewById(R.id.tv_age);
        String age = tv_age.getText().toString();
        if (TextUtils.isEmpty(age)) {
            ToastUtils.show(this, "请填写年龄");
            return;
        }
        showLoadingDialog("");
        Map<String, String> params = new HashMap<>();
        params.put("profileID", profileID);
        params.put("name", name);
        params.put("gender", String.valueOf(mSex));
        params.put("age", age);
        NetworkPath path = new NetworkPath(URLAddr.URL_MY_PROFILESAVE, params, this);
        VanCloudApplication vancloudApplication = (VanCloudApplication) getApplication();
        vancloudApplication.getNetworkManager().load(1, path, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case PHOTO_CLIP: {
                String path = data.getStringExtra("path");
                SimpleDraweeView iv_icon = (SimpleDraweeView) findViewById(R.id.iv_icon);
                iv_icon.setImageURI(Uri.fromFile(new File(path)));
                iv_icon.setTag(path);
                uploadPhoto(path);
            }
            break;
            case TAKE_PICTURE: {
                Intent intent = new Intent(this, ClipActivity.class);
                intent.putExtra("path", path);
                startActivityForResult(intent, PHOTO_CLIP);
            }
            break;
            case FROM_PHOTO: {
                String path = data.getStringExtra("path");
                Intent intent = new Intent(this, ClipActivity.class);
                intent.putExtra("path", path);
                startActivityForResult(intent, PHOTO_CLIP);
            }
            break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void uploadPhoto(String imgPath) {
        showLoadingDialog("");
        Map<String, String> params = new HashMap<>();
        params.put("mbrID", PrfUtils.getMbrId(this));
        FilePair filePair = new FilePair("file", new File(imgPath));
        NetworkPath path = new NetworkPath(URLAddr.URL_MY_UPLOADHEADIMG, params, filePair);
        VanCloudApplication vancloudApplication = (VanCloudApplication) getApplication();
        vancloudApplication.getNetworkManager().load(2, path, this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_selfinfo;
    }

    private String mUserPhoto;

    @Override
    public void dataLoaded(int callbackId, NetworkPath path, RootData rootData) {
        dismisLoadingDialog();
        boolean safe = ActivityUtils.prehandleNetworkData(this, rootData);
        if (!safe) {
            return;
        }
        switch (callbackId) {
            case 1:
                try {
                    ToastUtils.show(this,rootData.getMsg());
                    JSONObject proFile = new JSONObject(PrfUtils.getPrfparams(this, PrfConstants.PARAM_PROFILE));
                    proFile.put("name", path.getPostValues().get("name"));
                    proFile.put("gender", path.getPostValues().get("gender"));
                    proFile.put("age", path.getPostValues().get("age"));
                    PrfUtils.savePrfparams(this, PrfConstants.PARAM_PROFILE, proFile.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                try {
                    ToastUtils.show(this,rootData.getMsg());
                    mUserPhoto = rootData.getJson().getJSONObject("data").getString("fileUrl");
                    JSONObject proFile = new JSONObject(PrfUtils.getPrfparams(this, PrfConstants.PARAM_PROFILE));
                    proFile.put("headImg", mUserPhoto);
                    PrfUtils.savePrfparams(this, PrfConstants.PARAM_PROFILE, proFile.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(String response) {

    }
}
