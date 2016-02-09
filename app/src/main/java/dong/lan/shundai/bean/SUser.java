package dong.lan.shundai.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 桂栋 on 2015/5/28.
 */
public class SUser extends BmobObject {
    private User user;
    private String honest;
    private String meiLi;

    public String getMeiLi() {
        return meiLi;
    }

    public void setMeiLi(String meiLi) {
        this.meiLi = meiLi;
    }

    public String getHonest() {
        return honest;
    }

    public void setHonest(String honest) {
        this.honest = honest;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

