package dong.lan.shundai.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import cn.bmob.im.bean.BmobMsg;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import dong.lan.shundai.R;
import dong.lan.shundai.adapter.UserCommentAdapter;
import dong.lan.shundai.bean.Comment;
import dong.lan.shundai.bean.Help;
import dong.lan.shundai.bean.User;
import dong.lan.shundai.util.MyConstant;
import dong.lan.shundai.util.UserManager;

/**
 * Created by 梁桂栋 on 2015/11/28.
 */
public class CommentHandleActivity extends ActivityBase implements UserCommentAdapter.onItemReplyListener {
    private RecyclerView recyclerView;
    private UserCommentAdapter adapter;
    private Help help;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_comment);
        help = (Help) getIntent().getSerializableExtra("HELP");
        if(help==null)
            finish();
        ((TextView)findViewById(R.id.title_bar)).setText("评论");
        recyclerView = (RecyclerView) findViewById(R.id.handleCommentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        findViewById(R.id.back_forward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initData();

    }

    private void initData() {
        BmobQuery<Comment> query = new BmobQuery<>();
        query.addWhereEqualTo("help", new BmobPointer(help));
        query.include("user");
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findObjects(this, new FindListener<Comment>() {
            @Override
            public void onSuccess(List<Comment> list) {
                if (list.isEmpty()) {
                    ShowToast("没有用户评论");
                } else {
                    adapter = new UserCommentAdapter(CommentHandleActivity.this, list);
                    adapter.setOnItemClickListener(CommentHandleActivity.this);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    @Override
    public void onItemClick(Comment comment,String replay, int pos,boolean isHelp,float score) {
        StringBuilder s = new StringBuilder();
        s.append("●[");
        s.append(help.getTag().substring(0,2));
        s.append("]");
        s.append(help.getInfo());
        s.append("\n●[评论回复] ");
        s.append(replay);
        BmobMsg bmobMsg = BmobMsg.createTextSendMsg(this, comment.getUser().getObjectId(), s.toString());
        bmobMsg.setExtra("Bmob");
        manager.sendTextMessage(comment.getUser(), bmobMsg);
        User user = comment.getUser();
        String h = String.valueOf(((3 - score) * 0.02 + Double.parseDouble(user.getHonest())));
        String m = String.valueOf(((3 - score) * 0.03 + Double.parseDouble(user.getMeili())));
        UserManager.updateOtherSuser(CommentHandleActivity.this,user,m,h);

        manager.sendTagMessage(user.getObjectId(), MyConstant.UPDATE_USER_INFO, new PushListener() {
            @Override
            public void onSuccess() {
                Log.d("CommentHandleActivity", "推送用户信息更新成功");
            }

            @Override
            public void onFailure(int i, String s) {
                Log.d("CommentHandleActivity", "推送用户信息更新失败");
            }
        });
    }
}
