package yuhan.hgcq.client.localDatabase.task;

import android.os.AsyncTask;

import yuhan.hgcq.client.localDatabase.DAO.PhotoDAO;
import yuhan.hgcq.client.localDatabase.entity.Photo;

public class InsertPhotoTask extends AsyncTask<Photo,Void,Void> {
    private PhotoDAO photoDAO;

    public InsertPhotoTask(PhotoDAO photoDAO) {
        this.photoDAO = photoDAO;
    }

    @Override
    protected Void doInBackground(Photo... photos) {
        if (photos != null &&  photos.length > 0) {
            photoDAO.insertPhoto(photos[0]);
        }
        return null;
    }
}
