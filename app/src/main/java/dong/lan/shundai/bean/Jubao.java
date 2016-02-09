package dong.lan.shundai.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 桂栋 on 2015/7/10.
 */
public class Jubao extends BmobObject {

    private User from_user;
    private User to_user;
    private boolean isVerify;

    public User getFrom_user() {
        return from_user;
    }

    public void setFrom_user(User from_user) {
        this.from_user = from_user;
    }

    public User getTo_user() {
        return to_user;
    }

    public void setTo_user(User to_user) {
        this.to_user = to_user;
    }

    public boolean isVerify() {
        return isVerify;
    }

    public void setIsVerify(boolean isVerify) {
        this.isVerify = isVerify;
    }
}
