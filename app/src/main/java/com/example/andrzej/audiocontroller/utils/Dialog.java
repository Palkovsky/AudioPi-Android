package com.example.andrzej.audiocontroller.utils;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.interfaces.OnRemove;
import com.example.andrzej.audiocontroller.interfaces.OnSuccess;
import com.example.andrzej.audiocontroller.models.ExploreItem;
import com.example.andrzej.audiocontroller.models.Metadata;
import com.example.andrzej.audiocontroller.models.Playlist;
import com.example.andrzej.audiocontroller.models.Track;
import com.example.andrzej.audiocontroller.models.dbmodels.PlaylistDb;
import com.example.andrzej.audiocontroller.models.dbmodels.TrackDb;
import com.example.andrzej.audiocontroller.utils.network.Downloader;

import java.util.ArrayList;
import java.util.List;


public class Dialog {
    public static void showMetadataDialog(Context context, ExploreItem item) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(item.getName())
                .customView(R.layout.metadata_dialog_layout, true)
                .negativeText(R.string.back).build();


        View contentView = dialog.getCustomView();
        if (contentView != null) {
            Metadata metadata = item.getMetadata();

            String album = ((metadata.getAlbum() == null) ? "N/A" : metadata.getAlbum());
            String artist = ((metadata.getArtist() == null) ? "N/A" : metadata.getArtist());
            String genre = ((metadata.getGenre() == null) ? "N/A" : metadata.getGenre());
            String time = ((metadata.gerFormattedLength() == null) ? "N/A" : metadata.gerFormattedLength());
            double size = ((metadata.getFilesize() < 0) ? -1 : metadata.getFilesize());
            String sizeStr = String.format(context.getString(R.string.filesize_format), String.valueOf(size));

            ((TextView) contentView.findViewById(R.id.songTv)).setText(item.getFormattedName());
            ((TextView) contentView.findViewById(R.id.albumTv)).setText(album);
            ((TextView) contentView.findViewById(R.id.artistTv)).setText(artist);
            ((TextView) contentView.findViewById(R.id.genreTv)).setText(genre);
            ((TextView) contentView.findViewById(R.id.timeTv)).setText(time);
            ((TextView) contentView.findViewById(R.id.sizeTv)).setText(sizeStr);
            ((TextView) contentView.findViewById(R.id.pathTv)).setText(item.getPath());
        }

        dialog.show();
    }

    public static void showTrackExploreDialog(final Context context, final ExploreItem item, final OnSuccess onSuccess) {

        new MaterialDialog.Builder(context)
                .title(item.getName())
                .items(R.array.explore_file_dialog_items)
                .negativeText(R.string.back)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {

                        switch (i) {
                            case 0: //Download
                                Downloader.downloadFile(context, item);
                                break; //Add to playlist
                            case 1:
                                Dialog.showAddToPlaylistDialog(context, Converter.exploreItemToTrack(item), new OnSuccess() {
                                    @Override
                                    public void onSuccess() {
                                        if (onSuccess != null)
                                            onSuccess.onSuccess();
                                    }
                                });
                                break;
                            case 2: //Add to metadata
                                Dialog.showMetadataDialog(context, item);
                                break;
                        }
                    }
                }).show();
    }

    public static void showAddToPlaylistDialog(Context context, final Track track, final OnSuccess onSuccess) {

        final List<PlaylistDb> playlists = PlaylistDb.getAll();
        final TrackDb trackDb = Converter.standardToDb(track);

        CharSequence[] chars = new CharSequence[playlists.size()];

        for (int i = 0; i < playlists.size(); i++)
            chars[i] = playlists.get(i).name;

        new MaterialDialog.Builder(context)
                .title(R.string.add_to_playlist_title)
                .items(chars)
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        for (Integer pos : which) {
                            PlaylistDb playlistDb = playlists.get(pos);
                            trackDb.playlist = playlistDb;
                            trackDb.position = playlistDb.tracks().size();
                            trackDb.save();
                            onSuccess.onSuccess();
                        }
                        return true;
                    }
                })
                .positiveText(R.string.choose)
                .show();
    }

    public static void showTrackMediaDialog(final Context context, final Track track, final OnSuccess onSuccess, final OnRemove onRemove) {

        int items = R.array.explore_file_dialog_items;
        if (track.getPlaylist().isLocal())
            items = R.array.explore_file_dialog_items_local;

        new MaterialDialog.Builder(context)
                .title(track.getName())
                .items(items)
                .negativeText(R.string.back)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {

                        switch (i) {
                            case 0: //Download
                                Downloader.downloadFile(context, track);
                                break;
                            case 1:
                                if (track.getPlaylist().isLocal()) {
                                    TrackDb trackDb = TrackDb.load(TrackDb.class, track.getDbId());
                                    int pos = trackDb.position;
                                    trackDb.delete();
                                    DatabaseUtils.handleRemovedPositions(trackDb.playlist.getId(), pos);
                                    if (onRemove != null)
                                        onRemove.onRemove();
                                } else {
                                    Dialog.showAddToPlaylistDialog(context, track, new OnSuccess() {
                                        @Override
                                        public void onSuccess() {
                                            if (onSuccess != null)
                                                onSuccess.onSuccess();
                                        }
                                    });
                                }
                                break;
                            case 2: //Show metadata
                                Dialog.showMetadataDialog(context, track);
                                break;
                        }
                    }
                }).show();
    }
}
