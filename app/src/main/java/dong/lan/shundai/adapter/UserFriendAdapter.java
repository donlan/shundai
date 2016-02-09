package dong.lan.shundai.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import dong.lan.shundai.MyApplication;
import dong.lan.shundai.R;
import dong.lan.shundai.bean.User;
import dong.lan.shundai.ui.ChatActivity;
import dong.lan.shundai.util.ImageLoadOptions;


/**
 * 好友列表
 */
@SuppressLint("DefaultLocale")
public class UserFriendAdapter extends BaseAdapter implements SectionIndexer {
    private Context ct;
    private List<User> data = new ArrayList<User>();
    static String trends;
    private DecimalFormat decimalFormat;

    public UserFriendAdapter(Context ct, List<User> datas) {
        this.ct = ct;
        this.data = datas;
        decimalFormat = new DecimalFormat("######0.00");
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     */
    public void updateListView(List<User> list) {
        this.data = list;
        notifyDataSetChanged();
    }

    public void remove(User user) {
        this.data.remove(user);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(ct).inflate(
                    R.layout.friend_item, null);
            viewHolder = new ViewHolder();
            viewHolder.alpha = (TextView) convertView.findViewById(R.id.alpha);
            viewHolder.name = (TextView) convertView
                    .findViewById(R.id.friend_name_tvbtn);
            viewHolder.avatar = (ImageView) convertView
                    .findViewById(R.id.friend_picture_btn);

            viewHolder.distance = (TextView) convertView.findViewById(R.id.distance);
            viewHolder.trend = (TextView) convertView.findViewById(R.id.friend_trends_tvbtn);
            viewHolder.icon_1 = (TextView) convertView.findViewById(R.id.friend_iocn1_btn);
            viewHolder.icon_2 = (TextView) convertView.findViewById(R.id.friend_iocn2_btn);
            viewHolder.icon_3 = (TextView) convertView.findViewById(R.id.friend_iocn3_btn);
            viewHolder.icon_4 = (TextView) convertView.findViewById(R.id.friend_iocn4_btn);
            //viewHolder.chat_to_friend = (ImageView) convertView.findViewById(R.id.friend_picture1_chat);
            viewHolder.itemLayout = (LinearLayout) convertView.findViewById(R.id.item_layout);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        User friend = data.get(position);
        BmobGeoPoint location = friend.getLocation();
        String currentLat = MyApplication.getInstance().getLatitude();
        String currentLong = MyApplication.getInstance().getLongtitude();
        if (location != null && !currentLat.equals("") && !currentLong.equals("")) {
            double distance = DistanceOfTwoPoints(Double.parseDouble(currentLat), Double.parseDouble(currentLong), friend.getLocation().getLatitude(),
                    friend.getLocation().getLongitude());
            if (distance > 1000)
                viewHolder.distance.setText(String.valueOf(distance / 1000) + "千米");
            else
                viewHolder.distance.setText(String.valueOf(distance) + "米");
        } else {
            viewHolder.distance.setText("来自星星");
        }

        final String name = friend.getUsername();
        final String avatar = friend.getAvatar();
        final String obID = friend.getObjectId();
        trends = friend.getTrends();
        String icon1 = friend.getAge() + "";
        String icon2 = friend.getConstllation() + "";
        String h = friend.getHonest();
        String m = friend.getMeili();
        if(h==null)
            h="8";
        if(m==null)
            m="8";
        String icon3 = "诚信 " + String.valueOf(decimalFormat.format(Double.parseDouble(h)));
        String icon4 = "魅力 " + String.valueOf(decimalFormat.format(Double.parseDouble(m)));
        if (!TextUtils.isEmpty(avatar)) {
            ImageLoader.getInstance().displayImage(avatar, viewHolder.avatar, ImageLoadOptions.getOptions(1));
        } else {
            viewHolder.avatar.setImageDrawable(ct.getResources().getDrawable(R.drawable.default_head));
        }
        viewHolder.icon_1.setText(icon1);
        viewHolder.icon_2.setText(icon2);
        viewHolder.icon_3.setText(icon3);
        viewHolder.icon_4.setText(icon4);
        viewHolder.name.setText(name);
        if (trends != null && !trends.equals("")) {
            viewHolder.trend.setText(trends);
        } else {
            viewHolder.trend.setText("这家伙很懒没有写签名");
        }


        // 根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);
        // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            viewHolder.alpha.setVisibility(View.VISIBLE);
            viewHolder.alpha.setText(friend.getSortLetters());
        } else {
            viewHolder.alpha.setVisibility(View.GONE);
        }
        viewHolder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BmobChatUser user = new BmobChatUser();
                user.setAvatar(avatar);
                user.setNick(name);
                user.setUsername(name);
                user.setObjectId(obID);
                Intent intent = new Intent(ct, ChatActivity.class);
                intent.putExtra("user", user);
                ct.startActivity(intent);
            }
        });
        return convertView;
    }

    static class ViewHolder {
        TextView alpha;// 首字母提示
        ImageView avatar;
        TextView name;
        TextView trend;
        TextView icon_1, icon_2, icon_3, icon_4;
        LinearLayout itemLayout;
        public TextView distance;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return data.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    @SuppressLint("DefaultLocale")
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = data.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public Object[] getSections() {
        return null;
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

}