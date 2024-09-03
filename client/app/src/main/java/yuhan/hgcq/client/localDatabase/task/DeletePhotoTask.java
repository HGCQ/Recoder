package yuhan.hgcq.client.localDatabase.task;

import android.os.AsyncTask;

import yuhan.hgcq.client.localDatabase.DAO.PhotoDAO;
import yuhan.hgcq.client.localDatabase.entity.Photo;

public class DeletePhotoTask extends AsyncTask<Photo,Void,Void> {
    private PhotoDAO photoDao;

    public DeletePhotoTask(PhotoDAO photoDao) {
        this.photoDao = photoDao;
    }

    @Override
    protected Void doInBackground(Photo... photos) {
        photoDao.deletePhoto(photos[0]);
        return null;
    }
}
