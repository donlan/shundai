package dong.lan.shundai.ui;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.baidu.mapapi.SDKInitializer;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import dong.lan.shundai.R;
import dong.lan.shundai.adapter.MyPagerAdapter;
import dong.lan.shundai.util.AnimationUtil;
import dong.lan.shundai.util.SP;

/**
 * 引导页
 */
public class SplashActivity extends BaseActivity {

    ViewPager pager;
    FrameLayout pic1;


    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        SP.init(this);
        pic1 = (FrameLayout) findViewById(R.id.pic1);
        pager = (ViewPager) findViewById(R.id.welcome_pager);
        MobclickAgent.updateOnlineConfig(this);
        AnalyticsConfig.enableEncrypt(true);
        if (!SP.isLoad()) {
            if (userManager.getCurrentUser() != null) {
                // 每次自动登陆的时候就需要更新下当前位置和好友的资料，因为好友的头像，昵称啥的是经常变动的
                pager.setVisibility(View.GONE);
                pic1.setVisibility(View.VISIBLE);
                ObjectAnimator.ofFloat(pic1, "alpha", 0.5f, 1f).setDuration(1200).start();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        SP.setLoad(false);
                        startAnimActivity(NearPeopleActivity.class);
                        finish();
                    }
                }, 1500);
            } else {
                SP.setLoad(false);
                startActivity(new Intent(SplashActivity.this, GuideLoginActivity.class));
                finish();
            }
            SP.setLoad(false);
        }

        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        initView();
    }

    private void initView() {
        List<View> views = new ArrayList<>();
        views.add(LayoutInflater.from(this).inflate(R.layout.welcome_pager1, null));
        views.add(LayoutInflater.from(this).inflate(R.layout.welcome_pager2, null));
        views.add(LayoutInflater.from(this).inflate(R.layout.welcome_pager3, null));
        views.add(LayoutInflater.from(this).inflate(R.layout.welcome_pager4, null));
        views.get(3).findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userManager.getCurrentUser() != null) {
                    updateUserInfos();
                    startAnimActivity(NearPeopleActivity.class);
                    SP.setLoad(false);
                    AnimationUtil.finishActivityAnimation(SplashActivity.this);
                } else {
                    startActivity(new Intent(SplashActivity.this, GuideLoginActivity.class));
                    SP.setLoad(false);
                    AnimationUtil.finishActivityAnimation(SplashActivity.this);
                }
            }
        });
        pager.setAdapter(new MyPagerAdapter(views));
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (userManager.getCurrentUser() != null) {
            updateUserInfos();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
