package com.example.nearby.network;

import java.util.UUID;

import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Single;

public interface RoomsApi {

    @POST("room/create")
    Single<UUID> createRoom(@Query("username") String username,
                            @Query("phoneInfo") String phoneInfo);
}
