package com.example.nearby.network;

import com.example.nearby.models.Coordinates;
import com.example.nearby.models.Room;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;
import rx.Single;

public interface UserApi {

    @POST("user/room")
    Single<Room> createRoom(@Query("userId") String userId);

    @GET("user/room")
    Observable<List<Room>> getRooms(@Query("userId") String userId);

    @GET("user/room/{roomId}/enter")
    Single<Room> createRoom(@Path("roomId") String roomId, @Query("userId") String userId);

    @GET("user/room/{roomId}/leave")
    Single<Room> leaveRoom(@Path("roomId") String roomId, @Query("userId") String userId);

    @POST("user/room/{roomId}/update")
    Single<Room> updateCoordinates(@Path("roomId") String roomId, @Query("userId") String userId, @Body Coordinates coordinates);
}
