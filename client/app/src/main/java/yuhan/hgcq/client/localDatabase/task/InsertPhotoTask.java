/*
package yuhan.hgcq.client.localDatabase.task;

import android.os.AsyncTask;

import yuhan.hgcq.client.localDatabase.DAO.PhotoDAO;
import yuhan.hgcq.client.localDatabase.entity.Photo;

public class InsertPhotoTask extends AsyncTask<Photo,Void,Void> {
    private PhotoDAO photoDao;

    public InsertPhotoTask(PhotoDAO photoDao) {
        this.photoDao = photoDao;
    }

    @Override
    protected Void doInBackground(Photo... photos) {
        photoDao.insertPhoto(photos[0]);
        return null;
    }
}
*/
