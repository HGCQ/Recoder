package yuhan.hgcq.client.localDatabase;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import yuhan.hgcq.client.localDatabase.DAO.AlbumDAO;
import yuhan.hgcq.client.localDatabase.DAO.PhotoDAO;
import yuhan.hgcq.client.localDatabase.entity.Album;
import yuhan.hgcq.client.localDatabase.entity.Photo;

@Database(entities = {Album.class, Photo.class}, version = 1, exportSchema = false)
//@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract AlbumDAO albumDAO();
    public abstract PhotoDAO photoDAO();
}
