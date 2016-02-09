package dong.lan.shundai.broadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import dong.lan.shundai.BuildConfig;
import dong.lan.shundai.config.BmobConstants;

/**
 * Created by 梁桂栋 on 2016/1/28.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {

    private Activity activity;
    public MyBroadcastReceiver(Activity ac)
    {
        activity = ac;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent==null)
            return;
        switch (intent.getAction())
        {
            case BmobConstants.ACTION_FINISH:
                activity.finish();
                if (BuildConfig.DEBUG) Log.d(activity.getLocalClassName(), "广播退出");

        }

    }

}