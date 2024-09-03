package yuhan.hgcq.client.controller;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import yuhan.hgcq.client.model.dto.chat.ChatDTO;
import yuhan.hgcq.client.model.service.ChatService;

public class ChatController {

    private ChatService chatService;

    public void addChat(ChatDTO chatDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = chatService.addChat(chatDTO);
        call.enqueue(callback);
    }

    public void deleteChat(ChatDTO chatDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = chatService.deleteChat(chatDTO);
        call.enqueue(callback);
    }

    public void chatList(ChatDTO chatDTO, Callback<List<ChatDTO>> callback) {
        Call<List<ChatDTO>> call = chatService.chatList();
        call.enqueue(callback);
    }

}
