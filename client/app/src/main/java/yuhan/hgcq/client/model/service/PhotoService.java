package yuhan.hgcq.client.model.service;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import yuhan.hgcq.client.model.dto.photo.MovePhotoForm;
import yuhan.hgcq.client.model.dto.photo.PhotoDTO;

public interface PhotoService {

    @Multipart
    @POST("/photo/upload")
    Call<ResponseBody> uploadPhoto(
            @Part("albumId") RequestBody albumId,
            @Part List<MultipartBody.Part> files,
            @Part("creates") List<RequestBody> creates
    );

    @POST("/photo/delete")
    Call<ResponseBody> deletePhoto(@Body PhotoDTO photoDTO);

    @POST("/photo/delete/cancel")
    Call<ResponseBody> cancelDeletePhoto(@Body PhotoDTO photoDTO);

    @POST("/photo/move")
    Call<ResponseBody> moveAlbumPhoto(@Body MovePhotoForm movePhotoForm);

    @Multipart
    @POST("/photo/autosave")
    Call<ResponseBody> autoSavePhoto(
            @Part List<MultipartBody.Part> files,
            @Part("teamId") RequestBody teamId,
            @Part("creates") List<RequestBody> creates
    );

    @GET("/photo/list/albumId")
    Call<List<PhotoDTO>> photoList(@Query("albumId") Long albumId);

    @GET("/photo/list/albumId/trash")
    Call<List<PhotoDTO>> photoTrashList(@Query("albumId") Long albumId);
}
