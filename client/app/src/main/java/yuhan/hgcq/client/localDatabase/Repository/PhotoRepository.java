package yuhan.hgcq.client.localDatabase.Repository;

import android.content.Context;
import android.util.Log;

import androidx.room.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public void create(Long albumId, List<String> paths, List<LocalDateTime> times, List<String> regions, Callback<Boolean> callback) {
        executor.execute(() -> {
            try {
                int size = paths.size();
                for (int i = 0; i < size; i++) {
                    String region = regions.get(i);
                    if (region.equals("null")) {
                        region = "위치정보없음";
                    }
                    String path = paths.get(i);
                    Photo photo = Photo.create(path, albumId, times.get(i), region);
                    dao.save(photo);
                }
                callback.onSuccess(true);
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
    public void deleteCancel(List<Long> photoIdList, Callback<Boolean> callback) {
        executor.execute(() -> {
            try {
                for (Long id : photoIdList) {
                    Photo photo = dao.findById(id).get(0);
                    if (photo == null) {
                        callback.onSuccess(false);
                        return;
                    }
                    photo.deleteCancel();
                    dao.update(photo);
                }
                callback.onSuccess(Boolean.TRUE);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    @Transaction
    public void remove(List<Long> photoIdList, Callback<Boolean> callback) {
        executor.execute(() -> {
            try {
                for (Long id : photoIdList) {
                    Photo photo = dao.findById(id).get(0);
                    if (photo == null) {
                        callback.onSuccess(false);
                        return;
                    }
                    if (photo.getIs_deleted()) {
                        dao.delete(photo);
                    }
                }
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

                if (photoList.isEmpty()) {
                    callback.onError(new Exception("No photo found with the given id."));
                    return;
                }
                Photo photo = photoList.get(0);
                PhotoDTO dto = mapping(photo);

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
                List<Photo> findPhoto = dao.findById(id);
                if (findPhoto.isEmpty()) {
                    callback.onSuccess(false);
                    return;
                }
                Photo photo = findPhoto.get(0);
                photo.liked();
                dao.update(photo);
                callback.onSuccess(true);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public void gallery(Long albumId, Callback<Map<String, List<PhotoDTO>>> callback) {
        executor.execute(() -> {
            try {
                List<Photo> photoList = dao.findByAlbumId(albumId);
                Map<String, List<PhotoDTO>> gallery = new HashMap<>();

                for (Photo photo : photoList) {
                    LocalDate create = photo.getCreated().toLocalDate();
                    PhotoDTO dto = mapping(photo);

                    List<PhotoDTO> photoDTOList = gallery.getOrDefault(create.toString(), new ArrayList<>());

                    photoDTOList.add(dto);

                    gallery.put(create.toString(), photoDTOList);
                }

                callback.onSuccess(gallery);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public void galleryByDate(Long albumId, String startDate, String endDate, Callback<Map<String, List<PhotoDTO>>> callback) {
        executor.execute(() -> {
            try {
                LocalDate start = LocalDate.parse(startDate);
                LocalDate end = LocalDate.parse(endDate);
                List<Photo> photoList = dao.findByAlbumId(albumId);
                Map<String, List<PhotoDTO>> gallery = new HashMap<>();

                for (Photo photo : photoList) {
                    LocalDate create = photo.getCreated().toLocalDate();

                    if ((create.isEqual(start) || create.isAfter(start)) &&
                            (create.isEqual(end) || create.isBefore(end))) {
                        PhotoDTO dto = mapping(photo);

                        List<PhotoDTO> photoDTOList = gallery.getOrDefault(create.toString(), new ArrayList<>());

                        photoDTOList.add(dto);

                        gallery.put(create.toString(), photoDTOList);
                    }
                }

                callback.onSuccess(gallery);
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

    public void searchByLike(Callback<List<PhotoDTO>> callback) {
        executor.execute(() -> {
            try {
                List<Photo> likeList = dao.findIsLiked();
                List<PhotoDTO> dtoList = new ArrayList<>();
                for (Photo p : likeList) {
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
                    List<Photo> findPhoto = dao.findById(p.getPhotoId());
                    if (findPhoto.isEmpty()) {
                        continue;
                    }
                    Photo photo = findPhoto.get(0);
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
    public void autoSave(List<String> paths, List<LocalDateTime> creates, List<String> regions, Callback<Boolean> callback) {
        executor.execute(() -> {
            try {
                String noRegion = "위치정보없음";
                int size = paths.size();
                for (int i = 0; i < size; i++) {
                    String path = paths.get(i);
                    LocalDateTime create = creates.get(i);
                    String region = regions.get(i);

                    List<String> albumNameList = adao.findAlbumNameList();
                    Album album = null;

                    if (region != null) {
                        if (region.equals("null")) {
                            if (!albumNameList.contains(noRegion)) {
                                Album newAlbum = Album.create(noRegion);
                                adao.save(newAlbum);
                                albumNameList.add(noRegion);
                            }
                            album = adao.findOneByName(noRegion);
                        } else {
                            if (!albumNameList.contains(region)) {
                                Album newAlbum = Album.create(region);
                                adao.save(newAlbum);
                                albumNameList.add(region);
                            }
                            album = adao.findOneByName(region);
                        }
                    }

                    if (album != null) {
                        Long albumId = album.getAlbumId();
                        List<String> pathList = dao.findPathList(albumId);
                        if (pathList.contains(path)) {
                            continue;
                        }
                        Photo newPhoto = Photo.create(path, albumId, create, region);
                        dao.save(newPhoto);
                    }
                }
                callback.onSuccess(true);
            } catch (Exception e) {
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
        dto.setLiked(photo.getIs_liked());
        return dto;
    }
}