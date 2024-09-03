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
import yuhan.hgcq.client.model.dto.photo.PhotoDTO;

public interface PhotoService {

    @Multipart
    @POST("/photo/add")
    Call<ResponseBody> addPhoto(
            @Part("date") RequestBody date,
            @Part("imageName") RequestBody imageName,
            @Part MultipartBody.Part image);

    @POST("/photo/delete")
    Call<ResponseBody> deletePhoto(@Body PhotoDTO photoDTO);

    @GET("/photo/list")
    Call<List<String>> getPhotos(@Query("date") String eventDate);
}
