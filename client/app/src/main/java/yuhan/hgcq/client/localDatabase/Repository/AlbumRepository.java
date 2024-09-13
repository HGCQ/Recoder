package yuhan.hgcq.client.localDatabase.Repository;

import android.content.Context;

import androidx.room.Transaction;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import yuhan.hgcq.client.localDatabase.AppDatabase;
import yuhan.hgcq.client.localDatabase.DAO.AlbumDAO;
import yuhan.hgcq.client.localDatabase.callback.Callback;
import yuhan.hgcq.client.localDatabase.dto.PAlbumDTO;
import yuhan.hgcq.client.localDatabase.entity.Album;

public class AlbumRepository {
    private AlbumDAO dao;
    private ExecutorService executor;
    private final static int DELETE_DAY = 30;

    public AlbumRepository(Context context) {
        dao = AppDatabase.getInstance(context).albumDAO();
        executor = Executors.newSingleThreadExecutor();
    }

    @Transaction
    public void create(Album album, Callback<Boolean> callback) {
        executor.execute(() -> {
            try {
                dao.save(album);
                callback.onSuccess(Boolean.TRUE);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    @Transaction
    public void delete(Long id, Callback<Boolean> callback) {
        executor.execute(() -> {
            try {
                Album fa = dao.findById(id);
                fa.delete();
                dao.update(fa);
                callback.onSuccess(Boolean.TRUE);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    @Transaction
    public void deleteCancel(Long id, Callback<Boolean> callback) {
        executor.execute(() -> {
            try {
                Album fa = dao.findById(id);
                fa.deleteCancel();
                dao.update(fa);
                callback.onSuccess(Boolean.TRUE);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public void search(Long id, Callback<PAlbumDTO> callback) {
        executor.execute(() -> {
            try {
                Album fa = dao.findById(id);
                PAlbumDTO dto = mapping(fa);
                callback.onSuccess(dto);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }
    private void findByName(String name, Callback<List<PAlbumDTO>> callback){
        executor.execute(() -> {
            try {
                List<Album> albumList = dao.findByName(name);
                List<PAlbumDTO> dtoList = new ArrayList<>();

                for (Album album : albumList) {
                    PAlbumDTO dto = mapping(album);
                    dtoList.add(dto);
                }

                callback.onSuccess(dtoList);
            } catch (Exception e) {
                callback.onError(e);
            }
        });

    }

  //  private

    public void searchAll(Callback<List<PAlbumDTO>> callback) {
        executor.execute(() -> {
            try {
                List<Album> albumList = dao.findAll();
                List<PAlbumDTO> dtoList = new ArrayList<>();

                for (Album album : albumList) {
                    PAlbumDTO dto = mapping(album);
                    dtoList.add(dto);
                }

                callback.onSuccess(dtoList);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public void searchTrash(Callback<List<PAlbumDTO>> callback) {
        executor.execute(() -> {
            try {
                List<Album> albumList = dao.findByIsDeleted();
                List<PAlbumDTO> dtoList = new ArrayList<>();

                for (Album album : albumList) {
                    PAlbumDTO dto = mapping(album);
                    dtoList.add(dto);
                }

                callback.onSuccess(dtoList);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }
    private PAlbumDTO mapping(Album album) {
        PAlbumDTO dto = new PAlbumDTO();
        dto.setAlbumId(album.getAlbumId());
        dto.setName(album.getName());
        dto.setStartDate(album.getStartDate());
        dto.setEndDate(album.getEndDate());
        dto.setDeleted(album.getDeleted());
        dto.setDeletedTime(album.getDeletedTime());
        return dto;
    }
    private void trash(List<Album> albums) {
        LocalDateTime now = LocalDateTime.now();
        for (Album album : albums) {
            LocalDateTime deletedAt = album.getDeletedTime();
            long between = ChronoUnit.DAYS.between(deletedAt, now);

            if (between >= DELETE_DAY) {
                dao.delete(album);
            }
        }
    }


}