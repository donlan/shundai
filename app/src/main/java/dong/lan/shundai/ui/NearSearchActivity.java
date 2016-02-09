package dong.lan.shundai.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import dong.lan.shundai.BuildConfig;
import dong.lan.shundai.R;
import dong.lan.shundai.bean.User;

/**
 * Created by 梁桂栋 on 2016/1/1.
 */
public class NearSearchActivity extends AppCompatActivity {

    RadioButton male;
    RadioButton female;
    RadioButton all;
    NumberPicker agePicker;
    NumberPicker kiloPicker;
    NumberPicker honestPicker;
    NumberPicker meiLiPicker;
    NumberPicker xingZuoPicker;
    String age[] = new String[5];
    String distance[] = new String[2001];
    String honest[] = new String[21];
    String meiLi[] = new String[21];
    String xingZuo[] = new String[13];
    StringBuilder sb = new StringBuilder(8);
    List<BmobQuery<User>> queries = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_search);
        init();
    }
    public void init()
    {
        age[0] ="不限";
        age[1]="18-22岁";
        age[2]="23-26岁";
        age[3]="27-35岁";
        age[4]="35岁以上";

        xingZuo[0]="不限";
        xingZuo[1]="水瓶座";
        xingZuo[2]="双鱼座";
        xingZuo[3]="白羊座";
        xingZuo[4]="金牛座";
        xingZuo[5]="双子座";
        xingZuo[6]="巨蟹座";
        xingZuo[7]="狮子座";
        xingZuo[8]="处女座";
        xingZuo[9]="天秤座";
        xingZuo[10]="天蝎座";
        xingZuo[11]="射手座";
        xingZuo[12]="摩羯座";

        distance[0]="不限";
        for(int i = 1;i<=2000;i++)
        {
            sb.delete(0,sb.length());
            sb.append(i);
            sb.append(" 公里");
            distance[i] = sb.toString();
        }
        honest[0] ="不限";
        for(int i = 1;i<=20;i++)
        {
            sb.delete(0,sb.length());
            sb.append("大于 ");
            sb.append(i);
            honest[i] = sb.toString();
        }
        meiLi[0] ="不限";
        for(int i = 1;i<=20;i++)
        {
            sb.delete(0,sb.length());
            sb.append("大于 ");
            sb.append(i);
            meiLi[i] = sb.toString();
        }

        male = (RadioButton) findViewById(R.id.near_search_male);
        female = (RadioButton) findViewById(R.id.near_search_female);
        all = (RadioButton) findViewById(R.id.near_search_all);

        agePicker = (NumberPicker) findViewById(R.id.agePiker);
        kiloPicker = (NumberPicker) findViewById(R.id.kiloPicker);
        honestPicker = (NumberPicker) findViewById(R.id.honestPiker);
        meiLiPicker = (NumberPicker) findViewById(R.id.MeiLiPiker);
        xingZuoPicker = (NumberPicker) findViewById(R.id.xingZuoPiker);

        findViewById(R.id.near_search_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.near_search_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done();
            }
        });

        agePicker.setMaxValue(age.length - 1);
        agePicker.setMinValue(0);
        agePicker.setDisplayedValues(age);
        agePicker.setValue(0);

        kiloPicker.setMaxValue(distance.length - 1);
        kiloPicker.setMinValue(0);
        kiloPicker.setDisplayedValues(distance);
        kiloPicker.setValue(0);

        meiLiPicker.setMaxValue(meiLi.length - 1);
        meiLiPicker.setMinValue(0);
        meiLiPicker.setDisplayedValues(meiLi);
        meiLiPicker.setValue(5);

        honestPicker.setMaxValue(honest.length - 1);
        honestPicker.setMinValue(0);
        honestPicker.setDisplayedValues(honest);
        honestPicker.setValue(5);

        xingZuoPicker.setMaxValue(xingZuo.length - 1);
        xingZuoPicker.setMinValue(0);
        xingZuoPicker.setDisplayedValues(xingZuo);
        xingZuoPicker.setValue(0);
    }

    private void done() {
        if(listener!=null) {
            if(!all.isChecked())
            {
                BmobQuery<User> query  = new BmobQuery<>();
                query.addWhereEqualTo("sex",female.isChecked());
                queries.add(query);
            }

            int a = agePicker.getValue();
            if (a != 0) {
                switch (a) {
                    case 1:
                        BmobQuery<User> query2 = new BmobQuery<>();
                        BmobQuery<User> query3 = new BmobQuery<>();
                        query2.addWhereLessThanOrEqualTo("age", "22");
                        query3.addWhereGreaterThanOrEqualTo("age", "18");
                        queries.add(query2);
                        queries.add(query3);
                        break;
                    case 2:
                        BmobQuery<User> query21 = new BmobQuery<>();
                        BmobQuery<User> query31 = new BmobQuery<>();
                        query21.addWhereLessThanOrEqualTo("age", "26");
                        query31.addWhereGreaterThanOrEqualTo("age", "23");
                        queries.add(query21);
                        queries.add(query31);
                        break;
                    case 3:
                        BmobQuery<User> query22 = new BmobQuery<>();
                        BmobQuery<User> query32 = new BmobQuery<>();
                        query22.addWhereLessThanOrEqualTo("age", "35");
                        query32.addWhereGreaterThanOrEqualTo("age", "27");
                        queries.add(query22);
                        queries.add(query32);
                        break;
                    case 4:
                        BmobQuery<User> query = new BmobQuery<>();
                        query.addWhereGreaterThan("age", "35");
                        queries.add(query);
                        break;
                }
            }
            if(honestPicker.getValue()!=0) {
                BmobQuery<User> query12 = new BmobQuery<>();
                query12.addWhereGreaterThan("v_honest", String.valueOf(honestPicker.getValue()));
                queries.add(query12);
            }
            if(meiLiPicker.getValue()!=0) {
                BmobQuery<User> query11 = new BmobQuery<>();
                query11.addWhereGreaterThan("v_meili", String.valueOf(meiLiPicker.getValue()));
                queries.add(query11);
            }
            if(xingZuoPicker.getValue()!=0)
            {
                BmobQuery<User> query = new BmobQuery<>();
                query.addWhereEqualTo("Constllation",xingZuo[xingZuoPicker.getValue()]);
                queries.add(query);
            }
            listener.onSearch(queries,kiloPicker.getValue());
            finish();
        }else
        {
            if (BuildConfig.DEBUG) Log.d("NearSearchActivity", "未注册筛选监听");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        sb.setLength(0);
    }

    public interface onSearchDoneListener
    {
        void onSearch(List<BmobQuery<User>> queries,int dis);
    }

    static onSearchDoneListener listener;

    public static void setOnSearchListener(onSearchDoneListener onSearchDoneListener)
    {
        listener = onSearchDoneListener;
    }
}
