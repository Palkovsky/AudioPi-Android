package com.example.andrzej.audiocontroller.adapters;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.interfaces.OnItemClickListener;
import com.example.andrzej.audiocontroller.interfaces.OnLongItemClickListener;
import com.example.andrzej.audiocontroller.models.Track;
import com.example.andrzej.audiocontroller.utils.Image;

import java.util.List;

public class PlaylistDrawerRecyclerAdapter extends RecyclerView.Adapter<PlaylistDrawerRecyclerAdapter.TrackViewHolder> {

    private Context context;
    private List<Track> mDataset;

    private OnItemClickListener clickListener;

    public PlaylistDrawerRecyclerAdapter(Context context, List<Track> mDataset) {
        this.context = context;
        this.mDataset = mDataset;
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.playlist_drawer_list_item, null);

        return new TrackViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(TrackViewHolder holder, int position) {
        Track item = mDataset.get(position);

        holder.nameTv.setText(item.getFormattedName());

        if(item.isPlaying()){
            Image.setSourceDrawable(context, holder.iconIv, R.drawable.ic_play_arrow_black_24dp);
            holder.iconIv.setVisibility(View.VISIBLE);
            holder.nameTv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            holder.nameTv.setSelected(true);
            holder.nameTv.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            holder.rootLayout.setFocusable(false);
            holder.rootLayout.setClickable(false);
        }else{
            holder.iconIv.setVisibility(View.INVISIBLE);
            holder.nameTv.setEllipsize(null);
            holder.nameTv.setSelected(false);
            holder.nameTv.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            holder.rootLayout.setFocusable(true);
            holder.rootLayout.setClickable(true);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public int getPlayingPosition(){
        for(int i = 0; i < mDataset.size(); i++){
            Track track = mDataset.get(i);
            if(track.isPlaying())
                return i;
        }
        return -1;
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public RelativeLayout rootLayout;
        public ImageView iconIv;
        public TextView nameTv;

        public TrackViewHolder(View itemView) {
            super(itemView);

            rootLayout = (RelativeLayout) itemView.findViewById(R.id.rootLayout);
            iconIv = (ImageView) itemView.findViewById(R.id.iconIv);
            nameTv = (TextView) itemView.findViewById(R.id.nameTv);

            rootLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null)
                clickListener.onItemClick(v, getAdapterPosition());
        }
    }
}
