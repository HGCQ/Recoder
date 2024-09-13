package yuhan.hgcq.client.localDatabase;

import android.content.Context;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import yuhan.hgcq.client.localDatabase.DAO.AlbumDAO;
import yuhan.hgcq.client.localDatabase.DAO.PhotoDAO;
import yuhan.hgcq.client.localDatabase.entity.Album;
import yuhan.hgcq.client.localDatabase.entity.Photo;

@Database(entities = {Album.class, Photo.class}, version = 1, exportSchema = false)
//@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract AlbumDAO albumDAO();
    public abstract PhotoDAO photoDAO();


    //synchronized
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app_database")
                    .fallbackToDestructiveMigration()  // Handles migration by destroying and rebuilding the database if no migration strategy is provided
                    .build();
        }
        return instance;
    }
}