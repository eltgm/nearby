package com.example.nearby.network;

import com.example.nearby.models.Room;

import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Single;

public interface AdminApi {

    @POST("admin/{roomId}")
    Single<Room> activateRoom(@Path("roomId") String roomId, @Query("userId") String userId);

    @DELETE("admin/{roomId}")
    Single<Room> deleteRoom(@Path("roomId") String roomId, @Query("userId") String userId);
}
