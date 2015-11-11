package com.example.andrzej.audiocontroller.views;


import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.example.andrzej.audiocontroller.R;

public class TrackViewHolder extends ChildViewHolder {

    public LinearLayout rootLayout;
    public ImageView coverIv;
    public TextView nameTv;
    public TextView albumArtistTv;

    public TrackViewHolder(View itemView) {
        super(itemView);

        rootLayout = (LinearLayout) itemView.findViewById(R.id.rootLayout);
        coverIv = (ImageView) itemView.findViewById(R.id.coverIv);
        nameTv = (TextView) itemView.findViewById(R.id.trackTitleTv);
        albumArtistTv = (TextView) itemView.findViewById(R.id.trackAlbumArtistTv);
    }
}
