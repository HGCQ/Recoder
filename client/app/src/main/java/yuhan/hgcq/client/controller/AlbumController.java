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
import yuhan.hgcq.client.model.dto.album.DeleteCancelAlbumForm;
import yuhan.hgcq.client.model.service.AlbumService;

public class AlbumController {

    private AlbumService albumService;

    public AlbumController(Context context) {
        NetworkClient client = NetworkClient.getInstance(context.getApplicationContext());
        albumService = client.getAlbumService();
    }

    /**
     * 앨범 생성
     *
     * @param albumCreateForm 앨범 생성 폼
     * @param callback        비동기 콜백
     */
    public void createAlbum(AlbumCreateForm albumCreateForm, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = albumService.createAlbum(albumCreateForm);
        call.enqueue(callback);
    }

    /**
     * 앨범 삭제
     *
     * @param albumDTO 앨범 DTO
     * @param callback 비동기 콜백
     */
    public void deleteAlbum(AlbumDTO albumDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = albumService.deleteAlbum(albumDTO);
        call.enqueue(callback);
    }

    /**
     * 앨범 삭제 취소
     *
     * @param form     앨범 id 리스트
     * @param callback 비동기 콜백
     */
    public void deleteCancelAlbum(DeleteCancelAlbumForm form, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = albumService.deleteCancelAlbum(form);
        call.enqueue(callback);
    }

    public void removeAlbum(DeleteCancelAlbumForm form, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = albumService.removeAlbum(form);
        call.enqueue(callback);
    }

    /**
     * 앨범 수정
     *
     * @param albumUpdateForm 앨범 수정 폼
     * @param callback        비동기 콜백
     */
    public void updateAlbum(AlbumUpdateForm albumUpdateForm, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = albumService.updateAlbum(albumUpdateForm);
        call.enqueue(callback);
    }

    /**
     * 앨범 리스트
     *
     * @param teamId   팀 id
     * @param callback 비동기 콜백
     */
    public void albumList(Long teamId, Callback<List<AlbumDTO>> callback) {
        Call<List<AlbumDTO>> call = albumService.albumList(teamId);
        call.enqueue(callback);
    }

    public void moveAlbumList(Long teamId, Long albumId, Callback<List<AlbumDTO>> callback) {
        Call<List<AlbumDTO>> call = albumService.moveAlbumList(teamId, albumId);
        call.enqueue(callback);
    }

    /**
     * 앨범 검색
     *
     * @param teamId   팀 id
     * @param name     앨범 이름
     * @param callback 비동기 콜백
     */
    public void searchAlbumByName(Long teamId, String name, Callback<List<AlbumDTO>> callback) {
        Call<List<AlbumDTO>> call = albumService.searchAlbumByName(teamId, name);
        call.enqueue(callback);
    }

    /**
     * 앨범 휴지통
     *
     * @param teamId   팀 id
     * @param callback 비동기 콜백
     */
    public void albumTrashList(Long teamId, Callback<List<AlbumDTO>> callback) {
        Call<List<AlbumDTO>> call = albumService.albumTrashList(teamId);
        call.enqueue(callback);
    }
}
