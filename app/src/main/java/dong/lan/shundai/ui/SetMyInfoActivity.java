package dong.lan.shundai.ui;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.PortraitUploadResponse;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.ShareContent;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import dong.lan.shundai.MyApplication;
import dong.lan.shundai.R;
import dong.lan.shundai.bean.Help;
import dong.lan.shundai.bean.HomeComment;
import dong.lan.shundai.bean.Jubao;
import dong.lan.shundai.bean.User;
import dong.lan.shundai.bean.WallPhoto;
import dong.lan.shundai.config.BmobConstants;
import dong.lan.shundai.util.AnimUitls;
import dong.lan.shundai.util.CollectionUtils;
import dong.lan.shundai.util.FaceTextUtils;
import dong.lan.shundai.util.ImageLoadOptions;
import dong.lan.shundai.util.PhotoUtil;
import dong.lan.shundai.util.UserManager;
import dong.lan.shundai.view.dialog.DialogTips;

/**
 * 个人资料页面
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
@SuppressLint({"SimpleDateFormat", "ClickableViewAccessibility", "InflateParams"})
public class SetMyInfoActivity extends ActivityBase implements OnClickListener {


    TextView tv_set_nick, tv_change_trend, info_trend, push_time, need_help, goalUpdate,
            makeFriendGoal, icon1, icon2, icon3, icon4, tv_updataNick, affective_state,
            home_commet, home_share, home_jubao, seeCount, commentCount;
    private TextView home_time, home_username, home_content;
    EditText et_publish, et_setGoal, et_setNick;
    ImageView iv_set_avator, wall_photo1, zan_img;
    LinearLayout layout_all;
    LinearLayout layout_publicTime;
    LinearLayout layout_base_info;
    LinearLayout layout_status_emotion;
    LinearLayout layout_publish;
    LinearLayout layout_goal;
    LinearLayout layout_goalInfo;
    LinearLayout layout_setNick;
    static int Tag = 0, Tag1 = 0, Tag2 = 0;
    public static int See = 1;
    Button btn_chat, btn_back, btn_add_friend;
    RelativeLayout layout_head;
    RelativeLayout layout_help;
    RelativeLayout layout_wall;
    RelativeLayout click_ZAN;
    LinearLayout home_layout_bottom;

    String from = "";
    String username = "";
    User user;
    User CurUser;
    private TextView clickZAN;
    public static int W, H;
    private int WorH;
    private final static int WALL = 0;
    private final static int HEAD = 1;
    private final static int AnimTime = 6000;
    private boolean runing = false;
    private int day;
    private String state;
    private List<String> photos;
    private int index = 0;
    private int loop = 0;
    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        runing = true;
        day = new Date().getDate();
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= 14) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        setContentView(R.layout.activity_set_info);
        decimalFormat = new DecimalFormat("######0.00");
        from = getIntent().getStringExtra("from");
        username = getIntent().getStringExtra("username");
        if (from.equals("me") && BmobUser.getCurrentUser(this, User.class).getConstllation().equals("未知")) {
            startAnimActivity(new Intent(this, BirthActivity.class));
        }
        final android.os.Handler handler = new android.os.Handler() {
            @Override
            public void handleMessage(Message msg) {

                switch (msg.what) {
                    case 0x111:
                        if (photos != null) {
                            ImageLoader.getInstance().displayImage(photos.get(loop), wall_photo1);
                            ObjectAnimator.ofFloat(wall_photo1, "scaleX", 1f, 1.6f, 1f).setDuration(AnimTime).start();
                            ObjectAnimator.ofFloat(wall_photo1, "scaleY", 1f, 1.6f, 1f).setDuration(AnimTime).start();
                            loop++;
                            if (loop > index)
                                loop = 0;
                            break;
                        }
                }
            }
        };

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                if (runing) {
                    handler.sendEmptyMessage(0x111);
                }
            }
        }, 0, AnimTime);
        initView();
    }

    private void initView() {
        layout_setNick = (LinearLayout) findViewById(R.id
                .layout_setNick);
        layout_setNick.setVisibility(View.GONE);
        layout_wall = (RelativeLayout) findViewById(R.id.photos_wall);
        clickZAN = (TextView) findViewById(R.id.zan);
        click_ZAN = (RelativeLayout) findViewById(R.id.click_ZAN);
        layout_all = (LinearLayout) findViewById(R.id.layout_all);
        iv_set_avator = (ImageView) findViewById(R.id.iv_set_avator);
        tv_set_nick = (TextView) findViewById(R.id.tv_set_nick);
        layout_head = (RelativeLayout) findViewById(R.id.layout_head);
        layout_base_info = (LinearLayout) findViewById(R.id.layout_base_info);
        layout_help = (RelativeLayout) findViewById(R.id.layout_help);
        layout_status_emotion = (LinearLayout) findViewById(R.id.layout_status_emotion);
        layout_publish = (LinearLayout) findViewById(R.id.me_layout_publish);
        tv_change_trend = (TextView) findViewById(R.id.tv_change_trends);
        et_setNick = (EditText) findViewById(R.id.edit_nick);
        tv_updataNick = (TextView) findViewById(R.id.tv_updateNick);
        layout_publicTime = (LinearLayout) findViewById(R.id.layout_publicTime);
        layout_goal = (LinearLayout) findViewById(R.id.layout_setGoal);
        layout_goalInfo = (LinearLayout) findViewById(R.id.layout_goalInfo);
        et_publish = (EditText) findViewById(R.id.et_publish);
        et_setGoal = (EditText) findViewById(R.id.setGoal);
        goalUpdate = (TextView) findViewById(R.id.goalUpdata);
        info_trend = (TextView) findViewById(R.id.info_trends);
        push_time = (TextView) findViewById(R.id.push_time);
        need_help = (TextView) findViewById(R.id.need_help);
        icon1 = (TextView) findViewById(R.id.friend_base_info1);
        icon2 = (TextView) findViewById(R.id.friend_base_info2);
        icon3 = (TextView) findViewById(R.id.friend_base_info3);
        icon4 = (TextView) findViewById(R.id.friend_base_info4);
        makeFriendGoal = (TextView) findViewById(R.id.make_friend_goal);

        wall_photo1 = (ImageView) findViewById(R.id.wall_photo1);
        affective_state = (TextView) findViewById(R.id.affective_state);
        zan_img = (ImageView) findViewById(R.id.zan_img);

        seeCount = (TextView) findViewById(R.id.home_seeCount);
        commentCount = (TextView) findViewById(R.id.home_cmtCount);

        home_time = (TextView) findViewById(R.id.home_comTime);
        home_content = (TextView) findViewById(R.id.home_comContent);
        home_username = (TextView) findViewById(R.id.home_comUsername);
        home_content.setOnClickListener(this);
        affective_state.setOnClickListener(this);
        info_trend.setOnClickListener(this);
        goalUpdate.setOnClickListener(this);
        tv_change_trend.setOnClickListener(this);
        layout_goalInfo.setOnClickListener(this);
        tv_updataNick.setOnClickListener(this);
        tv_set_nick.setOnClickListener(this);
        click_ZAN.setOnClickListener(this);
        layout_wall.setOnClickListener(this);
        // 黑名单提示语
        layout_goal.setVisibility(View.GONE);
        btn_chat = (Button) findViewById(R.id.btn_chat);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_add_friend = (Button) findViewById(R.id.btn_add_friend);

        btn_add_friend.setEnabled(false);
        btn_chat.setEnabled(false);
        btn_back.setEnabled(false);


        configePhoto();
        if (from.equals("me")) {
            CurUser = BmobUser.getCurrentUser(this, User.class);
            initMeData();

        } else {
            initTopBarForLeft("详细资料");
            // 配置需要分享的相关平台
            // 设置分享的内容
            home_commet = (TextView) findViewById(R.id.home_comment);
            home_jubao = (TextView) findViewById(R.id.home_report);
            home_share = (TextView) findViewById(R.id.home_share);
            home_share.setOnClickListener(this);
            home_commet.setOnClickListener(this);
            home_jubao.setOnClickListener(this);
            home_layout_bottom = (LinearLayout) findViewById(R.id.home_layout_bottom);
            home_layout_bottom.setVisibility(View.VISIBLE);
            layout_publish.setVisibility(View.GONE);
            tv_change_trend.setVisibility(View.GONE);
            btn_add_friend.setVisibility(View.GONE);
            btn_chat.setVisibility(View.VISIBLE);
            btn_chat.setOnClickListener(this);
            if (from.equals("add")) {
                // 从附近的人列表添加好友--因为获取附近的人的方法里面有是否显示好友的情况，因此在这里需要判断下这个用户是否是自己的好友
                if (mApplication.getContactList().containsKey(username)) {// 是好友
                    btn_back.setVisibility(View.VISIBLE);
                    btn_back.setOnClickListener(this);
                    btn_add_friend.setVisibility(View.GONE);
                } else {
                    btn_add_friend.setVisibility(View.VISIBLE);
                    btn_back.setVisibility(View.GONE);
                    btn_add_friend.setVisibility(View.VISIBLE);
                    btn_add_friend.setOnClickListener(this);
                }
            } else {// 查看他人
                btn_add_friend.setVisibility(View.GONE);
                btn_back.setVisibility(View.VISIBLE);
                btn_back.setOnClickListener(this);
            }
            initOtherData(username);
        }
    }

    private void initMeData() {
        User u = userManager.getCurrentUser(User.class);
        initTopBarForLeft("个人资料");
        setWall_photo();
        refreshAvatar(u.getAvatar());
        layout_help.setVisibility(View.GONE);
        layout_publicTime.setVisibility(View.GONE);
        layout_publish.setVisibility(View.GONE);
        iv_set_avator.setOnClickListener(this);
        btn_back.setVisibility(View.GONE);
        btn_chat.setVisibility(View.GONE);
        btn_add_friend.setVisibility(View.GONE);
        if (u.getNick() != null && !u.getNick().equals(""))
            tv_set_nick.setText(u.getNick());
        else
            tv_set_nick.setText("设置昵称");
        if (u.getTrends() != null && !u.getTrends().equals(""))
            info_trend.setText(u.getTrends());
        else
            info_trend.setText("设置个性签名");
        if (u.getGoal() != null && !u.getGoal().equals(""))
            makeFriendGoal.setText(u.getGoal());
        else
            makeFriendGoal.setText("设置交友宣言");
        affective_state.setText(u.getState());
        if (u.getSex()) {
            icon1.setText("♂ " + u.getAge());
        } else {
            icon1.setText("♀ " + u.getAge());
        }
        affective_state.setText(u.getState());
        icon2.setText(u.getConstllation());
        icon3.setText("诚信 " + String.valueOf(decimalFormat.format(Double.parseDouble(u.getHonest()))));
        icon4.setText("魅力 " + String.valueOf(decimalFormat.format(Double.parseDouble(u.getMeili()))));
        BmobQuery<Help> query = new BmobQuery<>();
        query.addWhereEqualTo("user", u);
        query.order("-createAt");
        query.setLimit(2);
        query.findObjects(this, new FindListener<Help>() {
            @Override
            public void onSuccess(List<Help> list) {
                if (list.size() >= 1) {
                    push_time.setText(list.get(0).getCreatedAt());
                    SpannableString spannableString = FaceTextUtils
                            .toSpannableString(SetMyInfoActivity.this, list.get(0).getInfo());
                    need_help.setText(spannableString);
                } else {
                    push_time.setText("Ta 很懒啥都没写");
                    need_help.setText("给 ta 发一条消息吧");
                }
            }

            @Override
            public void onError(int i, String s) {
                need_help.setText("这家伙啥都没写");
                ShowToast("查询失败");
            }
        });
    }

    private void initOtherData(String name) {
        userManager.queryUser(name, new FindListener<User>() {
            @Override
            public void onError(int arg0, String arg1) {
            }

            @Override
            public void onSuccess(List<User> arg0) {
                if (!arg0.isEmpty()) {
                    user = arg0.get(0);
                    btn_chat.setEnabled(true);
                    btn_back.setEnabled(true);
                    btn_add_friend.setEnabled(true);
                    updateUser(user);
                } else {
                    ShowLog("onSuccess 查无此人");
                }
            }
        });
    }

    private void updateUser(User user) {
        refreshAvatar(user.getAvatar());
        if (!from.equals("me")) {
            setWall_photo();
            if (user.getNick() != null && !user.getNick().equals(""))
                tv_set_nick.setText(user.getNick());
            else
                tv_set_nick.setText("昵称");
            if (user.getTrends() != null && !user.getTrends().equals(""))
                info_trend.setText(user.getTrends());
            else
                info_trend.setText("个性签名");
            if (user.getGoal() != null && user.getGoal().equals(""))
                makeFriendGoal.setText(user.getGoal());
            else
                makeFriendGoal.setText("交友宣言");
            layout_setNick.setEnabled(false);
            layout_goalInfo.setEnabled(false);
            info_trend.setEnabled(false);
            tv_set_nick.setEnabled(false);
            info_trend.setEnabled(false);
            makeFriendGoal.setEnabled(false);
        }
        if (user.getSex()) {
            icon1.setText("♂ " + user.getAge());
        } else {
            icon1.setText("♀ " + user.getAge());
        }
        BmobQuery<HomeComment> query4 = new BmobQuery<>();
        query4.order("-createAt,-updateAt");
        query4.addWhereEqualTo("ComUser", new BmobPointer(user));
        query4.findObjects(SetMyInfoActivity.this, new FindListener<HomeComment>() {
            @Override
            public void onSuccess(List<HomeComment> list) {
                if (!list.isEmpty()) {
                    home_username.setText(list.get(0).getWho().getUsername());
                    home_time.setText(list.get(0).getCreatedAt());
                    home_content.setText(list.get(0).getHomeCom());
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });

        affective_state.setText(user.getState());
        icon2.setText(user.getConstllation());
        icon3.setText("诚信 " + String.valueOf(decimalFormat.format(Double.parseDouble(user.getHonest()))));
        icon4.setText("魅力 " + String.valueOf(decimalFormat.format(Double.parseDouble(user.getMeili()))));
        BmobQuery<Help> query = new BmobQuery<Help>();
        query.addWhereEqualTo("user", user);
        query.order("-createAt");
        query.setLimit(2);
        query.findObjects(this, new FindListener<Help>() {
            @Override
            public void onSuccess(List<Help> list) {
                if (list.size() >= 1) {
                    push_time.setText(list.get(0).getCreatedAt());
                    SpannableString spannableString = FaceTextUtils
                            .toSpannableString(SetMyInfoActivity.this, list.get(0).getInfo());
                    need_help.setText(spannableString);
                } else {
                    push_time.setText("Ta 很懒啥都没写");
                    need_help.setText("给 ta 发一条消息吧");
                }
            }

            @Override
            public void onError(int i, String s) {
                need_help.setText("这家伙啥都没写");
                ShowToast("查询失败");
            }
        });
        // 检测是否为黑名单用户
        if (from.equals("other")) {
            if (BmobDB.create(this).isBlackUser(user.getUsername())) {
                btn_back.setVisibility(View.GONE);
                //layout_black_tips.setVisibility(View.VISIBLE);
            } else {
                btn_back.setVisibility(View.VISIBLE);
                clickZAN.setEnabled(true);
                //layout_black_tips.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 更新头像 refreshAvatar
     */
    private void refreshAvatar(String avatar) {
        if (avatar != null && !avatar.equals("")) {
            ImageLoader.getInstance().displayImage(avatar, iv_set_avator,
                    ImageLoadOptions.getOptions(1));
            NearPeopleActivity.UPDATE_HEAD = true;
        } else {
            iv_set_avator.setImageResource(R.drawable.default_head);
        }
    }

    private void configePhoto() {
        getScreenWH();

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (from.equals("me")) {
            configePhoto();
            initMeData();
        } else {
            configePhoto();
            initOtherData(username);
        }
    }

    public void UpdateSeeCount(final User u) {
                BmobQuery<WallPhoto> q = new BmobQuery<WallPhoto>();
                q.addWhereEqualTo("user", u);
                q.findObjects(SetMyInfoActivity.this, new FindListener<WallPhoto>() {
                    @Override
                    public void onSuccess(List<WallPhoto> list) {
                        if (!list.isEmpty()) {
                            String id = list.get(0).getObjectId();
                            WallPhoto wallPhoto = new WallPhoto();
                            wallPhoto.setUser(u);
                            wallPhoto.setSeeCount(list.get(0) == null || list.get(0).getSeeCount()==null ? 1 : list.get(0).getSeeCount() + 1);
                            wallPhoto.setObjectId(id);
                            wallPhoto.update(SetMyInfoActivity.this, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        System.out.println(s);
                    }

                });
    }

    private void AddHomeCom(final User user1, String content) {

        HomeComment homeComment = new HomeComment();
        homeComment.setUser(user1);
        homeComment.setHomeCom(content);
        homeComment.setWho(BmobUser.getCurrentUser(SetMyInfoActivity.this, User.class));
        homeComment.save(SetMyInfoActivity.this, new SaveListener() {
            @Override
            public void onSuccess() {
                WallPhoto wallPhoto = new WallPhoto();
                wallPhoto.setObjectId(user1.getObjectId());
                BmobQuery<WallPhoto> q = new BmobQuery<WallPhoto>();
                q.addWhereEqualTo("user", user1);
                q.findObjects(getBaseContext(), new FindListener<WallPhoto>() {
                    @Override
                    public void onSuccess(List<WallPhoto> list) {
                        if (!list.isEmpty()) {
                            String id = list.get(0).getObjectId();
                            WallPhoto wallPhoto = new WallPhoto();
                            wallPhoto.setUser(user1);
                            wallPhoto.setComCount(list.get(0).getComCount() + 1);
                            wallPhoto.setObjectId(id);
                            wallPhoto.update(SetMyInfoActivity.this, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                }

                                @Override
                                public void onFailure(int i, String s) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }

                });
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    private void jubao(final User to_user) {
        Jubao jubao = new Jubao();
        jubao.setFrom_user(BmobUser.getCurrentUser(SetMyInfoActivity.this, User.class));
        jubao.setTo_user(to_user);
        jubao.save(SetMyInfoActivity.this, new SaveListener() {
            @Override
            public void onSuccess() {

                String h = String.valueOf((Double.parseDouble(to_user.getHonest()) - 0.4));
                String m = String.valueOf((Double.parseDouble(to_user.getMeili()) - 0.4));
                UserManager.updateOtherSuser(SetMyInfoActivity.this, user, m, h);
                userManager.addBlack(to_user.getUsername(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        ShowToast("此用户已经加入黑名单，我们会尽快处理举报用户");
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        ShowToast("黑名单添加失败:" + arg1);
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(SetMyInfoActivity.this, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_comment:
                AnimUitls.BunttonAnim(v, 300);
                HomeComPop();
                break;
            case R.id.home_report:
                AnimUitls.BunttonAnim(v, 300);
                jubao(user);
                break;
            case R.id.home_share:
                AnimUitls.BunttonAnim(v, 300);
                new ShareAction(this).setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SMS, SHARE_MEDIA.TENCENT)
                        .setContentList(new ShareContent(), new ShareContent())
                        .setListenerList(umShareListener, umShareListener)
                        .open();
                break;
            case R.id.affective_state:
                if (from.equals("me"))
                    showAffectivePop();
                break;
            case R.id.click_ZAN:
                if (!from.equals("me")) {
                    clickZAN.setText((Integer.parseInt(clickZAN.getText().toString()) + 1) + "");
                    BmobQuery<WallPhoto> query = new BmobQuery<WallPhoto>();
                    query.addWhereEqualTo("user", user);
                    query.findObjects(SetMyInfoActivity.this, new FindListener<WallPhoto>() {
                        @Override
                        public void onSuccess(List<WallPhoto> list) {

                            if (!list.isEmpty()) {
                                int d;
                                if (list.get(0).getDay() == null)
                                    d = 0;
                                else
                                    d = list.get(0).getDay();
                                if (Math.abs(day - d) >= 1) {
                                    WallPhoto p = new WallPhoto();
                                    p.setObjectId(list.get(0).getObjectId());
                                    p.setUser(user);
                                    p.setDay(new Date().getDate());
                                    p.setZan(list.get(0).getZan() + 1);
                                    p.update(getBaseContext(), new UpdateListener() {
                                        @Override
                                        public void onSuccess() {
                                            zan_img.setImageDrawable(getResources().getDrawable(R.drawable.click_zan2));
                                        }

                                        @Override
                                        public void onFailure(int i, String s) {
                                            ShowToast("点赞失败");
                                            clickZAN.setText((Integer.parseInt(clickZAN.getText().toString()) - 1) + "");
                                        }
                                    });
                                } else {
                                    ShowToast("今天你已经赞过Ta啦");
                                    clickZAN.setText((Integer.parseInt(clickZAN.getText().toString()) - 1) + "");
                                }
                            } else {
                                WallPhoto w = new WallPhoto();
                                w.setZan(Integer.parseInt(clickZAN.getText().toString()) + 1);
                                w.setUser(user);
                                w.setDay(new Date().getDate());
                                w.save(getBaseContext(), new SaveListener() {
                                    @Override
                                    public void onSuccess() {
                                        zan_img.setImageDrawable(getResources().getDrawable(R.drawable.click_zan2));
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });
                } else {
                }
                break;
            case R.id.btn_chat:// 发起聊天
                AnimUitls.BunttonAnim(v, 300);
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("user", user);
                startAnimActivity(intent);
                finish();
                break;

            case R.id.tv_updateNick:
                if (et_setNick.getText().toString() != null && !et_setNick.getText().toString().equals("")) {
                    User user1 = new User();
                    user1.setNick(et_setNick.getText().toString());
                    updateUserData(user1, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            tv_set_nick.setText(et_setNick.getText().toString());
                            layout_setNick.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                }
                break;
            case R.id.tv_set_nick:
                switch (Tag2) {
                    case 0:
                        layout_setNick.setVisibility(View.VISIBLE);
                        Tag2 = 1;
                        break;
                    case 1:
                        layout_setNick.setVisibility(View.GONE);
                        Tag2 = 0;
                        break;
                }
                break;
            case R.id.photos_wall:
                if (from.equals("me")) {
                    WorH = WALL;
                    showAvatarPop();
                }
                break;
            case R.id.iv_set_avator:
                WorH = HEAD;
                showAvatarPop();
                break;
            case R.id.info_trends:
                switch (Tag) {

                    case 0:
                        layout_publish.setVisibility(View.VISIBLE);
                        Tag = 1;
                        break;
                    case 1:
                        layout_publish.setVisibility(View.GONE);
                        Tag = 0;
                        break;

                }
                break;
            case R.id.tv_change_trends:
                if (et_publish.getText().toString() != "") {
                    User u = new User();
                    u.setTrends(et_publish.getText().toString());
                    updateUserData(u, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            ShowToast("更新成功");
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            ShowToast("更新失败");
                        }
                    });
                }
                info_trend.setText(et_publish.getText().toString());
                layout_publish.setVisibility(View.GONE);

                break;

            case R.id.layout_goalInfo:

                switch (Tag1) {

                    case 0:
                        layout_goal.setVisibility(View.VISIBLE);
                        Tag1 = 1;
                        break;
                    case 1:
                        layout_goal.setVisibility(View.GONE);
                        Tag1 = 0;
                        break;

                }
                break;

            case R.id.goalUpdata:
                if (!et_setGoal.getText().toString().equals("")) {
                    User u = new User();
                    u.setGoal(et_setGoal.getText().toString());
                    updateUserData(u, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            makeFriendGoal.setText(et_setGoal.getText().toString());
                            layout_goal.setVisibility(View.GONE);
                            ShowToast("更新成功");
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            ShowToast("更新失败");
                        }
                    });
                }

                break;

            case R.id.btn_back:// 黑名单
                showBlackDialog(user.getUsername());
                break;
            case R.id.btn_add_friend://添加好友
                addFriend();
                break;
        }
    }


    /**
     * 添加好友请求
     */
    private void addFriend() {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("正在添加...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        // 发送tag请求
        BmobChatManager.getInstance(this).sendTagMessage(BmobConfig.TAG_ADD_CONTACT,
                user.getObjectId(), new PushListener() {

                    @Override
                    public void onSuccess() {
                        progress.dismiss();
                        ShowToast("发送请求成功，等待对方验证！");
                    }

                    @Override
                    public void onFailure(int arg0, final String arg1) {
                        progress.dismiss();
                        ShowToast("发送请求成功，等待对方验证！");
                        ShowLog("发送请求失败:" + arg1);
                    }
                });
    }

    /**
     * 显示黑名单提示框
     */
    private void showBlackDialog(final String username) {
        DialogTips dialog = new DialogTips(this, "加入黑名单",
                "加入黑名单，你将不再收到对方的消息，确定要继续吗？", "确定", true, true);
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int userId) {
                // 添加到黑名单列表
                userManager.addBlack(username, new UpdateListener() {

                    @Override
                    public void onSuccess() {
                        ShowToast("黑名单添加成功!");
                        btn_back.setVisibility(View.GONE);
                        //layout_black_tips.setVisibility(View.VISIBLE);
                        // 重新设置下内存中保存的好友列表
                        MyApplication.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(SetMyInfoActivity.this).getContactList()));
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        // TODO Auto-generated method stub
                        ShowToast("黑名单添加失败:" + arg1);
                    }
                });
            }
        });
        // 显示确认对话框
        dialog.show();
        dialog = null;
    }

    RelativeLayout layout_choose;
    RelativeLayout layout_photo;
    PopupWindow avatorPop;

    public String filePath = "";

    private void showAffectivePop() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.alert);
        View view = LayoutInflater.from(this).inflate(R.layout.affective_state_pop, null);
        final RadioButton state_single = (RadioButton) view.findViewById(R.id.state_single);
        final RadioButton state_married = (RadioButton) view.findViewById(R.id.state_married);
        final RadioButton state_friend = (RadioButton) view.findViewById(R.id.state_friend);
        final RadioButton state_secret = (RadioButton) view.findViewById(R.id.state_secret);
        TextView state_ok = (TextView) view.findViewById(R.id.affective_ok);
        state_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state_friend.isChecked()) {
                    state = "求交友";
                }
                if (state_married.isChecked()) {
                    state = "已婚";
                }
                if (state_secret.isChecked()) {
                    state = "保密";
                }
                if (state_single.isChecked()) {
                    state = "单身";
                }
                if (!state_friend.isChecked() && !state_married.isChecked() && !state_secret.isChecked() && !state_single.isChecked()) {
                    ShowToast("没有选择呢");
                    return;
                } else {
                    affective_state.setText(state);
                    User u = new User();
                    u.setState(state);
                    updateUserData(u, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            ShowToast("更新情感状态失败");
                        }
                    });
                }
                dialog.dismiss();
            }
        });
        builder.setView(view);
        dialog = builder.show();

    }

    private void showAvatarPop() {
        View view = LayoutInflater.from(this).inflate(R.layout.pop_showavator,
                null);
        layout_choose = (RelativeLayout) view.findViewById(R.id.layout_choose);
        layout_photo = (RelativeLayout) view.findViewById(R.id.layout_photo);
        layout_photo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ShowLog("点击拍照");
                layout_choose.setBackgroundColor(getResources().getColor(
                        R.color.base_color_text_white));
                layout_photo.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.pop_bg_press));
                File dir = new File(BmobConstants.MyAvatarDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                // 原图
                File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss")
                        .format(new Date()));
                filePath = file.getAbsolutePath();// 获取相片的保存路径
                Uri imageUri = Uri.fromFile(file);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent,
                        BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA);
            }
        });
        layout_choose.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ShowLog("点击相册");
                layout_photo.setBackgroundColor(getResources().getColor(
                        R.color.base_color_text_white));
                layout_choose.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.pop_bg_press));
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent,
                        BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION);
            }
        });

        avatorPop = new PopupWindow(view, mScreenWidth, 700);
        avatorPop.setTouchInterceptor(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    avatorPop.dismiss();
                    return true;
                }
                return false;
            }
        });

        avatorPop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        avatorPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        avatorPop.setTouchable(true);
        avatorPop.setFocusable(true);
        avatorPop.setOutsideTouchable(true);
        avatorPop.setBackgroundDrawable(new BitmapDrawable());
        // 动画效果 从底部弹起
        avatorPop.setAnimationStyle(R.style.Animations_GrowFromBottom);
        avatorPop.showAtLocation(layout_all, Gravity.BOTTOM, 0, 0);
    }

    private void startImageAction(Uri uri, int outputX, int outputY,
                                  int requestCode, boolean isCrop) {
        Intent intent = null;
        if (isCrop) {
            intent = new Intent("com.android.camera.action.CROP");
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        }
        intent.setDataAndType(uri, "image/*");

        intent.putExtra("crop", "true");
        if (WorH == HEAD) {
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
        }
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, requestCode);
    }

    boolean isFromCamera = false;// 区分拍照旋转
    int degree = 0;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA:// 拍照修改头像
                if (resultCode == RESULT_OK) {
                    if (!Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        ShowToast("SD不可用");
                        return;
                    }
                    isFromCamera = true;
                    File file = new File(filePath);
                    degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
                    ShowToast("file :" + file.getAbsolutePath());
                    Log.i("life", "拍照后的角度：" + degree);
                    startImageAction(Uri.fromFile(file), 200, 200,
                            BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
                }
                break;
            case BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION:// 本地修改头像
                if (avatorPop != null) {
                    avatorPop.dismiss();
                }
                Uri uri = null;
                if (data == null) {
                    return;
                }
                if (resultCode == RESULT_OK) {
                    if (!Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        ShowToast("SD不可用");
                        return;
                    }
                    isFromCamera = false;
                    uri = data.getData();
                    ShowToast("Uri  :" + uri.toString());
                    startImageAction(uri, 200, 200,
                            BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
                } else {
                    ShowToast("照片获取失败");
                }

                break;
            case BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP:// 裁剪头像返回
                if (avatorPop != null) {
                    avatorPop.dismiss();
                }
                if (data == null) {
                    // Toast.makeText(this, "取消选择", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    saveCropAvator(data);
                }
                // 初始化文件路径
                filePath = "";
                // 上传头像
                if (WorH == HEAD)
                    uploadAvatar();
                break;
            default:
                break;

        }
    }

    private void uploadAvatar() {
        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.upload(this, new UploadFileListener() {
            @Override
            public void onSuccess() {
                String url = bmobFile.getFileUrl(SetMyInfoActivity.this);
                MyApplication.communitySDK.updateUserProtrait(BitmapFactory.decodeFile(path), new Listeners.SimpleFetchListener<PortraitUploadResponse>() {
                    @Override
                    public void onComplete(PortraitUploadResponse portraitUploadResponse) {
                    }
                });
                updateUserAvatar(url);
            }

            @Override
            public void onProgress(Integer arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFailure(int arg0, String msg) {
                // TODO Auto-generated method stub
                ShowToast("头像上传失败：" + msg);
            }
        });
    }

    private void deleteOldAvater(String oldAvater) {
        BmobFile file = new BmobFile();
        file.setUrl(oldAvater);
        file.delete(this);
    }

    private void updateUserAvatar(final String url) {
        User u = new User();
        u.setAvatar(url);
        final String old = userManager.getCurrentUser(User.class).getAvatar();
        updateUserData(u, new UpdateListener() {
            @Override
            public void onSuccess() {
                ShowToast("头像更新成功！");
                refreshAvatar(url);
                deleteOldAvater(old);
            }

            @Override
            public void onFailure(int code, String msg) {
                ShowToast("头像更新失败：" + msg);
            }
        });
    }

    String path;

    /**
     * 保存裁剪的头像
     *
     * @param data
     */
    private void saveCropAvator(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
            if (bitmap != null) {
                // 保存图片
                if (WorH == HEAD) {
                    bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
                    if (isFromCamera && degree != 0) {
                        bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
                    }
                    String filename = new SimpleDateFormat("yyMMddHHmmss")
                            .format(new Date()) + ".png";
                    path = BmobConstants.MyAvatarDir + filename;
                    PhotoUtil.saveBitmap(BmobConstants.MyAvatarDir, filename,
                            bitmap, true);
                }
                if (WorH == WALL) {
                    SaveBmp2SD(bitmap);
                    setWall_photo();
                }
                // 上传头像
                if (bitmap != null && bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        }
    }

    private void setWall_photo() {

        if (from.equals("me")) {
            BmobQuery<WallPhoto> query = new BmobQuery<>();
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
            query.addWhereEqualTo("user", userManager.getCurrentUser(User.class));
            query.findObjects(this, new FindListener<WallPhoto>() {
                @Override
                public void onSuccess(List<WallPhoto> object) {
                    if (!object.isEmpty()) {
                        WallPhoto wallPhoto = object.get(0);
                        seeCount.setText((wallPhoto==null || wallPhoto.getSeeCount()==null ? 1: wallPhoto.getSeeCount() )+ "人看过");
                        commentCount.setText((wallPhoto==null || wallPhoto.getComCount()==null ? 1: wallPhoto.getComCount() ) + "人评论");
                        photos = wallPhoto.getPhotos();
                        if (photos == null)
                            index = 0;
                        else
                            index = photos.size() - 1;
                    }

                }

                @Override
                public void onError(int code, String msg) {
                }
            });

        } else {

            BmobQuery<WallPhoto> query = new BmobQuery<WallPhoto>();
            query.addWhereEqualTo("user", user);
            query.findObjects(this, new FindListener<WallPhoto>() {
                @Override
                public void onSuccess(List<WallPhoto> object) {
                    if (!object.isEmpty()) {
                        WallPhoto wallPhoto = object.get(0);
                        photos = wallPhoto.getPhotos();
                        if (photos == null)
                            index = 0;
                        else
                            index = photos.size() - 1;
                        seeCount.setText((wallPhoto==null || wallPhoto.getSeeCount()==null ? 1: wallPhoto.getSeeCount() )+ "人看过");
                        commentCount.setText((wallPhoto==null || wallPhoto.getComCount()==null ? 1: wallPhoto.getComCount() ) + "人评论");
                        if (wallPhoto.getDay() == day) {
                            clickZAN.setText(object.get(0).getZan() + "");
                            zan_img.setImageDrawable(getResources().getDrawable(R.drawable.click_zan2));
                            clickZAN.setTextColor(Color.BLUE);
                        } else {
                            zan_img.setImageDrawable(getResources().getDrawable(R.drawable.click_zan1));
                            clickZAN.setText(object.get(0).getZan() + "");
                            clickZAN.setTextColor(Color.BLACK);
                        }
                    }

                }

                @Override
                public void onError(int code, String msg) {
                }
            });
        }
    }

    private void SaveBmp2SD(Bitmap bm) {
        String filename = "";
        filename = userManager.getCurrentUserName() + (new Date().getTime()) + "_Wall_" + index + "_.jpeg";
        File dir = new File(BmobConstants.MyWallPhoto);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, filename);
        if (file.exists()) {
            file.delete();
        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            try {
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        saveWall2Bmob(this, CurUser, BmobConstants.MyWallPhoto + filename);


        if (bm != null && bm.isRecycled())

        {
            bm.recycle();

        }

    }

    private void saveWall2Bmob(final Context context, final User curUser, final String photo_path) {
        final BmobFile bmobFile = new BmobFile(new File(photo_path));
        bmobFile.upload(this, new UploadFileListener() {
            @Override
            public void onSuccess() {
                final String url = bmobFile.getFileUrl(SetMyInfoActivity.this);
                BmobQuery<WallPhoto> q = new BmobQuery<WallPhoto>();
                q.addWhereEqualTo("user", curUser);
                q.findObjects(getBaseContext(), new FindListener<WallPhoto>() {
                    @Override
                    public void onSuccess(List<WallPhoto> list) {
                        if (!list.isEmpty()) {
                            String id = list.get(0).getObjectId();
                            WallPhoto wallPhoto = new WallPhoto();
                            wallPhoto.setUser(CurUser);
                            wallPhoto.setObjectId(id);
                            wallPhoto.setIndex(index + 1);
                            wallPhoto.addUnique("photos", url);
                            wallPhoto.update(context, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    ShowToast("更新展示图片成功");
                                }

                                @Override
                                public void onFailure(int i, String s) {

                                }
                            });

                        } else {
                            WallPhoto wallPhoto = new WallPhoto();
                            wallPhoto.setUser(CurUser);
                            wallPhoto.setIndex(index + 1);
                            wallPhoto.addUnique("photos", url);
                            wallPhoto.save(getBaseContext(), new SaveListener() {
                                @Override
                                public void onSuccess() {
                                    ShowToast("保存图片");
                                }

                                @Override
                                public void onFailure(int i, String s) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });

            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    private void updateUserData(User user, UpdateListener listener) {
        User current = (User) userManager.getCurrentUser(User.class);
        user.setObjectId(current.getObjectId());
        user.update(this, listener);
    }

    public void getScreenWH() {
        W = getWindowManager().getDefaultDisplay().getWidth();
        H = getWindowManager().getDefaultDisplay().getHeight();
//        ViewGroup.LayoutParams layoutParams = layout_wall.getLayoutParams();
//        layoutParams.height = H/4+ PixelUtil.dp2px(50);
//        layout_wall.setLayoutParams(layoutParams);
//        ViewGroup.LayoutParams layoutParams1 = wall_photo1.getLayoutParams();
//        layoutParams1.height =H/4;
//        wall_photo1.setLayoutParams(layoutParams1);

    }

    View home;
    AlertDialog dialog;

    private void HomeComPop() {
        home = LayoutInflater.from(SetMyInfoActivity.this).inflate(R.layout.home_comment_pop, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.alert);
        final EditText text = (EditText) home.findViewById(R.id.home_com_editText);
        TextView done = (TextView) home.findViewById(R.id.home_com_done);
        done.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().toString().equals("")) {
                    ShowToast("啥都不写 " + user.getUsername() + " 会伤心的 ");
                    return;
                } else {
                    AddHomeCom(user, text.getText().toString());
                    dialog.dismiss();
                }
            }
        });
        builder.setView(home);
        dialog = builder.show();
    }

    @Override
    protected void onStop() {
        if (!from.equals("me"))
            UpdateSeeCount(user);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!from.equals("me"))
            runing = false;
    }


}
