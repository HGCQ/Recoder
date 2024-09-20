package yuhan.hgcq.client.localDatabase.Repository;

import android.content.Context;
import android.util.Log;

import androidx.room.Transaction;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import yuhan.hgcq.client.localDatabase.AppDatabase;
import yuhan.hgcq.client.localDatabase.DAO.AlbumDAO;
import yuhan.hgcq.client.localDatabase.DAO.PhotoDAO;
import yuhan.hgcq.client.localDatabase.callback.Callback;
import yuhan.hgcq.client.localDatabase.entity.Album;
import yuhan.hgcq.client.localDatabase.entity.Photo;
import yuhan.hgcq.client.model.dto.photo.MovePhotoForm;
import yuhan.hgcq.client.model.dto.photo.PhotoDTO;

public class PhotoRepository {
    private PhotoDAO dao;
    private AlbumDAO adao;
    private ExecutorService executor;
    private final static int DELETE_DAY = 30;

    public PhotoRepository(Context context) {
        dao = AppDatabase.getInstance(context).photoDAO();
        adao = AppDatabase.getInstance(context).albumDAO();
        executor = Executors.newSingleThreadExecutor();
    }

    @Transaction
    public void create(Long albumId, List<String> paths, List<String> times, Callback<Boolean> callback) {
        executor.execute(() -> {
            try {
                int size = paths.size();
                for (int i = 0; i < size; i++) {
                    Photo photo = Photo.create(paths.get(i), albumId, LocalDateTime.parse(times.get(i)));
                    dao.save(photo);
                }
                callback.onSuccess(Boolean.TRUE);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    //사진 삭제
    @Transaction
    public void delete(Long id, Callback<Boolean> callback) {
        executor.execute(() -> {
            try {
                Photo findId = dao.findById(id).get(0);
                if (findId != null) {
                    findId.delete();
                    dao.update(findId);
                    callback.onSuccess(true);
                } else {
                    callback.onError(new Exception("Photo not found"));
                }
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    //삭제 취소
    //널값 비교 해야함
    @Transaction
    public void deleteCancel(Long id, Callback<Boolean> callback) {
        executor.execute(() -> {
            try {
                Photo photo = dao.findById(id).get(0);
                if (photo == null) {
                    callback.onSuccess(false);
                    return;
                }
                photo.deleteCancel();
                dao.update(photo);
                callback.onSuccess(Boolean.TRUE);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    //찾기
    //널 비교
    public void search(Long id, Callback<PhotoDTO> callback) {
        executor.execute(() -> {
            try {
                List<Photo> photoList = dao.findById(id);
                // Null 또는 빈 리스트 체크
                if (photoList == null) {
                    callback.onError(new Exception("No photo found with the given id."));
                    return;
                }
                Photo photo = photoList.get(0);
                PhotoDTO dto = mapping(photo);
                ;

                callback.onSuccess(dto);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    //휴지통에서 찾기List
    public void searchTrash(Callback<List<PhotoDTO>> callback) {
        executor.execute(() -> {
            try {
                List<Photo> photoList = dao.findByIsDeleted();
                List<PhotoDTO> dtoList = new ArrayList<>();

                for (Photo photo : photoList) {
                    PhotoDTO dto = mapping(photo);
                    dtoList.add(dto);
                }

                callback.onSuccess(dtoList);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public void Liked(Long id, Callback<Boolean> callback) {
        executor.execute(() -> {
            try {
                Photo photo = dao.findById(id).get(0);
                if (photo == null) {
                    callback.onSuccess(false);
                    return;
                }
                photo.liked();
                dao.update(photo);
                callback.onSuccess(true);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    //앨범 안에 있는 사진 리스트
    public void searchByAlbum(Long albumId, Callback<List<PhotoDTO>> callback) {
        executor.execute(() -> {
            try {
                List<Photo> photoList = dao.findByAlbumId(albumId);
                List<PhotoDTO> dtoList = new ArrayList<>();
                for (Photo p : photoList) {
                    PhotoDTO dto = mapping(p);
                    dtoList.add(dto);
                }
                callback.onSuccess(dtoList);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    //사진 자동 분류 서비스에
    public void move(MovePhotoForm form, Callback<Boolean> callback) {
        executor.execute(() -> {
            try {
                Long albumId = form.getNewAlbumId();
                List<PhotoDTO> photos = form.getPhotos();
                for (PhotoDTO p : photos) {
                    Photo photo = dao.findById(p.getPhotoId()).get(0);
                    photo.setAlbumId(albumId);
                    dao.update(photo);
                }
                callback.onSuccess(true);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    /**
     * 선택된 앨범에 사진 자동으로 저장
     */
    @Transaction
    public void autoSave(List<String> paths, List<LocalDateTime> creates, Callback<Boolean> callback) {
        executor.execute(() -> {
            try {
                List<Album> albumList = adao.findAll();
                int size = paths.size();
                for (int i = 0; i < size; i++) {
                    LocalDateTime photoCreate = creates.get(i);
                    // 앨범 정보 조회
                    for (Album album : albumList) {
                        // 앨범 시작일과 종료일을 Room DB에서 가져오는 로직
                        LocalDateTime startDate = album.getStartDate();
                        LocalDateTime endDate = album.getEndDate();
                        Log.d("startDate", startDate.toString());
                        Log.d("endDate", endDate.toString());

                        // 사진 시간이 앨범 기간 내에 있는지 확인
                        if (photoCreate.isAfter(startDate) && photoCreate.isBefore(endDate)) {
                            Photo photo = Photo.create(paths.get(i), album.getAlbumId(), photoCreate);
                            Log.d("photo", photo.toString());
                            dao.save(photo);
                        }
                    }
                }

                // 성공 콜백
                callback.onSuccess(Boolean.TRUE);
            } catch (Exception e) {
                // 오류 처리
                callback.onError(e);
            }
        });
    }

    //휴지통
    private void trash(List<Photo> photos) {
        LocalDateTime now = LocalDateTime.now();
        for (Photo photo : photos) {
            LocalDateTime deletedAt = photo.getDeletedTime();
            long between = ChronoUnit.DAYS.between(deletedAt, now);

            if (between >= DELETE_DAY) {
                dao.delete(photo);
            }
        }
    }

    private PhotoDTO mapping(Photo photo) {
        PhotoDTO dto = new PhotoDTO();
        dto.setPhotoId(photo.getPhotoId());
        dto.setAlbumId(photo.getAlbumId());
        dto.setCreated(photo.getCreated().toString());
        dto.setPath(photo.getPath());
        return dto;
    }
}