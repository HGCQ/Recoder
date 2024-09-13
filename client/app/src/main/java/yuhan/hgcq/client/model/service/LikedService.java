package yuhan.hgcq.client.model.service;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import yuhan.hgcq.client.model.dto.photo.LikedDTO;
import yuhan.hgcq.client.model.dto.photo.PhotoDTO;

public interface LikedService {
    @POST("/liked/add")
    Call<ResponseBody> addLiked(@Body LikedDTO likedDTO);

    @POST("/liked/delete")
    Call<ResponseBody> deleteLiked(@Body LikedDTO likedDTO);

    @GET("/liked/list")
    Call<List<PhotoDTO>> likedList();
}
