package yuhan.hgcq.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.AccessException;
import org.springframework.transaction.annotation.Transactional;
import yuhan.hgcq.server.domain.Album;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.domain.Photo;
import yuhan.hgcq.server.domain.Team;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PhotoServiceTest {
    @Autowired
    PhotoService ps;

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
    @DisplayName("사진 저장")
    void save() {
        Album a1 = as.search(a1Id);
        Photo p1 = new Photo(a1, "p1", "/t1/a1/p1", LocalDateTime.now());

        Long saveId = ps.create(p1);
        Photo find = ps.search(saveId);

        assertThat(find).isEqualTo(p1);
    }

    @Test
    @DisplayName("사진 삭제")
    void delete() {
        Album a1 = as.search(a1Id);
        Photo p1 = new Photo(a1, "p1", "/t1/a1/p1", LocalDateTime.now());

        Long saveId = ps.create(p1);
        Photo find = ps.search(saveId);

        ps.delete(find);

        find = ps.search(saveId);
        assertThat(find.getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("사진 삭제 취소")
    void cancelDelete() {
        Album a1 = as.search(a1Id);
        Photo p1 = new Photo(a1, "p1", "/t1/a1/p1", LocalDateTime.now());

        Long saveId = ps.create(p1);
        Photo find = ps.search(saveId);

        ps.delete(find);
        find = ps.search(saveId);

        ps.deleteCancel(find);
        find = ps.search(saveId);

        assertThat(find.getIsDeleted()).isFalse();
    }
    
    @Test
    @DisplayName("휴지통 사진 삭제")
    void trash() {
        Album a1 = as.search(a1Id);
        Photo p1 = new Photo(a1, "p1", "/t1/a1/p1", LocalDateTime.of(2024, 8, 1, 1, 1, 1));

        Long saveId = ps.create(p1);
        Photo find = ps.search(saveId);

        ps.delete(find);
        find = ps.search(saveId);
        find.test(LocalDateTime.of(2024, 8, 1, 1, 1, 1));

        List<Photo> trashList = ps.trashList(a1);
        ps.trash(trashList);

        assertThrows(IllegalStateException.class, () -> ps.search(saveId));
    }
    
    @Test
    @DisplayName("경로로 사진 검색")
    void searchByPath() {
        Album a1 = as.search(a1Id);
        Photo p1 = new Photo(a1, "p1", "/t1/a1/p1", LocalDateTime.of(2024, 8, 1, 1, 1, 1));

        Long saveId = ps.create(p1);
        Photo find = ps.search("/t1/a1/p1");

        assertThat(find).isEqualTo(p1);
    }
}