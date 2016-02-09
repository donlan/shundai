package dong.lan.shundai.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dong.lan.shundai.R;
import dong.lan.shundai.bean.Comment;
import dong.lan.shundai.ui.SetMyInfoActivity;
import dong.lan.shundai.util.ImageLoadOptions;
import dong.lan.shundai.util.TimeUtil;

/**
 * Created by 桂栋 on 2015/4/30.
 */
public class HelpComAdapter extends BaseAdapter {

    List<Comment> commentList = new ArrayList<Comment>();
    Context context;

    public HelpComAdapter(Context context, List<Comment> list) {
        this.context = context;
        this.commentList = list;
    }


    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Object getItem(int i) {
        return commentList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (viewHolder == null) {
            view = LayoutInflater.from(context).inflate(R.layout.comment_item, null);
            viewHolder = new ViewHolder();
            viewHolder.com_avater = (ImageView) view.findViewById(R.id.com_avater);
            viewHolder.ComTime = (TextView) view.findViewById(R.id.com_time);
            viewHolder.comment = (TextView) view.findViewById(R.id.com_info);
            viewHolder.com_Name = (TextView) view.findViewById(R.id.com_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) viewGroup.getTag();
        }
        Comment comment = commentList.get(i);
        final String com_avater = comment.getUser().getAvatar();
        final String com_name = comment.getUser().getUsername();
        String com_time = comment.getCreatedAt();
        final String com_info = comment.getContent();
        if (com_time==null)
            com_time= new SimpleDateFormat(TimeUtil.FORMAT_DATA_TIME_SECOND_1)
                    .format(new Date());
        if (com_avater == null || com_avater.equals("")) {
            viewHolder.com_avater.setImageDrawable(context.getResources().getDrawable(R.drawable.default_head));
        } else {
            ImageLoader.getInstance().displayImage(com_avater, viewHolder.com_avater, ImageLoadOptions.getOptions(1));
        }
        long currentTime = TimeUtil.stringToLong(com_time, TimeUtil.FORMAT_DATA_TIME_SECOND_1);

        viewHolder.com_Name.setText(com_name);
        viewHolder.comment.setText(com_info);
        viewHolder.ComTime.setText(TimeUtil.getDescriptionTimeFromTimestamp(currentTime));
        viewHolder.com_avater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SetMyInfoActivity.class);
                intent.putExtra("username", com_name);
                intent.putExtra("from", "other");
                context.startActivity(intent);
            }
        });
        return view;
    }

    public void add(Comment comment)
    {
        commentList.add(0,comment);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        ImageView com_avater;
        TextView comment;
        TextView ComTime;
        TextView com_Name;
    }
}
