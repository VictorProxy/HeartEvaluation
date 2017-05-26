package com.vgtech.vancloud.ui.common.publish.internal;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.vgtech.vancloud.R;

import java.io.Serializable;
import java.util.List;


public class PicSelectActivity extends Activity {
    // ArrayList<Entity> dataList;//用来装载数据源的列表
    List<ImageBucket> dataList;
    GridView gridView;
    ImageBucketAdapter adapter;// 自定义的适配器
    AlbumHelper helper;
    public static final String EXTRA_IMAGE_LIST = "imagelist";
    public static Bitmap bimap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_bucket);
        TextView tvTitle = (TextView) findViewById(android.R.id.title);
        tvTitle.setText(getString(R.string.vancloud_photo_album));
        findViewById(R.id.btn_back).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_back).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        finish();

                    }
                });
        helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());

        initData();
        initView();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // /**
        // * 这里，我们假设已经从网络或者本地解析好了数据，所以直接在这里模拟了10个实体类，直接装进列表中
        // */
        // dataList = new ArrayList<Entity>();
        // for(int i=-0;i<10;i++){
        // Entity entity = new Entity(R.drawable.picture, false);
        // dataList.add(entity);
        // }
        dataList = helper.getImagesBucketList(false);
        bimap = BitmapFactory.decodeResource(getResources(),
                R.drawable.img_default);
    }

    private static final int REQUEST_CODE_PIC = 2001;
    private boolean single;

    @Override
    public void finish() {
        super.finish();
        AlbumHelper.hasBuildImagesBucketList = false;
        AlbumHelper.getHelper().bucketList.clear();
        AlbumHelper.getHelper().albumList.clear();
        AlbumHelper.getHelper().thumbnailList.clear();
    }

    /**
     * 初始化view视图
     */
    private void initView() {
        gridView = (GridView) findViewById(R.id.gridview);
        adapter = new ImageBucketAdapter(PicSelectActivity.this, dataList);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                /**
                 * 根据position参数，可以获得跟GridView的子View相绑定的实体类，然后根据它的isSelected状态，
                 * 来判断是否显示选中效果。 至于选中效果的规则，下面适配器的代码中会有说明
                 */
                // if(dataList.get(position).isSelected()){
                // dataList.get(position).setSelected(false);
                // }else{
                // dataList.get(position).setSelected(true);
                // }
                /**
                 * 通知适配器，绑定的数据发生了改变，应当刷新视图
                 */
                // adapter.notifyDataSetChanged();
                Intent intent = new Intent(PicSelectActivity.this,
                        ImageGridActivity.class);
                single = getIntent().getBooleanExtra("single", false);
                intent.putExtra("single", single);
                intent.putExtra(PicSelectActivity.EXTRA_IMAGE_LIST,
                        (Serializable) dataList.get(position).imageList);
                // startActivity(intent);
                startActivityForResult(intent, REQUEST_CODE_PIC);
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_PIC:
                if (resultCode == RESULT_OK) {
                    if (single) {
                        String path = data.getStringExtra("path");
                        Intent intent = new Intent();
                        intent.putExtra("path", path);
                        setResult(RESULT_OK, intent);
                    } else {
                        setResult(RESULT_OK);
                    }
                    finish();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
