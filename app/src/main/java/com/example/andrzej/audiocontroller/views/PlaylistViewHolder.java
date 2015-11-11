package com.example.andrzej.audiocontroller.views;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.example.andrzej.audiocontroller.R;

public class PlaylistViewHolder extends ParentViewHolder {

    public LinearLayout rootLayout;
    public ImageView coverIv;
    public TextView nameTv;
    public TextView tracksCountTv;

    public PlaylistViewHolder(View itemView) {
        super(itemView);

        rootLayout = (LinearLayout) itemView.findViewById(R.id.rootLayout);
        coverIv = (ImageView) itemView.findViewById(R.id.coverIv);
        nameTv = (TextView) itemView.findViewById(R.id.playlistNameTv);
        tracksCountTv = (TextView) itemView.findViewById(R.id.playlistTrackCountTv);
    }
}