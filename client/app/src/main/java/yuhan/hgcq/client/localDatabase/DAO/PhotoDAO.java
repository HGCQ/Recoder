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
    void insertPhoto(Photo photo);
    @Query("SELECT * FROM photo WHERE albumId = :albumId")
    List<Photo> getPhotosForEvent(Long albumId);
    @Update
    void updatePhoto(Photo photo);
    @Delete
    void deletePhoto(Photo photo);
}
