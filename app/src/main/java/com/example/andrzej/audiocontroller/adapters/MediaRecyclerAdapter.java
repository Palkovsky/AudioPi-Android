package com.example.andrzej.audiocontroller.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.interfaces.OnChildItemClickListener;
import com.example.andrzej.audiocontroller.interfaces.OnChildItemLongClickListener;
import com.example.andrzej.audiocontroller.interfaces.OnMoreChildItemClickListener;
import com.example.andrzej.audiocontroller.models.Playlist;
import com.example.andrzej.audiocontroller.models.Track;
import com.example.andrzej.audiocontroller.utils.Image;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MediaRecyclerAdapter extends ExpandableRecyclerAdapter<MediaRecyclerAdapter.PlaylistViewHolder, MediaRecyclerAdapter.TrackViewHolder> {

    private LayoutInflater mInflater;
    private Context context;

    private OnChildItemClickListener onTrackClickListener;
    private OnChildItemLongClickListener onTrackLongClickListener;
    private OnMoreChildItemClickListener onMoreTrackItemClickListener;

    public MediaRecyclerAdapter(final Context context, final List<Playlist> parentItemList) {
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
        View view = mInflater.inflate(R.layout.track_item_list, childViewGroup, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(PlaylistViewHolder playlistViewHolder, final int position, ParentListItem parentListItem) {
        Playlist playlist = (Playlist) parentListItem;

        //Listeners


        playlistViewHolder.coverIv.setImageBitmap(null);
        playlistViewHolder.coverIv.setImageDrawable(null);
        Image.clearDrawable(playlistViewHolder.coverIv);

        if (playlist.getCoverUrl() != null && !playlist.getCoverUrl().equals(""))
            Picasso.with(context).load(playlist.getCoverUrl()).placeholder(R.drawable.ic_library_music_black_36dp).
                    error(R.drawable.ic_library_music_black_36dp).into(playlistViewHolder.coverIv);
        else
            Image.setBackgroundDrawable(context, playlistViewHolder.coverIv, R.drawable.ic_library_music_black_36dp);

        playlistViewHolder.nameTv.setText(playlist.getName());
        playlistViewHolder.tracksCountTv.setText((String.format(context.getResources().getString(R.string.playlist_count_format),
                String.valueOf(playlist.getTracks().size()))));

    }

    @Override
    public void onBindChildViewHolder(TrackViewHolder trackViewHolder, final int position, final Object childListItem) {
        final Track track = (Track) childListItem;
        if (!track.isDirectory()) {

            //Listeners
            trackViewHolder.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int internalPos = calculateInternalPosition(track.getPlaylist(), track);
                    if (onTrackClickListener != null)
                        onTrackClickListener.onChildItemClick(v, position, internalPos, track.getPlaylist());
                }
            });

            trackViewHolder.rootLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onTrackLongClickListener != null)
                        onTrackLongClickListener.onChildLongClickListener(v, position, childListItem);
                    return true;
                }
            });

            trackViewHolder.moreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onMoreTrackItemClickListener != null)
                        onMoreTrackItemClickListener.onMoreChildItemClick(v, position, childListItem);
                }
            });

            trackViewHolder.coverIv.setImageBitmap(null);
            trackViewHolder.coverIv.setImageDrawable(null);
            Image.clearDrawable(trackViewHolder.coverIv);

            String coverUrl = track.getMetadata().getCoverUrl();
            if (coverUrl != null && !coverUrl.equals(""))
                Picasso.with(context).load(coverUrl).placeholder(R.drawable.ic_music_note_black_36dp).
                        error(R.drawable.ic_music_note_black_36dp).into(trackViewHolder.coverIv);
            else
                Image.setBackgroundDrawable(context, trackViewHolder.coverIv, R.drawable.ic_music_note_black_36dp);


            String artist, album;
            if (track.getMetadata().getArtist() == null)
                artist = context.getString(R.string.unknown);
            else
                artist = track.getMetadata().getArtist();
            if (track.getMetadata().getAlbum() == null)
                album = context.getString(R.string.unknown);
            else
                album = track.getMetadata().getAlbum();

            String formattedArtistAlbum =
                    String.format(context.getString(R.string.artist_album_format), artist, album);


            trackViewHolder.nameTv.setText(track.getName());
            if ((artist.equals(context.getString(R.string.unknown)) && album.equals(context.getString(R.string.unknown))) ||
                    (artist.equals("null") && album.equals("null")))
                trackViewHolder.albumArtistTv.setVisibility(View.GONE);
            else {
                trackViewHolder.albumArtistTv.setVisibility(View.VISIBLE);
                trackViewHolder.albumArtistTv.setText(formattedArtistAlbum);
            }
        }
    }

    private int calculateInternalPosition(Playlist item, Track track) {
        int internalPos = 0;

        for (Track exploreItem : item.getTracks()) {
            if (exploreItem.equals(track))
                return internalPos;
            internalPos++;
        }

        return internalPos;
    }


    public void setOnTrackClickListener(OnChildItemClickListener onTrackClickListener) {
        this.onTrackClickListener = onTrackClickListener;
    }

    public void setOnTrackLongClickListener(OnChildItemLongClickListener onTrackLongClickListener) {
        this.onTrackLongClickListener = onTrackLongClickListener;
    }

    public void setOnMoreTrackItemClickListener(OnMoreChildItemClickListener onMoreTrackItemClickListener) {
        this.onMoreTrackItemClickListener = onMoreTrackItemClickListener;
    }

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

    public class TrackViewHolder extends ChildViewHolder {

        public LinearLayout rootLayout;
        public ImageView coverIv;
        public TextView nameTv;
        public TextView albumArtistTv;
        public ImageButton moreBtn;

        public TrackViewHolder(View itemView) {
            super(itemView);

            rootLayout = (LinearLayout) itemView.findViewById(R.id.rootLayout);
            coverIv = (ImageView) itemView.findViewById(R.id.coverIv);
            nameTv = (TextView) itemView.findViewById(R.id.trackTitleTv);
            albumArtistTv = (TextView) itemView.findViewById(R.id.trackAlbumArtistTv);
            moreBtn = (ImageButton) itemView.findViewById(R.id.moreBtn);
        }
    }
}
