package dong.lan.shundai.util;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;


/**
 * Created by 桂栋 on 2015/6/29.
 */
public class AnimUitls {
    public  static  LayoutAnimationController listZomeInAnim(Context context,int id)
    {
        LayoutAnimationController layoutAnimationController  = new LayoutAnimationController( getAnimFromXml(context,id));
        layoutAnimationController.setOrder(Animation.ZORDER_NORMAL);
        return layoutAnimationController;
    }
    public static Animation getAnimFromXml(Context context,int anim_id)
    {
        return AnimationUtils.loadAnimation(context, anim_id);
    }

    public static void BunttonAnim(View view,int duration)
    {
        ObjectAnimator.ofFloat(view,"scaleX",1f,0.8f,1f).setDuration(duration).start();
        ObjectAnimator.ofFloat(view,"scaleY",1f,0.8f,1f).setDuration(duration).start();
    }

}
