package yuhan.hgcq.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yuhan.hgcq.server.domain.*;
import yuhan.hgcq.server.dto.member.MemberDTO;
import yuhan.hgcq.server.dto.photo.LikedDTO;
import yuhan.hgcq.server.dto.photo.PhotoDTO;
import yuhan.hgcq.server.service.LikedService;
import yuhan.hgcq.server.service.MemberService;
import yuhan.hgcq.server.service.PhotoService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/liked")
public class LikedController {

    private final MemberService ms;
    private final PhotoService ps;
    private final LikedService ls;

    /**
     * 좋아요 추가
     *
     * @param likedDTO 좋아요 DTO
     * @param request  요청
     * @return 성공 시 201 상태 코드, 실패 시 404 상태 코드, 세션 없을 시 401 상태 코드
     */
    @PostMapping("/add")
    public ResponseEntity<?> createLiked(@RequestBody LikedDTO likedDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Photo fp = ps.search(likedDTO.getPhotoId());

                            if (fp != null) {
                                Liked newLiked = new Liked(findMember, fp);
                                try {
                                    ls.add(newLiked);
                                    return ResponseEntity.status(HttpStatus.CREATED).body("Add Liked Success");
                                } catch (IllegalArgumentException e) {
                                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Add Liked Fail");
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Photo Fail");
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
     * 좋아요 삭제
     *
     * @param likedDTO 좋아요 DTO
     * @param request  요청
     * @return 성공 시 200 상태 코드, 실패 시 404 상태 코드, 세션 없을 시 401 상태 코드
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deleteLiked(@RequestBody LikedDTO likedDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            Photo fp = ps.search(likedDTO.getPhotoId());

                            if (fp != null) {
                                try {
                                    Liked fl = ls.search(findMember, fp);

                                    if (fl != null) {
                                        try {
                                            ls.remove(fl);
                                            return ResponseEntity.status(HttpStatus.OK).body("Delete Liked Success");
                                        } catch (IllegalArgumentException e) {
                                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Delete Liked Fail");
                                        }
                                    }
                                } catch (IllegalArgumentException e) {
                                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Liked Fail");
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Photo Fail");
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
     * 좋아요한 사진 리스트
     *
     * @param request 요청
     * @return 성공 시 200 상태 코드와 사진 리스트, 실패 시 404 상태 코드, 세션 없을 시 401 상태 코드
     */
    @GetMapping("/list")
    public ResponseEntity<?> listLiked(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                try {
                    Member findMember = ms.search(loginMember.getMemberId());

                    if (findMember != null) {
                        try {
                            List<Photo> likedList = ls.searchAll(findMember);
                            List<PhotoDTO> photoDTOList = new ArrayList<>();

                            for (Photo photo : likedList) {
                                PhotoDTO dto = mapping(photo);
                                photoDTOList.add(dto);
                            }

                            return ResponseEntity.status(HttpStatus.OK).body(photoDTOList);
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found LikedList Fail");
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
     * Photo -> PhotoDTO
     *
     * @param photo 사진
     * @return PhotoDTO
     */
    private PhotoDTO mapping(Photo photo) {
        PhotoDTO dto = new PhotoDTO();
        dto.setPhotoId(photo.getId());
        dto.setAlbumId(photo.getAlbum().getId());
        dto.setName(photo.getName());
        dto.setPath(photo.getPath());
        dto.setCreated(photo.getCreated().toString());
        return dto;
    }
}
