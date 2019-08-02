package com.example.ccw.e_wasterecycling;

import com.example.ccw.e_wasterecycling.Notification.MyResponse;
import com.example.ccw.e_wasterecycling.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAMF6bR0Q:APA91bF5tpDKC1MJREYpdNRjWzE3RW2EgY0-3LwhQEVneuDc5QdFbUhs2sRH4GFlZLDTuY28yOyjKICr0iUglrwDlsZRvj63-pYrxKdreVzJhEh2m3-en83Af3Ogz38JaKNmOzCCHgN_"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
