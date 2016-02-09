package dong.lan.shundai.ui;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.push.FeedbackPush;
import com.umeng.message.PushAgent;

import java.util.ArrayList;

import dong.lan.shundai.MyPushIntentService;
import dong.lan.shundai.R;
import dong.lan.shundai.adapter.HelpFragmentAdapter;
import dong.lan.shundai.bean.Help;
import dong.lan.shundai.ui.fragment.HelpFragment;
import dong.lan.shundai.util.MyConstant;
import dong.lan.shundai.util.SP;

public class HelpActivity extends ActivityBase implements  OnClickListener {

    private static String passCity = "";

    public Context mContext;
    private TextView right;
    private String from = "";
    private boolean me = false;
    private Button tab[] = new Button[3];
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    private ViewPager pager;
    public HelpActivity() {
        mContext = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        passCity = getIntent().getStringExtra("City");
        from = getIntent().getStringExtra("from");
        me = getIntent().getBooleanExtra("me",false);
        setContentView(R.layout.activity_help);
        init();
        setUpUmengFeedback();
    }

    FeedbackAgent fb;

    private void setUpUmengFeedback() {
        fb = new FeedbackAgent(this);
        fb.sync();
        fb.openAudioFeedback();
        fb.openFeedbackPush();
        PushAgent.getInstance(this).enable();
        fb.setWelcomeInfo("感谢您使用顺带，欢迎您反馈使用产品的感受和建议。");
        FeedbackPush.getInstance(this).init(true);
        PushAgent.getInstance(this).setPushIntentServiceClass(MyPushIntentService.class);
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean result = fb.updateUserInfo();
            }
        }).start();
    }

    private void init() {

        TextView back = (TextView) findViewById(R.id.back_forward);
        TextView tittle = (TextView) findViewById(R.id.title_bar);
        right = (TextView) findViewById(R.id.bar_right);
        back.setOnClickListener(this);
        right.setOnClickListener(this);
        tab[1]= (Button) findViewById(R.id.help_goods);
        tab[2] = (Button) findViewById(R.id.help_other);
        tab[0] = (Button) findViewById(R.id.help_person);
        pager = (ViewPager) findViewById(R.id.helpViewPager);
        tab[0].setOnClickListener(this);
        tab[1].setOnClickListener(this);
        tab[2].setOnClickListener(this);
        String tag = from.substring(0, 2);
        if(tag.equals("求带"))
        {
            fragments.add(new HelpFragment().init(MyConstant.S_R,passCity,me));
            fragments.add(new HelpFragment().init(MyConstant.S_W, passCity,me));
            fragments.add(new HelpFragment().init(MyConstant.S_Q,passCity,me));
        }else
        {
            fragments.add(new HelpFragment().init(MyConstant.H_R,passCity,me));
            fragments.add(new HelpFragment().init(MyConstant.H_W,passCity,me));
            fragments.add(new HelpFragment().init(MyConstant.H_Q,passCity,me));
        }
        pager.setAdapter(new HelpFragmentAdapter(getSupportFragmentManager(), fragments));
        pager.setCurrentItem(0);
        tab[0].setSelected(true);
        pager.setOnPageChangeListener(new MyPagerChangeListener());
        if(passCity!=null && !passCity.equals(""))
            tag =passCity + tag;
            tittle.setText(tag);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void dismiss() {
        if (popup != null && popup.isShowing())
            popup.dismiss();
    }

    PopupWindow popup;

    private void tittlePop() {
        View root = LayoutInflater.from(this).inflate(R.layout.tittle_pop, null);
        popup = new PopupWindow(root, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        popup.setOutsideTouchable(true);
        popup.setSplitTouchEnabled(true);
        TextView publish = (TextView) root.findViewById(R.id.tittle_publish);
        TextView search = (TextView) root.findViewById(R.id.tittle_search);
        TextView add = (TextView) root.findViewById(R.id.tittle_add_friend);
       // TextView scan = (TextView) root.findViewById(R.id.title_scan);
        TextView feek = (TextView) root.findViewById(R.id.tittle_feekback);
        publish.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if( SP.isFirstPublish())
                {
                    new AlertDialog.Builder(HelpActivity.this)
                            .setMessage("从此页面跳转发布帮带信息的主类型以当前标题一致")
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent =new Intent(HelpActivity.this, Activity_Publish.class);
                                    intent.putExtra("TAG",from);
                                    startAnimActivity(intent);
                                    dismiss();
                                    SP.setFirstPublish(false);
                                }
                            }).show();
                }else {
                    Intent intent = new Intent(HelpActivity.this, Activity_Publish.class);
                    intent.putExtra("TAG", from);
                    startAnimActivity(intent);
                    dismiss();
                }
            }
        });
        search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimActivity(new Intent(HelpActivity.this, AddFriendActivity.class));
                dismiss();
            }
        });
        add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimActivity(new Intent(HelpActivity.this, AddFriendActivity.class));
                dismiss();
            }
        });
//        scan.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });
        feek.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                fb.startFeedbackActivity();
            }
        });
        popup.setTouchable(true);
        popup.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        popup.setBackgroundDrawable(getResources().getDrawable(R.color.com_bg));
        popup.showAsDropDown(right, 0, 0);

    }


    public void onItem(AdapterView<?> adapterView, View view, int i, long l) {
        Help help = (Help) adapterView.getAdapter().getItem(i);
        Intent intent = new Intent(HelpActivity.this, HelpInfoActivity.class);
        intent.putExtra("help", help);
        if (me)
            intent.putExtra("tag", "me");
        else
            intent.putExtra("tag", "help");
        startAnimActivity(intent);
    }


    @Override
    protected void onResume() {
        MobclickAgent.onResume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        MobclickAgent.onPause(this);
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.help_goods:
                tab[0].setSelected(false);
                tab[1].setSelected(true);
                tab[2].setSelected(false);
                pager.setCurrentItem(1);
                break;
            case R.id.help_person:
                tab[0].setSelected(true);
                tab[1].setSelected(false);
                tab[2].setSelected(false);
                pager.setCurrentItem(0);
                break;
            case R.id.help_other:
                tab[0].setSelected(false);
                tab[1].setSelected(false);
                tab[2].setSelected(true);
                pager.setCurrentItem(2);
                break;

            case R.id.back_forward:
                finish();
                break;
            case R.id.bar_right:
                if (popup != null && popup.isShowing()) {
                    popup.dismiss();
                    return;
                }
                tittlePop();
                break;
        }
    }




    class MyPagerChangeListener implements ViewPager.OnPageChangeListener
    {


        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            for (int i = 0;i<3;i++)
            {
                if(i==position)
                {
                    tab[i].setSelected(true);
                }else
                {
                    tab[i].setSelected(false);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}