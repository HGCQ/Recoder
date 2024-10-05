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
//
//    private final MemberService ms;
//    private final ChatService cs;
//    private final AlbumService as;
//
//    @PostMapping("/add")
//    public ResponseEntity<?> createChat(@RequestBody CreateChatForm createChatForm, HttpServletRequest request) {
//        HttpSession session = request.getSession(false);
//
//        if (session != null) {
//            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");
//
//            if (loginMember != null) {
//                try {
//                    Member findMember = ms.searchOne(loginMember.getMemberId());
//
//                    if (findMember != null) {
//                        Album fa = as.searchOne(createChatForm.getAlbumId());
//
//                        if (fa != null) {
//                            Chat c = new Chat(findMember, createChatForm.getMessage(), fa);
//                            try {
//                                cs.create(c);
//                                return ResponseEntity.status(HttpStatus.CREATED).body("Create Chat Success");
//                            } catch (IllegalArgumentException e) {
//                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Create Chat Fail");
//                            }
//                        }
//                    }
//                } catch (IllegalArgumentException e) {
//                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member Fail");
//                }
//            }
//        }
//
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
//    }
//
//    @PostMapping("/delete")
//    public ResponseEntity<?> deleteChat(@RequestBody ChatDTO chatDTO, HttpServletRequest request) {
//        HttpSession session = request.getSession(false);
//
//        if (session != null) {
//            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");
//
//            if (loginMember != null) {
//                try {
//                    Member findMember = ms.searchOne(loginMember.getMemberId());
//
//                    if (findMember != null) {
//                        try {
//                            Chat fc = cs.searchOne(chatDTO.getChatId());
//
//                            if (fc != null) {
//                                try {
//                                    cs.delete(findMember, fc);
//                                    return ResponseEntity.status(HttpStatus.OK).body("Delete Chat Success");
//                                } catch (AccessException e) {
//                                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Writer");
//                                } catch (IllegalArgumentException e) {
//                                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Delete Chat Fail");
//                                }
//                            }
//                        } catch (IllegalArgumentException e) {
//                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Chat Fail");
//                        }
//                    }
//                } catch (IllegalArgumentException e) {
//                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member Fail");
//                }
//            }
//        }
//
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
//    }
//
//    @GetMapping("/list/albumId")
//    public ResponseEntity<?> listChatsByAlbum(@RequestParam("albumId") Long albumId, HttpServletRequest request) {
//        HttpSession session = request.getSession(false);
//
//        if (session != null) {
//            MemberDTO loginMember = (MemberDTO) session.getAttribute("member");
//
//            if (loginMember != null) {
//                try {
//                    Member findMember = ms.searchOne(loginMember.getMemberId());
//
//                    if (findMember != null) {
//                        try {
//                            Album fa = as.searchOne(albumId);
//
//                            if (fa != null) {
//                                try {
//                                    List<Chat> chatList = cs.searchAll(fa);
//                                    List<ChatDTO> chatDTOList = new ArrayList<>();
//
//                                    for (Chat chat : chatList) {
//                                        ChatDTO dto = mapping(chat);
//                                        chatDTOList.add(dto);
//                                    }
//
//                                    return ResponseEntity.status(HttpStatus.OK).body(chatDTOList);
//                                } catch (IllegalArgumentException e) {
//                                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found ChatList Fail");
//                                }
//                            }
//                        } catch (IllegalArgumentException e) {
//                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Album Fail");
//                        }
//                    }
//                } catch (IllegalArgumentException e) {
//                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Found Member Fail");
//                }
//            }
//        }
//
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Login");
//    }
//
//    private ChatDTO mapping(Chat chat) {
//        ChatDTO dto = new ChatDTO();
//        dto.setChatId(chat.getId());
//        dto.setMessage(chat.getMessage());
//        dto.setTime(chat.getTime().toString());
//        dto.setWriterId(chat.getWriter().getId());
//        dto.setWriterName(chat.getWriter().getName());
//        return dto;
//    }
}
