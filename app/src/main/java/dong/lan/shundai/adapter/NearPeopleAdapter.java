package dong.lan.shundai.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DecimalFormat;
import java.util.List;

import cn.bmob.v3.datatype.BmobGeoPoint;
import dong.lan.shundai.MyApplication;
import dong.lan.shundai.R;
import dong.lan.shundai.adapter.base.BaseListAdapter;
import dong.lan.shundai.adapter.base.ViewHolder;
import dong.lan.shundai.bean.User;
import dong.lan.shundai.util.ImageLoadOptions;

/**
 * 附近的人
 * 
 * @ClassName: BlackListAdapter
 */
public class NearPeopleAdapter extends BaseListAdapter<User> {

	private final DecimalFormat decimalFormat;
	StringBuilder sb;

	public NearPeopleAdapter(Context context, List<User> list) {
		super(context, list);
		sb = new StringBuilder();
		decimalFormat = new DecimalFormat("######0.00");
	}

	@Override
	public View bindView(int arg0, View convertView, ViewGroup arg2) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_near_people, null);
		}
		final User contract = getList().get(arg0);
		TextView tv_name = ViewHolder.get(convertView, R.id.tv_name);
		TextView tv_distance = ViewHolder.get(convertView, R.id.tv_distance);
		TextView tv_logintime = ViewHolder.get(convertView, R.id.tv_logintime);
		ImageView iv_avatar = ViewHolder.get(convertView, R.id.iv_avatar);
		String avatar = contract.getAvatar();
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, iv_avatar,
					ImageLoadOptions.getOptions(1));
		} else {
			iv_avatar.setImageResource(R.drawable.default_head);
		}
		BmobGeoPoint location = contract.getLocation();
		BmobGeoPoint curPoint = MyApplication.getInstance().getCurPoint();
		String currentLat = String.valueOf(curPoint.getLatitude());
		String currentLong = String.valueOf(curPoint.getLongitude());
		if(location!=null && !currentLat.equals("") && !currentLong.equals("")){
			double distance=DistanceUtil.getDistance(new LatLng(location.getLatitude(),location.getLongitude()),
					new LatLng(Double.valueOf(currentLat),Double.valueOf(currentLong)));
			if(distance>=1000)
			{
				sb.delete(0,sb.length());
				sb.append(decimalFormat.format(distance/1000.0));
				sb.append("千米");
				tv_distance.setText(sb.toString());
			}
			else{
				sb.delete(0,sb.length());
				sb.append(decimalFormat.format(distance));
				sb.append("米");
				tv_distance.setText(sb.toString());
			}
		}else{
			tv_distance.setText("未知");
		}
		tv_name.setText(contract.getUsername());
		sb.delete(0,sb.length());
		sb.append("最近登录时间:");
		sb.append(contract.getUpdatedAt());
		tv_logintime.setText(sb.toString());
		return convertView;
	}

	public  void updateList(List<User> users)
	{
		setList(users);
		notifyDataSetChanged();
	}

}
