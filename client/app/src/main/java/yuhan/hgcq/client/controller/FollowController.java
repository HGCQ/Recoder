package yuhan.hgcq.client.controller;

import android.content.Context;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import yuhan.hgcq.client.config.NetworkClient;
import yuhan.hgcq.client.model.dto.follow.FollowDTO;
import yuhan.hgcq.client.model.dto.follow.Follower;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.service.FollowService;

public class FollowController {

    private FollowService followService;

    public FollowController(Context context) {
        NetworkClient client = NetworkClient.getInstance(context.getApplicationContext());
        followService = client.getFollowService();
    }

    public void addFollow(FollowDTO followDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = followService.addFollow(followDTO);
        call.enqueue(callback);
    }

    public void deleteFollow(FollowDTO followDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = followService.deleteFollow(followDTO);
        call.enqueue(callback);
    }

    public void followingList(Callback<List<MemberDTO>> callback) {
        Call<List<MemberDTO>> call = followService.followingList();
        call.enqueue(callback);
    }

    public void searchFollowingByName(String name, Callback<List<MemberDTO>> callback) {
        Call<List<MemberDTO>> call = followService.searchFollowingByName(name);
        call.enqueue(callback);
    }

    public void inviteFollowingList(Long teamId, Callback<List<MemberDTO>> callback) {
        Call<List<MemberDTO>> call = followService.inviteFollowingList(teamId);
        call.enqueue(callback);
    }

    public void followerList(Callback<Follower> callback) {
        Call<Follower> call = followService.followerList();
        call.enqueue(callback);
    }

    public void searchFollowerByName(String name, Callback<Follower> callback) {
        Call<Follower> call = followService.searchFollowerByName(name);
        call.enqueue(callback);
    }
}
