package dong.lan.shundai.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import dong.lan.shundai.MyApplication;
import dong.lan.shundai.R;
import dong.lan.shundai.adapter.UserFriendAdapter;
import dong.lan.shundai.bean.User;
import dong.lan.shundai.util.CharacterParser;
import dong.lan.shundai.util.CollectionUtils;
import dong.lan.shundai.util.PinyinComparator;
import dong.lan.shundai.view.MyLetterView;
import dong.lan.shundai.view.dialog.DialogTips;

/**
 * Created by 桂栋 on 2015/5/10.
 */
public class FriendsActivity extends ActivityBase {

    TextView dialog;
    ListView list_friends;
    MyLetterView right_letter;
    private UserFriendAdapter userAdapter;// 好友
    List<User> friends = new ArrayList<User>();
    private InputMethodManager inputMethodManager;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    public Context mContext;

    public FriendsActivity() {
        mContext = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_contacts);
        inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        init();
    }

    private void init() {
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        initTopBarForLeft("我的好友");
        initListView();
        initRightLetterView();
    }


    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     */
    private void filterData(String filterStr) {
        List<User> filterDateList = new ArrayList<User>();
        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = friends;
        } else {
            filterDateList.clear();
            for (User sortModel : friends) {
                String name = sortModel.getUsername();
                if (name != null) {
                    if (name.indexOf(filterStr.toString()) != -1
                            || characterParser.getSelling(name).startsWith(
                            filterStr.toString())) {
                        filterDateList.add(sortModel);
                    }
                }
            }
        }
        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        userAdapter.updateListView(filterDateList);
    }

    /**
     * 为ListView填充数据
     */
    /**
     * 为ListView填充数据
     */
    private void filledData(List<User> datas) {
        friends.clear();
        int total = datas.size();

        for (int i = 0; i < total; i++) {
            final User user = datas.get(i);
            String username = user.getUsername();
            if (username != null) {
                String pinyin = characterParser.getSelling(user.getUsername());
                String sortString = pinyin.substring(0, 1).toUpperCase();
                // 正则表达式，判断首字母是否是英文字母
                if (sortString.matches("[A-Z]")) {
                    user.setSortLetters(sortString.toUpperCase());
                } else {
                    user.setSortLetters("#");
                }
            } else {
                user.setSortLetters("#");
            }
            friends.add(user);
        }
        // 根据a-z进行排序
        Collections.sort(friends, pinyinComparator);
    }


    ImageView iv_msg_tips;
    LinearLayout layout_new;//新朋友
    LinearLayout layout_near;//附近的人

    private void initListView() {
        list_friends = (ListView) findViewById(R.id.list_friends);
        LayoutInflater mInflater = LayoutInflater.from(this);
        RelativeLayout headView = (RelativeLayout) mInflater.inflate(R.layout.include_new_friend, null);
        iv_msg_tips = (ImageView) headView.findViewById(R.id.iv_msg_tips);
        layout_new = (LinearLayout) headView.findViewById(R.id.layout_new);
        layout_near = (LinearLayout) headView.findViewById(R.id.layout_near);
        layout_new.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(mContext, NewFriendActivity.class);
                intent.putExtra("from", "contact");
                startAnimActivity(intent);
            }
        });
        layout_near.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        list_friends.addHeaderView(headView);
        userAdapter = new UserFriendAdapter(mContext, friends);
        list_friends.setAdapter(userAdapter);
        list_friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = (User) userAdapter.getItem(i - 1);
                //先进入好友的详细资料页面
                Intent intent = new Intent(mContext, SetMyInfoActivity.class);
                intent.putExtra("from", "other");
                intent.putExtra("username", user.getUsername());
                startAnimActivity(intent);
            }
        });
        list_friends.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                User user = (User) userAdapter.getItem(i - 1);
                showDeleteDialog(user);
                return true;
            }
        });
        list_friends.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 隐藏软键盘
                if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
                    if (getCurrentFocus() != null)
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });
        refresh();
    }


    private void initRightLetterView() {
        right_letter = (MyLetterView) findViewById(R.id.right_letter);
        dialog = (TextView) findViewById(R.id.dialog);
        right_letter.setTextView(dialog);
        right_letter.setOnTouchingLetterChangedListener(new LetterListViewListener());
    }

    private class LetterListViewListener implements
            MyLetterView.OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(String s) {
            // 该字母首次出现的位置
            int position = userAdapter.getPositionForSection(s.charAt(0));
            if (position != -1) {
                list_friends.setSelection(position);
            }
        }
    }

    /**
     * 获取好友列表
     */
    private void queryMyfriends() {
        //是否有新的好友请求
        if (BmobDB.create(this).hasNewInvite()) {
            iv_msg_tips.setVisibility(View.VISIBLE);
        } else {
            iv_msg_tips.setVisibility(View.GONE);
        }
        //在这里再做一次本地的好友数据库的检查，是为了本地好友数据库中已经添加了对方，但是界面却没有显示出来的问题
        // 重新设置下内存中保存的好友列表
        MyApplication.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(this).getContactList()));
        Map<String,BmobChatUser> users = MyApplication.getInstance().getContactList();
        BmobQuery<User> query = new BmobQuery<>();
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.addWhereRelatedTo("contacts", new BmobPointer(userManager.getCurrentUser()));
        query.findObjects(this, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                filledData(list);
                if(userAdapter==null){
                    userAdapter = new UserFriendAdapter(FriendsActivity.this, friends);
                    list_friends.setAdapter(userAdapter);
                }else{
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(int i, String s) {
                ShowToast("获取好友列表失败");
            }
        });
        //filledData(CollectionUtils.map2list(users));

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    public void refresh() {
        try {
            runOnUiThread(new Runnable() {
                public void run() {
                    queryMyfriends();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showDeleteDialog(final User user) {
        DialogTips dialog = new DialogTips(mContext, user.getUsername(), "删除联系人", "确定", true, true);
        // 设置成功事件
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int userId) {
                deleteContact(user);
            }
        });
        // 显示确认对话框
        dialog.show();
    }

    /**
     * 删除联系人
     * deleteContact
     *
     * @return void
     * @throws
     */
    private void deleteContact(final User user) {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("正在删除...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        userManager.deleteContact(user.getObjectId(), new UpdateListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                ShowToast("删除成功");
                //删除内存
                MyApplication.getInstance().getContactList().remove(user.getUsername());
                //更新界面
                runOnUiThread(new Runnable() {
                    public void run() {
                        progress.dismiss();
                        userAdapter.remove(user);
                    }
                });
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                ShowToast("删除失败：" + arg1);
                progress.dismiss();
            }
        });
    }

}
