package com.example.andrzej.audiocontroller.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.interfaces.OnItemClickListener;
import com.example.andrzej.audiocontroller.interfaces.OnLongItemClickListener;
import com.example.andrzej.audiocontroller.interfaces.OnMoreItemClickListener;
import com.example.andrzej.audiocontroller.models.ExploreItem;
import com.example.andrzej.audiocontroller.utils.Image;
import com.squareup.picasso.Picasso;


import java.util.List;


public class ExploreRecyclerAdapter extends RecyclerView.Adapter<ExploreRecyclerAdapter.ExploreViewHolder> {

    private Context context;
    private List<ExploreItem> dataset;
    private int layoutResId;

    private OnItemClickListener itemClickListener;
    private OnLongItemClickListener longItemClickListener;
    private OnMoreItemClickListener moreItemClickListener;

    public ExploreRecyclerAdapter(Context context, List<ExploreItem> dataset, int layoutResId) {
        this.context = context;
        this.dataset = dataset;
        this.layoutResId = layoutResId;
    }

    @Override
    public ExploreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(layoutResId, null);

        return new ExploreViewHolder(itemLayoutView);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }


    @Override
    public void onBindViewHolder(ExploreViewHolder holder, int position) {
        ExploreItem item = getItem(position);

        holder.nameTv.setText(item.getName());

        if (layoutResId != R.layout.explore_item_grid) {
            if (item.isDirectory()) {
                holder.filesizeTv.setVisibility(View.INVISIBLE);
                holder.moreBtn.setVisibility(View.INVISIBLE);
            }else {
                holder.filesizeTv.setText(String.format(context.getResources().getString(R.string.filesize_format), String.valueOf(item.getMetadata().getFilesize())));
                holder.filesizeTv.setVisibility(View.VISIBLE);
                holder.moreBtn.setVisibility(View.VISIBLE);
            }
        }

        holder.iconIv.setImageBitmap(null);
        holder.iconIv.setImageDrawable(null);
        Image.clearDrawable(holder.iconIv);

        if (item.isDirectory())
            Image.setDrawable(context, holder.iconIv, R.drawable.ic_folder_black_48dp);
        else if (item.getMetadata().getCoverUrl() != null) {
            Picasso.with(context).load(item.getMetadata().getCoverUrl()).
                    placeholder(R.drawable.ic_insert_drive_file_black_48dp).
                    error(R.drawable.ic_insert_drive_file_black_48dp).
                    fit().
                    into(holder.iconIv);
        } else
            Image.setDrawable(context, holder.iconIv, R.drawable.ic_insert_drive_file_black_48dp);

    }

    public ExploreItem getItem(int position) {
        return dataset.get(position);
    }


    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setOnLongItemClickListener(OnLongItemClickListener longItemClickListener) {
        this.longItemClickListener = longItemClickListener;
    }

    public void setOnMoreItemClickListener(OnMoreItemClickListener moreItemClickListener) {
        this.moreItemClickListener = moreItemClickListener;
    }

    // inner class to hold a reference to each item of RecyclerView
    public class ExploreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public RelativeLayout rootLayout;
        public ImageView iconIv;
        public TextView nameTv;
        public TextView filesizeTv;
        public ImageButton moreBtn;

        public ExploreViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            rootLayout = (RelativeLayout) itemLayoutView.findViewById(R.id.rootLayout);
            nameTv = (TextView) itemLayoutView.findViewById(R.id.nameTv);
            filesizeTv = (TextView) itemLayoutView.findViewById(R.id.sizeTv);
            iconIv = (ImageView) itemLayoutView.findViewById(R.id.iconIv);
            moreBtn = (ImageButton) itemLayoutView.findViewById(R.id.moreBtn);

            rootLayout.setOnClickListener(this);
            moreBtn.setOnClickListener(this);
            rootLayout.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rootLayout:
                    if (itemClickListener != null)
                        itemClickListener.onItemClick(v, getAdapterPosition());
                    break;
                case R.id.moreBtn:
                    if (moreItemClickListener != null)
                        moreItemClickListener.onMoreClick(v, getAdapterPosition());
                    break;
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (longItemClickListener != null)
                longItemClickListener.onLongItemClick(v, getAdapterPosition());
            return true;
        }
    }
}
