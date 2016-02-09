package dong.lan.shundai.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.UpdateListener;
import dong.lan.shundai.MyApplication;
import dong.lan.shundai.R;
import dong.lan.shundai.adapter.BlackListAdapter;
import dong.lan.shundai.bean.User;
import dong.lan.shundai.util.CollectionUtils;
import dong.lan.shundai.view.HeaderLayout;
import dong.lan.shundai.view.dialog.DialogTips;

/**
 * 黑名单列表
 */
public class BlackListActivity extends ActivityBase implements OnItemClickListener {

    ListView listview;
    BlackListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist);
        initView();
    }

    private void initView() {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        initTopBarForLeft("黑名单");
        adapter = new BlackListAdapter(this, BmobDB.create(this).getBlackList());
        listview = (ListView) findViewById(R.id.list_blacklist);
        listview.setOnItemClickListener(this);
        listview.setAdapter(adapter);
    }

    /**
     * 显示移除黑名单对话框
     */
    public void showRemoveBlackDialog(final int position, final BmobChatUser user) {
        DialogTips dialog = new DialogTips(this, "移出黑名单",
                "你确定将" + user.getUsername() + "移出黑名单吗?", "确定", true, true);
        // 设置成功事件
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int userId) {
                adapter.remove(position);
                User user1 = BmobUser.getCurrentUser(getBaseContext(), User.class);
                BmobRelation relation = new BmobRelation();
                relation.add(user);
                user1.setContacts(relation);
                user1.update(getBaseContext());
                BmobRelation relation1 = new BmobRelation();
                relation1.remove(user);
                user1.setBlacklist(relation1);
                user1.update(getBaseContext(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        ShowToast("移出黑名单成功");
                        //重新设置下内存中保存的好友列表
                        MyApplication.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(getApplicationContext()).getContactList()));
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        ShowToast("移出黑名单失败:" + s);
                    }
                });
            }
        });
        // 显示确认对话框
        dialog.show();
        dialog = null;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        BmobChatUser invite = (BmobChatUser) adapter.getItem(arg2);
        showRemoveBlackDialog(arg2, invite);
    }


}
