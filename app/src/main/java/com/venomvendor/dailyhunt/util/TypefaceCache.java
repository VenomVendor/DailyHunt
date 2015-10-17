package com.venomvendor.dailyhunt.util;

import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TypefaceCache {

    private static final TypefaceCache instance = new TypefaceCache();
    private static final Map<String, Typeface> mCache = new HashMap<>();

    public static TypefaceCache getInstance() {
        return instance;
    }

    /**
     * @return typeface
     * @throws RuntimeException If font not found in path.
     */
    public Typeface getTypeface(AssetManager assetManager, String fontName) {
        if (mCache.containsKey(fontName)) {
            return mCache.get(fontName);
        } else {
            Typeface typeface = new WeakReference<>(Typeface.createFromAsset(assetManager, String
                    .format(Locale.US, "fonts/%s", fontName))).get();
            mCache.put(fontName, typeface);
            return typeface;
        }
    }
}
