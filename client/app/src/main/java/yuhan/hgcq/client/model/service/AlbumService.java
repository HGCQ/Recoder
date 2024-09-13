package yuhan.hgcq.client.model.service;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import yuhan.hgcq.client.model.dto.album.AlbumCreateForm;
import yuhan.hgcq.client.model.dto.album.AlbumDTO;
import yuhan.hgcq.client.model.dto.album.AlbumUpdateForm;

public interface AlbumService {

    @POST("/album/create")
    Call<ResponseBody> createAlbum(@Body AlbumCreateForm albumCreateForm);

    @POST("/album/delete")
    Call<ResponseBody> deleteAlbum(@Body AlbumDTO albumDTO);

    @POST("/album/delete/cancel")
    Call<ResponseBody> cancelDeleteAlbum(@Body AlbumDTO albumDTO);

    @POST("/album/update")
    Call<ResponseBody> updateAlbum(@Body AlbumUpdateForm albumUpdateForm);

    @GET("/album/list/teamId")
    Call<List<AlbumDTO>> albumList(@Query("teamId") Long teamId);

    @GET("/album/list/teamId/name")
    Call<List<AlbumDTO>> searchAlbum(@Query("teamId") Long teamId, @Query("name") String name);

    @GET("/album/list/teamId/trash")
    Call<List<AlbumDTO>> albumTrash(@Query("teamId") Long teamId);
}
