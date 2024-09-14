package yuhan.hgcq.client.controller;

import android.content.Context;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import yuhan.hgcq.client.config.NetworkClient;
import yuhan.hgcq.client.model.dto.album.AlbumCreateForm;
import yuhan.hgcq.client.model.dto.album.AlbumDTO;
import yuhan.hgcq.client.model.dto.album.AlbumUpdateForm;
import yuhan.hgcq.client.model.service.AlbumService;

public class AlbumController {

    private AlbumService albumService;

    public AlbumController(Context context) {
        NetworkClient client = NetworkClient.getInstance(context.getApplicationContext());
        albumService = client.getAlbumService();
    }

    public void createAlbum(AlbumCreateForm albumCreateForm, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = albumService.createAlbum(albumCreateForm);
        call.enqueue(callback);
    }

    public void deleteAlbum(AlbumDTO albumDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = albumService.deleteAlbum(albumDTO);
        call.enqueue(callback);
    }

    public void cancelDeleteAlbum(AlbumDTO albumDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = albumService.cancelDeleteAlbum(albumDTO);
        call.enqueue(callback);
    }

    public void updateAlbum(AlbumUpdateForm albumUpdateForm, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = albumService.updateAlbum(albumUpdateForm);
        call.enqueue(callback);
    }

    public void albumList(Long teamId, Callback<List<AlbumDTO>> callback) {
        Call<List<AlbumDTO>> call = albumService.albumList(teamId);
        call.enqueue(callback);
    }

    public void searchAlbum(Long teamId, String name, Callback<List<AlbumDTO>> callback) {
        Call<List<AlbumDTO>> call = albumService.searchAlbum(teamId, name);
        call.enqueue(callback);
    }

    public void albumTrash(Long teamId, Callback<List<AlbumDTO>> callback) {
        Call<List<AlbumDTO>> call = albumService.albumTrash(teamId);
        call.enqueue(callback);
    }

}
