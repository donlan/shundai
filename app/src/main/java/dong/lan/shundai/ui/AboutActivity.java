package dong.lan.shundai.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.push.FeedbackPush;
import com.umeng.message.PushAgent;

import dong.lan.shundai.MyPushIntentService;
import dong.lan.shundai.R;

/**
 * Created by 梁桂栋 on 2015/12/29.
 */
public class AboutActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setUpUmengFeedback();
        findViewById(R.id.feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fb.startFeedbackActivity();
            }
        });
        findViewById(R.id.back_forward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    FeedbackAgent fb;
    private void setUpUmengFeedback() {
        fb = new FeedbackAgent(this);
        fb.sync();
        fb.openAudioFeedback();
        fb.openFeedbackPush();
        PushAgent.getInstance(this).enable();

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
}
