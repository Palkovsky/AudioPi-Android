package com.example.andrzej.audiocontroller.utils;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.widget.ImageButton;
import android.widget.ImageView;

public class Image {

    public static void setBackgroundDrawable(Context context, ImageView imageView, int id) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
            imageView.setBackgroundDrawable(getDrawable(context, id));
        else
            imageView.setBackground(getDrawable(context, id));
    }

    public static void setSourceDrawable(Context context, ImageView imageView, int redId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setImageDrawable(context.getResources().getDrawable(redId, context.getTheme()));
        } else {
            imageView.setImageDrawable(context.getResources().getDrawable(redId));
        }
    }

    public static void setSourceDrawable(Context context, ImageButton imageView, int redId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setImageDrawable(context.getResources().getDrawable(redId, context.getTheme()));
        } else {
            imageView.setImageDrawable(context.getResources().getDrawable(redId));
        }
    }

    public static void clearDrawable(ImageView imageView) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
            imageView.setBackgroundDrawable(null);
        else
            imageView.setBackground(null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            imageView.setImageDrawable(null);
        else
            imageView.setImageDrawable(null);

    }

    public static Drawable getDrawable(Context context, int id) {
        if (Build.VERSION.SDK_INT >= 21) {
            return ContextCompat.getDrawable(context, id);
        } else {
            return context.getResources().getDrawable(id);
        }
    }
}
