package yuhan.hgcq.client.model.service;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import yuhan.hgcq.client.model.dto.member.LoginForm;
import yuhan.hgcq.client.model.dto.member.SignupForm;
import yuhan.hgcq.client.model.dto.member.UpdateForm;

public interface MemberService {

    @POST("/member/join")
    Call<ResponseBody> joinMember(@Body SignupForm signupForm);

    @POST("/member/login")
    Call<ResponseBody> loginMember(@Body LoginForm loginForm);

    @POST("/member/logout")
    Call<ResponseBody> logoutMember();

    @POST("/member/update")
    Call<ResponseBody> updateMember(@Body UpdateForm updateForm);

    @GET("/member/duplicate/name")
    Call<Boolean> duplicateName(@Query("name") String name);

    @GET("/member/duplicate/email")
    Call<Boolean> duplicateEmail(@Query("email") String email);
}
