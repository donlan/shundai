package dong.lan.shundai.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.EmailVerifyListener;
import cn.bmob.v3.listener.UpdateListener;
import dong.lan.shundai.R;
import dong.lan.shundai.bean.User;

/**
 * Created by 桂栋 on 2015/6/19.
 */
public class VerifyEmailActivity extends BaseActivity {


    private EditText mailAddr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_mail);
        initView();
    }

    private void Verify_mail(final String email) {
        BmobUser.requestEmailVerify(VerifyEmailActivity.this, email, new EmailVerifyListener() {
            @Override
            public void onSuccess() {
                ShowToast("请求验证邮件成功，请到" + email + "邮箱中进行激活。");
            }

            @Override
            public void onFailure(int code, String e) {
                ShowToast("请求验证邮件失败:" + e);
            }
        });
    }

    private void initView() {
        ((TextView)findViewById(R.id.title_bar)).setText("验证邮箱");
        findViewById(R.id.back_forward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.bar_right).setVisibility(View.GONE);
        mailAddr = (EditText) findViewById(R.id.ver_email);
        findViewById(R.id.post_ver_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mailAddr.getText().toString();

                if (userManager.getCurrentUser(User.class).getEmail() == null) {
                    User user = new User();
                    user.setEmail(mailAddr.getText().toString());
                    user.update(VerifyEmailActivity.this, userManager.getCurrentUserObjectId(), new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            Verify_mail(email);
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            ShowToast("设置邮箱失败");
                        }
                    });
                } else if (email.equals(userManager.getCurrentUser().getEmail())) {
                    ShowToast("您已经认证了邮箱啦");
                    // Verify_mail(email);
                } else {
                    ShowToast("验证邮箱不一致");
                }
            }
        });

    }
}


