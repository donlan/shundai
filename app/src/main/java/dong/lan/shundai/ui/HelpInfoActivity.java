package dong.lan.shundai.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import dong.lan.shundai.R;
import dong.lan.shundai.adapter.HelpComAdapter;
import dong.lan.shundai.bean.Comment;
import dong.lan.shundai.bean.Help;
import dong.lan.shundai.bean.User;
import dong.lan.shundai.util.FaceTextUtils;
import dong.lan.shundai.util.ImageLoadOptions;

/**
 * Created by 桂栋 on 2015/5/10.
 */
public class HelpInfoActivity extends ActivityBase implements View.OnClickListener {

    private TextView name, tv_comment_count, tv_visit_count, helpINfo, noTips;
    private ListView comment_list;
    private Help help;
    private String CurrName, HelpInfo;
    List<Comment> comments = new ArrayList<Comment>();
    private User mesUser;
    private ImageView imgAvater;
    private static String tag;
    public Context mContext;
    private int flag = 0;
    private boolean me = false;
    private HelpComAdapter comAdapter;

    public HelpInfoActivity() {
        mContext = this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_info);
        tag = getIntent().getStringExtra("tag");
        help = (Help) getIntent().getSerializableExtra("help");
        me = getIntent().getBooleanExtra("me",false);
        initView();
        findData();
    }

    private void initView() {
        imgAvater = (ImageView) findViewById(R.id.popup_avater);
        name = (TextView) findViewById(R.id.popup_trend);
        tv_comment_count = (TextView) findViewById(R.id.popup_comment_count);
        tv_visit_count = (TextView) findViewById(R.id.popup_visit_count);
        helpINfo = (TextView) findViewById(R.id.info_ofHelp);
        noTips = (TextView) findViewById(R.id.no_tips);
        noTips.setOnClickListener(this);
        findViewById(R.id.popup_comment).setOnClickListener(this);
        comment_list = (ListView) findViewById(R.id.popup_comment_list);
        ((TextView) findViewById(R.id.title_bar)).setText("帮忙详情");
        TextView right = (TextView) findViewById(R.id.bar_right);
        if(me) {
            right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(HelpInfoActivity.this)
                            .setMessage("你确定要删除此条顺带信息吗？")
                            .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    help.delete(HelpInfoActivity.this, new DeleteListener() {
                                        @Override
                                        public void onSuccess() {
                                            Toast.makeText(HelpInfoActivity.this,"删除顺带信息成功",Toast.LENGTH_SHORT).show();
                                            finish();
                                        }

                                        @Override
                                        public void onFailure(int i, String s) {
                                            Toast.makeText(HelpInfoActivity.this,"删除顺带信息失败",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("否",null)
                            .show();
                }
            });
        }else
            right.setVisibility(View.GONE);
        findViewById(R.id.back_forward).setOnClickListener(this);
    }

    private void findData() {
        CurrName = help.getUser().getUsername();
        HelpInfo = help.getInfo();
        BmobQuery<Comment> commentBmobQuery = new BmobQuery<Comment>();
        commentBmobQuery.addWhereEqualTo("help", new BmobPointer(help));
        commentBmobQuery.include("user");
        commentBmobQuery.order("-createAt");
        commentBmobQuery.findObjects(mContext, new FindListener<Comment>() {
            @Override
            public void onSuccess(List<Comment> list) {
                if (list.isEmpty()) {
                    noTips.setVisibility(View.VISIBLE);
                } else {
                    comments = list;
                    comAdapter = new HelpComAdapter(mContext, comments);
                    comment_list.setAdapter(comAdapter);
                    comment_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Comment seek = (Comment) adapterView.getItemAtPosition(i);
                            Intent in = new Intent(HelpInfoActivity.this, SetMyInfoActivity.class);
                            in.putExtra("from", "other");
                            in.putExtra("username", seek.getUser().getUsername());
                            startAnimActivity(in);
                        }
                    });
                }
            }

            @Override
            public void onError(int i, String s) {
                ShowToast("获取评论列表失败");
            }

        });


        String avater = help.getUser().getAvatar();
        if (avater == null || avater.equals("")) {
            imgAvater.setImageDrawable(getResources().getDrawable(R.drawable.default_head));
        } else {
            ImageLoader.getInstance().displayImage(avater, imgAvater, ImageLoadOptions.getOptions(1));
        }
        name.setText(CurrName);
        tv_comment_count.setText(help.getComm() + "评论");
        tv_visit_count.setText(help.getSee() + "看过");
        SpannableString spannableString = FaceTextUtils
                .toSpannableString(mContext, help.getInfo());
        helpINfo.setText(spannableString);

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (help != null) {
                help.setSee(help.getSee() + 1);
                help.update(mContext, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailure(int i, String s) {
                    }
                });
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    AlertDialog dialog;

    private void showDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.hele_info_dialog, null);
        final EditText editText = (EditText) view.findViewById(R.id.pop_writeCom);
        final TextView helpTa = (TextView) view.findViewById(R.id.helpTa);
        TextView send = (TextView) view.findViewById(R.id.send);
        helpTa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (flag) {
                    case 0:
                        helpTa.setText("√");
                        flag = 1;
                        break;
                    case 1:
                        helpTa.setText("帮Ta");
                        flag = 0;
                        break;
                }
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().equals("")) {
                    ShowToast("没有写入内容");
                    return;
                }
                if (!CurrName.equals(BmobUser.getCurrentUser(HelpInfoActivity.this).getUsername())) {
                    BmobQuery<User> userBmobQuery = new BmobQuery<User>();
                    userBmobQuery.addWhereEqualTo("username", CurrName);
                    userBmobQuery.findObjects(mContext, new FindListener<User>() {
                        @Override
                        public void onSuccess(List<User> list) {
                            mesUser = list.get(0);
                            String string = help.getTag().substring(0,2);
                            StringBuilder s = new StringBuilder();
                            s.append("●[");
                            s.append(string);
                            s.append("]");
                            s.append(HelpInfo);
                            s.append("\n●[评论]：\n");
                            s.append(editText.getText().toString());
                            BmobMsg bmobMsg = BmobMsg.createTextSendMsg(mContext, mesUser.getObjectId(), s.toString());
                            bmobMsg.setExtra("Bmob");
                            manager.sendTextMessage(mesUser, bmobMsg);

                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });

                    help.setComm(help.getComm() + 1);
                    help.setSee(help.getSee() + 1);
                    help.update(mContext, new UpdateListener() {
                        @Override
                        public void onSuccess() {

                            Comment comment = new Comment();
                            if (helpTa.getText().equals("√")) {
                                comment.setIsHelped(true);
                            } else {
                                comment.setIsHelped(false);
                            }
                            comment.setUser(BmobUser.getCurrentUser(HelpInfoActivity.this, User.class));
                            comment.setContent(editText.getText().toString());
                            comment.setIsHelped(flag == 1);
                            comment.setSrore(0);
                            comment.setHelp(help);
                            comment.save(mContext, new SaveListener() {
                                @Override
                                public void onSuccess() {
                                    ShowToast("评论保存成功");
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    ShowToast("评论保存失败");
                                }
                            });

                            if(comAdapter==null)
                            {
                                comments.add(comment);
                                comAdapter = new HelpComAdapter(mContext, comments);
                                comment_list.setAdapter(comAdapter);
                                comment_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        Comment seek = (Comment) adapterView.getItemAtPosition(i);
                                        Intent in = new Intent(HelpInfoActivity.this, SetMyInfoActivity.class);
                                        in.putExtra("from", "other");
                                        in.putExtra("username", seek.getUser().getUsername());
                                        startAnimActivity(in);
                                    }
                                });
                            }else
                                comAdapter.add(comment);
                            noTips.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            ShowToast("更新失败");
                        }
                    });

                }
                else
                {
                    ShowToast("不能自己评论自己哟~");
                }
                dialog.dismiss();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.alert);
        builder.setView(view);
        dialog = builder.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.no_tips:
                if (CurrName != null) {
                    Intent intent = new Intent(HelpInfoActivity.this, SetMyInfoActivity.class);
                    if (CurrName.equals(BmobChatUser.getCurrentUser(getBaseContext()).getUsername())) {
                        ShowToast("为你跳转到个人中心");
                        intent.putExtra("from", "me");
                    } else {
                        intent.putExtra("from", "add");
                    }
                    intent.putExtra("username", CurrName);
                    startAnimActivity(intent);
                }
                break;
            case R.id.popup_comment:
                if (!me)
                    showDialog();
                break;

            case R.id.back_forward:
                finish();
                break;

            case R.id.bar_right:
                if (tag != null && me) {
                    deleteHelp();
                }
                break;
        }

    }

    private void deleteHelp() {
        new AlertDialog.Builder(this).setMessage("你确定要删除此条顺带信息吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        help.delete(getBaseContext(), new DeleteListener() {
                            @Override
                            public void onSuccess() {
                                ShowToast("删除成功");
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                ShowToast("删除失败");
                            }
                        });
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    protected void onPause() {
        MobclickAgent.onPause(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        MobclickAgent.onResume(this);
        List<SHARE_MEDIA> platforms = new ArrayList<SHARE_MEDIA>();
        platforms.add(SHARE_MEDIA.QZONE);
        platforms.add(SHARE_MEDIA.SINA);
        platforms.add(SHARE_MEDIA.QQ);
        platforms.add(SHARE_MEDIA.TENCENT);
        super.onResume();
    }

}
