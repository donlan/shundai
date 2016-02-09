package dong.lan.shundai.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by 桂栋 on 2015/5/28.
 */
public class WallPhoto extends BmobObject {

    private Integer SeeCount;
    private Integer ComCount;
    private Integer zan;
    private Integer Day;
    private List<String> photos;
    private SUser sUser;
    private User user;
    private Integer index;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public Integer getSeeCount() {
        return SeeCount;
    }

    public void setSeeCount(Integer seeCount) {
        SeeCount = seeCount;
    }

    public Integer getComCount() {
        return ComCount;
    }

    public void setComCount(Integer comCount) {
        ComCount = comCount;
    }

    public void setDay(Integer day) {
        Day = day;
    }

    public Integer getDay() {
        return Day;
    }

    public Integer getZan() {
        return zan;
    }

    public void setZan(Integer zan) {
        this.zan = zan;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SUser getsUser() {
        return sUser;
    }

    public void setsUser(SUser sUser) {
        this.sUser = sUser;
    }
}
