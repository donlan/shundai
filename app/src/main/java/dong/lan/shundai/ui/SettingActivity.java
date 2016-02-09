package dong.lan.shundai.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.push.FeedbackPush;
import com.umeng.message.PushAgent;

import cn.bmob.im.BmobUserManager;
import dong.lan.shundai.BuildConfig;
import dong.lan.shundai.MyApplication;
import dong.lan.shundai.MyPushIntentService;
import dong.lan.shundai.R;
import dong.lan.shundai.util.SharePreferenceUtil;


/**
 * Created by 桂栋 on 2015/5/10.
 */
public class SettingActivity  extends ActivityBase implements View.OnClickListener {


    Button btn_logout;
    TextView tv_set_name;
    RelativeLayout layout_info, rl_switch_notification, rl_switch_voice,
            rl_switch_vibrate,layout_blacklist,about,moreSettting;

    ImageView iv_open_notification, iv_close_notification, iv_open_voice,
            iv_close_voice, iv_open_vibrate, iv_close_vibrate;
    SharePreferenceUtil mSharedUtil;
    LinearLayout mainLayout;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_set);
        mSharedUtil = mApplication.getSpUtil();
        initView();
        initData();
        setUpUmengFeedback();
    }
    private void initView() {
        initTopBarForOnlyTitle("设置");
        mainLayout = (LinearLayout) findViewById(R.id.set_main_layout);
        layout_blacklist = (RelativeLayout) findViewById(R.id.layout_blacklist);
        layout_info = (RelativeLayout) findViewById(R.id.layout_info);
        rl_switch_notification = (RelativeLayout) findViewById(R.id.rl_switch_notification);
        rl_switch_voice = (RelativeLayout) findViewById(R.id.rl_switch_voice);
        rl_switch_vibrate = (RelativeLayout) findViewById(R.id.rl_switch_vibrate);
        about= (RelativeLayout) findViewById(R.id.about);
        moreSettting= (RelativeLayout) findViewById(R.id.moreSetting);
        moreSettting.setOnClickListener(this);
        about.setOnClickListener(this);
        rl_switch_notification.setOnClickListener(this);
        rl_switch_voice.setOnClickListener(this);
        rl_switch_vibrate.setOnClickListener(this);

        iv_open_notification = (ImageView) findViewById(R.id.iv_open_notification);
        iv_close_notification = (ImageView) findViewById(R.id.iv_close_notification);
        iv_open_voice = (ImageView) findViewById(R.id.iv_open_voice);
        iv_close_voice = (ImageView) findViewById(R.id.iv_close_voice);
        iv_open_vibrate = (ImageView) findViewById(R.id.iv_open_vibrate);
        iv_close_vibrate = (ImageView) findViewById(R.id.iv_close_vibrate);

        tv_set_name = (TextView) findViewById(R.id.tv_set_name);
        btn_logout = (Button) findViewById(R.id.btn_logout);

        // 初始化
        boolean isAllowNotify = mSharedUtil.isAllowPushNotify();

        if (isAllowNotify) {
            iv_open_notification.setVisibility(View.VISIBLE);
            iv_close_notification.setVisibility(View.INVISIBLE);
        } else {
            iv_open_notification.setVisibility(View.INVISIBLE);
            iv_close_notification.setVisibility(View.VISIBLE);
        }
        boolean isAllowVoice = mSharedUtil.isAllowVoice();
        if (isAllowVoice) {
            iv_open_voice.setVisibility(View.VISIBLE);
            iv_close_voice.setVisibility(View.INVISIBLE);
        } else {
            iv_open_voice.setVisibility(View.INVISIBLE);
            iv_close_voice.setVisibility(View.VISIBLE);
        }
        boolean isAllowVibrate = mSharedUtil.isAllowVibrate();
        if (isAllowVibrate) {
            iv_open_vibrate.setVisibility(View.VISIBLE);
            iv_close_vibrate.setVisibility(View.INVISIBLE);
        } else {
            iv_open_vibrate.setVisibility(View.INVISIBLE);
            iv_close_vibrate.setVisibility(View.VISIBLE);
        }
        btn_logout.setOnClickListener(this);
        layout_info.setOnClickListener(this);
        layout_blacklist.setOnClickListener(this);


    }

    private void initData() {
        tv_set_name.setText(BmobUserManager.getInstance(this)
                .getCurrentUser().getUsername());
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    FeedbackAgent fb;
    private void setUpUmengFeedback() {
        fb = new FeedbackAgent(this);
        // check if the app developer has replied to the feedback or not.
        fb.sync();
        fb.openAudioFeedback();
        fb.openFeedbackPush();
        PushAgent.getInstance(this).enable();

        //fb.setWelcomeInfo();
        fb.setWelcomeInfo("感谢您使用顺带，欢迎您反馈使用产品的感受和建议。");
        FeedbackPush.getInstance(this).init(true);
        PushAgent.getInstance(this).setPushIntentServiceClass(MyPushIntentService.class);


        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean result = fb.updateUserInfo();
            }
        }).start();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.moreSetting:
                startAnimActivity(new Intent(SettingActivity.this,VerifyEmailActivity.class));
                break;
            case R.id.about:
                startAnimActivity(new Intent(SettingActivity.this,AboutActivity.class));
                break;
            case R.id.layout_blacklist:// 启动到黑名单页面
                startAnimActivity(new Intent(this,BlackListActivity.class));
                break;
            case R.id.layout_info:// 启动到个人资料页面
                Intent intent =new Intent(this,SetMyInfoActivity.class);
                intent.putExtra("from", "me");
                startActivity(intent);
                break;
            case R.id.btn_logout:
                MyApplication.getInstance().logout(new LoginListener() {
                    @Override
                    public void onStart() {
                        if (BuildConfig.DEBUG) Log.d("SettingActivity", "退出有盟社区成功");
                    }

                    @Override
                    public void onComplete(int i, CommUser commUser) {
                        if (BuildConfig.DEBUG) Log.d("SettingActivity", "退出有盟社区失败");
                    }
                });
                SettingActivity.this.finish();
                startActivity(new Intent(this, GuideLoginActivity.class));
                break;
            case R.id.rl_switch_notification:
                if (iv_open_notification.getVisibility() == View.VISIBLE) {
                    iv_open_notification.setVisibility(View.INVISIBLE);
                    iv_close_notification.setVisibility(View.VISIBLE);
                    mSharedUtil.setPushNotifyEnable(false);
                    rl_switch_vibrate.setVisibility(View.GONE);
                    rl_switch_voice.setVisibility(View.GONE);
                } else {
                    iv_open_notification.setVisibility(View.VISIBLE);
                    iv_close_notification.setVisibility(View.INVISIBLE);
                    mSharedUtil.setPushNotifyEnable(true);
                    rl_switch_vibrate.setVisibility(View.VISIBLE);
                    rl_switch_voice.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.rl_switch_voice:
                if (iv_open_voice.getVisibility() == View.VISIBLE) {
                    iv_open_voice.setVisibility(View.INVISIBLE);
                    iv_close_voice.setVisibility(View.VISIBLE);
                    mSharedUtil.setAllowVoiceEnable(false);
                } else {
                    iv_open_voice.setVisibility(View.VISIBLE);
                    iv_close_voice.setVisibility(View.INVISIBLE);
                    mSharedUtil.setAllowVoiceEnable(true);
                }

                break;
            case R.id.rl_switch_vibrate:
                if (iv_open_vibrate.getVisibility() == View.VISIBLE) {
                    iv_open_vibrate.setVisibility(View.INVISIBLE);
                    iv_close_vibrate.setVisibility(View.VISIBLE);
                    mSharedUtil.setAllowVibrateEnable(false);
                } else {
                    iv_open_vibrate.setVisibility(View.VISIBLE);
                    iv_close_vibrate.setVisibility(View.INVISIBLE);
                    mSharedUtil.setAllowVibrateEnable(true);
                }
                break;


        }
    }
}
