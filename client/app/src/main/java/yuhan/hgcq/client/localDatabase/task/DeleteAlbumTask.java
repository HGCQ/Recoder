package yuhan.hgcq.client.localDatabase.task;

import android.os.AsyncTask;

import yuhan.hgcq.client.localDatabase.DAO.AlbumDAO;
import yuhan.hgcq.client.localDatabase.entity.Album;

public class DeleteAlbumTask extends AsyncTask<Album,Void,Void> {
    private AlbumDAO eventDao;

    public DeleteAlbumTask(AlbumDAO eventDao) {
        this.eventDao = eventDao;
    }

    @Override
    protected Void doInBackground(Album... albums) {
        eventDao.deleteAlbum(albums[0]);
        return null;
    }
}
