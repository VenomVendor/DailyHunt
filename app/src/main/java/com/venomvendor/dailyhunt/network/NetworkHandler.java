package com.venomvendor.dailyhunt.network;

import android.widget.Toast;

import com.venomvendor.dailyhunt.core.DHApplication;
import com.venomvendor.dailyhunt.model.GetPosts;
import com.venomvendor.dailyhunt.util.Constants;

import de.greenrobot.event.EventBus;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

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

        Call<GetPosts> call = Connection.getRestClient().getPosts(Constants.RESPONSE_TYPE, Constants.LIST_NEWS);

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
}
