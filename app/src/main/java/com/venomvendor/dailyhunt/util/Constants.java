package com.venomvendor.dailyhunt.util;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Constants {
    /**
     * Change this flag to <b>false</b> during release
     */
    public static final boolean DEBUG = true; // TODO - Set false
    public static final boolean RELEASE = !DEBUG;

    public static final int MAX_RETRY_COUNT = 3;
    public static final String END_POINT = "https://dailyhunt.0x10.info/api/";
    public static final String PATH = "dailyhunt";
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ResponseType.JSON, ResponseType.XML})
    public @interface ResponseTypes {
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({API.LIST_NEWS, API.API_HITS})
    public @interface QueryTypes {
    }

    public static class KEYS {
        public static final String LAST_JSON = "LAST_JSON";
        public static final String OPENED_TIMES_COUNT = "OPENED_TIMES_COUNT";
        public static final String POST_RETRY_COUNT = "POST_RETRY_COUNT";
        public static final String RETRY_COUNT = "RETRY_COUNT";
    }

    public static class ResponseType {
        public static final String JSON = "json";
        private static final String XML = "xml";
    }

    public static class API {
        public static final String LIST_NEWS = "list_news";
        public static final String API_HITS = "api_hits";
    }

}
