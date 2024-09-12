package yuhan.hgcq.server.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.AccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuhan.hgcq.server.domain.Album;
import yuhan.hgcq.server.domain.Chat;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.repository.ChatRepository;

import java.util.List;
import java.util.Objects;

/**
 * 채팅 기능 요구사항 분석
 * 1. 채팅은 작성자만 삭제할 수 있다.
 * 2. 앨범의 채팅 리스트를 추출할 수 있다.
 */

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final ChatRepository cr;

    /**
     * 채팅 생성(테스트 완료)
     *
     * @param chat 채팅
     * @return 채팅 id
     */
    @Transactional
    public Long create(Chat chat) {
        ensureNotNull(chat, "Chat");

        Long saveId = cr.save(chat);
        log.info("Create Chat : {}", chat);
        return saveId;
    }

    /**
     * 채팅 삭제(테스트 완료)
     *
     * @param member 주체
     * @param chat   대상
     * @throws AccessException 작성자가 아닐 시
     */
    @Transactional
    public void delete(Member member, Chat chat) throws AccessException {
        ensureNotNull(member, "Member");
        ensureNotNull(chat, "Chat");

        Member writer = chat.getWriter();
        boolean isWriter = Objects.equals(member.getId(), writer.getId());

        if (isWriter) {
            cr.delete(chat.getId());
            log.info("Delete Chat : {}", chat);
        } else {
            throw new AccessException("Not Writer");
        }
    }

    /**
     * 채팅 검색(테스트 완료)
     *
     * @param id 채팅 id
     * @return 채팅
     */
    public Chat search(Long id) {
        Chat find = cr.findOne(id);

        if (find == null) {
            throw new IllegalStateException("Chat not found");
        }

        return find;
    }

    /**
     * 채팅 리스트 검색(테스트 완료)
     *
     * @param album 앨범
     * @return 채팅 리스트
     */
    public List<Chat> searchAll(Album album) {
        ensureNotNull(album, "Album");

        return cr.findAll(album);
    }

    /* 매개변수가 null 값인지 확인 */
    private void ensureNotNull(Object obj, String name) {
        if (obj == null) {
            throw new IllegalArgumentException(name + " is null");
        }
    }
}
