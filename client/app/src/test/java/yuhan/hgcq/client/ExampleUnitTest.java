package yuhan.hgcq.client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.room.Transaction;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import yuhan.hgcq.client.localDatabase.AppDatabase;
import yuhan.hgcq.client.localDatabase.DAO.AlbumDAO;
import yuhan.hgcq.client.localDatabase.entity.Album;

@RunWith(RobolectricTestRunner.class)//에뮬레이터 없이 테스트를 위한 클래스
public class ExampleUnitTest {
    private AppDatabase db;
    private AlbumDAO albumDAO;

    @Before
    public void createDb() {
        // Robolectric provides a functional Context without needing an emulator
        Context context = androidx.test.core.app.ApplicationProvider.getApplicationContext();

        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()  // Only for testing purposes
                .build();
        albumDAO = db.albumDAO();
    }

    @Test
    public void testGetAllUsers() {
        // Create Album objects
        Album album = Album.create( LocalDateTime.now(),"lee", "내용", "region");
        Album album1 = Album.create(LocalDateTime.now(), "QWE", "내용QWE", "re");

        // Insert albums into the database
        albumDAO.insertAlbum(album);
        albumDAO.insertAlbum(album1);

        // Retrieve all albums
        List<Album> albums = albumDAO.getAllAlbum();

        // Verify the number of albums
        assertEquals(2, albums.size());

    }
    @Test
    public void logAlbumData() {
        // Create Album objects
        Album album = Album.create(  LocalDateTime.now(),"lee", "내용", "region");
        Album album1 = Album.create( LocalDateTime.now(), "QWE", "내용QWE", "re");

        // Insert albums into the database
        albumDAO.insertAlbum(album);
        albumDAO.insertAlbum(album1);


        List<Album> albums = albumDAO.getAllAlbum();
        for (Album al : albums) {
            System.out.println("Date:"+al.getDate()+",Album ID: " + al.getAlbumId() + ", Name: " + al.getName());

        }
        System.out.println(albums.get(0));
        System.out.println(albums.get(1));
        assertEquals(2, albums.size());

    }


    @After
    public void closeDb() {
        db.close();
    }
}
