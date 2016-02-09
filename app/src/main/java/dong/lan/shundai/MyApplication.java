package dong.lan.shundai;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.sdkmanager.LocationSDKManager;
import com.umeng.comm.core.sdkmanager.LoginSDKManager;
import com.umeng.community.location.DefaultLocationImpl;
import com.umeng.fb.push.FeedbackPush;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.IUmengUnregisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.PlatformConfig;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.datatype.BmobGeoPoint;
import dong.lan.shundai.config.Config;
import dong.lan.shundai.config.MyUmengCommunityLogin;
import dong.lan.shundai.util.CollectionUtils;
import dong.lan.shundai.util.MyConstant;
import dong.lan.shundai.util.SharePreferenceUtil;

/**
 * 自定义全局Applcation类
 */
public class MyApplication extends Application {

    public static MyApplication mInstance;
    public static BmobGeoPoint lastPoint = null;// 上一次定位到的经纬度
    public static CommunitySDK communitySDK;
    public static final String CALLBACK_RECEIVER_ACTION = "callback_receiver_action";
    public static IUmengRegisterCallback mRegisterCallback;
    public static IUmengUnregisterCallback mUnregisterCallback;
    public SharedPreferences GEOpreference;
    public LocationClient mLocationClient;
    public MyLocationListener mMyLocationListener;

