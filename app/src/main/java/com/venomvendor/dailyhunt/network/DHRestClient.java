package com.venomvendor.dailyhunt.network;

import com.venomvendor.dailyhunt.model.ApiHits;
import com.venomvendor.dailyhunt.model.GetPosts;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface DHRestClient {
    //type=json&query=list_news
    public static final String TYPE = "type";
    static final String QUERY = "query";

    /**
     * @return {@link GetPosts}
     */
    @GET
    Call<GetPosts> getPosts(@Query(TYPE) String type, @Query(QUERY) String query);

    /**
     * @return {@link GetPosts}
     */
    @GET
    Call<ApiHits> getApiHits(@Query(TYPE) String type, @Query(QUERY) String query);

}
