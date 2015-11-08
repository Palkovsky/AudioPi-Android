package com.example.andrzej.audiocontroller.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.interfaces.OnItemClickListener;
import com.example.andrzej.audiocontroller.models.ExploreItem;
import com.example.andrzej.audiocontroller.utils.Image;
import com.squareup.picasso.Picasso;


import java.util.List;


public class ExploreRecyclerAdapter extends RecyclerView.Adapter<ExploreRecyclerAdapter.ExploreViewHolder> {

    private Context context;
    private List<ExploreItem> dataset;
    private int layoutResId;

    private OnItemClickListener itemClickListener;

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

    /**
     * Remove all elements from the list.
     */
    public void clear() {
        final int size = getItemCount();
        dataset.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    // inner class to hold a reference to each item of RecyclerView
    public class ExploreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public RelativeLayout rootLayout;
        public ImageView iconIv;
        public TextView nameTv;

        public ExploreViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            rootLayout = (RelativeLayout) itemLayoutView.findViewById(R.id.rootLayout);
            nameTv = (TextView) itemLayoutView.findViewById(R.id.nameTv);
            iconIv = (ImageView) itemLayoutView.findViewById(R.id.iconIv);

            rootLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null)
                itemClickListener.onItemClick(v, getAdapterPosition());
        }
    }
}
