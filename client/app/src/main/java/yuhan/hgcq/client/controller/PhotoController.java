package yuhan.hgcq.client.controller;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    public void uploadPhoto(Long albumId, List<Uri> photoUris, List<LocalDateTime> creates, List<String> regions, Callback<ResponseBody> callback) {
        RequestBody albumIdPart = RequestBody.create(
                MediaType.parse("text/plain"),
                String.valueOf(albumId)
        );

        List<MultipartBody.Part> fileParts = new ArrayList<>();
        for (Uri uri : photoUris) {
            try {
                Bitmap thumbNail = getThumbNail(uri);

                if (thumbNail != null) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    thumbNail.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] imageBytes = byteArrayOutputStream.toByteArray();

                    RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), imageBytes);
                    MultipartBody.Part image = MultipartBody.Part.createFormData("files", getFileNameFromUri(uri), reqFile);
                    fileParts.add(image);
                }
            } catch (Exception e) {
                Log.e("Upload Error", Objects.requireNonNull(e.getMessage()));
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

        List<RequestBody> regionParts = new ArrayList<>();
        for (String region : regions) {
            RequestBody regionPart = RequestBody.create(
                    MediaType.parse("text/plain"),
                    region
            );
            regionParts.add(regionPart);
        }

        Call<ResponseBody> call = photoService.uploadPhoto(albumIdPart, fileParts, createParts, regionParts);
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

    public void removePhoto(DeleteCancelPhotoForm form, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = photoService.removePhoto(form);
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
    public void autoSavePhoto(List<Uri> photoUris, Long teamId, List<LocalDateTime> creates, List<String> regions, Callback<ResponseBody> callback) {
        RequestBody teamIdPart = RequestBody.create(
                MediaType.parse("text/plain"),
                String.valueOf(teamId)
        );

        List<MultipartBody.Part> fileParts = new ArrayList<>();
        for (Uri uri : photoUris) {
            try {
                Bitmap thumbNail = getThumbNail(uri);

                if (thumbNail != null) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    thumbNail.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] imageBytes = byteArrayOutputStream.toByteArray();

                    RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), imageBytes);
                    MultipartBody.Part image = MultipartBody.Part.createFormData("files", getFileNameFromUri(uri), reqFile);
                    fileParts.add(image);
                }
            } catch (Exception e) {
                Log.e("Upload Error", Objects.requireNonNull(e.getMessage()));
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

        List<RequestBody> regionParts = new ArrayList<>();
        for (String region : regions) {
            RequestBody regionPart = RequestBody.create(
                    MediaType.parse("text/plain"),
                    region
            );
            regionParts.add(regionPart);
        }

        Call<ResponseBody> call = photoService.autoSavePhoto(fileParts, teamIdPart, createParts, regionParts);
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

    public void galleryListByDate(Long albumId, String startDate, String endDate, Callback<Map<String, List<PhotoDTO>>> callback) {
        Call<Map<String, List<PhotoDTO>>> call = photoService.galleryListByDate(albumId, startDate, endDate);
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

    private Bitmap getThumbNail(Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.TITLE/*, MediaStore.Images.Media.ORIENTATION*/};

        ContentResolver cor = context.getContentResolver();
        Cursor cursor = cor.query(uri, filePathColumn, null, null, null);

        Bitmap thumbnail = null;
        if(cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            long ImageId = cursor.getLong(columnIndex);
            if(ImageId != 0) {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                thumbnail = MediaStore.Images.Thumbnails.getThumbnail(
                        context.getContentResolver(), ImageId,
                        MediaStore.Images.Thumbnails.MINI_KIND,
                        bmOptions);
            } else {
                Toast.makeText(context, "불러올수 없는 이미지 입니다.", Toast.LENGTH_LONG).show();
            }
            cursor.close();
        }
        return thumbnail;
    }
}
