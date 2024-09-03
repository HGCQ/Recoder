package yuhan.hgcq.client.localDatabase.task;

import android.os.AsyncTask;

import yuhan.hgcq.client.localDatabase.DAO.PhotoDAO;
import yuhan.hgcq.client.localDatabase.entity.Photo;

public class UpdatePhotoTask extends AsyncTask<Photo,Void,Void> {
    private PhotoDAO photoDao;

    public UpdatePhotoTask(PhotoDAO photoDao) {
        this.photoDao = photoDao;
    }

    @Override
    protected Void doInBackground(Photo... photos) {
        photoDao.updatePhoto(photos[0]);
        return null;
    }

}
