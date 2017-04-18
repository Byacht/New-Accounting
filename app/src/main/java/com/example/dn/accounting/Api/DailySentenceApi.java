package com.example.dn.accounting.Api;

import com.example.dn.accounting.Model.DailySentence;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by dn on 2017/4/11.
 */

public interface DailySentenceApi {
    @GET("/web/dailysentence.json")
    Call<DailySentence> getDailySentence();
}
