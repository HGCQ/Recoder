package yuhan.hgcq.server.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import yuhan.hgcq.server.domain.Album;
import yuhan.hgcq.server.domain.Chat;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatRepository {
    @PersistenceContext
    private final EntityManager em;

    /**
     * 채팅 저장
     *
     * @param chat 채팅
     */
    public Long save(Chat chat) {
        if (chat.getId() == null) {
            em.persist(chat);
        } else {
            em.merge(chat);
        }
        return chat.getId();
    }

    /**
     * 채팅 삭제
     *
     * @param id 채팅 id
     */
    public void delete(Long id) {
        Chat find = findOne(id);
        em.remove(find);
    }

    /**
     * 채팅 조회
     *
     * @param id 채팅 id
     * @return 채팅
     */
    public Chat findOne(Long id) {
        return em.find(Chat.class, id);
    }

    /**
     * 앨범 채팅 리스트 조회
     *
     * @param album 앨범
     * @return 채팅 리스트
     */
    public List<Chat> findAll(Album album) {
        return em.createQuery("select c from Chat c where c.album = :album order by c.time", Chat.class)
                .setParameter("album", album)
                .getResultList();
    }
}
