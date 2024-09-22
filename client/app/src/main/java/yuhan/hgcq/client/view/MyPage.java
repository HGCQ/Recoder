package yuhan.hgcq.client.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import yuhan.hgcq.client.R;
import yuhan.hgcq.client.controller.MemberController;
import yuhan.hgcq.client.model.dto.member.MemberDTO;

public class MyPage extends AppCompatActivity {

    /* View */
    ImageView profile;
    TextView name, email;
    ImageButton profileAdd, retouch;
    BottomNavigationView navi;
    /* 서버와 통신 */
    MemberController mc;
    /* 받아올 값 */
    boolean isPrivate;
    MemberDTO loginMember;
    /* Toast */
    Handler handler = new Handler(Looper.getMainLooper());

    /* Request Code */
    private static final int MODIFY_REQUEST_CODE = 1; // 요청 코드 정의

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("나의 정보");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* 서버와 연결할 Controller 생성 */
        mc = new MemberController(this);

        /* View와 Layout 연결 */
        navi = findViewById(R.id.bottom_navigation_view);
        profile = findViewById(R.id.profile);
        profileAdd = findViewById(R.id.profileadd);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        retouch = findViewById(R.id.retouch);

        /* 관련된 페이지 */
        Intent login = new Intent(this, Login.class);
        Intent join = new Intent(this, Join.class);
        Intent modify = new Intent(this, Modify.class);
        Intent myPage = new Intent(this, MyPage.class);
        Intent albumMainPage = new Intent(this, AlbumMain.class);
        Intent groupMainPage = new Intent(this, GroupMain.class);
        Intent friendListPage = new Intent(this, FriendList.class);
        Intent likePage = new Intent(this, Like.class);

        Intent getIntent = getIntent();

        /* 받아 올 값 */
        isPrivate = getIntent().getBooleanExtra("isPrivate", false);
        loginMember = (MemberDTO) getIntent().getSerializableExtra("loginMember");

        /* 공유 초기 설정 */
        if (loginMember != null) {
            name.setText(loginMember.getName());
            email.setText(loginMember.getEmail());
        }

        retouch.setOnClickListener(v -> {
            modify.putExtra("loginMember", loginMember);
            startActivityForResult(modify, MODIFY_REQUEST_CODE); // 수정된 코드
        });

        navi.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
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
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MODIFY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                loginMember = (MemberDTO) data.getSerializableExtra("updatedMember"); // 수정된 정보 받기

                name.setText(loginMember.getName());
                email.setText(loginMember.getEmail());
            }
        }
    }

    /* Confirm 창 */
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
