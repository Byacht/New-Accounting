package com.example.dn.accounting.Api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dn on 2017/4/11.
 */

public class ApiManager {
    public static ApiManager apiManager;
    public DailySentenceApi dailySentenceApi;
    private Object monitor = new Object();

    public static ApiManager getInstance(){
        if (apiManager == null){
            synchronized (ApiManager.class) {
                if (apiManager == null) {
                    apiManager = new ApiManager();
                }
            }
        }
        return apiManager;
    }

    public DailySentenceApi getDailySentenceApi(){
        if (dailySentenceApi == null) {
            synchronized (monitor){
                if (dailySentenceApi == null) {
                    dailySentenceApi = new Retrofit.Builder()
                            .baseUrl("http://10.0.2.2:8080/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                            .create(DailySentenceApi.class);
                }
            }
        }
        return dailySentenceApi;
    }
}
