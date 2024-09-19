package yuhan.hgcq.server.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import yuhan.hgcq.server.domain.Album;
import yuhan.hgcq.server.domain.Photo;
import yuhan.hgcq.server.domain.Team;
import yuhan.hgcq.server.dto.photo.AutoSavePhotoForm;
import yuhan.hgcq.server.dto.photo.UploadPhotoForm;
import yuhan.hgcq.server.repository.AlbumRepository;
import yuhan.hgcq.server.repository.PhotoRepository;
import yuhan.hgcq.server.repository.TeamRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 사진 기능 요구사항 분석
 * 1. 사진은 서버의 외부 디렉터리에 저장을 한다.(추후 AWS 변경 가능성 있음)
 * 2. 사진을 삭제 요청하면 휴지통으로 이동하고, 30일 후 삭제된다.
 * 3. 사진 리스트를 받아서 그룹에 있는 앨범의 날짜에 속하면 사진을 추가한다.
 * 4. 사진을 다른 앨범으로 이동할 수 있다.
 */

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PhotoService {
    private static final Logger log = LoggerFactory.getLogger(PhotoService.class);

    private final PhotoRepository pr;
    private final AlbumRepository ar;
    private final TeamRepository tr;

    private final static int DELETE_DAY = 30;
    private final static String DIRECTORY_PATH = "D:" + File.separator
            + "app" + File.separator
            + "images" + File.separator;

    /**
     * 사진 저장(테스트 완료)
     *
     * @param photo 사진
     * @return 사진 id
     */
    @Transactional
    public Long save(Photo photo) throws IllegalArgumentException {
        ensureNotNull(photo, "Photo");

        Long saveId = pr.save(photo);
        log.info("Save Photo : {}", photo);
        return saveId;
    }

    /**
     * 사진 리스트 저장
     *
     * @param form 사진 리스트 폼
     * @throws IOException 예외
     */
    @Transactional
    public void save(UploadPhotoForm form) throws IOException, IllegalArgumentException {
        List<MultipartFile> files = form.getFiles();
        List<String> creates = form.getCreates();

        ensureNotNull(files, "Files");
        ensureNotNull(creates, "Creates");

        int size = files.size();
        Long albumId = form.getAlbumId();
        Album fa = ar.findOne(albumId);

        try {
            String newPath = DIRECTORY_PATH + albumId + File.separator;
            File directory = new File(newPath);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            for (int i = 0; i < size; i++) {
                MultipartFile file = files.get(i);
                String name = file.getOriginalFilename();
                Path path = Paths.get(newPath + name);
                file.transferTo(path);
                String imagePath = "/images/" + albumId + "/" + name;

                Photo p = new Photo(fa, name, imagePath, LocalDateTime.parse(creates.get(i)));
                pr.save(p);

                log.info("Save Photos : {}", p);
            }
        } catch (IOException e) {
            log.error("IO Error");
            throw new IOException();
        }
    }

    /**
     * 사진 삭제(휴지통)(테스트 완료)
     *
     * @param photo 사진
     */
    @Transactional
    public void delete(Photo photo) throws IllegalArgumentException {
        ensureNotNull(photo, "Photo");

        photo.delete();

        pr.save(photo);
        log.info("Delete Photo : {}", photo);
    }

    /**
     * 사진 삭제 취소(테스트 완료)
     *
     * @param photo 사진
     */
    @Transactional
    public void deleteCancel(Photo photo) throws IllegalArgumentException {
        ensureNotNull(photo, "Photo");

        photo.cancelDelete();

        pr.save(photo);
        log.info("Delete Cancel Photo : {}", photo);
    }

    /**
     * 휴지통 자동 삭제(테스트 완료)
     *
     * @param photos 사진 리스트
     */
    @Transactional
    public void trash(List<Photo> photos) {
        LocalDateTime now = LocalDateTime.now();
        for (Photo photo : photos) {
            LocalDateTime deleted = photo.getDeleted();
            long between = ChronoUnit.DAYS.between(deleted, now);

            if (between >= DELETE_DAY) {
                pr.delete(photo.getId());
                log.info("Complete Delete Photo : {}", photo);
            }
        }
    }

    /**
     * 사진 검색(테스트 완료)
     *
     * @param id 사진 id
     * @return 사진
     */
    public Photo search(Long id) throws IllegalArgumentException {
        Photo find = pr.findOne(id);

        if (find == null) {
            throw new IllegalArgumentException("photo not found");
        }

        return find;
    }

    /**
     * 사진 경로로 검색(테스트 완료)
     *
     * @param path 경로
     * @return 사진
     */
    public Photo search(String path) throws IllegalArgumentException {
        Photo find = pr.findByPath(path);

        if (find == null) {
            throw new IllegalArgumentException("photo not found");
        }

        return find;
    }

    /**
     * 사진 리스트 검색(테스트 완료)
     *
     * @param album 앨범
     * @return 사진 리스트
     */
    public List<Photo> searchAll(Album album) throws IllegalArgumentException {
        ensureNotNull(album, "Album");

        return pr.findAll(album);
    }

    /**
     * 휴지통 리스트 검색(테스트 완료)
     *
     * @param album 앨범
     * @return 사진 리스트
     */
    public List<Photo> trashList(Album album) throws IllegalArgumentException {
        ensureNotNull(album, "Album");

        return pr.findByDeleted(album);
    }

    /**
     * 선택된 앨범에 사진 자동으로 저장
     *
     * @param form 사진 자동 저장 폼
     * @throws IOException 예외
     */
    @Transactional
    public void autoSave(AutoSavePhotoForm form) throws IOException {
        Long teamId = form.getTeamId();
        Team ft = tr.findOne(teamId);

        List<Album> albumList = ar.findAll(ft);
        List<MultipartFile> files = form.getFiles();
        List<String> creates = form.getCreates();

        int size = files.size();

        for (int i = 0; i < size; i++) {
            LocalDateTime photoTime = LocalDateTime.parse(creates.get(i));

            for (Album album : albumList) {
                LocalDate startDate = album.getStartDate();
                LocalDate endDate = album.getEndDate();

                if (photoTime.isAfter(startDate.atStartOfDay()) && photoTime.isBefore(endDate.atStartOfDay())) {
                    Long albumId = album.getId();

                    try {
                        String newPath = DIRECTORY_PATH + albumId + File.separator;
                        File directory = new File(newPath);

                        if (!directory.exists()) {
                            directory.mkdirs();
                        }

                        MultipartFile file = files.get(i);
                        String name = file.getOriginalFilename();
                        Path path = Paths.get(newPath + name);
                        file.transferTo(path);
                        String imagePath = "/images/" + albumId + "/" + name;

                        Photo p = new Photo(album, name, imagePath, LocalDateTime.parse(creates.get(i)));
                        pr.save(p);

                        log.info("Save Photo : {}", p);
                    } catch (IOException e) {
                        log.error("IO Error");
                        throw new IOException();
                    }
                }
            }
        }
    }

    /**
     * 앨범 이동(테스트 완료)
     *
     * @param newAlbum 옮길 앨범
     * @param photos   사진 리스트
     */
    @Transactional
    public void move(Album newAlbum, List<Photo> photos) throws IllegalArgumentException {
        ensureNotNull(newAlbum, "Album");
        ensureNotNull(photos, "Photos");

        for (Photo photo : photos) {
            photo.changeAlbum(newAlbum);
            pr.save(photo);
            log.info("Move Photo : {}", photo);
        }
    }

    /* 매개변수가 null 값인지 확인 */
    private void ensureNotNull(Object obj, String name) {
        if (obj == null) {
            throw new IllegalArgumentException(name + " is null");
        }
    }
}
