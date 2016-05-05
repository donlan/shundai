package dong.lan.shundai.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.utils.NetworkHelper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.fb.FeedbackAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.ShareContent;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;
import com.umeng.update.UmengUpdateAgent;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import dong.lan.shundai.BuildConfig;
import dong.lan.shundai.MyApplication;
import dong.lan.shundai.MyMessageReceiver;
import dong.lan.shundai.R;
import dong.lan.shundai.adapter.NearPeopleAdapter;
import dong.lan.shundai.bean.User;
import dong.lan.shundai.bean.UserCity;
import dong.lan.shundai.config.Config;
import dong.lan.shundai.config.MyUmengCommunityLogin;
import dong.lan.shundai.util.AnimUitls;
import dong.lan.shundai.util.CollectionUtils;
import dong.lan.shundai.util.ImageLoadOptions;
import dong.lan.shundai.util.MyConstant;
import dong.lan.shundai.util.UserManager;
import dong.lan.shundai.view.xlist.XListView;

public class NearPeopleActivity extends BaseActivity implements XListView.IXListViewListener, OnItemClickListener, View.OnClickListener, EventListener, NearSearchActivity.onSearchDoneListener {

    XListView mListView;
    NearPeopleAdapter adapter;
    List<BmobQuery<User>> queries;
    List<User> nears = new ArrayList<>();
    BmobGeoPoint point;
    int miles = 10;
    private static int LOC_TIME = 10;
    private ImageView headPhoto;
    private TextView msg_tip;
    private TextView noOneHint;
    private FrameLayout noOneLayout;
    private Context context;
    public LocationClient mLocationClient = null;
    public BDLocationListener locationListener = new MyLocationListener();
    public String loc = "";
    private static boolean once = false;
    private int Count;
    private static int limit = 10;
    public static boolean UPDATE_HEAD = false;
    private Vibrator vibrator;
    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();
    private SharedPreferences geoPreference;
    private TextView nick_name;
    private TextView cur_age;
    private TextView cur_conslelattio;
    private TextView cur_hobest;
    private TextView cur_meili;
    private TextView cur_trends;
    private DrawerLayout drawer;
    private User user;
    private DecimalFormat decimalFormat;
    private LocationClientOption option;
    private FeedbackAgent agent;

