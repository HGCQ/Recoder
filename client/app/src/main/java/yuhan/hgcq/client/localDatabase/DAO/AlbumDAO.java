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

    @Query("SELECT * FROM Album")
    List<Album> findAll();

    @Query("select * from Album where name like '%' || :name || '%'COLLATE NOCASE")//대소문자를 구분하지 않기 위해 COLLATE NOCASE사용
    List<Album> findByName(String name);

    @Query("select * from Album where isDeleted = 1")
    List<Album> findByIsDeleted();
}