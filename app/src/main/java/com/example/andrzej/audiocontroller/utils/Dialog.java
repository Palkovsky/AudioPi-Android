package com.example.andrzej.audiocontroller.utils;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.models.ExploreItem;
import com.example.andrzej.audiocontroller.models.Metadata;


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
}
