package dong.lan.shundai.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import dong.lan.shundai.R;
import dong.lan.shundai.adapter.CityAdapter;
import dong.lan.shundai.bean.RecentlyCity;
import dong.lan.shundai.bean.User;
import dong.lan.shundai.bean.UserCity;


public class CityActivity extends BaseActivity implements OnClickListener,AdapterView.OnItemClickListener{

	public LocationClient mLocationClient = null;
	public BDLocationListener locationListener = new MyLocationListener();
	public static String loc = "全国";
	public static String from = "";
	User user;
	private List<UserCity> userCityList = new ArrayList<UserCity>();
	private List<RecentlyCity> recentlyCityList = new ArrayList<RecentlyCity>();

	private ListView cityList;
	private TextView nowCity;
	private int cityTextId[] = {R.id.city_city11_tvbtn,R.id.city_city12_tvbtn,R.id.city_city13_tvbtn
	,R.id.city_city14_tvbtn,R.id.city_city15_tvbtn};
	private TextView cityText[] = new TextView[5];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		from=getIntent().getStringExtra("from");
		setContentView(R.layout.activity_city);
		initView();
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(locationListener); // 注册监听函数
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开GPS
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(3000);// 设置发起定位请求的间隔时间为3000ms
		mLocationClient.setLocOption(option);// 使用设置
		mLocationClient.start();// 开启定位SDK
		mLocationClient.requestLocation();// 开始请求位置

	}

	public void onClick(View v) {
		switch (v.getId())
		{

			case R.id.city_city11_tvbtn:
				IntentCity(cityText[0].getText().toString(),"城市1");
				break;

			case R.id.city_city12_tvbtn:
				IntentCity(cityText[1].getText().toString(),"城市2");
				break;

			case R.id.city_city13_tvbtn:
				IntentCity(cityText[3].getText().toString(),"城市3");
				break;

			case R.id.city_city14_tvbtn:
				IntentCity(cityText[4].getText().toString(),"城市4");
				break;

			case R.id.city_city15_tvbtn:
				IntentCity(cityText[5].getText().toString(),"城市5");
				break;
			case R.id.city_now_city_tvbtn:
				if(nowCity.getText().toString().equals("null")||nowCity.getText().toString().equals("定位失败"))
				{
					finish();
				}
				else
				IntentCity(nowCity.getText().toString(),"当前城市");
				break;
			case R.id.backUp:
				if (from==null||from.equals("")||loc.equals("null")||nowCity.getText().toString().equals("null")||nowCity.getText().toString().equals("当前城市"))
					finish();

				else if(from.equals("Help"))
				{
					Intent intent =new Intent(CityActivity.this,HelpActivity.class);
					intent.putExtra("City", loc);
					startAnimActivity(intent);
					finish();
				}
				else if (from.equals("Seek"))
				{
					Intent intent = new Intent(CityActivity.this,HelpActivity.class);
					intent.putExtra("City",loc);
					startAnimActivity(intent);
					finish();
				}
				break;
		}

	}

	private void initView()
	{
		for(int  i= 0;i<5;i++)
		{
			cityText[i]= (TextView) findViewById(cityTextId[i]);
			cityText[i].setOnClickListener(this);
		}
		nowCity= (TextView) findViewById(R.id.city_now_city_tvbtn);
		cityList= (ListView) findViewById(R.id.allRegiCity);
		nowCity.setOnClickListener(this);

		user = userManager.getCurrentUser(User.class);


		BmobQuery<RecentlyCity> recentlyCityBmobQuery = new BmobQuery<RecentlyCity>();
		recentlyCityBmobQuery.addWhereEqualTo("username", user.getUsername());
		recentlyCityBmobQuery.order("-Count");
		recentlyCityBmobQuery.findObjects(getBaseContext(), new FindListener<RecentlyCity>() {
			@Override
			public void onSuccess(List<RecentlyCity> list) {
				if(list.size()>=1) {
					recentlyCityList = list;
					for (int i = 0; i < list.size() && i<5; i++) {
						if (list.get(i).getCity() != null) {
							cityText[i].setText(list.get(i).getCity());

						}
					}
				}
			}

			@Override
			public void onError(int i, String s) {
			}
		});


		BmobQuery<UserCity> query = new BmobQuery<UserCity>();
		query.order("-count");
		query.findObjects(getBaseContext(), new FindListener<UserCity>() {
			@Override
			public void onSuccess(List<UserCity> list) {
				if (!userCityList.isEmpty()) {
					userCityList.clear();
				}
				userCityList = list;
				CityAdapter cityAdapter = new CityAdapter(getBaseContext(), userCityList);
				cityList.setAdapter(cityAdapter);


			}

			@Override
			public void onError(int i, String s) {

			}
		});


		findViewById(R.id.backUp).setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	});
		cityList.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		TextView textView = (TextView) view.findViewById(R.id.cityName);


		if (from==null||from.equals(""))
			finish();
		IntentCity(textView.getText().toString(),"ALL");
		addRecentCity(textView.getText().toString());



	}

	public  void addRecentCity(String city)
	{
		int i ;
		RecentlyCity recentlyCity = new RecentlyCity();
		if(recentlyCityList.size()<1)
		{
			recentlyCity.setCount(1);
			recentlyCity.setCity(loc);
			recentlyCity.setUsername(user.getUsername());
			recentlyCity.save(this, new SaveListener() {
				@Override
				public void onSuccess() {
				}

				@Override
				public void onFailure(int i, String s) {

				}
			});

		}
		else
		{
		for(i = 0;i<recentlyCityList.size();i++) {
			if (recentlyCityList.get(i).getCity().equals(city)) {
				recentlyCity.setCount(recentlyCityList.get(i).getCount() + 1);
				recentlyCity.setObjectId(recentlyCityList.get(i).getObjectId());
				recentlyCity.update(getBaseContext(), new UpdateListener() {
					@Override
					public void onSuccess() {
					}

					@Override
					public void onFailure(int i, String s) {

					}
				});
			}
		}
			if (i == recentlyCityList.size()) {

				recentlyCity.setCity(city);
				recentlyCity.setCount(1);
				recentlyCity.setUsername(user.getUsername());
				recentlyCity.save(getBaseContext(), new SaveListener() {
					@Override
					public void onSuccess() {
					}

					@Override
					public void onFailure(int i, String s) {

					}
				});
		}
		}
	}
	public  class MyLocationListener implements BDLocationListener
	{

		@Override
		public void onReceiveLocation(BDLocation bdLocation) {
			if (bdLocation != null)
			{
				StringBuffer sb = new StringBuffer(128);// 接受服务返回的缓冲区
				sb.append(bdLocation.getCity());// 获得城市
				loc = sb.toString().trim();
				if(!loc.equals("")&&!loc.equals("null"))
				nowCity.setText(loc);
				else {
					String city =userManager.getCurrentUser(User.class).getCity();
					if(city!=null && !city.equals("null"))
						nowCity.setText(city);
					else
					nowCity.setText("定位失败");
				}
			} else
			{
				Toast.makeText(getBaseContext(),"无法定位",Toast.LENGTH_SHORT).show();
				return;
			}
		}
	}
	public void stopListener()
	{
		if (mLocationClient != null && mLocationClient.isStarted())
		{
			mLocationClient.stop();// 关闭定位SDK
			mLocationClient = null;
		}
	}
	@Override
	protected void onDestroy() {
		stopListener();
		super.onDestroy();
	}

	private void IntentCity(String s,String value)
	{

		if (s==null||nowCity.getText().toString().equals(value))
			finish();
		Intent intent =new Intent(CityActivity.this,HelpActivity.class);
		if(from.equals("ALL"))
		{
			intent.putExtra("City", s);
			intent.putExtra("from", "顺带");
			startAnimActivity(intent);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
