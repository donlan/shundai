package dong.lan.shundai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dong.lan.shundai.R;
import dong.lan.shundai.bean.UserCity;


/**
 * Created by 桂栋 on 2015/5/3.
 */
public class CityAdapter extends BaseAdapter {

    Context context;
    List<UserCity> cityList = new ArrayList<UserCity>();
    public CityAdapter(Context context,List<UserCity> list)
    {
        this.context=context;
        this.cityList=list;

    }


    @Override
    public int getCount() {
        return cityList.size();
    }

    @Override
    public Object getItem(int i) {
        return cityList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (viewHolder==null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.cityitem, null);
            viewHolder.cityName = (TextView) view.findViewById(R.id.cityName);
            viewHolder.cityNum = (TextView) view.findViewById(R.id.cityNum);
            viewHolder.citySeq= (TextView) view.findViewById(R.id.city_seq);

        }
        else
        {
            viewHolder= (ViewHolder) viewGroup.getTag();
        }

        if(i<3)
        {
            switch (i)
            {
                case 0:
                    //viewHolder.citySeq.setText(1+"");
                    viewHolder.citySeq.setBackgroundResource(R.drawable.show_event_pk_rank1);
                    break;
                case 1:
                    //viewHolder.citySeq.setText(2+"");
                    viewHolder.citySeq.setBackgroundResource(R.drawable.show_event_pk_rank2);
                    break;
                case 2:
                    //viewHolder.citySeq.setText(3+"");
                    viewHolder.citySeq.setBackgroundResource(R.drawable.show_event_pk_rank3);
                    break;
            }
        }
        else
        {
            viewHolder.citySeq.setText(i+1+"");
            viewHolder.citySeq.setBackgroundResource(R.drawable.lock_check);
        }
        UserCity userCity = new UserCity();
        userCity=cityList.get(i);
        if(userCity.getCity()==null || userCity.getCity().equals("") ||userCity.getCity().equals("null"))
        {
            view.setVisibility(View.GONE);
        }
        viewHolder.cityName.setText(userCity.getCity());
        viewHolder.cityNum.setText(userCity.getCount() + "");
        view.setTag(viewHolder);
        return view;
    }

   static class ViewHolder
    {
        TextView cityName,cityNum,citySeq;
    }

}
