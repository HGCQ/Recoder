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
    Long save(Album album);

    @Delete
    void delete(Album album);

    @Update
    void update(Album album);

    @Query("select * from Album where albumId = :id")
    Album findById(Long id);

    @Query("select * from Album where name = :name")
    Album findOneByName(String name);

    @Query("SELECT * FROM Album where isDeleted = 0")
    List<Album> findAll();

    @Query("select * from Album where name like :name COLLATE NOCASE")
    List<Album> findByName(String name);

    @Query("select * from Album where isDeleted = 1")
    List<Album> findByIsDeleted();

    @Query("select name from Album where isDeleted = 0")
    List<String> findAlbumNameList();
}
