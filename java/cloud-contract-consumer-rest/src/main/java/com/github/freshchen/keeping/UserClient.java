package com.github.freshchen.keeping;

import com.github.freshchen.keeping.model.JsonResult;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

/**
 * @author darcy
 * @since 2020/12/12
 **/
public class UserClient {

    public static JsonResult createUser(User user) throws IOException {
        UserApi userApi = new Retrofit.Builder().baseUrl("http://127.0.0.1:8880")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UserApi.class);
        return userApi.create(user).execute().body();
    }
}
