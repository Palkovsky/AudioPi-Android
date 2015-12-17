package com.example.andrzej.audiocontroller.utils.explorer;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.handlers.ExploreManager;
import com.example.andrzej.audiocontroller.interfaces.OnItemClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DialogExplorer implements OnItemClickListener {

    private Context context;
    private ExploreManager exploreManager;
    private FileListener fileListener;

    private DialogPickerAdapter mAdapter;
    private MaterialDialog dialog;

    List<FileItem> files;

    private String[] whitelistedExtensions;

    public DialogExplorer(Context context, String defaultPath, FileListener fileListener) {
        this.context = context;
        this.fileListener = fileListener;
        files = new ArrayList<>();
        mAdapter = new DialogPickerAdapter(context, files);
        mAdapter.setOnItemClickListener(this);
        exploreManager = new ExploreManager(defaultPath);
        exploreManager.goTo(defaultPath);
    }

    @Override
    public void onItemClick(View v, int position) {
        //Select specified position
        if (position < files.size()) {
            FileItem fileItem = files.get(position);
            if (fileItem.getFile().isDirectory()) {
                exploreManager.goTo(fileItem.getFile().getAbsolutePath());
                if(dialog != null)
                    dialog.dismiss();
                showDialogPath();
            }
        }
    }

    public void showDialogPath() {
        restart();
        mAdapter = null;
        files = filterDataset(buildFileList(exploreManager.currentPath()));
        mAdapter = new DialogPickerAdapter(context, files);
        mAdapter.setOnItemClickListener(this);

        dialog = new MaterialDialog.Builder(context)
                .title(R.string.uploadFile)
                .customView(R.layout.dialog_view, false)
                .positiveText(R.string.add)
                .autoDismiss(true)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        List<FileItem> selectedItems = mAdapter.selectedItems();
                        if (selectedItems.size() > 0)
                            fileListener.onFilesReceive(fileItemsToFiles(selectedItems));
                    }
                })
                .build();


        RecyclerView mRecycler = (RecyclerView) dialog.getCustomView();
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        mRecycler.setAdapter(mAdapter);

        if (files.size() > 0)
            dialog.show();
    }

    private List<File> fileItemsToFiles(List<FileItem> fileItems) {
        List<File> files = new ArrayList<>();
        for (FileItem fileItem : fileItems)
            files.add(fileItem.getFile());
        return files;
    }

    private List<FileItem> buildFileList(String path) {
        List<FileItem> files = new ArrayList<>();
        File f = new File(path);

        if (!f.getAbsolutePath().equals("/"))
            files.add(new FileItem("...", f.getParentFile()));

        if (f.listFiles() != null) {
            for (File file : f.listFiles()) {
                files.add(new FileItem(file.getName(), file));
            }
        }

        return files;
    }

    private CharSequence[] convertListToArray(List<FileItem> fileItems) {
        List<String> stringList = new ArrayList<>();

        for (FileItem item : fileItems)
            stringList.add(item.getName());

        return stringList.toArray(new CharSequence[stringList.size()]);
    }

    private List<FileItem> filterDataset(List<FileItem> dataset) {
        if (whitelistedExtensions != null) {
            List<FileItem> filtered = new ArrayList<>(dataset);
            for (FileItem fileItem : filtered) {
                File file = fileItem.getFile();
                if (file.isFile()) {
                    for (int i = 0; i < whitelistedExtensions.length; i++) {
                        String filenameArray[] = file.getName().split("\\.");
                        String ext = filenameArray[filenameArray.length - 1];
                        if (whitelistedExtensions[i].equals(ext))
                            break;
                        else if (i == whitelistedExtensions.length - 1)
                            dataset.remove(fileItem);
                    }
                }
            }
        }
        return dataset;
    }

    private void restart() {
        files.clear();
        mAdapter.notifyDataSetChanged();
    }

    public void setWhitelistedExtensions(String[] whitelistedExtensions) {
        this.whitelistedExtensions = whitelistedExtensions;
    }

    public interface FileListener {
        void onFilesReceive(List<File> files);
    }
}
