package com.venuskimblessing.youtuberepeatlite.Retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitService  {
    @GET("search")
    Call<String> getYoutubeSearch(@Query("part") String part, @Query("q") String q, @Query("maxResults") String maxResults, @Query("order") String order, @Query("pageToken") String pageToken, @Query("key") String key);

    @GET("videos")
    Call<String> getYoutubeVideos(@Query("part") String part, @Query("id") String id, @Query("key") String key);
}
