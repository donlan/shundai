package dong.lan.shundai.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;
import dong.lan.shundai.R;
import dong.lan.shundai.bean.RecentlyCity;
import dong.lan.shundai.bean.SUser;
import dong.lan.shundai.bean.User;
import dong.lan.shundai.bean.UserCity;
import dong.lan.shundai.bean.WallPhoto;
import dong.lan.shundai.config.BmobConstants;
import dong.lan.shundai.config.Config;
import dong.lan.shundai.util.CommonUtils;

public class RegisterActivity extends BaseActivity implements OnClickListener {


    Button btn_register;
    EditText et_username, et_password, et_password_again, phoneNum, check_num;
    TextView random_check_num, agree;
    RadioButton female;
    private static String loc = "全国";
    private static int Count = 0;
    public LocationClient mLocationClient = null;
    public BDLocationListener locationListener = new MyLocationListener();
    private String name;
    private String phone;
    private String password;
    private int VER = 60;
    private boolean Click = false;
    private boolean isOversea = false;  //是否是海外用户
    private LinearLayout viewOfVerifyCode;
    private LinearLayout viewOfPhone;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        cn.bmob.sms.BmobSMS.initialize(this, Config.applicationId);
        mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
        mLocationClient.registerLocationListener(locationListener); // 注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开GPS
        option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
        option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(3000);// 设置发起定位请求的间隔时间为3000ms
        mLocationClient.setLocOption(option);// 使用设置
        mLocationClient.start();// 开启定位SDK
        mLocationClient.requestLocation();// 开始请求位置

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x123456) {
                    if (VER == 0) {
                        VER = 60;
                        random_check_num.setBackgroundResource(R.color.normal_bg_three);
                        random_check_num.setTextColor(Color.WHITE);
                        random_check_num.setText(R.string.get_verify_code);
                        Click = false;
                    } else {
                        random_check_num.setBackgroundResource(R.color.md_grey_500);
                        random_check_num.setTextColor(Color.DKGRAY);
                        random_check_num.setText(VER + "");
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
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        et_password_again = (EditText) findViewById(R.id.et_password_again);
        phoneNum = (EditText) findViewById(R.id.phoneNum);
        agree = (TextView) findViewById(R.id.use_agree);
        female = (RadioButton) findViewById(R.id.sex_female);
        agree.setOnClickListener(this);
        btn_register = (Button) findViewById(R.id.btn_register);
        random_check_num = (TextView) findViewById(R.id.random_check_num);
        viewOfPhone = (LinearLayout) findViewById(R.id.view_phone);
        viewOfVerifyCode = (LinearLayout) findViewById(R.id.view_verify_code);
        TextView versionSwitcher = (TextView) findViewById(R.id.version_switcher);

        versionSwitcher.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isOversea){
                    viewOfVerifyCode.setVisibility(View.GONE);
                    viewOfPhone.setVisibility(View.GONE);
                    et_username.setHint("2-16 Character or Number");
                    et_password.setHint("Password");
                    et_password_again.setHint("Password");
                    isOversea = true;
                }else{
                    viewOfVerifyCode.setVisibility(View.VISIBLE);
                    viewOfPhone.setVisibility(View.VISIBLE);
                    et_username.setHint(getString(R.string.register_username_tip_ch));
                    et_password.setHint(getString(R.string.login_pwd));
                    et_password_again.setHint(getString(R.string.login_pwd_again));
                    isOversea = true;
                }
            }
        });
        random_check_num.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMobileNO(phoneNum.getText().toString().trim())) {
                    ShowToast(getString(R.string.please_input_a_ok_phone_number));
                } else {
                    if (!Click) {
                        Click = true;
                        requestSmsCode();
                    } else {
                        if (VER != 60)
                            ShowToast("请等待" + VER + "秒后再点击获取验证码");
                    }
                }
            }
        });
        findViewById(R.id.back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        check_num = (EditText) findViewById(R.id.check_num);
        btn_register.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                register();
            }
        });
        checkUser();
    }

    private void checkUser() {
        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("username", "smile");
        query.findObjects(this, new FindListener<User>() {

            @Override
            public void onError(int arg0, String arg1) {
            }

            @Override
            public void onSuccess(List<User> arg0) {
                if (arg0 != null && arg0.size() > 0) {
                    User user = arg0.get(0);
                    user.setPassword("1234567");
                    user.update(RegisterActivity.this, new UpdateListener() {

                        @Override
                        public void onSuccess() {
                            userManager.login("smile", "1234567", new SaveListener() {

                                @Override
                                public void onSuccess() {
                                    Log.i("smile", "登陆成功");
                                }

                                @Override
                                public void onFailure(int code, String msg) {
                                    // TODO Auto-generated method stub
                                    Log.i("smile", "登陆失败：" + code + ".msg = " + msg);
                                }
                            });
                        }

                        @Override
                        public void onFailure(int code, String msg) {
                            // TODO Auto-generated method stub

                        }
                    });
                }
            }
        });
    }

    private void register() {
        name = et_username.getText().toString();
        password = et_password.getText().toString();
        String pwd_again = et_password_again.getText().toString();
        phone = phoneNum.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ShowToast(R.string.toast_error_username_null);
            return;
        }
        if (name.length() < 2 || name.length() > 16) {
            ShowToast(getString(R.string.register_username_tip_ch));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ShowToast(R.string.toast_error_password_null);
            return;
        }
        if (!pwd_again.equals(password)) {
            ShowToast(R.string.toast_error_comfirm_password);
            return;
        }
        if (!goodPWD(password)) {
            ShowToast(getString(R.string.register_password_tip_ch));
            return;
        }
        if (!isOversea && !isMobileNO(phone)) {
            ShowToast(getString(R.string.illegal_phone_number));
            return;
        }

        boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
        if (!isNetConnected) {
            ShowToast(R.string.network_tips);
            return;
        }
        verifySmsCode();


    }


    private void registerAction(){
        final ProgressDialog progress = new ProgressDialog(RegisterActivity.this);
        progress.setMessage(getString(R.string.registing));
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        final User bu = new User();
        bu.setUsername(name);
        bu.setPassword(password);
        bu.setTrends("");
        bu.setAge("");
        bu.setSex(female.isChecked());
        bu.setConstllation("未知");
        bu.setMeili("5.0");
        bu.setHonest("5.0");
        bu.setCity(loc);
        if(!isOversea) {
            bu.setMobilePhoneNumber(phone);
            bu.setMobilePhoneNumberVerified(true);
        }
        bu.setDeviceType("android");
        bu.setInstallId(BmobInstallation.getInstallationId(getBaseContext()));
        bu.signUp(RegisterActivity.this, new SaveListener() {

            @Override
            public void onSuccess() {
                SUser sUser = new SUser();
                sUser.setUser(bu);
                sUser.setHonest("5.0");
                sUser.setMeiLi("5.0");
                sUser.save(RegisterActivity.this);

                /*
                背景墙初始化
                 */
                WallPhoto wallPhoto = new WallPhoto();
                wallPhoto.setUser(bu);
                wallPhoto.setZan(0);
                wallPhoto.setIndex(0);
                wallPhoto.setComCount(0);
                wallPhoto.setSeeCount(0);
                wallPhoto.setDay(new Date().getDate() - 1);
                wallPhoto.save(RegisterActivity.this);

                progress.dismiss();
                ShowToast(getString(R.string.start_your_shundai_time));
                BmobQuery<UserCity> query = new BmobQuery<>();
                query.addWhereEqualTo("City", loc);
                query.findObjects(getBaseContext(), new FindListener<UserCity>() {
                    @Override
                    public void onSuccess(List<UserCity> list) {
                        if (list.size() >= 1) {
                            if (list.get(0).getCount() != null)
                                Count = list.get(0).getCount();
                           /*
                           初始化用户城市数据
                            */
                            if (Count >= 1) {

                                UserCity userCity = new UserCity();
                                userCity.setCity(loc);
                                userCity.setObjectId(list.get(0).getObjectId());
                                Count++;
                                userCity.setCount(Count);
                                userCity.update(RegisterActivity.this);
                            } else {
                                UserCity userCity = new UserCity();
                                userCity.setCity(loc);
                                userCity.setCount(1);
                                userCity.save(RegisterActivity.this);
                            }
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                    }
                });
                /*
                生成最近浏览数据
                 */
                RecentlyCity recentlyCity = new RecentlyCity();
                recentlyCity.setUsername(name);
                recentlyCity.setCity(loc);
                recentlyCity.setCount(1);
                recentlyCity.save(RegisterActivity.this);

                userManager.bindInstallationForRegister(bu.getUsername());
                updateUserLocation();
                sendBroadcast(new Intent(BmobConstants.ACTION_REGISTER_SUCCESS_FINISH));
                Intent intent = new Intent(RegisterActivity.this, NearPeopleActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                BmobLog.i(arg1);
                ShowToast(getString(R.string.register_error,arg1));
                progress.dismiss();
            }
        });
    }
    /**
     * 请求短信验证码
     */
    private void requestSmsCode() {
        String number = phoneNum.getText().toString();
        if (!TextUtils.isEmpty(number)) {
            cn.bmob.v3.BmobSMS.requestSMSCode(this, number, "注册验证码", new RequestSMSCodeListener() {

                @Override
                public void done(Integer smsId, cn.bmob.v3.exception.BmobException ex) {
                    if (ex == null) {
                        Click = true;
                        ShowToast("验证短信已发出");
                    } else {
                            ShowToast("获取验证码失败，请检查手机网络后再试");
                    }
                }
            });
        } else {
            ShowToast("请输入手机号码");
            Click = false;
        }
    }

    /**
     * 验证短信验证码
     */
    private void verifySmsCode() {
        String number = phoneNum.getText().toString();
        String code = check_num.getText().toString();
        if(!isOversea){
        if (!TextUtils.isEmpty(number) && !TextUtils.isEmpty(code)) {
            cn.bmob.v3.BmobSMS.verifySmsCode(this, number, code, new VerifySMSCodeListener() {
                @Override
                public void done(cn.bmob.v3.exception.BmobException ex) {
                    if (ex == null) {//短信验证码已验证成功
                        registerAction();
                    } else {
                        if (ex.getMessage().contains("already"))
                            ShowToast("此手机号码已经绑定有顺带账号了，请更换其他号码");
                        else
                        ShowToast("验证码错误或失效！请重新获取新验证码");
                    }
                }
            });
        } else {
            ShowToast("请输入手机号和验证码");
        }}else{
            registerAction();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.use_agree:
                new AlertDialog.Builder(RegisterActivity.this)
                        .setTitle(getString(R.string.using_pro))
                        .setMessage(R.string.agreement)
                        .setNeutralButton(getString(R.string.back), null)
                        .create()
                        .show();
                break;

        }
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation != null) {
                String sb = bdLocation.getCity();// 获得城市
                loc = sb.trim();
                if (!loc.equals(""))
                    stopListener();
            } else {
                ShowToast("无法定位");
            }
        }
    }

    public void stopListener() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();// 关闭定位SDK
            mLocationClient = null;
        }
    }

    @Override
    protected void onDestroy() {
        stopListener();
        super.onDestroy();

    }


    public boolean goodPWD(String pwd) {
        Pattern p = Pattern.compile("^[A-Za-z0-9]+$");
        Matcher m = p.matcher(pwd);
        return m.matches();
    }

    public boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((17[2])|(13[0-9])|(14[7])|(15[0-9])|(18[0-9]))\\d{8}$$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

}
