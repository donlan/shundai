package dong.lan.shundai.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.listener.SaveListener;
import dong.lan.shundai.R;
import dong.lan.shundai.adapter.EmoViewPagerAdapter;
import dong.lan.shundai.adapter.EmoteAdapter;
import dong.lan.shundai.bean.FaceText;
import dong.lan.shundai.bean.Help;
import dong.lan.shundai.bean.User;
import dong.lan.shundai.util.CommonUtils;
import dong.lan.shundai.util.FaceTextUtils;
import dong.lan.shundai.util.MyConstant;
import dong.lan.shundai.view.EmoticonsEditText;

/**
 * Created by 桂栋 on 2015/7/29.
 */
public class Activity_Publish extends ActivityBase implements View.OnClickListener {
    EmoticonsEditText edit_user_comment;
    private LinearLayout layout_emo;
    private String tag;
    private RadioButton rb_wu, rb_ren, rb_qita;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        tag = getIntent().getStringExtra("TAG");
        init();
    }

    private void init() {
        TextView back = (TextView) findViewById(R.id.back_forward);
        TextView tittle = (TextView) findViewById(R.id.title_bar);
        TextView right = (TextView) findViewById(R.id.bar_right);
        TextView emo = (TextView) findViewById(R.id.add_emo);
        layout_emo = (LinearLayout) findViewById(R.id.layout_emo);
        back.setOnClickListener(this);
        emo.setOnClickListener(this);
        right.setOnClickListener(this);
        tittle.setText("写帮忙");
        right.setCompoundDrawables(null, null, null, null);
        back.setText("取消");
        back.setCompoundDrawables(null, null, null, null);
        back.setTextSize(15);
        back.setTextColor(Color.WHITE);
        back.setBackgroundResource(R.color.normal_bg_dark);
        right.setText("发布");
        right.setBackgroundResource(R.color.normal_bg_dark);
        right.setTextColor(Color.WHITE);
        right.setTextSize(15);
        rb_qita = (RadioButton) findViewById(R.id.radio_qita);
        rb_ren = (RadioButton) findViewById(R.id.radio_ren);
        rb_wu = (RadioButton) findViewById(R.id.radio_wu);

        edit_user_comment = (EmoticonsEditText) findViewById(R.id.edit_publish);
        edit_user_comment.setOnClickListener(this);
        edit_user_comment.requestFocus();
        showSoftInputView();
        edit_user_comment.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        initEmoView();
    }

    List<FaceText> emos;

    private View getGridView(final int i) {
        View view = View.inflate(this, R.layout.include_emo_gridview, null);
        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        List<FaceText> list = new ArrayList<FaceText>();
        if (i == 0) {
            list.addAll(emos.subList(0, 21));
        } else if (i == 1) {
            list.addAll(emos.subList(21, emos.size()));
        }
        final EmoteAdapter gridAdapter = new EmoteAdapter(Activity_Publish.this,
                list);
        gridview.setAdapter(gridAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                FaceText name = (FaceText) gridAdapter.getItem(position);
                String key = name.text.toString();
                try {
                    if (edit_user_comment != null && !TextUtils.isEmpty(key)) {
                        int start = edit_user_comment.getSelectionStart();
                        CharSequence content = edit_user_comment.getText()
                                .insert(start, key);
                        edit_user_comment.setText(content);
                        // 定位光标位置
                        CharSequence info = edit_user_comment.getText();
                        if (info instanceof Spannable) {
                            Spannable spanText = (Spannable) info;
                            Selection.setSelection(spanText,
                                    start + key.length());
                        }
                    }
                } catch (Exception e) {

                }

            }
        });
        return view;
    }

    // 显示软键盘
    public void showSoftInputView() {
        if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .showSoftInput(edit_user_comment, 0);
        }
    }

    /**
     * 初始化表情布局
     *
     * @param
     * @return void
     * @throws
     * @Title: initEmoView
     * @Description: TODO
     */
    private void initEmoView() {
        ViewPager pager_emo = (ViewPager) findViewById(R.id.pager_emo);
        emos = FaceTextUtils.faceTexts;

        List<View> views = new ArrayList<View>();
        for (int i = 0; i < 2; ++i) {
            views.add(getGridView(i));
        }
        pager_emo.setAdapter(new EmoViewPagerAdapter(views));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_emo:
                if(layout_emo.getVisibility()==View.GONE) {
                    showEditState(true);
                    layout_emo.setVisibility(View.VISIBLE);
                }else
                {
                    showEditState(false);
                    layout_emo.setVisibility(View.GONE);
                }
                break;
            case R.id.back_forward:
                finish();
                break;
            case R.id.bar_right:
                publish();
                break;
            case R.id.edit_publish:
                showEditState(false);
                break;
        }
    }

    private String getRaido() {
        tag = tag.substring(0, 2);
        if (rb_ren.isChecked() && !rb_wu.isChecked() && !rb_qita.isChecked())
            return tag + "带人";
        if (!rb_ren.isChecked() && rb_wu.isChecked() && !rb_qita.isChecked())
            return tag + "带物";
        if (!rb_ren.isChecked() && !rb_wu.isChecked() && rb_qita.isChecked())
            return tag + "其他";

        return MyConstant.S_R;
    }

    private void Publish(String content, String tag) {
        final Help help = new Help();
        help.setInfo(content);
        User user = userManager.getCurrentUser(User.class);
        help.setComm(0);
        help.setSee(0);
        help.setUser(user);
        help.setTag(tag);
        help.setCity(user.getCity());
        help.setLocation(user.getLocation());
        help.save(Activity_Publish.this, new SaveListener() {
            @Override
            public void onSuccess() {
                ShowToast("发布成功");
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                ShowToast("发布失败");
            }
        });
    }

    private void publish() {
        boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
        if (!isNetConnected) {
            ShowToast(R.string.network_tips);
            return;
        }
        String s = edit_user_comment.getText().toString();

        Publish(s, getRaido());
        finish();

    }

    /**
     * 根据是否点击笑脸来显示文本输入框的状态
     */
    private void showEditState(boolean isEmo) {
        edit_user_comment.setVisibility(View.VISIBLE);
        edit_user_comment.requestFocus();
        if (isEmo) {
            layout_emo.setVisibility(View.VISIBLE);
            hideSoftInputView();
        } else {
            layout_emo.setVisibility(View.GONE);
            showSoftInputView();
        }
    }
}
