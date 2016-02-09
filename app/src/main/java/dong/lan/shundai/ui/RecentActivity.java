package dong.lan.shundai.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.db.BmobDB;
import dong.lan.shundai.R;
import dong.lan.shundai.adapter.MessageRecentAdapter;
import dong.lan.shundai.view.ClearEditText;
import dong.lan.shundai.view.dialog.DialogTips;

/**
 * Created by 桂栋 on 2015/5/11.
 */
public class RecentActivity extends ActivityBase implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    ClearEditText mClearEditText;

    ListView listview;

    MessageRecentAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_recent);
        initView();
    }


    private void initView(){
        findViewById(R.id.back_forward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView)(findViewById(R.id.title_bar))).setText("会话");
        findViewById(R.id.bar_right).setVisibility(View.INVISIBLE);
        listview = (ListView)findViewById(R.id.list);
        listview.setOnItemClickListener(this);
        listview.setOnItemLongClickListener(this);
        adapter = new MessageRecentAdapter(this, R.layout.item_conversation, BmobDB.create(this).queryRecents());
        listview.setAdapter(adapter);

        mClearEditText = (ClearEditText)findViewById(R.id.et_msg_search);
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    /** 删除会话
     */
    private void deleteRecent(BmobRecent recent){
        adapter.remove(recent);
        BmobDB.create(this).deleteRecent(recent.getTargetid());
        BmobDB.create(this).deleteMessages(recent.getTargetid());
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position,
                                   long arg3) {
        BmobRecent recent = adapter.getItem(position);
        showDeleteDialog(recent);
        return true;
    }

    public void showDeleteDialog(final BmobRecent recent) {
        DialogTips dialog = new DialogTips(this,recent.getUserName(),"删除会话", "确定",true,true);
        // 设置成功事件
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int userId) {
                deleteRecent(recent);
            }
        });
        // 显示确认对话框
        dialog.show();
        dialog = null;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        // TODO Auto-generated method stub
        BmobRecent recent = adapter.getItem(position);
        //重置未读消息
        BmobDB.create(this).resetUnread(recent.getTargetid());
        //组装聊天对象
        BmobChatUser user = new BmobChatUser();
        user.setAvatar(recent.getAvatar());
        user.setNick(recent.getNick());
        user.setUsername(recent.getUserName());
        user.setObjectId(recent.getTargetid());
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("user", user);
        startAnimActivity(intent);
    }


    public void refresh(){
        try {
            this.runOnUiThread(new Runnable() {
                public void run() {
                    adapter = new MessageRecentAdapter(getBaseContext(), R.layout.item_conversation, BmobDB.create(getBaseContext()).queryRecents());
                    listview.setAdapter(adapter);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
//        if(!hidden){
            refresh();
//        }
    }
}
