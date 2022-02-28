package com.example.nearby.network;

import com.example.nearby.models.Room;

import io.reactivex.Observable;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AdminApi {

    @POST("admin/{roomId}")
    Observable<Room> activateRoom(@Path("roomId") String roomId, @Query("userId") String userId);

    @DELETE("admin/{roomId}")
    Observable<Room> deleteRoom(@Path("roomId") String roomId, @Query("userId") String userId);
}
