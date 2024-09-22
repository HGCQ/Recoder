package yuhan.hgcq.client.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import yuhan.hgcq.client.adapter.ServerLikeAdapter;
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
    ServerLikeAdapter la;

    /* 개인, 공유 확인 */
    boolean isPrivate;

    /* 받아올 값 */
    MemberDTO loginMember;

    /* 서버와 통신 */
    LikedController lc;

    /* 로컬 DB */
    PhotoRepository pr;

    /* Toast */
    Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("즐겨찾기");
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_like);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* 서버와 연결할 Controller 생성 */
        lc = new LikedController(this);

        /* 로컬 DB 연결할 Repository 생성 */
        pr = new PhotoRepository(this);

        /* View와 Layout 연결 */
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

        Intent getIntent = getIntent();
        /* 개인, 공유 확인 */
        isPrivate = getIntent().getBooleanExtra("isPrivate", false);

        /* 받아 올 값 */
        loginMember = (MemberDTO) getIntent.getSerializableExtra("loginMember");

        /* 개인 초기 설정 */
        if (isPrivate) {
            getSupportActionBar().setTitle("개인 앨범 좋아요 리스트");
            pr.searchByLike(new Callback<List<PhotoDTO>>() {
                @Override
                public void onSuccess(List<PhotoDTO> result) {
                    if (result.isEmpty()) {
                        empty.post(() -> {
                            empty.setVisibility(View.VISIBLE);
                        });
                    } else {
                        empty.post(() -> {
                            empty.setVisibility(View.INVISIBLE);
                        });
                    }
                    la = new ServerLikeAdapter(result, Like.this);
                    likeListView.setAdapter(la);
                    la.setOnItemClickListener(new ServerLikeAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            photoPage.putExtra("isPrivate", isPrivate);
                            photoPage.putExtra("isLike", true);
                            photoPage.putExtra("position", position);
                            photoPage.putExtra("loginMember", loginMember);
                            startActivity(photoPage);
                        }
                    });
                    Log.i("Found Private LikeList", "Success");
                }

                @Override
                public void onError(Exception e) {
                    Log.e("Found Private LikeList", e.getMessage());
                }
            });

        }

        /* 공유 초기 설정 */
        else {
            getSupportActionBar().setTitle("공유 그룹 좋아요 리스트");
            lc.likedList(new retrofit2.Callback<List<PhotoDTO>>() {
                @Override
                public void onResponse(Call<List<PhotoDTO>> call, Response<List<PhotoDTO>> response) {
                    if (response.isSuccessful()) {
                        List<PhotoDTO> likeList = response.body();
                        if (likeList.isEmpty()) {
                            empty.post(() -> {
                               empty.setVisibility(View.VISIBLE);
                            });
                        } else {
                            empty.post(() -> {
                                empty.setVisibility(View.INVISIBLE);
                            });
                        }
                        la = new ServerLikeAdapter(likeList, Like.this);
                        likeListView.setAdapter(la);
                        la.setOnItemClickListener(new ServerLikeAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                photoPage.putExtra("isLike", true);
                                photoPage.putExtra("position", position);
                                photoPage.putExtra("loginMember", loginMember);
                                startActivity(photoPage);
                            }
                        });
                        Log.i("Found Shared LikeList", "Success");
                    } else {
                        Log.i("Found Shared LikeList", "Fail");
                    }
                }

                @Override
                public void onFailure(Call<List<PhotoDTO>> call, Throwable t) {
                    Log.e("Found Shared LikeList Error", t.getMessage());
                }
            });
        }

        /* 내비게이션 바 */
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