package com.venomvendor.dailyhunt.network;

import android.widget.Toast;

import com.venomvendor.dailyhunt.core.DHApplication;
import com.venomvendor.dailyhunt.model.ApiHits;
import com.venomvendor.dailyhunt.model.GetPosts;
import com.venomvendor.dailyhunt.util.Constants;

import de.greenrobot.event.EventBus;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static com.venomvendor.dailyhunt.util.Constants.API.API_HITS;
import static com.venomvendor.dailyhunt.util.Constants.API.LIST_NEWS;
import static com.venomvendor.dailyhunt.util.Constants.ResponseType.JSON;

public class NetworkHandler {
    private static NetworkHandler mInstance;

    public static NetworkHandler getInstance() {
        if (mInstance == null) {
            mInstance = new NetworkHandler();
        }
        return mInstance;
    }

    public EventBus getBus() {
        return EventBus.getDefault();
    }

    public void getPosts() {
        Call<GetPosts> call = Connection.getRestClient().getPosts(JSON, LIST_NEWS);
        call.enqueue(new Callback<GetPosts>() {
            @Override
            public void onResponse(Response<GetPosts> response, Retrofit retrofit) {
                getBus().post(response.body());
                if (Constants.DEBUG) {
                    String sentTime = response.headers().get("OkHttp-Sent-Millis");
                    String receivedTime = response.headers().get("OkHttp-Received-Millis");
                    double timeTaken = (Math.abs(Double.valueOf(sentTime) - Double.valueOf(receivedTime))) / 1000;
                    Toast.makeText(DHApplication.getContext(), "Got Posts in " + timeTaken + " secs", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                GetPosts failure = new GetPosts();
                failure.setStatus(Constants.ERROR);
                failure.setError(t.getLocalizedMessage());
                getBus().post(failure);
            }
        });
    }

    public void getApiCount() {
        Call<ApiHits> call = Connection.getRestClient().getApiHits(JSON, API_HITS);
        call.enqueue(new Callback<ApiHits>() {
            @Override
            public void onResponse(Response<ApiHits> response, Retrofit retrofit) {
                getBus().post(response.body());
                if (Constants.DEBUG) {
                    String sentTime = response.headers().get("OkHttp-Sent-Millis");
                    String receivedTime = response.headers().get("OkHttp-Received-Millis");
                    double timeTaken = (Math.abs(Double.valueOf(sentTime) - Double.valueOf(receivedTime))) / 1000;
                    Toast.makeText(DHApplication.getContext(), "Got Posts in " + timeTaken + " secs", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                ApiHits failure = new ApiHits();
                failure.setStatus(Constants.ERROR);
                failure.setError(t.getLocalizedMessage());
                getBus().post(failure);
            }
        });
    }
}
