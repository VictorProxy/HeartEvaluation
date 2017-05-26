package com.vgtech.vancloud.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.vgtech.common.URLAddr;
import com.vgtech.common.api.JsonDataFactory;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.api.Record;
import com.vgtech.vancloud.ui.adapter.RecordAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * Created by vic on 2017/3/12.
 */
public class RecordListActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private RecordAdapter recordAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView listView = (ListView) findViewById(R.id.record_list);
        findViewById(R.id.btn_back).setVisibility(View.VISIBLE);
         recordAdapter = new RecordAdapter(this);
        listView.setAdapter(recordAdapter);
        listView.setOnItemClickListener(this);
        String jsonStr = getIntent().getStringExtra("answerRecordList");
        if (!TextUtils.isEmpty(jsonStr)) {
            try {
                JSONArray jsonArray = new JSONArray(jsonStr);
                List<Record> recordList = JsonDataFactory.getDataArray(Record.class, jsonArray);
                recordAdapter.addAllDataAndNorify(recordList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_recordlist;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Record record = (Record) parent.getItemAtPosition(position);
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra("title", record.paperTitle);
        intent.setData(Uri.parse(URLAddr.URL_MY_REPORTVIEW).buildUpon().appendQueryParameter("answerRecordID", record.answerRecordID).build());
        startActivity(intent);
    }
}
