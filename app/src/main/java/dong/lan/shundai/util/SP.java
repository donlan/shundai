package dong.lan.shundai.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 梁桂栋 on 2015/12/25.
 */
public class SP {
    private static SharedPreferences sp;
    public static void init(Context context)
    {
        sp =context.getSharedPreferences("Setting", Context.MODE_PRIVATE);
    }
    public static void init(SharedPreferences s)
    {
       sp= s;
    }
    public static boolean isLoad()
    {
        return sp.getBoolean("LOAD",false);
    }

    public static void setLoad(boolean load)
    {
        sp.edit().remove("LOAD").apply();
        sp.edit().putBoolean("LOAD",load).apply();
    }

    public static boolean isFirstPublish()
    {
        return sp.getBoolean("First_Publish",true);
    }

    public static  void setFirstPublish(boolean firstPublish)
    {
        sp.edit().remove("First_Publish").apply();
        sp.edit().putBoolean("First_Publish",firstPublish).apply();
    }

}
