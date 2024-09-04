package yuhan.hgcq.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.AccessException;
import org.springframework.transaction.annotation.Transactional;
import yuhan.hgcq.server.domain.Album;
import yuhan.hgcq.server.domain.Chat;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.domain.Team;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ChatServiceTest {
    @Autowired
    ChatService cs;

    @Autowired
    MemberService ms;

    @Autowired
    TeamService ts;

    @Autowired
    AlbumService as;

    Long m1Id;
    Long m2Id;
    Long m3Id;
    Long m4Id;

    Long t1Id;
    Long t2Id;

    Long a1Id;
    Long a2Id;

    @BeforeEach
    void setUp() {
        Member m1 = new Member("m1", "m1@test.com", "1234");
        Member m2 = new Member("m2", "m2@test.com", "1234");
        Member m3 = new Member("m3", "m3@test.com", "1234");
        Member m4 = new Member("m4", "m4@test.com", "1234");

        m1Id = ms.join(m1);
        m2Id = ms.join(m2);
        m3Id = ms.join(m3);
        m4Id = ms.join(m4);

        Team t1 = new Team(m1, "t1");
        Team t2 = new Team(m1, "t2");

        t1Id = ts.create(t1);
        t2Id = ts.create(t2);

        Album a1 = new Album(t1, LocalDate.now(), "a1", "Seoul", "test");
        Album a2 = new Album(t1, LocalDate.now(), "a2", "Seoul", "test");

        try {
            a1Id = as.create(m1, a1);
            a2Id = as.create(m1, a2);
        } catch (AccessException e) {
            fail();
        }
    }
    
    @Test
    @DisplayName("채팅 추가")
    void add() {
        Member m1 = ms.search(m1Id);
        Album a1 = as.search(a1Id);

        Chat c1 = new Chat(m1, "test1", a1);
        Long saveId = cs.create(c1);

        Chat find = cs.search(saveId);
        assertThat(find).isEqualTo(c1);
    }

    @Test
    @DisplayName("채팅 삭제")
    void delete() {
        Member m1 = ms.search(m1Id);
        Album a1 = as.search(a1Id);

        Chat c1 = new Chat(m1, "test1", a1);
        Long saveId = cs.create(c1);

        Chat find = cs.search(saveId);

        try {
            cs.delete(m1, find);
        } catch (AccessException e) {
            fail();
        }

        assertThrows(IllegalStateException.class, () -> cs.search(saveId));
    }
    
    @Test
    @DisplayName("채팅 삭제는 작성자만 할 수 있다")
    void deleteNotWriter() {
        Member m1 = ms.search(m1Id);
        Member m2 = ms.search(m2Id);

        Album a1 = as.search(a1Id);

        Chat c1 = new Chat(m1, "test1", a1);
        Long saveId = cs.create(c1);

        Chat find = cs.search(saveId);

        assertThrows(AccessException.class, () -> cs.delete(m2, find));
    }

    @Test
    @DisplayName("채팅 리스트")
    void chatList() {
        Member m1 = ms.search(m1Id);
        Member m2 = ms.search(m2Id);

        Album a1 = as.search(a1Id);

        Chat c1 = new Chat(m1, "test1", a1);
        Chat c2 = new Chat(m2, "test2", a1);
        Chat c3 = new Chat(m1, "test3", a1);

        Long c1Id = cs.create(c1);
        Long c2Id = cs.create(c2);
        Long c3Id = cs.create(c3);

        List<Chat> chatList = cs.searchAll(a1);
        assertThat(chatList).hasSize(3).contains(c1, c2, c3);
    }
}