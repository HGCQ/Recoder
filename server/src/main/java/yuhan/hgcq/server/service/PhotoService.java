package yuhan.hgcq.server.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuhan.hgcq.server.domain.Album;
import yuhan.hgcq.server.domain.Photo;
import yuhan.hgcq.server.repository.PhotoRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PhotoService {
    private static final Logger log = LoggerFactory.getLogger(PhotoService.class);

    private final PhotoRepository pr;

    private final static int DELETE_DAY = 30;

    /**
     * 사진 저장
     *
     * @param photo 사진
     * @return 사진 id
     */
    @Transactional
    public Long create(Photo photo) {
        ensureNotNull(photo, "Photo");

        Long saveId = pr.save(photo);
        log.info("save photo : {}", photo);
        return saveId;
    }

    /**
     * 사진 삭제(휴지통)
     *
     * @param photo 사진
     */
    @Transactional
    public void delete(Photo photo) {
        ensureNotNull(photo, "Photo");

        Boolean isDeleted = photo.getIsDeleted();

        if (!isDeleted) {
            photo.changeIsDeleted();
            pr.save(photo);
            log.info("delete photo : {}", photo);
        }
    }

    /**
     * 사진 삭제 취소
     *
     * @param photo 사진
     */
    @Transactional
    public void deleteCancel(Photo photo) {
        ensureNotNull(photo, "Photo");

        Boolean isDeleted = photo.getIsDeleted();

        if (isDeleted) {
            photo.changeIsDeleted();
            pr.save(photo);
            log.info("delete cancel photo : {}", photo);
        }
    }

    /**
     * 휴지통 자동 삭제
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
                log.info("complete delete photo : {}", photo);
            }
        }
    }

    /**
     * 사진 검색
     *
     * @param id 사진 id
     * @return 사진
     */
    public Photo search(Long id) {
        Photo find = pr.findOne(id);

        if (find == null) {
            throw new IllegalArgumentException("photo not found");
        }

        return find;
    }

    /**
     * 사진 검색
     *
     * @param path 경로
     * @return 사진
     */
    public Photo search(String path) {
        Photo find = pr.findByPath(path);

        if (find == null) {
            throw new IllegalArgumentException("photo not found");
        }

        return find;
    }

    /**
     * 사진 리스트 검색
     *
     * @param album 앨범
     * @return 사진 리스트
     */
    public List<Photo> searchAll(Album album) {
        ensureNotNull(album, "Album");

        return pr.findAll(album);
    }

    /**
     * 휴지통 리스트 검색
     *
     * @param album 앨범
     * @return 사진 리스트
     */
    public List<Photo> trashList(Album album) {
        ensureNotNull(album, "Album");

        return pr.findByDeleted(album);
    }

    
    /* 매개변수가 null 값인지 확인 */
    private void ensureNotNull(Object obj, String name) {
        if (obj == null) {
            throw new IllegalArgumentException(name + " is null");
        }
    }
}
