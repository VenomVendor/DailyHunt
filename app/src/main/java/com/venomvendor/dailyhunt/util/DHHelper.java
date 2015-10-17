package com.venomvendor.dailyhunt.util;

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
}
