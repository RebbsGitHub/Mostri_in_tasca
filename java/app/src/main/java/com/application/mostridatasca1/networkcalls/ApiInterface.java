package com.application.mostridatasca1.networkcalls;

import com.application.mostridatasca1.database.playerdb.User;
import com.application.mostridatasca1.database.simplevirtualobjdb.SimpleVirtualObj;
import com.application.mostridatasca1.database.virtualobjdb.VirtualObj;
import com.application.mostridatasca1.ui.interactionFragment.InteractionData;
import com.application.mostridatasca1.ui.interactionFragment.InteractionDataRequest;
import com.application.mostridatasca1.ui.rankedlist.SimpleUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @POST("users/")
    Call<SignUp> register();

    @GET("users/{uid}")
    Call<User> getUserByID(@Path("uid") int uid, @Query("sid") String sid);

    @GET("objects/")
    Call<List<SimpleVirtualObj>> getNearObjects(@Query("sid") String sid, @Query("lat") double lat, @Query("lon") double lon);

    @PATCH("users/{uid}")
    Call<Void> updateUser(@Path("uid") int uid, @Body UserUpdate userUpdate);

    @PATCH("users/{uid}")
    Call<Void> updatePositionShare(@Path("uid") int uid, @Body UpdatePositionShare updatePositionShare);

    @GET("objects/{id}")
    Call<VirtualObj> getVirtualObjectByID(@Path("id") int id, @Query("sid") String sid);

    @GET("ranking/")
    Call<List<SimpleUser>> getRanking(@Query("sid") String sid);

    @POST("objects/{id}/activate")
    Call<InteractionData> activateObjectID(@Path("id") int id, @Body InteractionDataRequest interactionDataRequest);

    @GET("users/")
    Call<List<SimpleUser>> getPlayersOnMap(@Query("sid") String sid, @Query("lat") double lat, @Query("lon") double lon);
}
