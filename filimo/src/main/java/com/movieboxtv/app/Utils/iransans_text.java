package com.movieboxtv.app.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;



@SuppressLint("AppCompatCustomView")
public class iransans_text extends TextView {
    public iransans_text(Context context, AttributeSet attributeSet)
    {

        super(context,attributeSet);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/yekan.ttf"));


    }
}
