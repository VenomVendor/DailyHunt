package com.venomvendor.dailyhunt.util;

public class Constants {
    /**
     * Change this flag to <b>false</b> during release
     */
    public static final boolean DEBUG = true; // TODO - Set false
    public static final boolean RELEASE = !DEBUG;

    public static final int MAX_RETRY_COUNT = 3;

    public static final String END_POINT = "https://dailyhunt.0x10.info/api/dailyhunt";
    public static final String RESPONSE_TYPE = "json";
    public static final String LIST_NEWS = "list_news";
    public static final String API_HITS = "api_hits";
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    public static class KEYS {
        public static final String LAST_JSON = "LAST_JSON";
        public static final String OPENED_TIMES_COUNT = "OPENED_TIMES_COUNT";
        public static final String POST_RETRY_COUNT = "POST_RETRY_COUNT";
        public static final String RETRY_COUNT = "RETRY_COUNT";
    }
}
