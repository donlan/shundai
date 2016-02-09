package dong.lan.shundai.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 桂栋 on 2015/5/3.
 */
public class RecentlyCity  extends BmobObject {


    private String username;
    private Integer Count;
    private String City;

    public String getCity() {
        return City;
    }


    public void setCity(String city) {
        City = city;
    }

    public String getUsername() {
        return username;
    }

    public Integer getCount() {
        return Count;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCount(Integer count) {
        Count = count;
    }
}
