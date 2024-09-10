package yuhan.hgcq.client.model.service;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import yuhan.hgcq.client.model.dto.album.AlbumDTO;

public interface AlbumService {

    @POST("/album/create")
    Call<ResponseBody> createAlbum(@Body AlbumDTO albumDTO);

    @POST("/album/delete")
    Call<ResponseBody> deleteAlbum(@Body AlbumDTO albumDTO);

    @GET("/album/list")
    Call<List<AlbumDTO>> albumList();

    @GET("/album/search")
    Call<List<AlbumDTO>> searchAlbum(@Query("name") String name);

    @POST("/album/update")
    Call<ResponseBody> updateAlbum(@Body AlbumDTO albumDTO);
}
