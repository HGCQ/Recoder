package yuhan.hgcq.client.localDatabase.task;

import android.os.AsyncTask;

import yuhan.hgcq.client.localDatabase.DAO.PhotoDAO;
import yuhan.hgcq.client.localDatabase.entity.Photo;

public class UpdatePhotoTask extends AsyncTask<Photo,Void,Void> {
    private PhotoDAO photoDAO;

    public UpdatePhotoTask(PhotoDAO photoDAO) {this.photoDAO = photoDAO;}

    @Override
    protected Void doInBackground(Photo... photos) {
        if (photos != null && photos.length > 0) {
            photoDAO.updatePhoto(photos[0]);
        }
        return null;
    }

}
