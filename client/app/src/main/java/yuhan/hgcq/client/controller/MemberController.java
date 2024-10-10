package yuhan.hgcq.client.controller;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import yuhan.hgcq.client.config.NetworkClient;
import yuhan.hgcq.client.model.dto.member.LoginForm;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.dto.member.Members;
import yuhan.hgcq.client.model.dto.member.SignupForm;
import yuhan.hgcq.client.model.dto.member.MemberUpdateForm;
import yuhan.hgcq.client.model.service.MemberService;

public class MemberController {

    private MemberService memberService;
    private Context context;

    public MemberController(Context context) {
        this.context = context;
        NetworkClient client = NetworkClient.getInstance(context.getApplicationContext());
        memberService = client.getMemberService();
    }

    public void joinMember(SignupForm signupForm, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = memberService.joinMember(signupForm);
        call.enqueue(callback);
    }

    public void deleteMember(Callback<ResponseBody> callback) {
        Call<ResponseBody> call = memberService.deleteMember();
        call.enqueue(callback);
    }

    public void loginMember(LoginForm loginForm, Callback<MemberDTO> callback) {
        Call<MemberDTO> call = memberService.loginMember(loginForm);
        call.enqueue(callback);
    }

    public void logoutMember(Callback<ResponseBody> callback) {
        Call<ResponseBody> call = memberService.logoutMember();
        call.enqueue(callback);
    }

    public void updateMember(MemberUpdateForm memberUpdateForm, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = memberService.updateMember(memberUpdateForm);
        call.enqueue(callback);
    }

    public void duplicateEmail(String email, Callback<Boolean> callback) {
        Call<Boolean> call = memberService.duplicateEmail(email);
        call.enqueue(callback);
    }

    public void duplicateName(String name, Callback<Boolean> callback) {
        Call<Boolean> call = memberService.duplicateName(name);
        call.enqueue(callback);
    }

    public void memberList(Callback<Members> callback) {
        Call<Members> call = memberService.memberList();
        call.enqueue(callback);
    }

    public void memberListByName(String name, Callback<Members> callback) {
        Call<Members> call = memberService.memberListByName(name);
        call.enqueue(callback);
    }

    public void isloginMember(Callback<MemberDTO> callback) {
        Call<MemberDTO> call = memberService.isloginMember();
        call.enqueue(callback);
    }

    public void upload(Uri uri, Callback<ResponseBody> callback) {
        try {
            Bitmap thumbNail = getThumbNail(uri);

            if (thumbNail != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumbNail.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();

                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), imageBytes);
                MultipartBody.Part image = MultipartBody.Part.createFormData("file", getFileNameFromUri(uri), reqFile);
                Call<ResponseBody> call = memberService.upload(image);
                call.enqueue(callback);
            }
        } catch (Exception e) {
            Log.e("Upload Error", Objects.requireNonNull(e.getMessage()));
        }
    }

    public void changeSearched(Callback<MemberDTO> callback) {
        Call<MemberDTO> call = memberService.searched();
        call.enqueue(callback);
    }

    private String getFileNameFromUri(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DISPLAY_NAME };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
            cursor.close();
            return fileName;
        }
        return "unknown_file_name";
    }

    private Bitmap getThumbNail(Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.TITLE/*, MediaStore.Images.Media.ORIENTATION*/};

        ContentResolver cor = context.getContentResolver();
        Cursor cursor = cor.query(uri, filePathColumn, null, null, null);

        Bitmap thumbnail = null;
        if(cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            long ImageId = cursor.getLong(columnIndex);
            if(ImageId != 0) {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                thumbnail = MediaStore.Images.Thumbnails.getThumbnail(
                        context.getContentResolver(), ImageId,
                        MediaStore.Images.Thumbnails.MINI_KIND,
                        bmOptions);
            } else {
                Toast.makeText(context, "불러올수 없는 이미지 입니다.", Toast.LENGTH_LONG).show();
            }
            cursor.close();
        }
        return thumbnail;
    }
}
