package com.example.andrzej.audiocontroller.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class BlankingImageButton extends ImageButton {

    public BlankingImageButton(Context context) {
        super(context);
    }

    public BlankingImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BlankingImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        Drawable background = getBackground();
        Drawable drawable = getDrawable();

        if (enabled) {
            if (background != null)
                background.setColorFilter(null);
            if (drawable != null)
                drawable.setColorFilter(null);
        } else {
            if (background != null)
                background.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);
            if (drawable != null)
                drawable.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);
        }
    }
}