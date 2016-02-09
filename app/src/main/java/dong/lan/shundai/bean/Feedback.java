package dong.lan.shundai.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 桂栋 on 2015/6/13.
 */
public class Feedback extends BmobObject {

    public String feedback;
    public String contact;
    public String username;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
