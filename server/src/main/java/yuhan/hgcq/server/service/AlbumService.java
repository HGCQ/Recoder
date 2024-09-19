package yuhan.hgcq.server.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.AccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuhan.hgcq.server.domain.Album;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.domain.Team;
import yuhan.hgcq.server.repository.AlbumRepository;
import yuhan.hgcq.server.repository.TeamMemberRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 앨범 기능 요구사항 분석
 * 1. 그룹 관리자만 앨범을 생성할 수 있다.
 * 2. 그룹 관리자만 앨범을 삭제할 수 있다(휴지통).
 * 3. 그룹 관리자만 앨범 삭제를 취소할 수 있다.
 * 4. 그룹 관리자만 앨범을 수정할 수 있다.
 * 5. 30일이 지나면 휴지통에서 자동 삭제한다. (사진도 같이 삭제)
 * 6. 휴지통에 있는 앨범 리스트 추출할 수 있다.
 * 7. 그룹에 속한 앨범 리스트를 추출할 수 있다.
 */

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlbumService {
    private static final Logger log = LoggerFactory.getLogger(AlbumService.class);

    private final AlbumRepository ar;
    private final TeamMemberRepository tmr;

    private final static int DELETE_DAY = 30;

    /**
     * 앨범 생성(테스트 완료)
     *
     * @param member 회원
     * @param album  앨범
     * @return 앨범 id
     * @throws AccessException 관리자가 아닐 시
     */
    @Transactional
    public Long create(Member member, Album album) throws AccessException, IllegalArgumentException {
        ensureNotNull(album, "Album");
        ensureNotNull(member, "Member");

        boolean isAdmin = isAdmin(member, album);

        if (isAdmin) {
            ar.save(album);
            log.info("Create Album : {}", album);
            return album.getId();
        } else {
            throw new AccessException("Don't have Permission");
        }
    }

    /**
     * 앨범 수정(테스트 완료)
     *
     * @param member 회원
     * @param album  앨범
     * @throws AccessException 관리자가 아닐 시
     */
    @Transactional
    public void modify(Member member, Album album) throws AccessException, IllegalArgumentException {
        ensureNotNull(member, "Member");
        ensureNotNull(album, "Album");

        boolean isAdmin = isAdmin(member, album);

        if (isAdmin) {
            ar.save(album);
            log.info("Modify Album : {}", album);
        } else {
            throw new AccessException("Don't have Permission");
        }
    }

    /**
     * 앨범 삭제(테스트 완료)
     *
     * @param member 회원
     * @param album  앨범
     * @throws AccessException 관리자가 아닐 시
     */
    @Transactional
    public void delete(Member member, Album album) throws AccessException, IllegalArgumentException {
        ensureNotNull(album, "Album");

        boolean isAdmin = isAdmin(member, album);

        if (isAdmin) {
            album.deleteAlbum();
            ar.save(album);
            log.info("Delete Album : {}", album);
        } else {
            throw new AccessException("Don't have Permission");
        }
    }

    /**
     * 앨범 삭제 취소(테스트 완료)
     *
     * @param member 회원
     * @param album  앨범
     * @throws AccessException 관리자가 아닐 시
     */
    @Transactional
    public void cancelDelete(Member member, Album album) throws AccessException, IllegalArgumentException {
        ensureNotNull(album, "Album");

        boolean isAdmin = isAdmin(member, album);

        if (isAdmin) {
            album.cancelDeleteAlbum();
            ar.save(album);
            log.info("Cancel Delete Album : {}", album);
        } else {
            throw new AccessException("Don't have Permission");
        }
    }

    /**
     * 휴지통 자동 삭제(테스트 완료)
     *
     * @param albums 앨범
     */
    @Transactional
    public void trash(List<Album> albums) {
        LocalDateTime now = LocalDateTime.now();
        for (Album album : albums) {
            LocalDate deletedAt = album.getDeletedAt();
            long between = ChronoUnit.DAYS.between(deletedAt, now);

            if (between >= DELETE_DAY) {
                ar.delete(album.getId());
                log.info("Complete Delete Album : {}", album);
            }
        }

    }

    /**
     * 앨범 검색(테스트 완료)
     *
     * @param id 앨범 id
     * @return 앨범
     */
    public Album search(Long id) throws IllegalArgumentException {
        Album find = ar.findOne(id);

        if (find == null) {
            throw new IllegalArgumentException("Album not found");
        }

        return find;
    }

    /**
     * 앨범 리스트 검색(테스트 완료)
     *
     * @param team 그룹
     * @return 앨범 리스트
     */
    public List<Album> searchAll(Team team) throws IllegalArgumentException {
        ensureNotNull(team, "Team");

        return ar.findAll(team);
    }

    /**
     * 앨범 리스트 이름으로 검색(테스트 완료)
     *
     * @param team 그룹
     * @param name 이름
     * @return 앨범 리스트
     */
    public List<Album> searchByName(Team team, String name) throws IllegalArgumentException {
        ensureNotNull(team, "Team");
        ensureNotNull(name, "Name");

        return ar.findByName(team, name);
    }

    /**
     * 휴지통에 속한 앨범 리스트 검색(테스트 완료)
     *
     * @param team 그룹
     * @return 앨범 리스트
     */
    public List<Album> searchTrash(Team team) throws IllegalArgumentException {
        ensureNotNull(team, "Team");

        return ar.findByDeleted(team);
    }

    /* 매개변수가 null 값인지 확인 */
    private void ensureNotNull(Object obj, String name) {
        if (obj == null) {
            throw new IllegalArgumentException(name + " is null");
        }
    }

    /* 관리자인지 확인 */
    private boolean isAdmin(Member member, Album album) {
        Team team = album.getTeam();

        List<Member> adminList = tmr.findAdminByTeam(team);

        return adminList.contains(member);
    }
}
