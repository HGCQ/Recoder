package yuhan.hgcq.client.controller;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import yuhan.hgcq.client.model.dto.album.AlbumDTO;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.dto.member.UpdateForm;
import yuhan.hgcq.client.model.service.AlbumService;
import yuhan.hgcq.client.model.service.FollowService;

public class AlbumController {

    private AlbumService albumService;

    public void createAlbum(AlbumDTO albumDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = albumService.createAlbum(albumDTO);
        call.enqueue(callback);
    }

    public void deleteAlbum(AlbumDTO albumDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = albumService.deleteAlbum(albumDTO);
        call.enqueue(callback);
    }

    public void albumList(AlbumDTO albumDTO, Callback<List<AlbumDTO>> callback) {
        Call<List<AlbumDTO>> call = albumService.albumList();
        call.enqueue(callback);
    }

    public void searchAlbum(String name, Callback<List<AlbumDTO>> callback) {
        Call<List<AlbumDTO>> call = albumService.searchAlbum(name);
        call.enqueue(callback);
    }

    public void updateAlbum(AlbumDTO albumDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = albumService.updateAlbum(albumDTO);
        call.enqueue(callback);
    }
}
