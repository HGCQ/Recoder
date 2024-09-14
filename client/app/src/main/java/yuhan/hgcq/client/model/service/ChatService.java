package yuhan.hgcq.client.model.service;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import yuhan.hgcq.client.model.dto.chat.ChatDTO;
import yuhan.hgcq.client.model.dto.chat.CreateChatForm;

public interface ChatService {

    @POST("/chat/add")
    Call<ResponseBody> addChat(@Body CreateChatForm createChatForm);

    @POST("/chat/delete")
    Call<ResponseBody> deleteChat(@Body ChatDTO chatDTO);

    @GET("/chat/list/albumId")
    Call<List<ChatDTO>> chatList(@Query("albumId") Long albumId);
}
