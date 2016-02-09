package dong.lan.shundai.adapter.base;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DecimalFormat;
import java.util.List;

import cn.bmob.v3.datatype.BmobGeoPoint;
import dong.lan.shundai.BuildConfig;
import dong.lan.shundai.MyApplication;
import dong.lan.shundai.R;
import dong.lan.shundai.bean.Help;
import dong.lan.shundai.bean.User;
import dong.lan.shundai.ui.SetMyInfoActivity;
import dong.lan.shundai.util.FaceTextUtils;
import dong.lan.shundai.util.ImageLoadOptions;
import dong.lan.shundai.util.TimeUtil;

/**
 * Created by 桂栋 on 2015/4/28.
 */
public class HelpAdapter extends BaseListAdapter<Help> {
    static String icon1, icon2, icon3, icon4, icon5;
    private String M[] = {"火星", "来自星星", "来自外太空", "来自银河系", "来自黑洞", "来自岛国", "亲，我崩溃了"};
    private DecimalFormat decimalFormat;
    public HelpAdapter(Context context, List<Help> datas) {
        super(context, datas);
        decimalFormat = new DecimalFormat("######0.00");
    }


    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.help_item, null);
            viewHolder = new ViewHolder();
            viewHolder.nick_name =dong.lan.shundai.adapter.base.ViewHolder.get(convertView, R.id.help_name_tvbtn);
            viewHolder.Avatar =dong.lan.shundai.adapter.base.ViewHolder.get(convertView, R.id.help_avater);
            viewHolder.distance =dong.lan.shundai.adapter.base.ViewHolder.get(convertView, R.id.help_meter_tvbtn);
            viewHolder.publish_time =dong.lan.shundai.adapter.base.ViewHolder.get(convertView, R.id.help_time_tvbtn);
            viewHolder.seek_help_info =dong.lan.shundai.adapter.base.ViewHolder.get(convertView, R.id.help_trends_tvbtn);
            viewHolder.helpicon_1 =dong.lan.shundai.adapter.base.ViewHolder.get(convertView, R.id.help_iocn1_btn);
            viewHolder.helpicon_2 =dong.lan.shundai.adapter.base.ViewHolder.get(convertView, R.id.help_iocn2_btn);
            viewHolder.helpicon_3 =dong.lan.shundai.adapter.base.ViewHolder.get(convertView, R.id.help_iocn3_btn);
            viewHolder.helpicon_4 =dong.lan.shundai.adapter.base.ViewHolder.get(convertView, R.id.help_iocn4_btn);
            viewHolder.helpicon_5 =dong.lan.shundai.adapter.base.ViewHolder.get(convertView, R.id.help_picture1_btn);
            viewHolder.conmment_count =dong.lan.shundai.adapter.base.ViewHolder.get(convertView, R.id.help_comment_tvbtn);
            viewHolder.seen_count =dong.lan.shundai.adapter.base.ViewHolder.get(convertView, R.id.help_visit_tvbtn);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Help help = getList().get(position);
        final User user = help.getUser();
        final String avatar = user.getAvatar();
        final String Name = user.getUsername();
        final String Info = help.getInfo();
        icon5 = help.getTag();
        icon1 = user.getAge();
        icon2 = user.getConstllation();
        if (BuildConfig.DEBUG) Log.d("HelpAdapter", user.getHonest()+"    "+user.getMeili());

        icon3 = String.valueOf(decimalFormat.format(Double.valueOf(user.getHonest())));
        icon4 = String.valueOf(decimalFormat.format(Double.valueOf(user.getMeili())));
        boolean isMan = user.getSex();


        final String Commment = "评论" + help.getComm();
        final String Visit = "看过" + help.getSee();
        long currentTime;
        currentTime = TimeUtil.stringToLong(help.getCreatedAt(), TimeUtil.FORMAT_DATA_TIME_SECOND_1);
        if (currentTime == 0)
            currentTime = TimeUtil.stringToLong(help.getCreatedAt(), TimeUtil.FORMAT_DATA_TIME_SECOND_1);
        if (avatar == null || avatar.equals("")) {
            viewHolder.Avatar.setImageDrawable(mContext.getResources().getDrawable(R.drawable.default_head));
        } else {
            ImageLoader.getInstance().displayImage(avatar, viewHolder.Avatar, ImageLoadOptions.getOptions(1));
        }
        BmobGeoPoint location = help.getLocation();
        String currentLat = MyApplication.getInstance().getLatitude();
        String currentLong = MyApplication.getInstance().getLongtitude();
        if (location != null && !currentLat.equals("") && !currentLong.equals("")) {
            double distance = DistanceOfTwoPoints(Double.parseDouble(currentLat), Double.parseDouble(currentLong), help.getLocation().getLatitude(),
                    help.getLocation().getLongitude());
            if (distance < 1000)
                viewHolder.distance.setText(String.valueOf(distance) + "米");
            else
                viewHolder.distance.setText(String.valueOf(distance / 1000.0) + "千米");
        } else {
            viewHolder.distance.setText(M[((int) (Math.random() * 100 % 7))]);
        }
        SpannableString spannableString = FaceTextUtils
                .toSpannableString(mContext, Info);
        viewHolder.seek_help_info.setText(spannableString);
        viewHolder.conmment_count.setText(Commment);
        viewHolder.seen_count.setText(Visit);
        viewHolder.publish_time.setText(TimeUtil.getDescriptionTimeFromTimestamp(currentTime));
        viewHolder.nick_name.setText(Name);
        if (isMan) {
            viewHolder.helpicon_1.setText("♂ " + icon1);
        } else {
            viewHolder.helpicon_1.setText("♀ " + icon1);
        }
        if (icon5 != null) {
            if (icon5.equals("带物")) {
                viewHolder.helpicon_5.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wu));
            } else if (icon5.equals("带人")) {
                viewHolder.helpicon_5.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ren));
            } else {
                viewHolder.helpicon_5.setImageDrawable(mContext.getResources().getDrawable(R.drawable.qita));
            }
        } else {
            viewHolder.helpicon_5.setImageDrawable(mContext.getResources().getDrawable(R.drawable.qita));
        }
        viewHolder.helpicon_2.setText(icon2);
        viewHolder.helpicon_3.setText("诚信 " + icon3);
        viewHolder.helpicon_4.setText("魅力 " + icon4);
        viewHolder.Avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SetMyInfoActivity.class);
                intent.putExtra("username", user.getUsername());
                intent.putExtra("from", "other");
                mContext.startActivity(intent);
            }
        });


        return convertView;
    }

    private static final double EARTH_RADIUS = 6378137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public static double DistanceOfTwoPoints(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }


    static class ViewHolder {
        ImageView Avatar;
        TextView nick_name, distance;
        TextView seek_help_info, publish_time, conmment_count, seen_count;
        TextView helpicon_1, helpicon_2, helpicon_3, helpicon_4;
        ImageView helpicon_5;
    }


}
