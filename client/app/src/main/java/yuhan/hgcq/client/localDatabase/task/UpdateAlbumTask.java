package yuhan.hgcq.client.localDatabase.task;

import android.os.AsyncTask;

import yuhan.hgcq.client.localDatabase.DAO.AlbumDAO;
import yuhan.hgcq.client.localDatabase.entity.Album;

public class UpdateAlbumTask extends AsyncTask<Album,Void,Void> {
    private AlbumDAO eventDao;

    public UpdateAlbumTask(AlbumDAO eventDao) {
        this.eventDao = eventDao;
    }

    @Override
    protected Void doInBackground(Album... albums) {
        eventDao.updateAlbum(albums[0]);
        return null;
    }
}
