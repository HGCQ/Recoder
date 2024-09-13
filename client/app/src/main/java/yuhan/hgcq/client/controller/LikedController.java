package yuhan.hgcq.client.controller;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import yuhan.hgcq.client.model.dto.chat.ChatDTO;
import yuhan.hgcq.client.model.dto.photo.LikedDTO;
import yuhan.hgcq.client.model.dto.photo.PhotoDTO;
import yuhan.hgcq.client.model.service.LikedService;

public class LikedController {

    private LikedService likedService;

    public void addLiked(LikedDTO likedDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = likedService.addLiked(likedDTO);
        call.enqueue(callback);
    }

    public void deleteLiked(LikedDTO likedDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = likedService.deleteLiked(likedDTO);
        call.enqueue(callback);
    }

    public void likedList(Callback<List<PhotoDTO>> callback) {
        Call<List<PhotoDTO>> call = likedService.likedList();
        call.enqueue(callback);
    }
}
