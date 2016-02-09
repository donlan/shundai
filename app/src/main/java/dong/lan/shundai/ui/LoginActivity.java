package dong.lan.shundai.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.sms.BmobSMS;
import cn.bmob.v3.listener.SaveListener;
import dong.lan.shundai.R;
import dong.lan.shundai.bean.User;
import dong.lan.shundai.broadcast.MyBroadcastReceiver;
import dong.lan.shundai.config.BmobConstants;
import dong.lan.shundai.config.Config;
import dong.lan.shundai.util.CommonUtils;

/**
 * Created by 梁桂栋 on 2015/12/21.
 */
public class LoginActivity extends BaseActivity {


    EditText username;
    EditText password;
    TextView login;
    TextView register;
    TextView forgotPWD;
    private MyBroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        receiver = new MyBroadcastReceiver(this);
        BmobSMS.initialize(this, Config.applicationId);
        username = (EditText) findViewById(R.id.et_username);
        password = (EditText) findViewById(R.id.et_password);
        login = (TextView) findViewById(R.id.btn_login_new);
        register = (TextView) findViewById(R.id.register_new);
        forgotPWD = (TextView) findViewById(R.id.forgot_pass);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isNetConnected = CommonUtils.isNetworkAvailable(LoginActivity.this);
                if (!isNetConnected) {
                    Toast.makeText(LoginActivity.this,R.string.network_tips,Toast.LENGTH_SHORT).show();
                    return;
                }
                login();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,
                        RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        });
        forgotPWD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimActivity(new Intent(LoginActivity.this,ResetPWDActivity.class));
            }
        });
//        注册退出广播
        initReceiver();
    }

    private void login() {
        String name = username.getText().toString();
        String pwd = password.getText().toString();

        if (TextUtils.isEmpty(name)) {

            Toast.makeText(LoginActivity.this,R.string.toast_error_username_null,Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(LoginActivity.this,R.string.toast_error_password_null,Toast.LENGTH_SHORT).show();
            return;
        }

        resetLogin(name, pwd);
    }

    private void resetLogin(String name, String password) {
        final ProgressDialog progress = new ProgressDialog(
                LoginActivity.this);
        progress.setMessage("正在登陆...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        User user = new User();
        user.setUsername(name);
        user.setPassword(password);
        userManager.login(user, new SaveListener() {

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        progress.setMessage("正在获取好友列表...");
                    }
                });
                updateUserInfos();
                progress.dismiss();
                Intent intent = new Intent(LoginActivity.this, NearPeopleActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                Intent filter = new Intent();
                filter.setAction(BmobConstants.ACTION_FINISH);
                sendBroadcast(filter);
            }

            @Override
            public void onFailure(int errorcode, String arg0) {
                progress.dismiss();
                ShowToast(arg0);
            }
        });

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
