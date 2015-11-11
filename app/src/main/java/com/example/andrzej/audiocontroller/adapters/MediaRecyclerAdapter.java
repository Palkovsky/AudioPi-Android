package com.example.andrzej.audiocontroller.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.models.ExploreItem;
import com.example.andrzej.audiocontroller.models.Playlist;
import com.example.andrzej.audiocontroller.utils.Image;
import com.example.andrzej.audiocontroller.views.PlaylistViewHolder;
import com.example.andrzej.audiocontroller.views.TrackViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MediaRecyclerAdapter extends ExpandableRecyclerAdapter<PlaylistViewHolder, TrackViewHolder> {

    private LayoutInflater mInflater;
    private Context context;

    public MediaRecyclerAdapter(Context context, List<Playlist> parentItemList) {
        super(parentItemList);
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public PlaylistViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View view = mInflater.inflate(R.layout.playlist_item_list, parentViewGroup, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public TrackViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View view = mInflater.inflate(R.layout.track_list_item, childViewGroup, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(PlaylistViewHolder playlistViewHolder, int position, ParentListItem parentListItem) {
        Playlist playlist = (Playlist) parentListItem;

        playlistViewHolder.coverIv.setImageBitmap(null);
        playlistViewHolder.coverIv.setImageDrawable(null);
        Image.clearDrawable(playlistViewHolder.coverIv);

        if (playlist.getCoverUrl() != null && !playlist.getCoverUrl().equals(""))
            Picasso.with(context).load(playlist.getCoverUrl()).placeholder(R.drawable.ic_library_music_black_36dp).
                    error(R.drawable.ic_library_music_black_36dp).into(playlistViewHolder.coverIv);
        else
            Image.setDrawable(context, playlistViewHolder.coverIv, R.drawable.ic_library_music_black_36dp);

        playlistViewHolder.nameTv.setText(playlist.getName());
        playlistViewHolder.tracksCountTv.setText((String.format(context.getResources().getString(R.string.playlist_count_format),
                String.valueOf(playlist.getTracks().size()))));
    }

    @Override
    public void onBindChildViewHolder(TrackViewHolder trackViewHolder, int position, Object childListItem) {
        ExploreItem track = (ExploreItem) childListItem;
        if (!track.isDirectory()) {

            trackViewHolder.coverIv.setImageBitmap(null);
            trackViewHolder.coverIv.setImageDrawable(null);
            Image.clearDrawable(trackViewHolder.coverIv);

            String coverUrl = track.getMetadata().getCoverUrl();
            if (coverUrl != null && !coverUrl.equals(""))
                Picasso.with(context).load(coverUrl).placeholder(R.drawable.ic_music_note_black_36dp).
                        error(R.drawable.ic_music_note_black_36dp).into(trackViewHolder.coverIv);
            else
                Image.setDrawable(context, trackViewHolder.coverIv, R.drawable.ic_music_note_black_36dp);


            String artist, album;
            if(track.getMetadata().getArtist() == null)
                artist = context.getString(R.string.unknown);
            else
                artist = track.getMetadata().getArtist();
            if(track.getMetadata().getAlbum() == null)
                album = context.getString(R.string.unknown);
            else
                album = track.getMetadata().getAlbum();

            String formattedArtistAlbum =
                    String.format(context.getString(R.string.artist_album_format), artist, album);


            trackViewHolder.nameTv.setText(track.getName());
            trackViewHolder.albumArtistTv.setText(formattedArtistAlbum);
        }
    }
}
