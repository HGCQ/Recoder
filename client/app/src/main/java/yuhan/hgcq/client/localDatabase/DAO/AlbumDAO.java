package yuhan.hgcq.client.localDatabase.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import yuhan.hgcq.client.localDatabase.entity.Album;

@Dao
public interface AlbumDAO {
    @Insert
    void insertAlbum(Album album);

    @Query("SELECT * FROM Album")
    List<Album> getAllAlbum();

    @Update
    void updateAlbum(Album album);

    @Delete
    void deleteAlbum(Album album);
}
