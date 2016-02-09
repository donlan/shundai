package dong.lan.shundai.bean;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * 重载BmobChatUser对象：若还有其他需要增加的属性可在此添加
 */
public class User extends BmobChatUser {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * //显示数据拼音的首字母
     */
    private String sortLetters;

    /**
     * //性别-true-男
     */
    private Boolean sex;

    /**
     * 地理坐标
     */
    private BmobGeoPoint location;//

    private String trends;
    private String birth;
    private String goal;
    private String Constllation;
    private String v_honest;
    private String v_meili;
    private String age;
    private String City;
    private String state;


    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }


    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getHonest() {
        return v_honest;
    }

    public String getMeili() {
        return v_meili;
    }

    public void setMeili(String meili) {
        this.v_meili = meili;
    }

    public void setHonest(String honest) {
        this.v_honest = honest;
    }

    public String getConstllation() {
        return Constllation;
    }

    public void setConstllation(String constllation) {
        Constllation = constllation;
    }

    public String getGoal() {
        return goal;
    }


    public void setGoal(String goal) {
        this.goal = goal;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getBirth() {
        return this.birth;
    }

    public void setTrends(String trends1) {
        this.trends = trends1;
    }

    public String getTrends() {
        return this.trends;

    }




    public BmobGeoPoint getLocation() {
        return location;
    }

    public void setLocation(BmobGeoPoint location) {
        this.location = location;
    }

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

}
