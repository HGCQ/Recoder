/*
package yuhan.hgcq.client.localDatabase.task;

import android.os.AsyncTask;

import yuhan.hgcq.client.localDatabase.DAO.AlbumDAO;
import yuhan.hgcq.client.localDatabase.entity.Album;

public class InsertAlbumTask extends AsyncTask<Album,Void,Void> {
    private AlbumDAO eventDao;

    public InsertAlbumTask(AlbumDAO eventDao) {
        this.eventDao = eventDao;
    }

    @Override
    protected Void doInBackground(Album... albums) {
        eventDao.insertAlbum(albums[0]);
        return null;
    }
}
*/
