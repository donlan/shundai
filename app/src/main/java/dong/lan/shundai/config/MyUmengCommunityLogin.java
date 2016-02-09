package dong.lan.shundai.config;

import android.content.Context;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Source;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.login.Loginable;

import cn.bmob.im.BmobUserManager;
import dong.lan.shundai.bean.User;

/**
 * Created by 桂栋 on 2015/8/4.
 */
public class MyUmengCommunityLogin implements Loginable {

    private boolean isLogin = false;
    CommUser user;
    private static MyUmengCommunityLogin instance;

    public static MyUmengCommunityLogin getInstance() {
        if (instance == null)
            instance = new MyUmengCommunityLogin();
        return instance;
    }

    public MyUmengCommunityLogin() {
    }

    @Override
    public void login(Context context, LoginListener loginListener) {
        if (!isLogin) {
            User bUser =BmobUserManager.getInstance(context).getCurrentUser(User.class);
            if (bUser == null)
                return;
            user = new CommUser("id" + bUser.getUsername().hashCode());
            user.source = Source.SELF_ACCOUNT;
            user.name = bUser.getUsername();
            user.iconUrl = bUser.getAvatar();
            if (bUser.getSex())
                user.gender = CommUser.Gender.FEMALE;
            else
                user.gender = CommUser.Gender.MALE;
            isLogin = true;
            Config.commUser =user;
            loginListener.onComplete(200, user);
        } else {
            loginListener.onComplete(200, Config.commUser);
        }
    }

    @Override
    public void logout(Context context, LoginListener loginListener) {
        isLogin = false;
    }

}
