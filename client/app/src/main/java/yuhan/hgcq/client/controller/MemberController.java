package yuhan.hgcq.client.controller;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import yuhan.hgcq.client.model.dto.member.LoginForm;
import yuhan.hgcq.client.model.dto.member.SignupForm;
import yuhan.hgcq.client.model.dto.member.MemberUpdateForm;
import yuhan.hgcq.client.model.service.MemberService;

public class MemberController {

    private MemberService memberService;

    public void joinMember(SignupForm signupForm, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = memberService.joinMember(signupForm);
        call.enqueue(callback);
    }

    /**
     * 회원 이름 중복 체크 API 호출
     * name 회원 이름
     * callback 중복 체크 결과 콜백
     */
    public void duplicateName(String name, Callback<Boolean> callback) {
        Call<Boolean> call = memberService.duplicateName(name);
        call.enqueue(callback);
    }

    /**
     * 회원 이메일 중복 체크 API 호출
     * email 회원 이메일
     * callback 중복 체크 결과 콜백
     */
    public void duplicateEmail(String email, Callback<Boolean> callback) {
        Call<Boolean> call = memberService.duplicateEmail(email);
        call.enqueue(callback);
    }

    /**
     * 로그인 API 호출
     * memberDto 회원 정보 DTO
     * callback 로그인 결과 콜백
     */
    public void loginMember(LoginForm loginForm, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = memberService.loginMember(loginForm);
        call.enqueue(callback);
    }

    /**
     * 로그아웃 API 호출
     * callback 로그아웃 결과 콜백
     */
    public void logoutMember(Callback<ResponseBody> callback) {
        Call<ResponseBody> call = memberService.logoutMember();
        call.enqueue(callback);
    }

    /**
     * 회원 정보 수정 API 호출
     * memberDto 수정된 회원 정보 DTO
     * callback 수정 결과 콜백
     */
    public void updateMember(MemberUpdateForm updateForm, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = memberService.updateMember(updateForm);
        call.enqueue(callback);
    }

}
