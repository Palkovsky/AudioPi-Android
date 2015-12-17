package com.example.andrzej.audiocontroller.utils.explorer;


import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.interfaces.OnItemClickListener;
import com.example.andrzej.audiocontroller.utils.Image;

import java.util.ArrayList;
import java.util.List;

public class DialogPickerAdapter extends RecyclerView.Adapter<DialogPickerAdapter.DialogItemViewHolder> {

    private Context context;
    private List<FileItem> mDataset;

    private OnItemClickListener itemClickListener;

    public DialogPickerAdapter(Context context, List<FileItem> mDataset) {
        this.context = context;
        this.mDataset = mDataset;
    }


    @Override
    public DialogItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_recycler_item, null);

        return new DialogItemViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(DialogItemViewHolder holder, int position) {
        FileItem item = mDataset.get(position);

        if (item.getFile().isDirectory())
            holder.selectChb.setVisibility(View.GONE);
        else {
            holder.selectChb.setChecked(item.isSelected());
            holder.selectChb.setVisibility(View.VISIBLE);
        }


        switch (item.getType()) {
            case FileItem.DIRECTORY:
                Image.setSourceDrawable(context, holder.iconIv, R.drawable.ic_folder_black_36dp);
                break;
            case FileItem.AUDIO:
                Image.setSourceDrawable(context, holder.iconIv, R.drawable.ic_audiotrack_black_36dp);
                break;
            case FileItem.VIDEO:
                Image.setSourceDrawable(context, holder.iconIv, R.drawable.ic_video_library_black_36dp);
                break;
            case FileItem.GRAPHIC:
                Image.setSourceDrawable(context, holder.iconIv, R.drawable.ic_collections_black_36dp);
                break;
            case FileItem.ARCHIVE:
                Image.setSourceDrawable(context, holder.iconIv, R.drawable.ic_archive_black_36dp);
                break;
            case FileItem.TEXT:
                Image.setSourceDrawable(context, holder.iconIv, R.drawable.ic_receipt_black_36dp);
                break;
        }


        holder.filenameTv.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void selectItem(final int position) {
        FileItem fileItem = mDataset.get(position);
        if (fileItem.getFile().isFile()) {
            fileItem.setSelected(!fileItem.isSelected());

            Handler handler = new Handler();

            final Runnable r = new Runnable() {
                public void run() {
                    notifyItemChanged(position);
                }
            };

            handler.post(r);
        }
    }

    private void selectItem(final int position, boolean checked){
        FileItem fileItem = mDataset.get(position);
        if (fileItem.getFile().isFile()) {
            fileItem.setSelected(checked);
            Handler handler = new Handler();

            final Runnable r = new Runnable() {
                public void run() {
                    notifyItemChanged(position);
                }
            };

            handler.post(r);
        }
    }

    public List<FileItem> selectedItems() {
        List<FileItem> selectedItems = new ArrayList<>();
        for (FileItem item : mDataset) {
            if (item.isSelected())
                selectedItems.add(item);
        }
        return selectedItems;
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class DialogItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        LinearLayout rootLayout;
        ImageView iconIv;
        TextView filenameTv;
        CheckBox selectChb;

        public DialogItemViewHolder(View itemView) {
            super(itemView);

            rootLayout = (LinearLayout) itemView.findViewById(R.id.rootLayout);
            iconIv = (ImageView) itemView.findViewById(R.id.iconIv);
            filenameTv = (TextView) itemView.findViewById(R.id.filenameTv);
            selectChb = (CheckBox) itemView.findViewById(R.id.selectChk);

            rootLayout.setOnClickListener(this);
            selectChb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    selectItem(getAdapterPosition(), isChecked);
                }
            });
        }

        @Override
        public void onClick(View v) {
            selectItem(getAdapterPosition());
            if (itemClickListener != null)
                itemClickListener.onItemClick(v, getAdapterPosition());
        }
    }
}
