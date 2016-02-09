package dong.lan.shundai.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import dong.lan.shundai.R;
import dong.lan.shundai.bean.Help;
import dong.lan.shundai.util.FaceTextUtils;


/**
 * Created by 梁桂栋 on 2015/11/28.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.Holder> {

    Context mContext;
    LayoutInflater inflater;
    List<Help> helps;
    public CommentAdapter(Context context,List<Help> helps)
    {
        inflater = LayoutInflater.from(context);
        this.helps = helps;
        mContext = context;
    }
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.my_comment_item,null));
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final Help help = helps.get(position);
        holder.count.setText(help.getComm()+" 评论");
        String time = help.getCreatedAt();
        StringBuffer s = new StringBuffer();
        if (time != null) {
            s.append(time.substring(0, 4));
            s.append("年 ");
            s.append(time.substring(5, 7));
            s.append("月 ");
            s.append(time.substring(8, 10));
            s.append("日");
            holder.time.setText(s.toString());
        }
        holder.time.setText(s.toString());
        holder.content.setText(FaceTextUtils
                .toSpannableString(mContext, help.getInfo()));

        if(listener!=null)
        {
            holder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(help,holder.getLayoutPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return helps.size();
    }

    static class Holder extends RecyclerView.ViewHolder
    {

        public TextView time;
        public TextView count;
        public TextView content;
        public RelativeLayout parent;
        public Holder(View v) {
            super(v);
            time = (TextView) v.findViewById(R.id.comment_time);
            count = (TextView) v.findViewById(R.id.comment_count);
            content = (TextView) v.findViewById(R.id.comment);
            parent = (RelativeLayout) v.findViewById(R.id.comment_parent);
        }
    }


    public interface onItemClickListener
    {
        void onItemClick(Help help, int pos);
    }

    onItemClickListener listener;
    public void setOnItemClickListener(onItemClickListener listener)
    {
        this.listener = listener;
    }
}
