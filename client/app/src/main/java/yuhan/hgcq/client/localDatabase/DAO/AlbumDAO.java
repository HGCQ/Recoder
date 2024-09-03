package yuhan.hgcq.client.localDatabase.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import yuhan.hgcq.client.localDatabase.entity.Album;
import yuhan.hgcq.client.localDatabase.entity.AlbumWithPhotos;

@Dao
public interface AlbumDAO {
    @Insert
    void insertAlbum(Album album);

    //앨범 목록
    @Query("SELECT * FROM Album")
    List<Album> getAllAlbum();

    //앨범 안 사진들  AlbumWithPhotos -> 1:N관계
    @Transaction
    @Query("SELECT * FROM Album")
    List<AlbumWithPhotos> getAlbumsWithPhotos();

    @Update
    void updateAlbum(Album album);

    @Delete
    void deleteAlbum(Album album);
}
