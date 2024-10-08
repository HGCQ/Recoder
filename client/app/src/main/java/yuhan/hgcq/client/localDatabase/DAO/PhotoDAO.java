package yuhan.hgcq.client.localDatabase.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import yuhan.hgcq.client.localDatabase.entity.Photo;

@Dao
public interface PhotoDAO {
    @Insert
    void save(Photo photo);

    @Update
    void update(Photo photo);

    @Delete
    void delete(Photo photo);

    @Query("SELECT * FROM photo WHERE photoId = :photoId")
    List<Photo> findById(Long photoId);

    @Query("SELECT * FROM photo WHERE albumId = :albumId and is_deleted = 0")
    List<Photo> findByAlbumId(Long albumId);

    @Query("SELECT * FROM photo where is_deleted = 0")
    List<Photo> findAll();

    @Query("select * from photo where is_deleted = 1")
    List<Photo> findByIsDeleted();

    @Query("select*from photo where is_liked = 1 and is_deleted = 0")
    List<Photo>findIsLiked();
}
