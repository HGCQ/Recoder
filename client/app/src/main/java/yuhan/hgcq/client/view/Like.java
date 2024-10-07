package yuhan.hgcq.client.view;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import yuhan.hgcq.client.R;
import yuhan.hgcq.client.adapter.LikeAdapter;
import yuhan.hgcq.client.controller.LikedController;
import yuhan.hgcq.client.localDatabase.Repository.PhotoRepository;
import yuhan.hgcq.client.localDatabase.callback.Callback;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.dto.photo.PhotoDTO;

public class Like extends AppCompatActivity {
    /* View */
    TextView empty;
    RecyclerView likeListView;
    BottomNavigationView navi;

    /* Adapter */
    LikeAdapter la;

    /* 받아올 값 */
    boolean isPrivate;
    MemberDTO loginMember;

    /* http 통신 */
    LikedController lc;

    /* Room DB */
    PhotoRepository pr;

    /* 메인 스레드 */
    Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("즐겨찾기");

        EdgeToEdge.enable(this);
        /* Layout */
        setContentView(R.layout.activity_like);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* 초기화 */
        lc = new LikedController(this);

        pr = new PhotoRepository(this);

        empty = findViewById(R.id.empty);
        likeListView = findViewById(R.id.likeList);
        navi = findViewById(R.id.bottom_navigation_view);

        /* 관련된 페이지 */
        Intent groupMainPage = new Intent(this, GroupMain.class);
        Intent albumMainPage = new Intent(this, AlbumMain.class);
        Intent friendListPage = new Intent(this, FriendList.class);
        Intent likePage = new Intent(this, Like.class);
        Intent myPage = new Intent(this, MyPage.class);
        Intent photoPage = new Intent(this, Photo.class);

        /* 받아 올 값 */
        Intent getIntent = getIntent();
        isPrivate = getIntent().getBooleanExtra("isPrivate", false);
        loginMember = (MemberDTO) getIntent.getSerializableExtra("loginMember");

        /* 초기 설정 */

        /* 개인 */
        if (isPrivate) {
            getSupportActionBar().setTitle("[개인] 좋아요");
            pr.searchByLike(new Callback<List<PhotoDTO>>() {
                @Override
                public void onSuccess(List<PhotoDTO> result) {
                    if (result.isEmpty()) {
                        handler.post(() -> {
                            empty.setVisibility(View.VISIBLE);
                        });
                    } else {
                        handler.post(() -> {
                            empty.setVisibility(View.INVISIBLE);
                        });
                    }
                    la = new LikeAdapter(result, Like.this, isPrivate);
                    handler.post(() -> {
                        likeListView.setAdapter(la);
                    });
                    la.setOnItemClickListener(new LikeAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            photoPage.putExtra("isPrivate", isPrivate);
                            photoPage.putExtra("isLike", true);
                            photoPage.putExtra("position", position);
                            photoPage.putExtra("loginMember", loginMember);
                            startActivity(photoPage);
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    /* Toast 메시지 */
                }
            });
        }
        /* 공유 */
        else {
            getSupportActionBar().setTitle("[공유] 좋아요");
            lc.likedList(new retrofit2.Callback<List<PhotoDTO>>() {
                @Override
                public void onResponse(Call<List<PhotoDTO>> call, Response<List<PhotoDTO>> response) {
                    if (response.isSuccessful()) {
                        List<PhotoDTO> likeList = response.body();
                        if (likeList.isEmpty()) {
                            handler.post(() -> {
                               empty.setVisibility(View.VISIBLE);
                            });
                        } else {
                            handler.post(() -> {
                                empty.setVisibility(View.INVISIBLE);
                            });
                        }
                        la = new LikeAdapter(likeList, Like.this, isPrivate);
                        handler.post(() -> {
                            likeListView.setAdapter(la);
                        });
                        la.setOnItemClickListener(new LikeAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                photoPage.putExtra("isLike", true);
                                photoPage.putExtra("position", position);
                                photoPage.putExtra("loginMember", loginMember);
                                startActivity(photoPage);
                            }
                        });
                    } else {
                        /* Toast 메시지 */
                    }
                }

                @Override
                public void onFailure(Call<List<PhotoDTO>> call, Throwable t) {
                    /* Toast 메시지 */
                }
            });
        }

        /* 네비게이션 */
        navi.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.fragment_home) {
                    if (isPrivate) {
                        albumMainPage.putExtra("isPrivate", true);
                        albumMainPage.putExtra("loginMember", loginMember);
                        startActivity(albumMainPage);
                    } else {
                        groupMainPage.putExtra("loginMember", loginMember);
                        startActivity(groupMainPage);
                    }
                    return true;
                } else if (itemId == R.id.fragment_friend) {
                    if (loginMember == null) {
                        Toast.makeText(Like.this, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Like.this, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
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