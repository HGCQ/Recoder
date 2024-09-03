package yuhan.hgcq.client.localDatabase.repository;

import android.content.Context;

import androidx.room.Room;

import yuhan.hgcq.client.localDatabase.AppDatabase;
import yuhan.hgcq.client.localDatabase.DAO.PhotoDAO;
import yuhan.hgcq.client.localDatabase.entity.Photo;
import yuhan.hgcq.client.localDatabase.task.DeletePhotoTask;
import yuhan.hgcq.client.localDatabase.task.InsertAlbumTask;
import yuhan.hgcq.client.localDatabase.task.InsertPhotoTask;
import yuhan.hgcq.client.localDatabase.task.UpdatePhotoTask;

public class PhotoRepository {
    private PhotoDAO photoDAO;
    public PhotoRepository(Context context) {
        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "app-database").build();
        photoDAO = db.photoDAO();
    }

    // Insert
    public void insertPhoto(Photo photo) {
        new InsertPhotoTask(photoDAO).execute(photo);
    }

    // Update
    public void updatePhoto(Photo photo) {
        new UpdatePhotoTask(photoDAO).execute(photo);
    }

    // Delete
    public void deletePhoto(Photo photo) {
        new DeletePhotoTask(photoDAO).execute(photo);
    }
}
