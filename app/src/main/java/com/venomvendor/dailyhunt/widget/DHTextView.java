package com.venomvendor.dailyhunt.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.venomvendor.dailyhunt.R;
import com.venomvendor.dailyhunt.util.TypefaceCache;

public class DHTextView extends AppCompatTextView {
    public DHTextView(Context context) {
        super(context);
    }

    public DHTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCustom(context, attrs);
    }

    public DHTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCustom(context, attrs);
    }

    private void initCustom(Context context, AttributeSet attrs) {
        TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.DHTypeface);
        String fontName = styledAttrs.getString(R.styleable.DHTypeface_typeface);
        styledAttrs.recycle();
        if (fontName != null) {
            Typeface typeface = TypefaceCache.getInstance().getTypeface(context.getAssets(), fontName);
            this.setTypeface(typeface, typeface.getStyle());
        }
    }

}
