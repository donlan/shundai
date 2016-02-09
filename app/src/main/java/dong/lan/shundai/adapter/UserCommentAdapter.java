package dong.lan.shundai.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import dong.lan.shundai.R;
import dong.lan.shundai.bean.Comment;
import dong.lan.shundai.util.FaceTextUtils;


/**
 * Created by 梁桂栋 on 2015/11/28.
 */
public class UserCommentAdapter extends RecyclerView.Adapter<UserCommentAdapter.Holder> {

    Context mContext;
    LayoutInflater inflater;
    List<Comment> comments;
    float score;

    public UserCommentAdapter(Context context, List<Comment> comments) {
        inflater = LayoutInflater.from(context);
        this.comments = comments;
        mContext = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.my_child_com_item, null));
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final Comment comment = comments.get(position);
        String time = comment.getCreatedAt();
        StringBuffer s = new StringBuffer();
        s.append(comment.getContent());
        s.append("\n\n");
        if (time != null) {
            s.append(time.substring(0, 4));
            s.append("年 ");
            s.append(time.substring(5, 7));
            s.append("月 ");
            s.append(time.substring(8, 10));
            s.append("日");
        }
        holder.name.setText(comment.getUser().getUsername());
        holder.content.setText(FaceTextUtils
                .toSpannableString(mContext, s.toString()));
        if (comment.getIsHelped()) {
            holder.rating.setRating(comment.getSrore());
            if(comment.getUpdatedAt().equals(comment.getCreatedAt()))
            {
                holder.rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        score = rating;
                    }
                });
            }else {
                holder.rating.setEnabled(false);
                holder.rating.setFocusable(false);
                holder.rating.setActivated(false);
            }
        } else {
            holder.rating.setVisibility(View.GONE);
        }
        String replyStr = comment.getReply();
        if(replyStr!=null && !replyStr.equals(""))
        {

            holder.replyText.setText(replyStr);
            holder.reply.setVisibility(View.GONE);
            holder.replyText.setFocusable(false);
        }
        holder.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String replyText =holder.replyText.getText().toString();
                if (replyText.equals("")) {
                    Toast.makeText(mContext, "回复不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "回复成功", Toast.LENGTH_SHORT).show();
                    holder.reply.setVisibility(View.GONE);
                    comment.setSrore((int) score);
                    comment.setReply(replyText);
                    comment.update(mContext);
                        if (listener != null)
                            listener.onItemClick(comment, replyText, holder.getLayoutPosition(),comment.getIsHelped(),score);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        public ImageView head;
        public TextView name;
        public TextView content;
        public RatingBar rating;
        public EditText replyText;
        public TextView reply;

        public Holder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            reply = (TextView) v.findViewById(R.id.com_replay);
            replyText = (EditText) v.findViewById(R.id.com_replay_et);
            head = (ImageView) v.findViewById(R.id.com_user_head);
            content = (TextView) v.findViewById(R.id.comment_text);
            rating = (RatingBar) v.findViewById(R.id.rating);

        }
    }


    public interface onItemReplyListener {
        void onItemClick(Comment comment, String replay, int pos,boolean isHelp,float score);
    }

    onItemReplyListener listener;

    public void setOnItemClickListener(onItemReplyListener listener) {
        this.listener = listener;
    }
}
