package yuhan.hgcq.client.controller;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import yuhan.hgcq.client.model.dto.chat.ChatDTO;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.service.ChatService;
import yuhan.hgcq.client.model.service.FollowService;

public class FollowController {

    private FollowService followService;

    public void addFollow(MemberDTO memberDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = followService.addFollow(memberDTO);
        call.enqueue(callback);
    }

    public void deleteFollow(MemberDTO memberDTO, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = followService.deleteFollow(memberDTO);
        call.enqueue(callback);
    }

    public void followList(MemberDTO memberDTO, Callback<List<MemberDTO>> callback) {
        Call<List<MemberDTO>> call = followService.followList();
        call.enqueue(callback);
    }

    public void searchFollow(String name, Callback<List<MemberDTO>> callback) {
        Call<List<MemberDTO>> call = followService.searchFollow(name);
        call.enqueue(callback);
    }
}
