package yuhan.hgcq.client.localDatabase.task;

import android.os.AsyncTask;

import yuhan.hgcq.client.localDatabase.DAO.AlbumDAO;
import yuhan.hgcq.client.localDatabase.entity.Album;

public class InsertAlbumTask extends AsyncTask<Album,Void,Void> {
    private AlbumDAO albumDAO;

    public InsertAlbumTask(AlbumDAO albumDAO) {
        this.albumDAO = albumDAO;
    }

    @Override
    protected Void doInBackground(Album... albums) {
        if (albums != null && albums.length > 0) {
            albumDAO.insertAlbum(albums[0]);
        }
        return null;
    }
}
