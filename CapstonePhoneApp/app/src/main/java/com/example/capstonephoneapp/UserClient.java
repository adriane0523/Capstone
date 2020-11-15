package com.example.capstonephoneapp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserClient {

   @POST("user")
   Call<User> createAccount(@Body User user);
}
