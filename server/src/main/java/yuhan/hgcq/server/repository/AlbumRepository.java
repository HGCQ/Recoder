package yuhan.hgcq.server.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import yuhan.hgcq.server.domain.Album;
import yuhan.hgcq.server.domain.Team;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AlbumRepository {
    @PersistenceContext
    private final EntityManager em;

    /**
     * 앨범 저장
     *
     * @param album 앨범
     */
    public Long save(Album album) {
        if (album.getId() == null) {
            em.persist(album);
        } else {
            em.merge(album);
        }
        return album.getId();
    }

    /**
     * 앨범 삭제
     *
     * @param id 앨범 id
     */
    public void delete(Long id) {
        Album find = findOne(id);
        em.remove(find);
    }

    /**
     * 앨범 조회
     *
     * @param id 앨범 id
     * @return 앨범
     */
    public Album findOne(Long id) {
        return em.find(Album.class, id);
    }

    /**
     * 그룹에 속한 앨범 리스트 조회
     *
     * @param team 그룹
     * @return 앨범 리스트
     */
    public List<Album> findAll(Team team) {
        return em.createQuery("select a from Album a where a.team = :team", Album.class)
                .setParameter("team", team)
                .getResultList();
    }

    /**
     * 휴지통에 속한 앨범 리스트 조회
     *
     * @param team 그룹
     * @return 앨범 리스트
     */
    public List<Album> findByDeleted(Team team) {
        return em.createQuery("select a from Album a where a.team = :team and a.isDeleted = true order by a.deletedAt", Album.class)
                .setParameter("team", team)
                .getResultList();
    }
}
