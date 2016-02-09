package dong.lan.shundai.ui;

import android.os.Bundle;
import android.view.View;

import com.umeng.analytics.MobclickAgent;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.ui.fragments.CommunityMainFragment;

import dong.lan.shundai.R;

public class CommunityActivity extends ActivityBase {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community);

        CommunitySDK mCommSDK = CommunityFactory.getCommSDK(getApplicationContext());
        // 初始化sdk，请传递ApplicationContext
        mCommSDK.initSDK(getApplicationContext());

        CommunityMainFragment mFeedsFragment = new CommunityMainFragment();
        //设置Feed流页面的返回按钮不可见
        mFeedsFragment.setBackButtonVisibility(View.INVISIBLE);
        //添加并显示Fragment
        getSupportFragmentManager().beginTransaction().add(R.id.container, mFeedsFragment).commit();


    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
