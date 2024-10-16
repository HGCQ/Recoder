package yuhan.hgcq.client.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yuhan.hgcq.client.R;
import yuhan.hgcq.client.adapter.MemberAdapter;
import yuhan.hgcq.client.config.NetworkClient;
import yuhan.hgcq.client.controller.MemberController;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.dto.member.Members;

public class MyPage extends AppCompatActivity {
    /* View */
    ImageView profile;
    TextView name, email;
    ImageButton profileAdd;
    Button retouch, secession,logout;
    BottomNavigationView navi;

    /* http 통신 */
    MemberController mc;

    /* 받아올 값 */
    boolean isPrivate;
    MemberDTO loginMember;
    MemberAdapter ma;

    /* 서버 주소 */
    String serverIp = NetworkClient.getInstance(MyPage.this).getServerIp();

    /* 메인 스레드 */
    Handler handler = new Handler(Looper.getMainLooper());

    /* Intent 요청 코드 */
    private static final int GALLERY = 1000;
    private static final int REQUEST_PERMISSION = 1111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("내 정보");

        EdgeToEdge.enable(this);
        /* Layout */
        setContentView(R.layout.activity_my_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* 초기화 */
        mc = new MemberController(this);

        navi = findViewById(R.id.bottom_navigation_view);
        profile = findViewById(R.id.profile);
        profileAdd = findViewById(R.id.profileadd);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        retouch = findViewById(R.id.retouch);
        secession = findViewById(R.id.secession);
        logout=findViewById(R.id.logout);
        secession=findViewById(R.id.secession);

        /* 갤러리 */
        Intent gallery = new Intent(Intent.ACTION_PICK);
        gallery.setType("image/*");
        gallery.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        gallery.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        /* 관련된 페이지 */
        Intent modifyPage = new Intent(this, Modify.class);
        Intent myPage = new Intent(this, MyPage.class);
        Intent groupMainPage = new Intent(this, GroupMain.class);
        Intent friendListPage = new Intent(this, FriendList.class);
        Intent likePage = new Intent(this, Like.class);
        Intent loginPage=new Intent(this, Login.class);

        /* 받아올 값 */
        Intent getIntent = getIntent();
        isPrivate = getIntent.getBooleanExtra("isPrivate", false);
        loginMember = (MemberDTO) getIntent.getSerializableExtra("loginMember");

        /* 초기 설정 */
        if (loginMember != null) {
            name.setText(loginMember.getName());
            email.setText(loginMember.getEmail());
            String path = loginMember.getImage();
            if (path != null) {
                Log.i("path", path);
                Glide.with(MyPage.this)
                        .load(serverIp + path)
                        .into(profile);
            }
        }
           secession.setOnClickListener(v -> {
                mc.deleteMember(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful()){
                            NetworkClient.getInstance(MyPage.this.getApplicationContext()).deleteCookie();
                            Toast.makeText(MyPage.this,"회원 탈퇴 완료",Toast.LENGTH_SHORT).show();
                            startActivity(loginPage);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            });
        logout.setOnClickListener(v->{
            mc.logoutMember(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()){
                        String name=loginMember.getName();
                        NetworkClient.getInstance(MyPage.this.getApplicationContext()).deleteCookie();
                        Toast.makeText(MyPage.this,name+"님 로그아웃되었습니다.",Toast.LENGTH_SHORT).show();
                        startActivity(loginPage);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        });

        /* 프로필 추가 */
        profileAdd.setOnClickListener(v -> {
            /* Android 11 이상 */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    startActivityForResult(Intent.createChooser(gallery, "사진 선택"), GALLERY);
                } else {
                    Toast.makeText(MyPage.this, "권한이 없습니다.", Toast.LENGTH_SHORT).show();
                    Intent permission = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    permission.addCategory("android.intent.category.DEFAULT");
                    permission.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                    startActivityForResult(permission, REQUEST_PERMISSION);
                }
            }
            /* Android 11 미만 */
        });

        /* 정보 수정 */
        retouch.setOnClickListener(v -> {
            modifyPage.putExtra("loginMember", loginMember);
            startActivity(modifyPage);
        });

        /* 네비게이션 */
        navi.setOnNavigationItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.fragment_home) {
                if (isPrivate) {
                    groupMainPage.putExtra("isPrivate", true);
                    groupMainPage.putExtra("loginMember", loginMember);
                    startActivity(groupMainPage);
                } else {
                    groupMainPage.putExtra("loginMember", loginMember);
                    startActivity(groupMainPage);
                }
                return true;
            } else if (itemId == R.id.fragment_friend) {
                if (loginMember == null) {
                    Toast.makeText(MyPage.this, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    if (isPrivate) {
                        friendListPage.putExtra("isPrivate", true);
                    }
                    friendListPage.putExtra("loginMember", loginMember);
                    startActivity(friendListPage);
                }
                return true;
            } else if (itemId == R.id.fragment_like) {
                if (isPrivate) {
                    likePage.putExtra("isPrivate", true);
                }
                likePage.putExtra("loginMember", loginMember);
                startActivity(likePage);
                return true;
            } else if (itemId == R.id.fragment_setting) {
                if (loginMember == null) {
                    Toast.makeText(MyPage.this, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    if (isPrivate) {
                        myPage.putExtra("isPrivate", true);
                    }
                    myPage.putExtra("loginMember", loginMember);
                    startActivity(myPage);
                }
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /* 갤러리 */
        if (requestCode == GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                mc.upload(uri, new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            ResponseBody body = response.body();
                            try {
                                String path = body.string();
                                loginMember.setImage(path);
                            } catch (IOException e) {
                                handler.post(() -> {
                                    Toast.makeText(MyPage.this, "프로필 등록을 실패했습니다.", Toast.LENGTH_SHORT).show();
                                });
                            }
                            handler.post(() -> {
                                Toast.makeText(MyPage.this, "프로필 등록됐습니다.", Toast.LENGTH_SHORT).show();
                                Glide.with(MyPage.this)
                                        .load(uri)
                                        .into(profile);
                            });
                        } else {
                            handler.post(() -> {
                                Toast.makeText(MyPage.this, "프로필 등록을 실패했습니다.", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        handler.post(() -> {
                            Toast.makeText(MyPage.this, "서버와 통신을 실패했습니다.", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }
        }
    }

    /* confirm 창 */
    public void onClick_setting_costume_cancel(String message,
                                               DialogInterface.OnClickListener positive,
                                               DialogInterface.OnClickListener negative) {
        new AlertDialog.Builder(this)
                .setTitle("Recoder")
                .setMessage(message)
                .setIcon(R.drawable.album)
                .setPositiveButton(android.R.string.yes, positive)
                .setNegativeButton(android.R.string.no, negative)
                .show();
    }

    /* 화면 이벤트 처리 */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
