package dong.lan.shundai.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.message.PushAgent;

import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import dong.lan.shundai.BuildConfig;
import dong.lan.shundai.MyApplication;
import dong.lan.shundai.R;
import dong.lan.shundai.bean.SUser;
import dong.lan.shundai.bean.User;
import dong.lan.shundai.broadcast.MyBroadcastReceiver;
import dong.lan.shundai.config.BmobConstants;
import dong.lan.shundai.util.CollectionUtils;
import dong.lan.shundai.util.UserManager;
import dong.lan.shundai.view.HeaderLayout;
import dong.lan.shundai.view.dialog.DialogTips;

/**
 * 基类
 *
 */
public class BaseActivity extends AppCompatActivity {

    BmobUserManager userManager;
    BmobChatManager manager;

    MyApplication mApplication;
    protected HeaderLayout mHeaderLayout;

    protected int mScreenWidth;
    protected int mScreenHeight;

    private MyBroadcastReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushAgent.getInstance(this).onAppStart();

        userManager = BmobUserManager.getInstance(this);
        manager = BmobChatManager.getInstance(this);
        mApplication = MyApplication.getInstance();
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;
    }

    public void initReceiver()
    {
        receiver = new MyBroadcastReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BmobConstants.ACTION_FINISH);
        registerReceiver(receiver, filter);
    }

    Toast mToast;

    public void ShowToast(final String text) {
        if (!TextUtils.isEmpty(text)) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    if (mToast == null) {
                        mToast = Toast.makeText(getApplicationContext(), text,
                                Toast.LENGTH_LONG);
                    } else {
                        mToast.setText(text);
                    }
                    mToast.show();
                }
            });

        }
    }

    public void ShowToast(final int resId) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mToast == null) {
                    mToast = Toast.makeText(BaseActivity.this.getApplicationContext(), resId,
                            Toast.LENGTH_LONG);
                } else {
                    mToast.setText(resId);
                }
                mToast.show();
            }
        });
    }

    /**
     * 打Log
     * ShowLog
     */
    public void ShowLog(String msg) {
        Log.i(this.getLocalClassName(), msg);
    }

    /**
     * 只有title initTopBarLayoutByTitle
     *
     * @throws
     * @Title: initTopBarLayoutByTitle
     */
    public void initTopBarForOnlyTitle(String titleName) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.DEFAULT_TITLE);
        mHeaderLayout.setDefaultTitle(titleName);
    }

    /**
     * 初始化标题栏-带左右按钮
     *
     * @return void
     * @throws
     */
    public void initTopBarForBoth(String titleName, int rightDrawableId, String text,
                                  HeaderLayout.onRightImageButtonClickListener listener) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
        mHeaderLayout.setTitleAndLeftImageButton(titleName,
                R.drawable.base_action_bar_back_bg_selector,
                new OnLeftButtonClickListener());
        mHeaderLayout.setTitleAndRightButton(titleName, rightDrawableId, text,
                listener);
    }


    /**
     * 只有左边按钮和Title initTopBarLayout
     *
     * @throws
     */
    public void initTopBarForLeft(String titleName) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
        mHeaderLayout.setBackgroundColor(Color.rgb(0xc0, 0x6c, 0x85));
        mHeaderLayout.setTitleAndLeftImageButton(titleName,
                R.drawable.base_action_bar_back_bg_selector,
                new OnLeftButtonClickListener());
    }

    /**
     * 显示下线的对话框
     * showOfflineDialog
     *
     * @return void
     * @throws
     */
    public void showOfflineDialog(final Context context) {
        DialogTips dialog = new DialogTips(this, "您的账号已在其他设备上登录!", "重新登录");
        // 设置成功事件
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int userId) {
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
                startActivity(new Intent(context, GuideLoginActivity.class));
                finish();
                dialogInterface.dismiss();
            }
        });
        // 显示确认对话框
        dialog.show();
    }

    // 左边按钮的点击事件
    public class OnLeftButtonClickListener implements
            HeaderLayout.onLeftImageButtonClickListener {

        @Override
        public void onClick() {
            finish();
        }
    }

    public void startAnimActivity(Class<?> cla) {
        this.startActivity(new Intent(this, cla));
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    public void startAnimActivity(Intent intent) {
        this.startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    /**
     * 用于登陆或者自动登陆情况下的用户资料及好友资料的检测更新
     */
    public void updateUserInfos() {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                BmobQuery<SUser> query = new BmobQuery<SUser>();
                query.addWhereEqualTo("user",userManager.getCurrentUser(User.class));
                query.findObjects(getBaseContext(), new FindListener<SUser>() {
                    @Override
                    public void onSuccess(List<SUser> list) {
                        if(!list.isEmpty()) {
                            UserManager.setsUser(list.get(0));
                            UserManager.updateUserInfo(getBaseContext(),userManager.getCurrentUser(User.class));
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
                updateUserLocation();
                userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {

                    @Override
                    public void onError(int arg0, String arg1) {
                        if (arg0 == BmobConfig.CODE_COMMON_NONE) {
                            ShowLog(arg1);
                        } else {
                            ShowLog("查询好友列表失败：" + arg1);
                        }
                    }

                    @Override
                    public void onSuccess(List<BmobChatUser> arg0) {
                        MyApplication.getInstance().setContactList(CollectionUtils.list2map(arg0));
                    }
                });
                return null;
            }
        }.execute("null");
    }

    /**
     * 更新用户的经纬度信息
     */
    public void updateUserLocation() {
        if (MyApplication.lastPoint != null) {
            String saveLatitude = mApplication.getLatitude();
            String saveLongtitude = mApplication.getLongtitude();
            String newLat = String.valueOf(MyApplication.lastPoint.getLatitude());
            String newLong = String.valueOf(MyApplication.lastPoint.getLongitude());
            if (!saveLatitude.equals(newLat) || !saveLongtitude.equals(newLong)) {//只有位置有变化就更新当前位置，达到实时更新的目的
                User u = userManager.getCurrentUser(User.class);
                final User user = new User();
                user.setLocation(MyApplication.lastPoint);
                user.setObjectId(u.getObjectId());
                user.update(this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        MyApplication.getInstance().setLatitude(String.valueOf(user.getLocation().getLatitude()));
                        MyApplication.getInstance().setLongtitude(String.valueOf(user.getLocation().getLongitude()));
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver!=null)
        unregisterReceiver(receiver);
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }
}
