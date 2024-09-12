package yuhan.hgcq.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.AccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yuhan.hgcq.server.domain.Album;
import yuhan.hgcq.server.domain.Chat;
import yuhan.hgcq.server.domain.Member;
import yuhan.hgcq.server.dto.chat.ChatDTO;
import yuhan.hgcq.server.dto.chat.CreateChatForm;
import yuhan.hgcq.server.dto.member.MemberDTO;
import yuhan.hgcq.server.service.AlbumService;
import yuhan.hgcq.server.service.ChatService;
import yuhan.hgcq.server.service.MemberService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final MemberService ms;
    private final ChatService cs;
    private final AlbumService as;

    /**
     * 채팅 추가
     *
     * @param createChatForm 채팅 생성 폼
     * @param request        요청
     * @return 성공 시 201 상태 코드, 실패 시 401 상태 코드
     */
    @PostMapping("/add")
    public ResponseEntity<?> createChat(@RequestBody CreateChatForm createChatForm, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                Member findMember = ms.search(loginMember.getMemberId());

                if (findMember != null) {
                    Album fa = as.search(createChatForm.getAlbumId());

                    if (fa != null) {
                        Chat c = new Chat(findMember, createChatForm.getMessage(), fa);
                        cs.create(c);

                        return ResponseEntity.status(HttpStatus.CREATED).build();
                    }
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * 채팅 삭제
     *
     * @param chatDTO 채팅 DTO
     * @param request 요청
     * @return 성공 시 200 상태 코드, 실패 시 401 상태 코드
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deleteChat(@RequestBody ChatDTO chatDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                Member findMember = ms.search(loginMember.getMemberId());

                if (findMember != null) {
                    Chat fc = cs.search(chatDTO.getChatId());

                    if (fc != null) {
                        try {
                            cs.delete(findMember, fc);
                        } catch (AccessException e) {
                            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Writer");
                        }
                    }

                    return ResponseEntity.status(HttpStatus.OK).build();
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * 채팅 리스트
     *
     * @param albumId 앨범 id
     * @param request 요청
     * @return 성공 시 200 상태 코드와 채팅 리스트, 실패 시 401 상태 코드
     */
    @GetMapping("/list/albumId")
    public ResponseEntity<?> listChatsByAlbum(@RequestParam("albumId") Long albumId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");

            if (loginMember != null) {
                Member findMember = ms.search(loginMember.getMemberId());

                if (findMember != null) {
                    Album fa = as.search(albumId);

                    if (fa != null) {
                        List<Chat> chatList = cs.searchAll(fa);
                        List<ChatDTO> chatDTOList = new ArrayList<>();

                        for (Chat chat : chatList) {
                            ChatDTO dto = mapping(chat);
                            chatDTOList.add(dto);
                        }

                        return ResponseEntity.status(HttpStatus.OK).body(chatDTOList);
                    }
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
    }

    /**
     * Chat -> ChatDTO
     *
     * @param chat 채팅
     * @return ChatDTO
     */
    private ChatDTO mapping(Chat chat) {
        ChatDTO dto = new ChatDTO();
        dto.setChatId(chat.getId());
        dto.setMessage(chat.getMessage());
        dto.setTime(chat.getTime());
        dto.setWriterId(chat.getWriter().getId());
        dto.setWriterName(chat.getWriter().getName());
        return dto;
    }
}
