package yuhan.hgcq.client.controller;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import yuhan.hgcq.client.config.NetworkClient;
import yuhan.hgcq.client.model.dto.photo.MovePhotoForm;
import yuhan.hgcq.client.model.dto.photo.PhotoDTO;
import yuhan.hgcq.client.model.service.PhotoService;

public class PhotoController {

    private PhotoService photoService;
    private Context context; // 액티비티

    public PhotoController(Context context) {
        this.context = context;
        NetworkClient client = NetworkClient.getInstance(context.getApplicationContext());
        photoService = client.getPhotoService();
    }


    public void uploadPhoto(Long albumId, List<Uri> photoUris, List<LocalDateTime> creates, Callback<ResponseBody> callback) {
        RequestBody albumIdPart = RequestBody.create(
                MediaType.parse("text/plain"),
                String.valueOf(albumId)
        );

        List<MultipartBody.Part> fileParts = new ArrayList<>();
        for (Uri uri : photoUris) {
            String filePath = getRealPathFromURI(uri);
            File file = new File(filePath);
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part image = MultipartBody.Part.createFormData("image", file.getName(), reqFile);
            fileParts.add(image);
        }

        List<RequestBody> createParts = new ArrayList<>();
        for (LocalDateTime createTime : creates) {
            RequestBody createPart = RequestBody.create(
                    MediaType.parse("text/plain"),
                    createTime.toString()
            );
            createParts.add(createPart);
        }

        Call<ResponseBody> call = photoService.uploadPhoto(albumIdPart, fileParts, createParts);
        call.enqueue(callback);
    }

    /**
     * 사진 삭제
     *
     * @param photoDTO 사진 객체
     * @param callback 콜백
     */
    public void deletePhoto(PhotoDTO photoDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = photoService.deletePhoto(photoDTO);
        call.enqueue(callback);
    }

    public void cancelDeletePhoto(PhotoDTO photoDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = photoService.cancelDeletePhoto(photoDTO);
        call.enqueue(callback);
    }

    public void movePhoto(MovePhotoForm movePhotoForm, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = photoService.movePhoto(movePhotoForm);
        call.enqueue(callback);
    }

    public void autosavePhoto(List<Uri> photoUris, List<Long> albumIds, List<LocalDateTime> creates, Callback<ResponseBody> callback) {
        List<RequestBody> albumIdParts = new ArrayList<>();
        for (Long albumId : albumIds) {
            RequestBody albumIdPart = RequestBody.create(
                    MediaType.parse("text/plain"),
                    String.valueOf(albumId)
            );
            albumIdParts.add(albumIdPart);
        }
        List<MultipartBody.Part> fileParts = new ArrayList<>();
        for (Uri uri : photoUris) {
            String filePath = getRealPathFromURI(uri);
            File file = new File(filePath);
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part image = MultipartBody.Part.createFormData("image", file.getName(), reqFile);
            fileParts.add(image);
        }

        List<RequestBody> createParts = new ArrayList<>();
        for (LocalDateTime createTime : creates) {
            RequestBody createPart = RequestBody.create(
                    MediaType.parse("text/plain"),
                    createTime.toString()
            );
            createParts.add(createPart);
        }

        Call<ResponseBody> call = photoService.autosavePhoto(fileParts, albumIdParts, createParts);
        call.enqueue(callback);
    }

    /**
     * 사진 리스트 조회
     *
     *
     * @param callback  콜백
     */
    public void getPhotos(Long albumId, Callback<List<PhotoDTO>> callback) {
        Call<List<PhotoDTO>> call = photoService.getPhotos(albumId);
        call.enqueue(callback);
    }

    public void trashPhotos(Long albumId, Callback<List<PhotoDTO>> callback) {
        Call<List<PhotoDTO>> call = photoService.trashPhotos(albumId);
        call.enqueue(callback);
    }



    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

}
