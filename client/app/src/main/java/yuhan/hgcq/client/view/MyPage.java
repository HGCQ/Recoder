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
import yuhan.hgcq.client.config.NetworkClient;
import yuhan.hgcq.client.controller.MemberController;
import yuhan.hgcq.client.model.dto.member.MemberDTO;

public class MyPage extends AppCompatActivity {

    ImageView profile;
    TextView name, email;
    ImageButton profileAdd;
    Button retouch;
    BottomNavigationView navi;

    MemberController mc;

    boolean isPrivate;
    MemberDTO loginMember;
    String serverIp = NetworkClient.getInstance(MyPage.this).getServerIp();

    Handler handler = new Handler(Looper.getMainLooper());

    private static final int GALLERY = 1000;
    private static final int REQUEST_PERMISSION = 1111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("내 정보");
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mc = new MemberController(this);

        navi = findViewById(R.id.bottom_navigation_view);
        profile = findViewById(R.id.profile);
        profileAdd = findViewById(R.id.profileadd);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        retouch = findViewById(R.id.retouch);

        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        gallery.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        gallery.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent modifyPage = new Intent(this, Modify.class);
        Intent myPage = new Intent(this, MyPage.class);
        Intent groupMainPage = new Intent(this, GroupMain.class);
        Intent friendListPage = new Intent(this, FriendList.class);
        Intent likePage = new Intent(this, Like.class);

        Intent getIntent = getIntent();

        isPrivate = getIntent.getBooleanExtra("isPrivate", false);
        loginMember = (MemberDTO) getIntent.getSerializableExtra("loginMember");

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

        profileAdd.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
                Intent permission = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                permission.addCategory("android.intent.category.DEFAULT");
                permission.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                startActivityForResult(permission, REQUEST_PERMISSION);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    startActivityForResult(Intent.createChooser(gallery, "사진 선택"), GALLERY);
                } else {
                    Toast.makeText(MyPage.this, "권한이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        retouch.setOnClickListener(v -> {
            modifyPage.putExtra("loginMember", loginMember);
            startActivity(modifyPage);
        });

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
