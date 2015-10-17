package com.venomvendor.dailyhunt.util;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import com.venomvendor.dailyhunt.core.DHApplication;
import com.venomvendor.dailyhunt.util.Constants.KEYS;

public class DHHelper {
    private DHHelper() {
    }

    public static boolean hasRetriesLeft() {
        int retryCount = DHApplication.getSharedPreferences().getInt(KEYS.RETRY_COUNT, 0);
        return retryCount < Constants.MAX_RETRY_COUNT;
    }

    public static void incrementRetry() {
        int retryCount = DHApplication.getSharedPreferences().getInt(KEYS.RETRY_COUNT, 0);
        retryCount++;
        DHApplication.getSharedPreferences().putInt(KEYS.RETRY_COUNT, retryCount);
    }

    public static void removeRetry() {
        DHApplication.getSharedPreferences().remove(KEYS.RETRY_COUNT);
    }
    /**
     * Method to hide keyPad
     *
     * @param activity calling activity in which keypad need to hidden
     */
    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager
                    = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager
                    .hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
}