    @Override
    public void onCreate() {
        GEOpreference = getSharedPreferences("GeoPoint", MODE_PRIVATE);
        BmobChat.getInstance(this).init(Config.applicationId);
        BmobChat.getInstance(this).startPollService(30);
        BmobChat.getInstance(this).start(this);
        PushAgent mPushAgent = PushAgent.getInstance(this);
        FeedbackPush.getInstance(this).init(true);
        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                new Handler(getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {

                        UTrack.getInstance(getApplicationContext()).trackMsgClick(msg, true);

                    }
                });
            }

            @Override
            public Notification getNotification(Context context,
                                                UMessage msg) {
                switch (msg.builder_id) {
                    case 1:
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                        RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
                        myNotificationView.setTextViewText(R.id.notification_title, msg.title);
                        myNotificationView.setTextViewText(R.id.notification_text, msg.text);
                        myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
                        myNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
                        builder.setContent(myNotificationView);
                        builder.setAutoCancel(false);
                        Notification mNotification = builder.build();
                        //由于Android v4包的bug，在2.3及以下系统，Builder创建出来的Notification，并没有设置RemoteView，故需要添加此代码
                        mNotification.contentView = myNotificationView;
                        return mNotification;
                    default:
                        //默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }
        };
        mPushAgent.setMessageHandler(messageHandler);

        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                if (BuildConfig.DEBUG) Log.d("MyApplication", msg.activity);
            }
        };
        mPushAgent.setNotificationClickHandler(notificationClickHandler);

        mRegisterCallback = new IUmengRegisterCallback() {

            @Override
            public void onRegistered(String registrationId) {
                Intent intent = new Intent(CALLBACK_RECEIVER_ACTION);
                sendBroadcast(intent);
            }

        };
        mPushAgent.setRegisterCallback(mRegisterCallback);

        mUnregisterCallback = new IUmengUnregisterCallback() {

            @Override
            public void onUnregistered(String registrationId) {
                Intent intent = new Intent(CALLBACK_RECEIVER_ACTION);
                sendBroadcast(intent);
            }
        };
        mPushAgent.setUnregisterCallback(mUnregisterCallback);
        BmobChat.DEBUG_MODE = true;
        mInstance = this;
        LocationSDKManager.getInstance().addAndUse(new DefaultLocationImpl());
        LoginSDKManager.getInstance().addAndUse(MyUmengCommunityLogin.getInstance());
        communitySDK = CommunityFactory.getCommSDK(this);
        communitySDK.initSDK(this);
        PlatformConfig.setWeixin(Config.wxAPPID, Config.wxSecret);
        PlatformConfig.setQQZone(MyConstant.QQ_APP_ID, MyConstant.QQ_APP_KEY);
        PlatformConfig.setSinaWeibo(MyConstant.SINA_KEY, MyConstant.SINA_SECRET);
        mPushAgent.enable(new IUmengRegisterCallback() {
            @Override
            public void onRegistered(String s) {
                if (BuildConfig.DEBUG) Log.d("MyApplication", s);
            }
        });
        mPushAgent.setDebugMode(true);
        mPushAgent.setMessageHandler(new UmengMessageHandler() {
            @Override
            public void dealWithNotificationMessage(Context arg0, UMessage msg) {
                super.dealWithNotificationMessage(arg0, msg);
                if (BuildConfig.DEBUG) Log.d("MyApplication", msg.activity);
            }
        });
        mPushAgent.onAppStart();
        init();
        super.onCreate();
    }

    private void init() {

        mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        initImageLoader(getApplicationContext());
        if (BmobUserManager.getInstance(getApplicationContext())
                .getCurrentUser() != null) {
            contactList = CollectionUtils.list2map(BmobDB.create(getApplicationContext()).getContactList());
        }
        initBaidu();

    }

    /**
     * 初始化百度相关sdk
     */
    private void initBaidu() {
        SDKInitializer.initialize(this);
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
    }


    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;
            double latitude = location.getLatitude();
            double longtitude = location.getLongitude();
            if (lastPoint != null) {
                if (lastPoint.getLatitude() == location.getLatitude()
                        && lastPoint.getLongitude() == location.getLongitude()) {
                    mLocationClient.stop();
                    return;
                }
            }

            lastPoint = new BmobGeoPoint(longtitude, latitude);
        }
    }

    /**
     * 初始化ImageLoader
     */
    public static void initImageLoader(Context context) {
        File cacheDir = StorageUtils.getOwnCacheDirectory(context,
                "Shundai/Cache");// 获取到缓存的目录地址
        // 创建配置ImageLoader(所有的选项都是可选的,只使用那些你真的想定制)，这个可以设定在APPLACATION里面，设置为全局的配置参数
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                // 线程池内加载的数量
                .threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCache(new WeakMemoryCache())
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                        // 将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCache(new UnlimitedDiscCache(cacheDir))// 自定义缓存路径
                        // .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);// 全局初始化此配置
    }

    public static MyApplication getInstance() {
        return mInstance;
    }

    // 单例模式，才能及时返回数据
    SharePreferenceUtil mSpUtil;
    public static final String PREFERENCE_NAME = "_sharedinfo";

    public synchronized SharePreferenceUtil getSpUtil() {
        if (mSpUtil == null) {
            String currentId = BmobUserManager.getInstance(
                    getApplicationContext()).getCurrentUserObjectId();
            String sharedName = currentId + PREFERENCE_NAME;
            mSpUtil = new SharePreferenceUtil(this, sharedName);
        }
        return mSpUtil;
    }

    NotificationManager mNotificationManager;

    public NotificationManager getNotificationManager() {
        if (mNotificationManager == null)
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return mNotificationManager;
    }

    MediaPlayer mMediaPlayer;

    public synchronized MediaPlayer getMediaPlayer() {
        if (mMediaPlayer == null)
            mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
        return mMediaPlayer;
    }

    private String latitude = "";
    private String longtitude = "";

    /**
     * 获取经度
     */
    public String getLongtitude() {
        longtitude = GEOpreference.getString("Longitude", "110");
        return longtitude;
    }

    /**
     * 设置经度
     */
    public void setLongtitude(String lon) {

        GEOpreference.edit().remove("Longitude").apply();
        GEOpreference.edit().putString("Longitude", lon).apply();
        longtitude = lon;
    }

    /**
     * 获取纬度
     */
    public String getLatitude() {
        latitude = GEOpreference.getString("Latitude", "23");
        return latitude;
    }

    /**
     * 设置维度
     */
    public void setLatitude(String lat) {
        GEOpreference.edit().remove("Latitude").apply();
        GEOpreference.edit().putString("Latitude", lat).apply();
        latitude = lat;
    }

    public BmobGeoPoint getCurPoint()
    {
        if(lastPoint==null)
            return  new BmobGeoPoint(Double.valueOf(getLongtitude()),Double.valueOf(getLatitude()));
        else
            return lastPoint;
    }
    private Map<String, BmobChatUser> contactList = new HashMap<String, BmobChatUser>();

    /**
     * 获取内存中好友user list
     *
     * @return
     */
    public Map<String, BmobChatUser> getContactList() {
        return contactList;
    }

    /**
     * 设置好友user list到内存中
     *
     * @param contactList
     */
    public void setContactList(Map<String, BmobChatUser> contactList) {
        if (this.contactList != null) {
            this.contactList.clear();
        }
        this.contactList = contactList;
    }

    /**
     * 退出登录,清空缓存数据
     */
    public void logout(LoginListener listener) {
        if(lastPoint!=null) {
            setLatitude(String.valueOf(lastPoint.getLatitude()));
            setLongtitude(String.valueOf(lastPoint.getLongitude()));
        }
        BmobUserManager.getInstance(getApplicationContext()).logout();
        communitySDK.logout(this, listener);
        setContactList(null);
    }


}