    public NearPeopleActivity() {
        context = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_1);
        decimalFormat = new DecimalFormat("######0.00");
        geoPreference = getSharedPreferences("GeoPoint", MODE_PRIVATE);
        UmengUpdateAgent.update(this);
        MyUmengCommunityLogin.getInstance().login(this, new LoginListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(int i, CommUser commUser) {
//                PushSDKManager.getInstance().addImpl("dooge_push", new UmengPushImpl());
//                PushSDKManager.getInstance().useThis("dooge_push");
//                PushSDKManager.getInstance().getCurrentSDK().enable(getApplicationContext());
            }
        });
        agent = new FeedbackAgent(this);
        agent.openFeedbackPush();
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        initNewMessageBroadCast();
        initTagMessageBroadCast();
        initLocClient();
        initView();
        initReceiver();

    }


    private void initView() {
        initXListView();
        noOneHint = (TextView) findViewById(R.id.near_no_one);
        noOneLayout = (FrameLayout) findViewById(R.id.no_one_layout);
        findViewById(R.id.need_for_help).setOnClickListener(this);
        findViewById(R.id.offer_help).setOnClickListener(this);
        findViewById(R.id.my_recent).setOnClickListener(this);
        findViewById(R.id.my_near).setOnClickListener(this);
        findViewById(R.id.my_seek).setOnClickListener(this);
        findViewById(R.id.my_help).setOnClickListener(this);
        findViewById(R.id.my_all).setOnClickListener(this);
        findViewById(R.id.my_message).setOnClickListener(this);
        findViewById(R.id.my_friends).setOnClickListener(this);
        findViewById(R.id.my_community).setOnClickListener(this);
        findViewById(R.id.my_setting).setOnClickListener(this);
        findViewById(R.id.share_app).setOnClickListener(this);
        findViewById(R.id.bar_near_dialog).setOnClickListener(this);
        findViewById(R.id.top_myInfo).setOnClickListener(this);
        msg_tip = (TextView) findViewById(R.id.msg_tip);
        soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        soundMap.put(1, soundPool.load(this, R.raw.notify, 1));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        findViewById(R.id.no_one_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noOneLayout.setVisibility(View.GONE);
            }
        });
        drawer.setDrawerListener(toggle);
        nick_name = (TextView) findViewById(R.id.my_nick);
        cur_age = (TextView) findViewById(R.id.my_age);
        cur_conslelattio = (TextView) findViewById(R.id.my_constellation);
        cur_hobest = (TextView) findViewById(R.id.my_honest);
        cur_meili = (TextView) findViewById(R.id.my_meili);
        cur_trends = (TextView) findViewById(R.id.my_trends);
        headPhoto = (ImageView) findViewById(R.id.headPortrait);
        user = userManager.getCurrentUser(User.class);
        if (user.getAvatar() == null || user.getAvatar().equals("")) {
            headPhoto.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.default_head));
        } else {
            ImageLoader.getInstance().displayImage(user.getAvatar(), headPhoto, ImageLoadOptions.getOptions(1));
        }

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        toggle.syncState();
        // setHeader();
        NearSearchActivity.setOnSearchListener(this);
    }

    private void noPeopleHint(boolean isNo, String text) {
        if (noOneHint != null) {
            noOneHint.setText(text);
            if (isNo)
                noOneLayout.setVisibility(View.VISIBLE);
            else
                noOneLayout.setVisibility(View.GONE);
        }
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(NearPeopleActivity.this, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
        }
    };
    private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {

        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
            new ShareAction(NearPeopleActivity.this).setPlatform(share_media).setCallback(umShareListener)
                    .withText("顺带，见上一面就有可能")
                    .withTitle("顺带")
                    .withTargetUrl(Config.APPWEBSITE)
                    //.withMedia(new UMImage(NearPeopleActivity.this, BitmapFactory.decodeResource(getResources(), R.drawable.logo2)))
                    .setShareboardclickCallback(shareBoardlistener)
                    .share();
        }
    };

    private void configPlatforms() {

        new ShareAction(this).setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SMS, SHARE_MEDIA.TENCENT)
                .setContentList(new ShareContent(), new ShareContent())
                .withText("顺带，见上一面就有可能")
                .withTitle("顺带")
                .withTargetUrl(Config.APPWEBSITE)
                //.withMedia(new UMImage(this,BitmapFactory.decodeResource(getResources(), R.drawable.logo2)))
                .setShareboardclickCallback(shareBoardlistener)
                .setListenerList(umShareListener, umShareListener)
                .open();
    }


    private void setHeader() {

        if (UserManager.getUser() != null)
            user = UserManager.getUser();
        cur_trends.setText(user.getTrends());
        nick_name.setText(user.getUsername());
        if (user.getSex()) {
            cur_age.setText("♂ " + user.getAge());
        } else {
            cur_age.setText("♀ " + user.getAge());
        }
        cur_conslelattio.setText(user.getConstllation());
        cur_meili.setText("魅力 " + String.valueOf(decimalFormat.format(Double.parseDouble(user.getHonest()))));
        cur_hobest.setText("诚信 " + String.valueOf(decimalFormat.format(Double.parseDouble(user.getMeili()))));
    }

    private void initXListView() {
        mListView = (XListView) findViewById(R.id.list_near);
        mListView.setOnItemClickListener(this);
        mListView.setPullLoadEnable(false);
        mListView.setPullRefreshEnable(true);
        mListView.setXListViewListener(this);
        mListView.pullRefreshing();
        adapter = new NearPeopleAdapter(NearPeopleActivity.this, nears);
        mListView.setAdapter(adapter);
        //initNearByList(false, null);
        progress = new ProgressDialog(NearPeopleActivity.this);
        progress.setMessage("正在查询附近" + miles + "千米内的人...");
        progress.setCanceledOnTouchOutside(true);
        progress.show();
    }


    ProgressDialog progress;

    private void initNearByList(final int isUpdate, List<BmobQuery<User>> queries) {
        if(!NetworkHelper.isNetworkAvailable(this)){
            ShowToast("网络不可用");
            return;
        }
        if (point != null) {
            if (isUpdate == 0 && progress == null) {
                progress = new ProgressDialog(NearPeopleActivity.this);
                progress.setMessage("正在查询附近" + miles + "千米内的人...");
                progress.setCanceledOnTouchOutside(true);
                progress.show();
            }
            Count = 0;
            BmobQuery<User> query = new BmobQuery<User>();
            if (queries == null) {
                queries = new ArrayList<>();
                BmobQuery<User> q = new BmobQuery<>();
                q.addWhereNotEqualTo("username", BmobUserManager.getInstance(this).getCurrentUserName());

                BmobQuery<User> q1 = new BmobQuery<>();
                q1.addWhereWithinRadians("location", point, miles);
                queries.add(q);
                queries.add(q1);

            }
            query.and(queries);
            query.findObjects(this, new FindListener<User>() {
                @Override
                public void onSuccess(List<User> list) {
                    if (CollectionUtils.isNotNull(list)) {
                        noPeopleHint(false, "");
                        if (isUpdate != 0) {
                            nears.clear();
                        }
                        nears = list;
                        adapter.updateList(nears);
                        Count = list.size();
                        if (Count < limit) {
                            mListView.setPullLoadEnable(false);
                        } else {
                            mListView.setPullLoadEnable(true);
                        }
                        listAnim();
                    } else {
                        if (isUpdate != 2)
                            noPeopleHint(true, "暂时找不到附近的人，点击右上角可以筛选附近的人哟");
                        else
                            noPeopleHint(true, "找不到符合筛选条件的人");
                    }

                    if (isUpdate == 0) {
                        progress.dismiss();
                    } else {
                        refreshPull();
                        if (progress != null)
                            progress.dismiss();
                    }
                }

                @Override
                public void onError(int i, String s) {
                    if (isUpdate != 2)
                        noPeopleHint(true, "获取附近的人失败");
                    else
                        noPeopleHint(true, "获取当前筛选条件的人失败");
                    mListView.setPullLoadEnable(false);
                    if (isUpdate == 0) {
                        progress.dismiss();
                    } else {
                        refreshPull();
                        if (progress != null)
                            progress.dismiss();
                    }
                }
            });
        } else {
            noPeopleHint(true, "获取当前位置失败\n刷新列表再次获取附近的人");
            progress.dismiss();
            refreshPull();
        }
    }

    /**
     * 查询更多
     */
    private void queryMoreNearList() {
        if(!NetworkHelper.isNetworkAvailable(this)){
            ShowToast("网络不可用");
            return;
        }
        BmobQuery<User> query = new BmobQuery<>();
        if (queries == null) {
            queries = new ArrayList<>();
            BmobQuery<User> q = new BmobQuery<>();
            q.addWhereNotEqualTo("username", BmobUserManager.getInstance(this).getCurrentUserName());

            BmobQuery<User> q1 = new BmobQuery<>();
            q1.addWhereWithinRadians("location", point, miles);
            queries.add(q);
            queries.add(q1);

        }
        query.and(queries);
        query.setSkip(Count);
        query.findObjects(this, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if (CollectionUtils.isNotNull(list)) {
                    adapter.addAll(list);
                    Count += list.size();
                    listAnim();
                    if (list.size() < limit) {
                        mListView.setPullLoadEnable(false);
                        ShowToast("已无更多附近的人");
                    }
                }
                refreshLoad();
            }

            @Override
            public void onError(int i, String s) {
                ShowLog("查询更多附近的人出错:" + s);
                mListView.setPullLoadEnable(false);
                refreshLoad();
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        User user = (User) adapter.getItem(position - 1);
        Intent intent = new Intent(this, SetMyInfoActivity.class);
        intent.putExtra("from", "add");
        intent.putExtra("username", user.getUsername());
        startAnimActivity(intent);
    }

    @Override
    public void onRefresh() {
        initNearByList(1, queries);
    }

    private void refreshLoad() {
        if (mListView.getPullLoading()) {
            mListView.stopLoadMore();
        }
    }

    private void refreshPull() {
        if (mListView.getPullRefreshing()) {
            mListView.stopRefresh();
        }
    }

    @Override
    public void onLoadMore() {
        queryMoreNearList();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bar_near_dialog:
                startAnimActivity(new Intent(NearPeopleActivity.this, NearSearchActivity.class));
                break;
            case R.id.need_for_help:
                AnimUitls.BunttonAnim(view, 300);
                Intent intent = new Intent(this, HelpActivity.class);
                intent.putExtra("from", MyConstant.S_R);
                intent.putExtra("me", false);
                startAnimActivity(intent);
                break;
            case R.id.offer_help:
                AnimUitls.BunttonAnim(view, 300);
                Intent i = new Intent(this, HelpActivity.class);
                i.putExtra("from", MyConstant.H_R);
                i.putExtra("me", false);
                startAnimActivity(i);
                break;

            case R.id.my_near:
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.my_recent:
                msg_tip.setVisibility(View.GONE);
                drawer.closeDrawer(GravityCompat.START);
                startAnimActivity(new Intent(NearPeopleActivity.this, RecentActivity.class));
                break;
            case R.id.my_seek:
                drawer.closeDrawer(GravityCompat.START);
                Intent intent0 = new Intent(NearPeopleActivity.this, HelpActivity.class);
                intent0.putExtra("from", MyConstant.S_R);
                intent0.putExtra("me", false);
                startAnimActivity(intent0);
                break;
            case R.id.my_help:
                drawer.closeDrawer(GravityCompat.START);
                Intent intent1 = new Intent(NearPeopleActivity.this, HelpActivity.class);
                intent1.putExtra("from", MyConstant.H_R);
                intent1.putExtra("me", false);
                startAnimActivity(intent1);
                break;
            case R.id.my_all:
                drawer.closeDrawer(GravityCompat.START);
                startAnimActivity(new Intent(context, CityActivity.class).putExtra("from", "ALL"));
                break;
            case R.id.share_app:
                configPlatforms();
                break;
            case R.id.my_message:
                messageDialog();
                break;
            case R.id.top_myInfo:
                drawer.closeDrawer(GravityCompat.START);
                Intent intent4 = new Intent(NearPeopleActivity.this, SetMyInfoActivity.class);
                intent4.putExtra("username", userManager.getCurrentUserName());
                intent4.putExtra("from", "me");
                startAnimActivity(intent4);
                break;

            case R.id.my_friends:
                drawer.closeDrawer(GravityCompat.START);
                Intent intent5 = new Intent(NearPeopleActivity.this, FriendsActivity.class);
                startAnimActivity(intent5);
                break;
            case R.id.my_setting:
                drawer.closeDrawer(GravityCompat.START);
                Intent intent6 = new Intent(NearPeopleActivity.this, SettingActivity.class);
                startAnimActivity(intent6);
                break;
            case R.id.my_community:
                drawer.closeDrawer(GravityCompat.START);
                Intent intent7 = new Intent(NearPeopleActivity.this, CommunityActivity.class);
                startAnimActivity(intent7);
                break;
        }
    }


    private void ShowMsgTip() {
        if (BmobDB.create(this).hasUnReadMsg()) {
            if (!drawer.isDrawerOpen(GravityCompat.START))
                drawer.openDrawer(GravityCompat.START);
            msg_tip.setVisibility(View.VISIBLE);
            vibrator.vibrate(500);
            soundPool.play(soundMap.get(1), 1, 1, 0, 0, 1);
        } else {
            if (drawer.isDrawerOpen(GravityCompat.START))
                msg_tip.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (UPDATE_HEAD) {
            String url = userManager.getCurrentUser(User.class).getAvatar();
            if (url != null && !url.equals("")) {
                ImageLoader.getInstance().displayImage(url, headPhoto, ImageLoadOptions.getOptions(1));
                UPDATE_HEAD = false;
            }
        }
        MyMessageReceiver.ehList.add(this);// 监听推送的消息
        //清空
        MyMessageReceiver.mNewNum = 0;
        updateUserInfos();
        setHeader();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MyMessageReceiver.ehList.remove(this);// 取消监听推送的消息
    }

    @Override
    public void onMessage(BmobMsg message) {
        refreshNewMsg(message);
    }


    /**
     * 刷新界面
     */
    private void refreshNewMsg(BmobMsg message) {
        boolean isAllow = MyApplication.getInstance().getSpUtil().isAllowVoice();
        if (isAllow) {
            MyApplication.getInstance().getMediaPlayer().start();
        }
        ShowMsgTip();
        if (message != null) {
            BmobChatManager.getInstance(NearPeopleActivity.this).saveReceiveMessage(true, message);
        }
    }

    NewBroadcastReceiver newReceiver;

    private void initNewMessageBroadCast() {
        newReceiver = new NewBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
        //优先级要低于ChatActivity
        intentFilter.setPriority(3);
        registerReceiver(newReceiver, intentFilter);
    }

    @Override
    public void onSearch(List<BmobQuery<User>> queries, int k) {
        this.queries = queries;
        if (k != 0 && point != null) {
            miles = k;
            BmobQuery<User> query = new BmobQuery<>();
            query.addWhereWithinKilometers("location", point, miles);
            queries.add(query);
        }
        BmobQuery<User> q = new BmobQuery<>();
        q.addWhereNotEqualTo("username", BmobUserManager.getInstance(this).getCurrentUserName());
        queries.add(q);
        initNearByList(2, queries);
    }


    /**
     * 新消息广播接收者
     */
    private class NewBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            //刷新界面
//            refreshNewMsg(null);
            abortBroadcast();
        }
    }

    TagBroadcastReceiver userReceiver;

    private void initTagMessageBroadCast() {
        // 注册接收消息广播
        userReceiver = new TagBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_ADD_USER_MESSAGE);
        //优先级要低于ChatActivity
        intentFilter.setPriority(3);
        registerReceiver(userReceiver, intentFilter);
    }

    /**
     * 标签消息广播接收者
     */
    private class TagBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            BmobInvitation message = (BmobInvitation) intent.getSerializableExtra("invite");
            refreshInvite(message);
            // 记得把广播给终结掉
            abortBroadcast();
        }
    }

    @Override
    public void onNetChange(boolean isNetConnected) {
        if (isNetConnected) {
            ShowToast(R.string.network_tips);
        }
    }

    @Override
    public void onAddUser(BmobInvitation message) {
        refreshInvite(message);
    }

    /**
     * 刷新好友请求
     *
     * @param @param message
     * @return void
     * @throws
     * @Title: notifyAddUser
     * @Description: TODO
     */
    private void refreshInvite(BmobInvitation message) {
        boolean isAllow = MyApplication.getInstance().getSpUtil().isAllowVoice();
        if (isAllow) {
            MyApplication.getInstance().getMediaPlayer().start();
        }
        ShowMsgTip();
//		}else{
        //同时提醒通知
        String tickerText = message.getFromname() + "请求添加好友";
        boolean isAllowVibrate = MyApplication.getInstance().getSpUtil().isAllowVibrate();
        BmobNotifyManager.getInstance(this).showNotify(isAllow, isAllowVibrate, R.drawable.logo, tickerText, message.getFromname(), tickerText.toString(), NewFriendActivity.class);
        //}
    }

    @Override
    public void onOffline() {
        showOfflineDialog(this);
    }

    @Override
    public void onReaded(String conversionId, String msgTime) {
    }


    private static long firstTime;

    /**
     * 连续按两次返回键就退出
     */
    @Override
    public void onBackPressed() {
        if (firstTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            ShowToast("再按一次退出程序");
        }
        firstTime = System.currentTimeMillis();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopLocListener();
        try {
            unregisterReceiver(newReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            unregisterReceiver(userReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //取消定时检测服务
        BmobChat.getInstance(this).stopPollService();
    }


    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation != null && mLocationClient != null) {

                loc = bdLocation.getCity();
                if (loc == null)
                    return;
                final double lat = bdLocation.getLatitude();
                final double lng = bdLocation.getLongitude();
                point = new BmobGeoPoint(bdLocation.getLongitude(), bdLocation.getLatitude());
                User user = userManager.getCurrentUser(User.class);
                user.setCity(loc);
                user.setLocation(point);
                user.update(getBaseContext(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        geoPreference.edit().remove("Latitude").apply();
                        geoPreference.edit().remove("Longitude").apply();
                        geoPreference.edit().putString("Latitude", String.valueOf(lat)).apply();
                        geoPreference.edit().putString("Longitude", String.valueOf(lng)).apply();


                        if (BuildConfig.DEBUG) Log.d("MyLocationListener", "更新地理位置成功   " + loc);
                        option.setScanSpan(1800000);
                        if (mLocationClient != null)
                            mLocationClient.setLocOption(option);// 使用设置
                        initNearByList(0, null);
                        if (!once) {
                            BmobQuery<UserCity> query = new BmobQuery<UserCity>();
                            query.addWhereEqualTo("City", loc);
                            query.findObjects(getBaseContext(), new FindListener<UserCity>() {
                                @Override
                                public void onSuccess(List<UserCity> list) {
                                    if (!list.isEmpty()) {
                                        if (list.get(0).getCount() != null)
                                            Count = list.get(0).getCount();
                                        if (Count >= 1) {
                                            UserCity userCity = new UserCity();
                                            userCity.setCity(loc);
                                            userCity.setObjectId(list.get(0).getObjectId());
                                            Count++;
                                            userCity.setCount(Count);
                                            userCity.update(getBaseContext(), new UpdateListener() {
                                                @Override
                                                public void onSuccess() {
                                                    once = true;
                                                }

                                                @Override
                                                public void onFailure(int i, String s) {
                                                }
                                            });
                                        } else {
                                            UserCity userCity = new UserCity();
                                            userCity.setCity(loc);
                                            userCity.setCount(1);
                                            userCity.save(getBaseContext(), new SaveListener() {
                                                @Override
                                                public void onSuccess() {

                                                    System.out.println("City saved");
                                                    once = true;
                                                }

                                                @Override
                                                public void onFailure(int i, String s) {
                                                }
                                            });
                                        }
                                    } else {
                                        UserCity userCity = new UserCity();
                                        userCity.setCity(loc);
                                        userCity.setCount(1);
                                        userCity.save(getBaseContext(), new SaveListener() {
                                            @Override
                                            public void onSuccess() {
                                                System.out.println("City saved");
                                                once = true;
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
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        ShowToast("更新地理位置失败");
                    }
                });
            } else {
                LOC_TIME--;
                if (LOC_TIME < 0)
                    stopLocListener();
            }
        }
    }

    public void stopLocListener() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
            mLocationClient = null;
        }
    }

    private void initLocClient() {
        mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
        mLocationClient.registerLocationListener(locationListener); // 注册监听函数
        option = new LocationClientOption();
        option.setOpenGps(true);// 打开GPS
        option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
        option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(10000);// 设置发起定位请求的间隔时间为3000ms
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        option.setTimeOut(5000);
        mLocationClient.setLocOption(option);// 使用设置
        mLocationClient.start();// 开启定位SDK
        mLocationClient.requestLocation();// 开始请求位置
    }


    AlertDialog dialog;
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.dismiss();
            drawer.closeDrawer(GravityCompat.START);
            Intent intent4 = new Intent(NearPeopleActivity.this, SetMyInfoActivity.class);
            intent4.putExtra("username", userManager.getCurrentUserName());
            intent4.putExtra("from", "me");
            startAnimActivity(intent4);
        }
    };

    private void messageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.alert);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_massage_select, null);
        view.findViewById(R.id.my_message_seek).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                drawer.closeDrawer(GravityCompat.START);
                Intent intent11 = new Intent(NearPeopleActivity.this, HelpActivity.class);
                intent11.putExtra("from", MyConstant.H_R);
                intent11.putExtra("me", true);
                startAnimActivity(intent11);
            }
        });
        view.findViewById(R.id.my_message_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                drawer.closeDrawer(GravityCompat.START);
                Intent intent12 = new Intent(NearPeopleActivity.this, HelpActivity.class);
                intent12.putExtra("from", MyConstant.H_R);
                intent12.putExtra("me", true);
                startAnimActivity(intent12);
            }
        });
        view.findViewById(R.id.my_message_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                drawer.closeDrawer(GravityCompat.START);
                startAnimActivity(new Intent(NearPeopleActivity.this, MyCommentActivity.class));
            }
        });
        view.findViewById(R.id.my_honest_info).setOnClickListener(listener);
        view.findViewById(R.id.my_meili_info).setOnClickListener(listener);
        builder.setView(view);
        dialog = builder.show();
    }

    public void listAnim() {
        LayoutAnimationController lac = new LayoutAnimationController(AnimationUtils.loadAnimation(NearPeopleActivity.this, R.anim.zoom_enter));
        lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
        mListView.setLayoutAnimation(lac);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                drawer.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
