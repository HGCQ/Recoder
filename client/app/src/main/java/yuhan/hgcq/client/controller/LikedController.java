package yuhan.hgcq.client.controller;

import android.content.Context;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import yuhan.hgcq.client.config.NetworkClient;
import yuhan.hgcq.client.model.dto.photo.LikedDTO;
import yuhan.hgcq.client.model.dto.photo.PhotoDTO;
import yuhan.hgcq.client.model.service.LikedService;

public class LikedController {

    private LikedService likedService;

    public LikedController(Context context) {
        NetworkClient client = NetworkClient.getInstance(context.getApplicationContext());
        likedService = client.getLikedService();
    }

    /**
     * 좋아요 추가
     *
     * @param likedDTO 좋아요 DTO
     * @param callback 비동기 콜백
     */
    public void addLiked(LikedDTO likedDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = likedService.addLiked(likedDTO);
        call.enqueue(callback);
    }

    /**
     * 좋아요 삭제
     *
     * @param likedDTO 좋아요 DTO
     * @param callback 비동기 콜백
     */
    public void deleteLiked(LikedDTO likedDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = likedService.deleteLiked(likedDTO);
        call.enqueue(callback);
    }

    /**
     * 좋아요한 사진 리스트
     *
     * @param callback 비동기 콜백
     */
    public void likedList(Callback<List<PhotoDTO>> callback) {
        Call<List<PhotoDTO>> call = likedService.likedList();
        call.enqueue(callback);
    }
}
