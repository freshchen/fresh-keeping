package com.github.freshchen.keeping;

import com.github.freshchen.keeping.model.JsonResult;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserApi {

    @POST("/user")
    Call<JsonResult> create(@Body User user);
}
