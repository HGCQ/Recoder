package yuhan.hgcq.client.controller;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import yuhan.hgcq.client.model.dto.photo.PhotoDTO;
import yuhan.hgcq.client.model.service.PhotoService;

public class PhotoController {

    private PhotoService photoService;
    private Context context; // 액티비티



    public void addPhoto(Uri photoUri, String photoName, String eventDate, Callback<ResponseBody> callback) {
        String filePath = getRealPathFromURI(photoUri);
        File file = new File(filePath);

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), reqFile);

        RequestBody datePart = RequestBody.create(MultipartBody.FORM, eventDate);
        RequestBody namePart = RequestBody.create(MultipartBody.FORM, photoName);

        Call<ResponseBody> call = photoService.addPhoto(datePart, namePart, body);
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


    /**
     * 사진 리스트 조회
     *
     * @param eventDate 이벤트 날짜
     * @param callback  콜백
     */
    public void getPhotos(String eventDate, Callback<List<String>> callback) {
        Call<List<String>> call = photoService.getPhotos(eventDate);
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
