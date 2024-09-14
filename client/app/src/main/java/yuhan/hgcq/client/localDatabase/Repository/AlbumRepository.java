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
    //앨법 생성
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
    //앨범 삭제
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
    //삭제 취소
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
    //찾기
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

    //이름으로 찾기
    private void findByName(String name, Callback<List<PAlbumDTO>> callback){
        executor.execute(() -> {
            try {

                List<Album> albumList = dao.findByName(name);
                List<PAlbumDTO> dtoList = new ArrayList<>();
                if (albumList == null) {
                    albumList = new ArrayList<>(); // null을 빈 리스트로 처리
                }

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

  // 전체 찾기

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

    //휴지통에서 찾기
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

    //수정
    public void update(Album album, Callback<Album> callback) {
        executor.execute(() -> {
            try {
                // DAO의 업데이트 메서드 호출
                dao.update(album);

                // 성공 콜백 호출 (결과가 album 반환을 위해 사용)
                callback.onSuccess(album);
            } catch (Exception e) {
                // 실패 시 에러 콜백 호출
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


    //휴지통
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