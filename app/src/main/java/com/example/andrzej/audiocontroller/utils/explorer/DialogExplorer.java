package com.example.andrzej.audiocontroller.utils.explorer;


import android.content.Context;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.handlers.ExploreManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DialogExplorer {

    private Context context;
    private ExploreManager exploreManager;
    private FileListener fileListener;

    private String[] whitelistedExtensions;

    public DialogExplorer(Context context, String defaultPath, FileListener fileListener) {
        this.context = context;
        this.fileListener = fileListener;
        exploreManager = new ExploreManager(defaultPath);
        exploreManager.goTo(defaultPath);
    }

    public void showDialogPath() {
        File currentPath = new File(exploreManager.currentPath());
        final List<FileItem> files = filterDataset(buildFileList(exploreManager.currentPath()));

        new MaterialDialog.Builder(context)
                .title(R.string.uploadFile)
                .content(currentPath.getAbsolutePath())
                .items(convertListToArray(files))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        File file = files.get(which).getFile();

                        if (file.isDirectory()) {
                            exploreManager.goTo(file.getAbsolutePath());
                            dialog.dismiss();
                            showDialogPath();
                        } else
                            fileListener.onFileReceive(file);
                    }
                })
                .show();
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
                        else if(i == whitelistedExtensions.length - 1)
                            dataset.remove(fileItem);
                    }
                }
            }
        }
        return dataset;
    }

    public void setWhitelistedExtensions(String[] whitelistedExtensions) {
        this.whitelistedExtensions = whitelistedExtensions;
    }

    public interface FileListener {
        void onFileReceive(File file);
    }
}
