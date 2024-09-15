package yuhan.hgcq.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.AccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yuhan.hgcq.server.domain.Album;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.domain.Team;
import yuhan.hgcq.server.dto.album.AlbumCreateForm;
import yuhan.hgcq.server.dto.album.AlbumDTO;
import yuhan.hgcq.server.dto.album.AlbumUpdateForm;
import yuhan.hgcq.server.dto.member.MemberDTO;
import yuhan.hgcq.server.service.AlbumService;
import yuhan.hgcq.server.service.MemberService;
import yuhan.hgcq.server.service.TeamService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/album")
public class AlbumController {

    private final MemberService ms;
    private final TeamService ts;
    private final AlbumService as;

    /**
     * 앨범 생성
     *
     * @param albumCreateForm 앨범 생성 폼
     * @param request         요청
     * @return 성공시 201 상태 코드, 실패 시 404 상태 코드, 권한 없을 시 401 상태 코드, 세션 없을 시 401 상태 코드
     */
    @PostMapping("/create")
    public ResponseEntity<?> createAlbum(@RequestBody AlbumCreateForm albumCreateForm, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Team ft = ts.search(albumCreateForm.getTeamId());

                            if (ft != null) {
                                Album newAlbum = new Album(ft, albumCreateForm.getStartTime(), albumCreateForm.getEndTime(), albumCreateForm.getName());

                                try {
                                    as.create(findMember, newAlbum);
                                    return ResponseEntity.status(HttpStatus.CREATED).body("Create Album Success");
                                } catch (AccessException e) {
                                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Admin In Group");
                                } catch (IllegalArgumentException e) {
                                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Create Album Fail");
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Team Fail");
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member Fail");
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * 앨범 삭제
     *
     * @param albumDTO 앨범 DTO
     * @param request  요청
     * @return 성공시 200 상태 코드, 실패 시 404 상태 코드, 권한 없을 시 401 상태 코드, 세션 없을 시 401 상태 코드
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deleteAlbum(@RequestBody AlbumDTO albumDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Album fa = as.search(albumDTO.getAlbumId());

                            if (fa != null) {
                                try {
                                    as.delete(findMember, fa);
                                    return ResponseEntity.status(HttpStatus.OK).body("Delete Album Success");
                                } catch (AccessException e) {
                                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Admin In Group");
                                } catch (IllegalArgumentException e) {
                                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Delete Album Fail");
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Album Fail");
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member Fail");
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * 앨범 삭제 취소
     *
     * @param albumDTO 앨범 DTO
     * @param request  요청
     * @return 성공시 200 상태 코드, 실패 시 404 상태 코드, 권한 없을 시 401 상태 코드, 세션 없을 시 401 상태 코드
     */
    @PostMapping("/delete/cancel")
    public ResponseEntity<?> deleteCancelAlbum(@RequestBody AlbumDTO albumDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Album fa = as.search(albumDTO.getAlbumId());

                            if (fa != null) {
                                try {
                                    as.cancelDelete(findMember, fa);
                                    return ResponseEntity.status(HttpStatus.OK).body("Delete Cancel Album Success");
                                } catch (AccessException e) {
                                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Admin In Group");
                                } catch (IllegalArgumentException e) {
                                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Delete Cancel Album Fail");
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Album Fail");
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member Fail");
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * 앨범 수정
     *
     * @param albumUpdateForm 앨범 수정 폼
     * @param request         요청
     * @return 성공시 200 상태 코드, 실패 시 404 상태 코드, 권한 없을 시 401 상태 코드, 세션 없을 시 401 상태 코드
     */
    @PostMapping("/update")
    public ResponseEntity<?> updateAlbum(@RequestBody AlbumUpdateForm albumUpdateForm, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Album fa = as.search(albumUpdateForm.getAlbumId());

                            if (fa != null) {
                                String updateName = albumUpdateForm.getName();
                                LocalDateTime updateStartDate = albumUpdateForm.getStartDate();
                                LocalDateTime updateEndDate = albumUpdateForm.getEndDate();

                                if (updateName != null) {
                                    fa.changeName(updateName);
                                }

                                if (updateStartDate != null) {
                                    fa.changeStartDate(updateStartDate);
                                }

                                if (updateEndDate != null) {
                                    fa.changeEndDate(updateEndDate);
                                }

                                try {
                                    as.modify(findMember, fa);
                                    return ResponseEntity.status(HttpStatus.OK).body("Update Album Success");
                                } catch (AccessException e) {
                                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Admin");
                                } catch (IllegalArgumentException e) {
                                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Update Album Fail");
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Album Fail");
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member Fail");
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * 앨범 리스트
     *
     * @param teamId  그룹 id
     * @param request 요청
     * @return 성공시 200 상태 코드와 앨범 리스트, 실패 시 404 상태 코드, 세션 없을 시 401 상태 코드
     */
    @GetMapping("/list/teamId")
    public ResponseEntity<?> listAlbums(@RequestParam("teamId") Long teamId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Team ft = ts.search(teamId);

                            if (ft != null) {
                                try {
                                    List<Album> albumList = as.searchAll(ft);

                                    List<AlbumDTO> albumDTOList = new ArrayList<>();

                                    for (Album album : albumList) {
                                        AlbumDTO albumDTO = mapping(album);
                                        albumDTOList.add(albumDTO);
                                    }

                                    return ResponseEntity.status(HttpStatus.OK).body(albumDTOList);
                                } catch (IllegalArgumentException e) {
                                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found AlbumList Fail");
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Team Fail");
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member Fail");
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * 앨범 리스트 이름으로 조회
     *
     * @param teamId  그룹 id
     * @param name    이름
     * @param request 요청
     * @return 성공시 200 상태 코드와 앨범 리스트, 실패 시 404 상태 코드, 세션 없을 시 401 상태 코드
     */
    @GetMapping("/list/teamId/name")
    public ResponseEntity<?> listAlbumsByName(@RequestParam("teamId") Long teamId, @RequestParam("name") String name, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Team ft = ts.search(teamId);

                            if (ft != null) {
                                try {
                                    List<Album> albumList = as.searchByName(ft, name);

                                    List<AlbumDTO> albumDTOList = new ArrayList<>();

                                    for (Album album : albumList) {
                                        AlbumDTO albumDTO = mapping(album);
                                        albumDTOList.add(albumDTO);
                                    }

                                    return ResponseEntity.status(HttpStatus.OK).body(albumDTOList);
                                } catch (IllegalArgumentException e) {
                                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found AlbumList Fail");
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Team Fail");
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member Fail");
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * 휴지통 리스트
     *
     * @param teamId  그룹 id
     * @param request 요청
     * @return 성공시 200 상태 코드와 앨범 리스트, 실패 시 404 상태 코드, 세션 없을 시 401 상태 코드
     */
    @GetMapping("/list/teamId/trash")
    public ResponseEntity<?> listAlbumsTrash(@RequestParam("teamId") Long teamId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Team ft = ts.search(teamId);

                            if (ft != null) {
                                try {
                                    List<Album> trashList = as.searchTrash(ft);

                                    as.trash(trashList);

                                    List<Album> trashListAfterClear = as.searchTrash(ft);
                                    List<AlbumDTO> albumDTOList = new ArrayList<>();

                                    for (Album album : trashListAfterClear) {
                                        AlbumDTO dto = mapping(album);
                                        albumDTOList.add(dto);
                                    }

                                    return ResponseEntity.status(HttpStatus.OK).body(albumDTOList);
                                } catch (IllegalArgumentException e) {
                                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found AlbumTrashList Fail");
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Team Fail");
                        }
                    }
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member Fail");
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }


    /**
     * Album -> AlbumDTO
     *
     * @param album 앨범
     * @return AlbumDTO
     */
    private AlbumDTO mapping(Album album) {
        AlbumDTO dto = new AlbumDTO();
        dto.setAlbumId(album.getId());
        dto.setName(album.getName());
        dto.setStartDate(album.getStartDate());
        dto.setEndDate(album.getEndDate());
        dto.setTeamId(album.getTeam().getId());
        return dto;
    }
}
