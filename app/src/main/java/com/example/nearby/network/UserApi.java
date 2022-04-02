package com.example.nearby.network;

import com.example.nearby.models.Coordinates;
import com.example.nearby.models.Room;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApi {

    @POST("user/room")
    Observable<Room> createRoom(@Query("userId") String userId);

    @GET("user/room/{roomId}/enter")
    Observable<Room> enterRoom(@Path("roomId") String roomId, @Query("userId") String userId);

    @GET("user/room/{roomId}/leave")
    Observable<Room> leaveRoom(@Path("roomId") String roomId, @Query("userId") String userId);

    @POST("user/room/{roomId}/update")
    Observable<Room> updateCoordinates(@Path("roomId") String roomId, @Query("userId") String userId, @Body Coordinates coordinates);
}
