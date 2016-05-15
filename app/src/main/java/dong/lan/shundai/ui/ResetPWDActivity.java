package dong.lan.shundai.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.ResetPasswordByCodeListener;
import cn.bmob.v3.listener.ResetPasswordByEmailListener;
import cn.bmob.v3.listener.SaveListener;
import dong.lan.shundai.R;
import dong.lan.shundai.bean.User;

/**
 * Created by 梁桂栋 on 2015/12/21.
 */
public class ResetPWDActivity extends BaseActivity {
    private boolean Click = false;
    private int VER = 60;
    private boolean isMail = false;//is use email to reset password
    private Handler handler;
    LinearLayout getResetLayout;
    LinearLayout ResetLayout;
    LinearLayout emailLayout;
    EditText name, phone, reset_code, pass, ver_pass,email;
    TextView getSmsCode, done,verifySwitcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        getResetLayout = (LinearLayout) findViewById(R.id.getResetLayout);
        ResetLayout = (LinearLayout) findViewById(R.id.ResetLayout);
        name = (EditText) findViewById(R.id.reset_username);
        phone = (EditText) findViewById(R.id.reset_phoneNum);
        reset_code = (EditText) findViewById(R.id.reset_check_num);
        pass = (EditText) findViewById(R.id.reset_password);
        ver_pass = (EditText) findViewById(R.id.verify_reset_pass);
        getSmsCode = (TextView) findViewById(R.id.getResetCode);
        done = (TextView) findViewById(R.id.VerifyCode);
        verifySwitcher = (TextView) findViewById(R.id.verify_switcher);
        emailLayout = (LinearLayout) findViewById(R.id.email_layout);
        findViewById(R.id.img_missingPop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        done.setText("验证");
        getSmsCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUser(name.getText().toString(), phone.getText().toString());
            }
        });
        verifySwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMail){
                  isMail = false;
                    getResetLayout.setVisibility(View.VISIBLE);
                    ResetLayout.setVisibility(View.VISIBLE);
                    emailLayout.setVisibility(View.GONE);
                    done.setText("验证");
                }else{
                    isMail =true;
                    getResetLayout.setVisibility(View.GONE);
                    ResetLayout.setVisibility(View.GONE);
                    emailLayout.setVisibility(View.VISIBLE);
                    done.setText("Send e-mail");
                }
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isMail) {
                    switch (done.getText().toString()) {
                        case "验证":
                            verifySmsCode();
                            break;
                        case "设置密码并登陆":
                            if (!pass.getText().toString().equals(ver_pass.getText().toString())) {
                                ShowToast("两次密码不一致，请重新输入密码");
                                return;
                            } else {
                                BmobUser.resetPasswordBySMSCode(ResetPWDActivity.this, reset_code.getText().toString(), pass.getText().toString(), new ResetPasswordByCodeListener() {
                                    @Override
                                    public void done(BmobException ex) {
                                        if (ex == null) {
                                            ShowToast("密码重置成功");
                                            resetLogin(name.getText().toString(), ver_pass.getText().toString());
                                            ResetLayout.setVisibility(View.VISIBLE);
                                            getSmsCode.setVisibility(View.GONE);
                                        } else {
                                            ShowToast("重置失败：错误代码 =" + ex.getErrorCode() + ",错误描述 = " + ex.getLocalizedMessage());
                                        }
                                    }
                                });
                            }

                            break;
                    }

                } else {
                    BmobUser.resetPasswordByEmail(ResetPWDActivity.this, email.getText().toString(), new ResetPasswordByEmailListener() {
                        @Override
                        public void onSuccess() {
                            ShowToast("We have send a reset password link to your email: "+email.getText().toString());
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            ShowToast("Got some error :"+s);
                        }
                    });
                }
            }
        });

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x123456) {
                    if (VER == 0) {
                        VER = 60;
                        getSmsCode.setBackgroundResource(R.color.normal_bg_three);
                        getSmsCode.setTextColor(Color.WHITE);
                        getSmsCode.setText("获取验证码");
                        Click = false;
                    } else {
                        getSmsCode.setBackgroundResource(R.color.md_grey_600);
                        getSmsCode.setTextColor(Color.DKGRAY);
                        getSmsCode.setText(VER + "");
                        VER--;
                    }
                }
            }
        };

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (Click)
                    handler.sendEmptyMessage(0x123456);
            }
        }, 0, 1000);
    }


    private void checkUser(String username, final String phoneNum) {
        if (!Click) {
            ShowToast("核对用户手机绑定信息...");
            BmobQuery<User> query = new BmobQuery<User>();
            query.addWhereEqualTo("username", username);
            query.findObjects(getBaseContext(), new FindListener<User>() {
                @Override
                public void onSuccess(List<User> list) {
                    if (list.isEmpty()) {
                        ShowToast("用户不存在");
                    } else if (list.size() >= 1) {
                        if (!phoneNum.equals(list.get(0).getMobilePhoneNumber()) && list.get(0).getMobilePhoneNumberVerified()) {
                            ShowToast("当前号码与绑定号码不一致！");
                            return;
                        }
                        if (phone.getText().toString().length() != 11) {
                            ShowToast("请核对手机号码");
                            return;
                        }
                        if (!Click) {
                            requestSmsCode();
                        } else {
                            ShowToast("请等待" + VER + "秒后在点击获取验证码");
                        }
                    } else {
                        ShowToast("用户检验失败");
                    }
                }

                @Override
                public void onError(int i, String s) {
                    ShowToast("用户检验失败");
                }
            });
        }
    }

    private void requestSmsCode() {
        ShowToast("验证用户完毕，开始发送验证码");
        String number = phone.getText().toString();
        if (!TextUtils.isEmpty(number)) {
            cn.bmob.v3.BmobSMS.requestSMSCode(this, number, "注册模板", new RequestSMSCodeListener() {

                @Override
                public void done(Integer smsId, BmobException ex) {
                    if (ex == null) {//验证码发送成功
                        Click = true;
                        ShowToast("验证码发送成功");//用于查询本次短信发送详情
                    }
                }
            });
        } else {
            ShowToast("请输入手机号码");
        }
    }

    String code;

    private void verifySmsCode() {
        String number = phone.getText().toString();
        code = reset_code.getText().toString();
        if (!TextUtils.isEmpty(number) && !TextUtils.isEmpty(code)) {
            ResetLayout.setVisibility(View.VISIBLE);
            done.setText("设置密码并登陆");
        } else {
            ShowToast("请输入手机号和验证码");
        }
    }

    private void resetLogin(String name, String password) {
        final ProgressDialog progress = new ProgressDialog(
                ResetPWDActivity.this);
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
                Intent intent = new Intent(ResetPWDActivity.this, NearPeopleActivity.class);
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }

            @Override
            public void onFailure(int errorcode, String arg0) {
                progress.dismiss();
                ShowToast(arg0);
            }
        });

    }
}
