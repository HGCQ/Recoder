package yuhan.hgcq.client.model.service;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import yuhan.hgcq.client.model.dto.chat.ChatDTO;

public interface ChatService {

    @POST("/chat/add")
    Call<ResponseBody> addChat(@Body ChatDTO chatDTO);

    @POST("/chat/delete")
    Call<ResponseBody> deleteChat(@Body ChatDTO chatDTO);

    @GET("/chat/list")
    Call<List<ChatDTO>> chatList();
}
