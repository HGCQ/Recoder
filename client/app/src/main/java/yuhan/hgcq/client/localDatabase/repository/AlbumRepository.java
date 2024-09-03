package yuhan.hgcq.client.localDatabase.repository;

import android.content.Context;

import androidx.room.Room;

import yuhan.hgcq.client.localDatabase.AppDatabase;
import yuhan.hgcq.client.localDatabase.DAO.AlbumDAO;
import yuhan.hgcq.client.localDatabase.entity.Album;
import yuhan.hgcq.client.localDatabase.task.DeleteAlbumTask;
import yuhan.hgcq.client.localDatabase.task.InsertAlbumTask;
import yuhan.hgcq.client.localDatabase.task.UpdateAlbumTask;

public class AlbumRepository {
    private AlbumDAO albumDAO;

    public AlbumRepository(Context context) {
        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "app-database").build();
        albumDAO = db.albumDAO();
    }

    // Insert
    public void insertAlbum(Album album) {
        new InsertAlbumTask(albumDAO).execute(album);
    }

    // Update
    public void updateAlbum(Album album) {
        new UpdateAlbumTask(albumDAO).execute(album);
    }

    // Delete
    public void deleteAlbum(Album album) {
        new DeleteAlbumTask(albumDAO).execute(album);
    }
}
