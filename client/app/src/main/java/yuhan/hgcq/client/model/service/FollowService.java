package yuhan.hgcq.client.model.service;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import yuhan.hgcq.client.model.dto.follow.FollowDTO;
import yuhan.hgcq.client.model.dto.follow.Follower;
import yuhan.hgcq.client.model.dto.member.MemberDTO;

public interface FollowService {

    @POST("/follow/add")
    Call<ResponseBody> addFollow(@Body FollowDTO followDTO);

    @POST("/follow/delete")
    Call<ResponseBody> deleteFollow(@Body FollowDTO followDTO);

    @GET("/follow/followinglist")
    Call<List<MemberDTO>> followingList();

    @GET("/follow/followinglist/name")
    Call<List<MemberDTO>> searchFollowingByName(@Query("name") String name);

    @GET("/follow/followinglist/teamId")
    Call<List<MemberDTO>> inviteFollowingList(@Query("teamId") Long teamId);

    @GET("/follow/followerlist")
    Call<Follower> followerList();

    @GET("/follow/followerlist/name")
    Call<Follower> searchFollowerByName(@Query("name") String name);
}
