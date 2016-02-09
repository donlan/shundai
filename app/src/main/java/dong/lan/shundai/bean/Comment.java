package dong.lan.shundai.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 梁桂栋 on 2015/11/27.
 */
public class Comment extends BmobObject {
    private User user;
    private Help help;
    private String content;
    private Boolean isHelped;
    private Integer srore;
    private String reply;

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public Integer getSrore() {
        return srore;
    }

    public void setSrore(Integer srore) {
        this.srore = srore;
    }

    public Boolean getIsHelped() {
        return isHelped;
    }

    public void setIsHelped(Boolean isHelped) {
        this.isHelped = isHelped;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Help getHelp() {
        return help;
    }

    public void setHelp(Help help) {
        this.help = help;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
