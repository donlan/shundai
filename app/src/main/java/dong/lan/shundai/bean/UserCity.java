package dong.lan.shundai.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 桂栋 on 2015/5/3.
 */
public class UserCity extends BmobObject {


    private String username;
    private String City;
    private Integer count;

    public String getCity() {
        return City;
    }

    public Integer getCount() {
        return count;
    }


    public String getUsername() {
        return username;
    }

    public void setCity(String city) {
        City = city;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
