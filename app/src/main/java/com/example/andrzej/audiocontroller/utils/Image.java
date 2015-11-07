package com.example.andrzej.audiocontroller.utils;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

public class Image {

    public static void setDrawable(Context context, ImageView imageView, int id) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
            imageView.setBackgroundDrawable(getDrawable(context, id));
        else
            imageView.setBackground(getDrawable(context, id));

    }

    public static Drawable getDrawable(Context context, int id) {
        if (Build.VERSION.SDK_INT >= 21) {
            return ContextCompat.getDrawable(context, id);
        } else {
            return context.getResources().getDrawable(id);
        }
    }
}
