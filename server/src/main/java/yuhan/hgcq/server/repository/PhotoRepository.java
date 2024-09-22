package yuhan.hgcq.server.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import yuhan.hgcq.server.domain.Album;
import yuhan.hgcq.server.domain.Photo;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PhotoRepository {
    @PersistenceContext
    private final EntityManager em;

    /**
     * 사진 저장
     *
     * @param photo 사진
     */
    public Long save(Photo photo) {
        if (photo.getId() == null) {
            em.persist(photo);
        } else {
            em.merge(photo);
        }
        return photo.getId();
    }

    /**
     * 사진 삭제
     *
     * @param id 사진 id
     */
    public void delete(Long id) {
        Photo find = findOne(id);
        em.remove(find);
    }

    /**
     * 사진 조회
     *
     * @param id 사진 id
     * @return 사진
     */
    public Photo findOne(Long id) {
        return em.find(Photo.class, id);
    }

    /**
     * 사진 경로로 조회
     *
     * @param path 사진 경로
     * @return 사진
     */
    public Photo findByPath(String path) {
        try {
            return em.createQuery("select p from Photo p where p.path = :path", Photo.class)
                    .setParameter("path", path)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * 앨범 사진 리스트 조회
     *
     * @param album 앨범
     * @return 사진 리스트
     */
    public List<Photo> findAll(Album album) {
        return em.createQuery("select p from Photo p where p.album = :album and p.isDeleted = false order by p.created", Photo.class)
                .setParameter("album", album)
                .getResultList();
    }

    /**
     * 앨범 사진 이름 리스트 조회
     *
     * @param album 앨범
     * @return 사진 이름 리스트
     */
    public List<String> findNameAll(Album album) {
        return em.createQuery("select p.name from Photo p where p.album = :album and p.isDeleted = false order by p.album.name", String.class)
                .setParameter("album", album)
                .getResultList();
    }

    /**
     * 휴지통에 있는 사진 리스트 조회
     *
     * @param album 앨범
     * @return 사진 리스트
     */
    public List<Photo> findByDeleted(Album album) {
        return em.createQuery("select p from Photo p where p.isDeleted = true order by p.created desc", Photo.class)
                .getResultList();
    }

    /**
     * 앨범에 속한 사진 전체 삭제
     *
     * @param album 앨범
     */
    public void deleteAll(Album album) {
        em.createQuery("delete from Photo p where p.album = :album")
                .setParameter("album", album)
                .executeUpdate();
    }
}