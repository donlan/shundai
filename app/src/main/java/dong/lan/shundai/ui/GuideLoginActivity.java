package dong.lan.shundai.ui;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import dong.lan.shundai.R;
import dong.lan.shundai.broadcast.MyBroadcastReceiver;
import dong.lan.shundai.config.BmobConstants;

/**
 */
public class GuideLoginActivity extends Activity implements OnClickListener {

    TextView btn_login;
    TextView btn_register;
    private MyBroadcastReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_login);
        receiver = new MyBroadcastReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BmobConstants.ACTION_FINISH);
        registerReceiver(receiver, filter);
        init();
    }

    private void init() {
        btn_login = (TextView) findViewById(R.id.btn_login);
        btn_register = (TextView) findViewById(R.id.btn_register);
        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                startActivity(new Intent(GuideLoginActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                break;
            case R.id.btn_register:
                Intent intent = new Intent(GuideLoginActivity.this,
                        RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_left,R.anim.slide_out_to_right);
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        ObjectAnimator.ofFloat(btn_login, "translationY", 2000f, 1f).setDuration(1000).start();
        ObjectAnimator.ofFloat(btn_register,"translationY",2000f,1f).setDuration(1000).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent filter = new Intent();
        filter.setAction(BmobConstants.ACTION_FINISH);
        sendBroadcast(filter);
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
