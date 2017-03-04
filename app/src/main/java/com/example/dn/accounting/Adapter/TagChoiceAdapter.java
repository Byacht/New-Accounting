package com.example.dn.accounting.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dn.accounting.Model.Tag;
import com.example.dn.accounting.R;

import java.util.ArrayList;

/**
 * Created by dn on 2017/2/24.
 */

public class TagChoiceAdapter extends RecyclerView.Adapter<TagChoiceAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Tag> tags;
    private OnItemClickListener onItemClickListener;

    public TagChoiceAdapter(Context context, ArrayList<Tag> tags){
        mContext = context;
        this.tags = tags;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.tag_view, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Tag tag = tags.get(position);
        Drawable tagImage = tag.getTagImage();
        String tagName = tag.getTagName();
        holder.imageview.setImageDrawable(tagImage);
        holder.textview.setText(tagName);
        holder.textview.setTextColor(Color.BLACK);
        holder.imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position);
                holder.textview.setTextColor(Color.RED);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageview;
        TextView textview;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageview = (ImageView) itemView.findViewById(R.id.tag_image);
            textview = (TextView) itemView.findViewById(R.id.tag_text);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
