package dong.lan.shundai.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 桂栋 on 2015/7/10.
 */
public class HomeComment extends BmobObject {
    private User ComUser;
    private String HomeCom;
    private User who;


    public void setUser(User user) {
        this.ComUser = user;
    }


    public void setHomeCom(String homeCom) {
        HomeCom = homeCom;
    }

    public void setWho(User who) {
        this.who = who;
    }

    public User getUser() {
        return ComUser;
    }


    public String getHomeCom() {
        return HomeCom;
    }

    public User getWho() {
        return who;
    }
}
