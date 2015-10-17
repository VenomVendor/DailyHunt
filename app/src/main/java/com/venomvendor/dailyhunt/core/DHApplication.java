package com.venomvendor.dailyhunt.core;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import com.venomvendor.dailyhunt.R;
import com.venomvendor.dailyhunt.util.Constants;
import com.venomvendor.dailyhunt.util.DHHelper;

import java.lang.reflect.Field;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import vee.android.lib.SimpleSharedPreferences;

public class DHApplication extends Application {
    private static final String TYPEFACE_TYPE = "SERIF";
    private static Context mContext;
    private static SimpleSharedPreferences mPreferences;
    private static DHApplication singleton;

    public static synchronized Context getContext() {
        return mContext;
    }

    public static synchronized SimpleSharedPreferences getSharedPreferences() {
        if (null == mPreferences) {
            mPreferences = new SimpleSharedPreferences(mContext);
        }
        return mPreferences;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        mContext = getApplicationContext();
        FontsOverride.setDefaultFont(getApplicationContext(), getString(R.string.font_roboto_condensed)); //TODO
        EventBus.builder()
                .logNoSubscriberMessages(Constants.DEBUG)
                .sendNoSubscriberEvent(Constants.DEBUG)
                .throwSubscriberException(Constants.DEBUG)
                .installDefaultEventBus();
        DHHelper.removeRetry();
    }

    public DHApplication getInstance() {
        return singleton;
    }

    public static class FontsOverride {

        private static final String TAG = "FontsOverride";

        public static void setDefaultFont(Context context, String font) {
            try {
                final Typeface regular = Typeface.createFromAsset(context.getAssets(), String
                        .format(Locale.ENGLISH, "fonts/%s", font));
                replaceFont(regular);
            } catch (NoSuchFieldException e) {
                Log.d(TAG, e.getLocalizedMessage(), e);
            } catch (IllegalAccessException e) {
                Log.d(TAG, e.getLocalizedMessage(), e);
            } catch (RuntimeException e) {
                Log.d(TAG, e.getLocalizedMessage(), e);
            }
        }

        protected static void replaceFont(final Typeface newTypeface) throws
                NoSuchFieldException, IllegalAccessException {
            final Field field = Typeface.class.getDeclaredField(TYPEFACE_TYPE);
            field.setAccessible(true);
            field.set(null, newTypeface);
        }

    }
}
