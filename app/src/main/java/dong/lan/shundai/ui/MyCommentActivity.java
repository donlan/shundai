package dong.lan.shundai.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import dong.lan.shundai.R;
import dong.lan.shundai.adapter.CommentAdapter;
import dong.lan.shundai.bean.Help;
import dong.lan.shundai.bean.User;

/**
 * Created by 桂栋 on 2015/5/12.
 */
public class MyCommentActivity extends ActivityBase implements View.OnClickListener, CommentAdapter.onItemClickListener {

    private User user;
    private RecyclerView recyclerView;
    private CommentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_comment);
        initView();
        init();
    }

    private void init() {
        user = BmobUser.getCurrentUser(this, User.class);
        BmobQuery<Help> query = new BmobQuery<>();
        query.addWhereEqualTo("user", user);
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findObjects(this, new FindListener<Help>() {
            @Override
            public void onSuccess(List<Help> list) {
                if (list.isEmpty()) {
                    ShowToast("你还没有发布过的顺带信息");
                } else {
                    adapter = new CommentAdapter(getBaseContext(), list);
                    recyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener(MyCommentActivity.this);
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }


    private void initView() {
        TextView backUp = (TextView) findViewById(R.id.back_forward);
        TextView title = (TextView) findViewById(R.id.title_bar);
        TextView filter = (TextView) findViewById(R.id.bar_right);
        title.setTextSize(18);
        title.setText("我的评论");
        backUp.setOnClickListener(this);
        filter.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.myCommentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.back_forward:
                finish();
                break;
            case R.id.bar_right:
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onItemClick(Help help, int pos) {
        startAnimActivity(new Intent(MyCommentActivity.this, CommentHandleActivity.class).putExtra("HELP", help));
    }
}


