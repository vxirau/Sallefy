package com.prpr.androidpprog2.entregable.controller.restapi.service;

import com.prpr.androidpprog2.entregable.model.Search;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface SearchService {
    @GET("search")
    Call<Search> getSearch (@Header("Authorization") String token, @Query("keyword") String searchText);
}
