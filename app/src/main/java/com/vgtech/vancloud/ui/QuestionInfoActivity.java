package com.vgtech.vancloud.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.android.volley.VolleyError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.vgtech.common.PrfUtils;
import com.vgtech.common.URLAddr;
import com.vgtech.common.api.JsonDataFactory;
import com.vgtech.common.api.RootData;
import com.vgtech.common.config.ImageOptions;
import com.vgtech.common.network.NetworkPath;
import com.vgtech.common.network.android.HttpListener;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.api.Option;
import com.vgtech.vancloud.ui.adapter.OptionAdapter;
import com.vgtech.vancloud.ui.view.NoScrollListview;
import com.vgtech.vancloud.utils.ActivityUtils;
import com.vgtech.vancloud.utils.HtmlUtils;
import com.vgtech.vancloud.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vic on 2017/3/11.
 */
public class QuestionInfoActivity extends BaseActivity implements HttpListener<String>, Animation.AnimationListener, AdapterView.OnItemClickListener {
    private static final int CALLBACK_ENTER = 1;
    private static final int CALLBACK_SUBMIT_ANSWER = 2;
    private OptionAdapter optionAdapter;
    private ProgressBar mQueProgressBar;
    private TextView mQueCount;
    private int mQueIndex = 1;
    private Animation mNextInAnimation;
    private Animation mNextOutAnimation;
    private Animation mPreviousInAnimation;
    private Animation mPreviousOutAnimation;
    private ViewFlipper mViewFlipper;
    private LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("题目信息");
        findViewById(R.id.btn_back).setVisibility(View.VISIBLE);
        mQueProgressBar = (ProgressBar) findViewById(R.id.question_progressbar);
        mQueCount = (TextView) findViewById(R.id.tv_question_progress);
        initView();
        init();
    }

    private View mQuestionInfoView;

    private void initView() {
        mInflater = getLayoutInflater();
        mViewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);
        mNextInAnimation = AnimationUtils.loadAnimation(this,
                R.anim.in_righttoleft);
        mNextOutAnimation = AnimationUtils.loadAnimation(this,
                R.anim.out_righttoleft);
        mNextOutAnimation.setAnimationListener(this);
        mPreviousInAnimation = AnimationUtils.loadAnimation(this,
                R.anim.in_lefttoright);
        mPreviousOutAnimation = AnimationUtils.loadAnimation(this,
                R.anim.out_lefttoright);
        mPreviousOutAnimation.setAnimationListener(mPreAnimationListener);
        mQuestionInfoView = mInflater.inflate(R.layout.question_item, null);
        mViewFlipper.addView(mQuestionInfoView);
    }

    private void init() {
        String testBegin = getIntent().getStringExtra("testBegin");
        try {
            JSONObject jsonObject = new JSONObject(testBegin);
            JSONObject myAnswer = jsonObject.getJSONObject("data").getJSONObject("myAnswer");
            String testPaperID = myAnswer.getString("testPaperID");
            String currQuestionID = myAnswer.getString("currQuestionID");
            String answerRecordID = myAnswer.getString("answerRecordID");
            enterQuestion(testPaperID, currQuestionID, answerRecordID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enterQuestion(String testPaperID, String currQuestionID, String answerRecordID) {
        Map<String, String> params = new HashMap<>();
        params.put("testPaperID", testPaperID);
        params.put("currQuestionID", currQuestionID);
        params.put("answerRecordID", answerRecordID);
        NetworkPath path = new NetworkPath(URLAddr.URL_TEST_QUESTIONVIEW, params, this);
        getAppliction().getNetworkManager().load(CALLBACK_ENTER, path, this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_question;
    }

    private View mSubmitBtn;

    @Override
    public void dataLoaded(int callbackId, NetworkPath path, RootData rootData) {
        dismisLoadingDialog();
        boolean safe = ActivityUtils.prehandleNetworkData(this, rootData);
        if (!safe) {
            return;
        }
        switch (callbackId) {
            case CALLBACK_ENTER:
                try {
                    mSubmitBtn = findViewById(R.id.btn_submit);
                    mSubmitBtn.setOnClickListener(this);
                    JSONObject jsonObject = rootData.getJson().getJSONObject("data");
                    String count = jsonObject.getJSONObject("scale").getString("questionNum");
                    String last = jsonObject.getString("questionNum2");
                    mQueIndex = Integer.parseInt(count) - Integer.parseInt(last);
                    updateQuestionProgress(Integer.parseInt(count), mQueIndex);
                    JSONObject questionObject = jsonObject.getJSONObject("question");
                    mQuestionInfoView.setTag(jsonObject);
                    initQuestionView(mQuestionInfoView, questionObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CALLBACK_SUBMIT_ANSWER:
                try {
                    JSONObject jsonObject = rootData.getJson().getJSONObject("data");
                    if (jsonObject.has("answerRecordList")) {
                        Intent intent = new Intent(this, RecordListActivity.class);
                        intent.putExtra("answerRecordList", jsonObject.getJSONArray("answerRecordList").toString());
                        startActivity(intent);
                        finish();
                    } else {
                        String count = jsonObject.getJSONObject("scale").getString("questionNum");
                        String last = jsonObject.getString("questionNum2");
                        mQueIndex = Integer.parseInt(count) - Integer.parseInt(last);
                        updateQuestionProgress(Integer.parseInt(count), mQueIndex);
                        JSONObject questionObject = jsonObject.getJSONObject("question");
                        mQuestionInfoView = mInflater.inflate(R.layout.question_item, null);
                        mQuestionInfoView.setTag(jsonObject);
                        initQuestionView(mQuestionInfoView, questionObject);
                        mViewFlipper.addView(mQuestionInfoView);
                        mViewFlipper.setInAnimation(mNextInAnimation);
                        mViewFlipper.setOutAnimation(mNextOutAnimation);
                        mViewFlipper.showNext();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                try {
                    submitAnswer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    private void submitAnswer() throws Exception {

        JSONObject scaleObject = (JSONObject) mQuestionInfoView.getTag();
        JSONObject myAnswer = scaleObject.getJSONObject("myAnswer");
        Map<String, String> params = new HashMap<>();
        params.put("mbrID", PrfUtils.getMbrId(this));
        params.put("testPaperID", myAnswer.getString("testPaperID"));
        params.put("answerRecordID", myAnswer.getString("answerRecordID"));
        params.put("currQuestionID", myAnswer.getString("currQuestionID"));
        params.put("questionGroupIndex", myAnswer.getString("questionGroupIndex"));
        JSONObject questionObject = scaleObject.getJSONObject("question");
        JSONArray jsonArray = questionObject.getJSONArray("optionGroups");
        for (int i = 0; i < jsonArray.length(); i++) {
            if (i == 0) {
                JSONObject groupObject = jsonArray.getJSONObject(i);
                int optionGroupType = groupObject.getInt("optionGroupType");
                if (optionGroupType == 0 || optionGroupType == 1 || optionGroupType == 2) {
                    List<Option> options = optionAdapter.getSelectedOption();
                    if (options.isEmpty()) {
                        ToastUtils.show(this, "必须答完题目！");
                        return;
                    }
                    StringBuffer optionValue = new StringBuffer();
                    for (Option option : options) {
                        optionValue.append(option.optionID).append(",");
                    }
                    params.put("optionValue", optionValue.deleteCharAt(optionValue.length() - 1).toString());
                } else if (optionGroupType == 3) {
                    EditText et_answer = (EditText) mQuestionInfoView.findViewById(R.id.et_answer);
                    String answer = et_answer.getText().toString();
                    if (TextUtils.isEmpty(answer)) {
                        ToastUtils.show(this, "必须答完题目！");
                        return;
                    }
                    params.put("optionValue_0", answer);
                }
                break;
            }
        }
        showLoadingDialog("");
        NetworkPath path = new NetworkPath(URLAddr.URL_TEST_SUBMITANSWER, params, this);
        getAppliction().getNetworkManager().load(CALLBACK_SUBMIT_ANSWER, path, this);
    }

    private void initQuestionView(View view, JSONObject questionObject) throws Exception {
        TextView tv_subtitle = (TextView) view.findViewById(R.id.tv_subtitle);
        SimpleDraweeView img_subtitle = (SimpleDraweeView) view.findViewById(R.id.img_subtitle);
        String subtitleContent = questionObject.getString("content");
        if (HtmlUtils.isImg(subtitleContent)) {
            ImageOptions.setImage(img_subtitle, HtmlUtils.getImgSrc(this, subtitleContent));
            tv_subtitle.setVisibility(View.GONE);
            img_subtitle.setVisibility(View.VISIBLE);
        } else {
            tv_subtitle.setText(subtitleContent);
            tv_subtitle.setVisibility(View.VISIBLE);
            img_subtitle.setVisibility(View.GONE);
        }


        JSONArray jsonArray = questionObject.getJSONArray("optionGroups");
        for (int i = 0; i < jsonArray.length(); i++) {
            if (i == 0) {
                JSONObject groupObject = jsonArray.getJSONObject(i);
                String content = groupObject.getString("content");
                TextView tv_desc = (TextView) view.findViewById(R.id.tv_desc);
                SimpleDraweeView img_desc = (SimpleDraweeView) view.findViewById(R.id.img_desc);

                if (!TextUtils.isEmpty(content)) {
                    if (HtmlUtils.isImg(content)) {
                        ImageOptions.setImage(img_desc, HtmlUtils.getImgSrc(this, content));
                        tv_desc.setVisibility(View.GONE);
                        img_desc.setVisibility(View.VISIBLE);
                    } else {
                        tv_desc.setText(content);
                        tv_desc.setVisibility(View.VISIBLE);
                        img_desc.setVisibility(View.GONE);
                    }
                } else {
                    tv_desc.setVisibility(View.GONE);
                    img_desc.setVisibility(View.GONE);
                }
                int optionGroupType = groupObject.getInt("optionGroupType");
                if (optionGroupType == 0 || optionGroupType == 1 || optionGroupType == 2) {
                    JSONArray optionArray = groupObject.getJSONArray("options");
                    List<Option> options = JsonDataFactory.getDataArray(Option.class, optionArray);
                    NoScrollListview listView = (NoScrollListview) view.findViewById(R.id.option_list);
                    listView.setItemClick(true);
                    listView.setVisibility(View.VISIBLE);
                    listView.setOnItemClickListener(this);
                    optionAdapter = new OptionAdapter(this);
                    if (optionGroupType == 2) {
                        mSignle = false;
                        optionAdapter.setOptionType(OptionAdapter.OPTIONTYPE_MUL);
                        mSubmitBtn.setVisibility(View.VISIBLE);
                    } else {
                        mSignle = true;
                        mSubmitBtn.setVisibility(View.GONE);
                    }
                    listView.setAdapter(optionAdapter);
                    optionAdapter.addAllDataAndNorify(options);
                } else if (optionGroupType == 3) {
                    mSignle = false;
                    mSubmitBtn.setVisibility(View.VISIBLE);
                    EditText et_answer = (EditText) view.findViewById(R.id.et_answer);
                    et_answer.setVisibility(View.VISIBLE);
                }
                break;
            }
        }

    }

    private boolean mSignle;

    private void updateQuestionProgress(int count, int cur) {
        mQueProgressBar.setMax(count);
        mQueProgressBar.setProgress(cur);
        mQueCount.setText(cur + "/" + count);
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(String response) {

    }

    private static final int MSG_PRE = 1;
    private static final int MSG_NEXT = 2;
    private Handler mAniHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_PRE: {
                    // releaseBitmap();
                    // initQuestionView();
                }
                break;
                case MSG_NEXT:
                    // releaseBitmap();
                    // initQuestionView();
                    break;
                default:
                    break;
            }
        }

        ;
    };
    private Animation.AnimationListener mPreAnimationListener = new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mViewFlipper.post(new Runnable() {
                @Override
                public void run() {
                    if (mViewFlipper.getChildCount() > 1) {
                        mViewFlipper.setInAnimation(null);
                        mViewFlipper.setOutAnimation(null);
                        mViewFlipper.removeViewAt(0);
                        mAniHandler.sendEmptyMessage(MSG_PRE);
                    }
                }

            });

        }
    };

    @Override
    public void onAnimationEnd(Animation animation) {
        mViewFlipper.post(new Runnable() {
            @Override
            public void run() {
                if (mViewFlipper.getChildCount() > 1) {
                    mViewFlipper.setInAnimation(null);
                    mViewFlipper.setOutAnimation(null);
                    mViewFlipper.removeViewAt(0);
                    mAniHandler.sendEmptyMessage(MSG_NEXT);
                }
            }

        });
    }

    @Override
    public void onAnimationRepeat(Animation arg0) {

    }

    @Override
    public void onAnimationStart(Animation arg0) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object obj = parent.getItemAtPosition(position);
        if (obj instanceof Option) {
            Option option = (Option) obj;
            optionAdapter.addSelectedOption(option);
            if (mSignle) {
                try {
                    submitAnswer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
