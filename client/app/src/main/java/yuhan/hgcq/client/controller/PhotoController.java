package yuhan.hgcq.client.controller;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import yuhan.hgcq.client.config.NetworkClient;
import yuhan.hgcq.client.model.dto.photo.DeleteCancelPhotoForm;
import yuhan.hgcq.client.model.dto.photo.MovePhotoForm;
import yuhan.hgcq.client.model.dto.photo.PhotoDTO;
import yuhan.hgcq.client.model.service.PhotoService;

public class PhotoController {

    private PhotoService photoService;
    private Context context;

    public PhotoController(Context context) {
        this.context = context;
        NetworkClient client = NetworkClient.getInstance(context.getApplicationContext());
        photoService = client.getPhotoService();
    }

    /**
     * 사진 업로드
     *
     * @param albumId   앨범 id
     * @param photoUris 사진 uri
     * @param creates   사진 날짜
     * @param callback  비동기 콜백
     */
    public void uploadPhoto(Long albumId, List<Uri> photoUris, List<LocalDateTime> creates, Callback<ResponseBody> callback) {
        RequestBody albumIdPart = RequestBody.create(
                MediaType.parse("text/plain"),
                String.valueOf(albumId)
        );

        List<MultipartBody.Part> fileParts = new ArrayList<>();
        for (Uri uri : photoUris) {
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                byte[] imageBytes = IOUtils.toByteArray(inputStream); // Apache commons-io 사용
                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), imageBytes);
                MultipartBody.Part image = MultipartBody.Part.createFormData("files", getFileNameFromUri(uri), reqFile);
                fileParts.add(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
     * @param photoDTO 사진 DTO
     * @param callback 비동기 콜백
     */
    public void deletePhoto(PhotoDTO photoDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = photoService.deletePhoto(photoDTO);
        call.enqueue(callback);
    }

    /**
     * 사진 삭제 취소
     *
     * @param form 사진 id 리스트
     * @param callback 비동기 콜백
     */
    public void cancelDeletePhoto(DeleteCancelPhotoForm form, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = photoService.cancelDeletePhoto(form);
        call.enqueue(callback);
    }

    /**
     * 앨범 이동
     *
     * @param movePhotoForm 앨범 이동 폼
     * @param callback      비동기 콜백
     */
    public void moveAlbumPhoto(MovePhotoForm movePhotoForm, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = photoService.moveAlbumPhoto(movePhotoForm);
        call.enqueue(callback);
    }

    /**
     * 사진 자동 저장
     *
     * @param photoUris 사진 uri
     * @param teamId    팀 id
     * @param creates   사진 날짜
     * @param callback  비동기 콜백
     */
    public void autoSavePhoto(List<Uri> photoUris, Long teamId, List<LocalDateTime> creates, Callback<ResponseBody> callback) {
        RequestBody teamIdPart = RequestBody.create(
                MediaType.parse("text/plain"),
                String.valueOf(teamId)
        );

        List<MultipartBody.Part> fileParts = new ArrayList<>();
        for (Uri uri : photoUris) {
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                byte[] imageBytes = IOUtils.toByteArray(inputStream); // Apache commons-io 사용
                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), imageBytes);
                MultipartBody.Part image = MultipartBody.Part.createFormData("files", getFileNameFromUri(uri), reqFile);
                fileParts.add(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        List<RequestBody> createParts = new ArrayList<>();
        for (LocalDateTime createTime : creates) {
            RequestBody createPart = RequestBody.create(
                    MediaType.parse("text/plain"),
                    createTime.toString()
            );
            createParts.add(createPart);
        }

        Call<ResponseBody> call = photoService.autoSavePhoto(fileParts, teamIdPart, createParts);
        call.enqueue(callback);
    }

    /**
     * 갤러리 리스트
     *
     * @param albumId  앨범 id
     * @param callback 비동기 콜백
     */
    public void galleryList(Long albumId, Callback<Map<String, List<PhotoDTO>>> callback) {
        Call<Map<String, List<PhotoDTO>>> call = photoService.galleryList(albumId);
        call.enqueue(callback);
    }

    /**
     * 사진 리스트
     *
     * @param albumId  앨범 id
     * @param callback 비동기 콜백
     */
    public void photoList(Long albumId, Callback<List<PhotoDTO>> callback) {
        Call<List<PhotoDTO>> call = photoService.photoList(albumId);
        call.enqueue(callback);
    }

    /**
     * 사진 휴지통 리스트
     *
     * @param albumId  앨범 id
     * @param callback 비동기 콜백
     */
    public void photoTrashList(Long albumId, Callback<List<PhotoDTO>> callback) {
        Call<List<PhotoDTO>> call = photoService.photoTrashList(albumId);
        call.enqueue(callback);
    }

    private String getFileNameFromUri(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DISPLAY_NAME };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
            cursor.close();
            return fileName;
        }
        return "unknown_file_name";
    }
}
