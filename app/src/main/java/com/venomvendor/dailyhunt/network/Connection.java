package com.venomvendor.dailyhunt.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.venomvendor.dailyhunt.core.DHApplication;
import com.venomvendor.dailyhunt.util.Constants;

import java.io.IOException;

import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;

public class Connection {

    private static ConnectivityManager cm;
    private static Retrofit mRetrofit;
    private static DHRestClient mRestClient;
    private static ObjectMapper mObjectMapper = new ObjectMapper();

    public static boolean isAvail() {
        if (cm == null) {
            cm = (ConnectivityManager) DHApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }

    public static Retrofit getRetrofit() {
        // Create REST adapter.
        if (mRetrofit == null) {
            final Retrofit.Builder retrofitBuilder = new Retrofit.Builder();

            if (Constants.DEBUG) {
                OkHttpClient client = new OkHttpClient();
                client.interceptors().add(new LoggingInterceptor());
                retrofitBuilder.client(client);
            }

            retrofitBuilder.baseUrl(Constants.END_POINT);
            retrofitBuilder.addConverterFactory(JacksonConverterFactory.create(getObjectMapper()));
            mRetrofit = retrofitBuilder.build();
        }

        return mRetrofit;
    }

    public static DHRestClient getRestClient() {
        if (mRestClient == null) {
            mRestClient = getRetrofit().create(DHRestClient.class);
        }
        return mRestClient;
    }

    private static ObjectMapper getObjectMapper() {
        mObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mObjectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        mObjectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        mObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mObjectMapper;
    }

    private static class LoggingInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Log.i("LoggingInterceptor", "inside intercept callback");
            Request request = chain.request();

            long t1 = System.nanoTime();
            String requestLog = String.format("Sending request to %s with body %n%s and headers %n%s",
                    request.url(), request.body(), request.headers());
            Log.d("LoggingInterceptor", requestLog);
            Log.d("LoggingInterceptor", "...");
            Response response = chain.proceed(request);
            long t2 = System.nanoTime();
            String sentTime = response.headers().get("OkHttp-Sent-Millis");
            String receivedTime = response.headers().get("OkHttp-Received-Millis");

            double timeTaken = Math.abs(Double.valueOf(sentTime) - Double.valueOf(receivedTime));
            String bodyString = response.body().string();

            String responseLog = String.format("Received response for %s in %.1fms but %sms %n %n %s %n %n %s",
                    response.request().url(), (t2 - t1) / 1e6d, timeTaken, bodyString, response.headers());

            Log.d("LoggingInterceptor", responseLog);

            return response.newBuilder()
                    .body(ResponseBody.create(response.body().contentType(), bodyString))
                    .build();
        }
    }
}
