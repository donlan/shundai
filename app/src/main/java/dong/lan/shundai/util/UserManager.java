package dong.lan.shundai.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import dong.lan.shundai.bean.SUser;
import dong.lan.shundai.bean.User;

/**
 * Created by 梁桂栋 on 2016/1/2.
 */
public class UserManager {
    static SUser sUser;
    static User user;

    public static User getUser()
    {
        return user;
    }
    public static void setUser(User u)
    {
        user = u;
    }
    public static SUser getsUser() {
        return sUser;
    }

    public static void setsUser(SUser sUser) {
        UserManager.sUser = sUser;
    }

    public static void updateUserInfo(final Context context,final User u)
    {
        if(u==null)
            return;
            new AsyncTask<String, String, String>() {
                @Override
                protected String doInBackground(String... params) {
                    BmobQuery<SUser> query = new BmobQuery<>();
                    query.addWhereEqualTo("user",user);
                    query.findObjects(context, new FindListener<SUser>() {
                        @Override
                        public void onSuccess(List<SUser> list) {
                            if(!list.isEmpty()) {
                                sUser = list.get(0);
                                u.setMeili(sUser.getMeiLi());
                                u.setHonest(sUser.getHonest());
                                u.update(context);
                                user = u;
                            }
                        }

                        @Override
                        public void onError(int i, String s) {
                            Log.d("UserManager", "更新用户信息失败");
                        }
                    });
                    return null;
                }
            }.execute("");
    }

    public static void updateOtherSuser(final Context context, final User user,final String m,final String h)
    {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                BmobQuery<SUser> query = new BmobQuery<>();
                query.addWhereEqualTo("user",user);
                query.findObjects(context, new FindListener<SUser>() {
                    @Override
                    public void onSuccess(List<SUser> list) {
                        if(list.isEmpty()) {
                            Toast.makeText(context, "无法更新"+user.getUsername()+"的信息", Toast.LENGTH_SHORT).show();
                        }else
                        {

                            sUser = list.get(0);
                            sUser.setMeiLi(m);
                            sUser.setHonest(h);
                            sUser.update(context, new UpdateListener() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    Toast.makeText(context, "无法更新"+user.getUsername()+"的信息", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(context, "更新用户信息失败："+s, Toast.LENGTH_SHORT).show();
                    }
                });
                return null;
            }
        }.execute("");
    }



}
