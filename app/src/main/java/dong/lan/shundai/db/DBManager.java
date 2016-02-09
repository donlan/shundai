package dong.lan.shundai.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobGeoPoint;
import dong.lan.shundai.bean.User;

/**
 * Created by 梁桂栋 on 2015/12/8.
 */
public class DBManager {

    private static DBManager manager = new DBManager();
    private static DBHelper helper;

    public void init(Context context) {
        helper = DBHelper.getInstance(context);
    }

    public static synchronized DBManager getManager() {
        return manager;
    }

    /*

    添加一个todo事件
     */
    synchronized public void addAUser(User user) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(UserDao.COLUMN_OBID, user.getObjectId());
            values.put(UserDao.COLUMN_USERNAME, user.getUsername());
            values.put(UserDao.COLUMN_AVATAR, user.getAvatar());
            values.put(UserDao.COLUMN_TRENDS, user.getTrends());
            values.put(UserDao.COLUMN_STATUS, user.getState());
            values.put(UserDao.COLUMN_AGE, user.getAge());
            values.put(UserDao.COLUMN_XINGZUO, user.getConstllation());
            values.put(UserDao.COLUMN_HONEST, user.getHonest());
            values.put(UserDao.COLUMN_MEILI,user.getMeili());
            values.put(UserDao.COLUMN_GOAL,user.getGoal());
            if (user.getLocation() != null) {
                values.put(UserDao.COLUMN_LNG, user.getLocation().getLongitude() + "");
                values.put(UserDao.COLUMN_LAT, user.getLocation().getLatitude() + "");
            }
            values.put(UserDao.COLUMN_SEX, user.getSex() ? "1" : "0");
            values.put(UserDao.COLUMN_PHONE, user.getMobilePhoneNumber());
            values.put(UserDao.COLUMN_CITY, user.getCity());
            values.put(UserDao.COLUMN_BIRTH, user.getBirth());
            values.put(UserDao.COLUMN_STATUS, user.getState());
            db.replace(UserDao.COLUMN_TABLE_NAME, null, values);
        }
    }

    synchronized public void addUsers(List<User> users)
    {
        for (User user:users)
        {
            addAUser(user);
        }
    }

    /*

    获取数据库的所有todo事件
     */
    synchronized public List<User> getAllContacts() {

        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + UserDao.COLUMN_TABLE_NAME
                    + " order by " + UserDao.COLUMN_USERNAME + " asc", null);
            List<User> users = new ArrayList<>();
            int index[] = new int[16];
            index[0] = cursor.getColumnIndex(UserDao.COLUMN_OBID);
            index[1] = cursor.getColumnIndex(UserDao.COLUMN_USERNAME);
            index[2] = cursor.getColumnIndex(UserDao.COLUMN_AVATAR);
            index[3] = cursor.getColumnIndex(UserDao.COLUMN_TRENDS);
            index[4] = cursor.getColumnIndex(UserDao.COLUMN_BIRTH);
            index[5] = cursor.getColumnIndex(UserDao.COLUMN_LAT);
            index[6] = cursor.getColumnIndex(UserDao.COLUMN_LNG);
            index[7] = cursor.getColumnIndex(UserDao.COLUMN_AGE);
            index[8] = cursor.getColumnIndex(UserDao.COLUMN_CITY);
            index[9] = cursor.getColumnIndex(UserDao.COLUMN_XINGZUO);
            index[10] = cursor.getColumnIndex(UserDao.COLUMN_MEILI);
            index[11] = cursor.getColumnIndex(UserDao.COLUMN_HONEST);
            index[12] = cursor.getColumnIndex(UserDao.COLUMN_PHONE);
            index[13] = cursor.getColumnIndex(UserDao.COLUMN_GOAL);
            index[14] = cursor.getColumnIndex(UserDao.COLUMN_SEX);
            index[15] = cursor.getColumnIndex(UserDao.COLUMN_STATUS);
            if (cursor.moveToFirst()) {
                do {
                    User user = new User();
                    user.setObjectId(cursor.getString(index[0]));
                    user.setUsername(cursor.getString(index[1]));
                    user.setAvatar(cursor.getString(index[2]));
                    user.setTrends(cursor.getString(index[3]));
                    user.setBirth(cursor.getString(index[4]));
                    String lat = cursor.getString(index[5]);
                    if (lat != null && !lat.equals(""))
                        user.setLocation(new BmobGeoPoint(Double.valueOf(cursor.getString(index[6]))
                                , Double.valueOf(lat)));
                    user.setAge(cursor.getString(index[7]));
                    user.setCity(cursor.getString(index[8]));
                    user.setConstllation(cursor.getString(index[9]));
                    user.setMeili(cursor.getString(index[10]));
                    user.setHonest(cursor.getString(index[11]));
                    user.setMobilePhoneNumber(cursor.getString(index[12]));
                    user.setGoal(cursor.getString(index[13]));
                    user.setSex(cursor.getString(index[14]).equals("1"));
                    user.setState(cursor.getString(index[15]));
                    users.add(user);
                } while (cursor.moveToNext());
            }
            cursor.close();
            return users;
        }
        return null;
    }


    synchronized public void updateUser(ContentValues values, String id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen())
            db.update(UserDao.COLUMN_TABLE_NAME, values, UserDao.COLUMN_OBID + " =? ", new String[]{id});
    }


    synchronized public void updateUser(User user) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(UserDao.COLUMN_OBID, user.getObjectId());
            values.put(UserDao.COLUMN_USERNAME, user.getUsername());
            values.put(UserDao.COLUMN_AVATAR, user.getAvatar());
            values.put(UserDao.COLUMN_TRENDS, user.getTrends());
            values.put(UserDao.COLUMN_STATUS, user.getState());
            values.put(UserDao.COLUMN_AGE, user.getAge());
            values.put(UserDao.COLUMN_XINGZUO, user.getConstllation());
            values.put(UserDao.COLUMN_HONEST, user.getHonest());
            if (user.getLocation() != null) {
                values.put(UserDao.COLUMN_LNG, user.getLocation().getLongitude() + "");
                values.put(UserDao.COLUMN_LAT, user.getLocation().getLatitude() + "");
            }
            values.put(UserDao.COLUMN_SEX, user.getSex() ? "1" : "0");
            values.put(UserDao.COLUMN_PHONE, user.getMobilePhoneNumber());
            values.put(UserDao.COLUMN_CITY, user.getCity());
            values.put(UserDao.COLUMN_BIRTH, user.getBirth());
            values.put(UserDao.COLUMN_STATUS, user.getState());
            db.update(UserDao.COLUMN_TABLE_NAME, values, UserDao.COLUMN_OBID + " =? ", new String[]{user.getObjectId()});
        }
    }

    synchronized public void deteteAll(String name) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen())
            db.delete(UserDao.COLUMN_TABLE_NAME, UserDao.COLUMN_USERNAME + " =? ", new String[]{name});
    }

    /*
    删除一个todo事件
     */
    synchronized public void deleteUser(String id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen())
            db.delete(UserDao.COLUMN_TABLE_NAME, UserDao.COLUMN_OBID + " =? ", new String[]{id});
    }

    synchronized public void deleteUserByName(String name) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen())
            db.delete(UserDao.COLUMN_TABLE_NAME, UserDao.COLUMN_USERNAME + " =? ", new String[]{name});
    }

}
