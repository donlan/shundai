package dong.lan.shundai.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 梁桂栋 on 2015/12/7.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "DB_ShunDai.db";
    public static final int VERSION = 1;

    private static final String CREATE_TIME_TABLE = "create table "
            + UserDao.COLUMN_TABLE_NAME + " ("
            + UserDao.COLUMN_OBID + " TEXT PRIMARY KEY , "
            + UserDao.COLUMN_USERNAME + " TEXT, "
            + UserDao.COLUMN_AVATAR + " TEXT, "
            + UserDao.COLUMN_GOAL + " TEXT, "
            + UserDao.COLUMN_TRENDS + " TEXT, "
            + UserDao.COLUMN_AGE + " TEXT, "
            + UserDao.COLUMN_XINGZUO + " TEXT, "
            + UserDao.COLUMN_HONEST + " TEXT, "
            + UserDao.COLUMN_MEILI + " TEXT, "
            + UserDao.COLUMN_NICK + " TEXT, "
            + UserDao.COLUMN_CITY + " TEXT, "
            + UserDao.COLUMN_PHONE + " TEXT, "
            + UserDao.COLUMN_STATUS + " TEXT, "
            + UserDao.COLUMN_SEX + " TEXT, "
            + UserDao.COLUMN_BIRTH + " TEXT, "
            + UserDao.COLUMN_LAT + " TEXT, "
            + UserDao.COLUMN_LNG + " TEXT);";

    private DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    private static DBHelper helper;

    public static DBHelper getInstance(Context context) {
        if (helper == null) {
            helper = new DBHelper(context.getApplicationContext());
        }
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TIME_TABLE);
    }

    public void closeDB() {
        if (helper != null) {
            try {
                SQLiteDatabase database = helper.getWritableDatabase();
                database.close();
            } catch (Exception E) {
                E.printStackTrace();
            }
        }
        helper = null;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
