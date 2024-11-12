package yuhan.hgcq.client.model.service;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import yuhan.hgcq.client.model.dto.member.LoginForm;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.dto.member.Members;
import yuhan.hgcq.client.model.dto.member.SignupForm;
import yuhan.hgcq.client.model.dto.member.MemberUpdateForm;

public interface MemberService {

    @POST("/member/join")
    Call<ResponseBody> joinMember(@Body SignupForm signupForm);

    @POST("/member/delete")
    Call<ResponseBody> deleteMember();

    @POST("/member/login")
    Call<MemberDTO> loginMember(@Body LoginForm loginForm);

    @GET("/member/logout")
    Call<ResponseBody> logoutMember();

    @POST("/member/update")
    Call<ResponseBody> updateMember(@Body MemberUpdateForm memberUpdateForm);

    @GET("/member/duplicate/email")
    Call<Boolean> duplicateEmail(@Query("email") String email);

    @GET("/member/duplicate/name")
    Call<Boolean> duplicateName(@Query("name") String name);

    @GET("/member/list")
    Call<Members> memberList();

    @GET("/member/list/name")
    Call<Members> memberListByName(@Query("name") String name);

    @GET("/member/islogin")
    Call<MemberDTO> isloginMember();

    @Multipart
    @POST("/member/upload")
    Call<ResponseBody> upload(@Part MultipartBody.Part file);

    @POST("/member/searched")
    Call<MemberDTO> searched();

    @GET("/member/me")
    Call<MemberDTO> me();
}
