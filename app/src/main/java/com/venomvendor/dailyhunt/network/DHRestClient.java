package com.venomvendor.dailyhunt.network;

import com.venomvendor.dailyhunt.model.ApiHits;
import com.venomvendor.dailyhunt.model.GetPosts;
import com.venomvendor.dailyhunt.util.Constants;
import com.venomvendor.dailyhunt.util.Constants.QueryTypes;
import com.venomvendor.dailyhunt.util.Constants.ResponseTypes;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface DHRestClient {
    String TYPE = "type";
    String QUERY = "query";

    /**
     * @return {@link GetPosts}
     */
    @GET(Constants.PATH)
    Call<GetPosts> getPosts(@ResponseTypes @Query(TYPE) String type,
                            @QueryTypes @Query(QUERY) String query);

    /**
     * @return {@link ApiHits}
     */
    @GET(Constants.PATH)
    Call<ApiHits> getApiHits(@ResponseTypes @Query(TYPE) String type,
                             @QueryTypes @Query(QUERY) String query);

}
