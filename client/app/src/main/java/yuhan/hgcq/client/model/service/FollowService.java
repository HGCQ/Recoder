package yuhan.hgcq.client.model.service;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import yuhan.hgcq.client.model.dto.member.MemberDTO;

public interface FollowService {

    @POST("/follow/add")
    Call<ResponseBody> addFollow(@Body MemberDTO memberDTO);

    @POST("/follow/delete")
    Call<ResponseBody> deleteFollow(@Body MemberDTO memberDTO);

    @GET("/follow/list")
    Call<List<MemberDTO>> followList();

    @GET("/follow/search")
    Call<List<MemberDTO>> searchFollow(@Query("name") String name);
}
