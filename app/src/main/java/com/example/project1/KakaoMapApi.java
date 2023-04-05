package com.example.project1;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface KakaoMapApi {
    @Headers("Authorization: KakaoAK 0b289b1ef91f12a6ae8a369ddd779e6a")
    @GET("/v2/local/search/keyword.json")
    Call<SearchResult> searchPlace(@Query("query") String query);
}
