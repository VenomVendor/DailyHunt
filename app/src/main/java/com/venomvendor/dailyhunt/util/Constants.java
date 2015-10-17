package com.venomvendor.dailyhunt.util;

public class Constants {
    /**
     * Change this flag to <b>false</b> during release
     */
    public static final boolean DEBUG = true; // TODO - Set false
    public static final boolean RELEASE = !DEBUG;
    public static final int MAX_RETRY_COUNT = 3;

    public static class KEYS {
        public static final String LAST_JSON = "LAST_JSON";
        public static final String OPENED_TIMES_COUNT = "OPENED_TIMES_COUNT";
        public static final String POST_RETRY_COUNT = "POST_RETRY_COUNT";
        public static final String RETRY_COUNT = "RETRY_COUNT";
    }
}
