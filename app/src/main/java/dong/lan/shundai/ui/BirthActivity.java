package dong.lan.shundai.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;
import dong.lan.shundai.R;
import dong.lan.shundai.bean.User;

/**
 * Created by 梁桂栋 on 2016/1/23.
 */
public class BirthActivity extends BaseActivity {
    private String year, month, day;
    private DatePicker picker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birth);
        initView();
    }

    private void initView() {
        picker = (DatePicker) findViewById(R.id.birth_picker);
        picker.setMaxDate(new Date().getTime());
        picker.init(2015, 4, 20, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                year = i + "";
                month = (i1 + 1) + "";
                day = i2 + "";
            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done();
            }
        });
    }

    private void done() {
        String birthday = year + "年" + month + "月" + day + "日";
        User user = BmobUser.getCurrentUser(this,User.class);
        user.setBirth(birthday);
        user.setAge(String.valueOf(getAge(year, month)));
        user.setConstllation(getConstllation(month, day));
        user.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                ShowToast("更新个人信息成功！");
            }

            @Override
            public void onFailure(int i, String s) {
                ShowToast("更新个人信息失败！");
            }
        });
        finish();
    }

    private String getConstllation(String mm, String dd) {
        int M = Integer.parseInt(mm);
        int D = Integer.parseInt(dd);
        if (M == 1 && D >= 21 || M == 2 && D <= 19) {
            return "水瓶座";
        }
        if (M == 2 && D >= 20 || M == 3 && D <= 20) {
            return "双鱼座";
        }
        if (M == 3 && D >= 21 || M == 4 && D <= 20) {
            return "白羊座";
        }
        if (M == 4 && D >= 21 || M == 5 && D <= 21) {
            return "金牛座";
        }
        if (M == 5 && D >= 22 || M == 6 && D <= 21) {
            return "双子座";
        }
        if (M == 6 && D >= 22 || M == 7 && D <= 22) {
            return "巨蟹座";
        }
        if (M == 7 && D >= 23 || M == 8 && D <= 22) {
            return "狮子座";
        }
        if (M == 8 && D >= 23 || M == 9 && D <= 23) {
            return "处女座";
        }
        if (M == 9 && D >= 24 || M == 10 && D <= 23) {
            return "天秤座";
        }
        if (M == 10 && D >= 24 || M == 11 && D <= 22) {
            return "天蝎座";
        }
        if (M == 11 && D >= 23 || M == 12 && D <= 21) {
            return "射手座";
        }
        if (M == 12 && D >= 22 || M == 1 && D <= 20) {
            return "摩羯座";
        }
        return "未知";
    }

    private int getAge(String y, String m) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int age = year - Integer.parseInt(y);
        if (Integer.parseInt(m) < month) {
            age--;
        }
        return age;
    }

}
