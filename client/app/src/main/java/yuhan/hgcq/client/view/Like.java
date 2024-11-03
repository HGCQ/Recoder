package yuhan.hgcq.client.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
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

        // 액션바 설정
        ActionBar actionBar = getSupportActionBar(); // 액션바 가져오기
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true); // 커스텀 뷰 사용 허용
            actionBar.setDisplayShowTitleEnabled(false); // 기본 제목 비활성화
            actionBar.setDisplayHomeAsUpEnabled(true);

            Intent getIntent = getIntent();
            isPrivate = getIntent.getBooleanExtra("isPrivate", false);
            loginMember = (MemberDTO) getIntent.getSerializableExtra("loginMember");
            // 액션바 배경 색상 설정
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c2dcff")));

            // 커스텀 타이틀 텍스트뷰 설정
            TextView customTitle = new TextView(this);
            customTitle.setTextSize(20); // 텍스트 크기 조정
            customTitle.setTypeface(ResourcesCompat.getFont(this, R.font.hangle_l)); // 폰트 설정
            customTitle.setTextColor(getResources().getColor(R.color.white)); // 텍스트 색상 설정

            // 개인 또는 공유에 따라 제목 설정
            if (isPrivate) {
                customTitle.setText("[개인] 좋아요"); // 개인 좋아요 제목
            } else {
                customTitle.setText("[공유] 좋아요"); // 공유 좋아요 제목
            }

            actionBar.setCustomView(customTitle); // 커스텀 뷰 설정
        }

        // EdgeToEdge 설정 및 나머지 초기화 코드
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_like);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 초기화
        lc = new LikedController(this);
        pr = new PhotoRepository(this);
        empty = findViewById(R.id.empty);
        likeListView = findViewById(R.id.likeList);
        navi = findViewById(R.id.bottom_navigation_view);

        Intent getIntent = getIntent();
        isPrivate = getIntent.getBooleanExtra("isPrivate", false);
        loginMember = (MemberDTO) getIntent.getSerializableExtra("loginMember");

        loadLikes();

        // 네비게이션 설정
        navi.setOnNavigationItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();
            Intent targetIntent = null;

            if (itemId == R.id.fragment_home) {
                targetIntent = isPrivate ? new Intent(this, AlbumMain.class) : new Intent(this, GroupMain.class);
            } else if (itemId == R.id.fragment_friend) {
                targetIntent = new Intent(this, FriendList.class);
            } else if (itemId == R.id.fragment_like) {
                targetIntent = new Intent(this, Like.class);
            } else if (itemId == R.id.fragment_setting) {
                targetIntent = new Intent(this, MyPage.class);
            }

            if (targetIntent != null) {
                targetIntent.putExtra("loginMember", loginMember);
                targetIntent.putExtra("isPrivate", isPrivate);
                startActivity(targetIntent);
                return true;
            }
            return false;
        });
    }
    private void loadLikes() {
        if (isPrivate) {
            pr.searchByLike(new Callback<List<PhotoDTO>>() {
                @Override
                public void onSuccess(List<PhotoDTO> result) {
                    handler.post(() -> {
                        empty.setVisibility(result.isEmpty() ? View.VISIBLE : View.INVISIBLE);
                        la = new LikeAdapter(result, Like.this, isPrivate);
                        likeListView.setAdapter(la);
                        la.setOnItemClickListener((view, position) -> {
                            PhotoDTO dto = result.get(position); // 선택된 PhotoDTO 가져오기
                            Intent photoPage = new Intent(Like.this, Photo.class);
                            photoPage.putExtra("isPrivate", isPrivate);
                            photoPage.putExtra("isLike", true);
                            photoPage.putExtra("position", position);
                            photoPage.putExtra("loginMember", loginMember);
                            startActivity(photoPage);

                            // 커스텀 타이틀 설정
                            ActionBar actionBar = getSupportActionBar();
                            if (actionBar != null) {
                                TextView customTitle = (TextView) actionBar.getCustomView();
                                if (customTitle != null) {
                                    customTitle.setText("[공유자] " + dto.getMember()); // 공유자 이름 설정
                                }
                            }
                        });
                    });
                }

                @Override
                public void onError(Exception e) {
                    // Toast 메시지 처리
                }
            });
        } else {
            lc.likedList(new retrofit2.Callback<List<PhotoDTO>>() {
                @Override
                public void onResponse(Call<List<PhotoDTO>> call, Response<List<PhotoDTO>> response) {
                    if (response.isSuccessful()) {
                        List<PhotoDTO> likeList = response.body();
                        handler.post(() -> {
                            empty.setVisibility(likeList.isEmpty() ? View.VISIBLE : View.INVISIBLE);
                            la = new LikeAdapter(likeList, Like.this, isPrivate);
                            likeListView.setAdapter(la);
                            la.setOnItemClickListener((view, position) -> {
                                PhotoDTO dto = likeList.get(position); // 선택된 PhotoDTO 가져오기
                                Intent photoPage = new Intent(Like.this, Photo.class);
                                photoPage.putExtra("isLike", true);
                                photoPage.putExtra("position", position);
                                photoPage.putExtra("loginMember", loginMember);
                                startActivity(photoPage);

                                // 커스텀 타이틀 설정
                                ActionBar actionBar = getSupportActionBar();
                                if (actionBar != null) {
                                    TextView customTitle = (TextView) actionBar.getCustomView();
                                    if (customTitle != null) {
                                        customTitle.setText("[공유자] " + dto.getMember()); // 공유자 이름 설정
                                    }
                                }
                            });
                        });
                    } else {
                        // Toast 메시지 처리
                    }
                }
    @Override
                public void onFailure(Call<List<PhotoDTO>> call, Throwable t) {
                    // Toast 메시지 처리
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(isPrivate ? R.menu.menu_actionbar_icon_share : R.menu.menu_actionbar_icon_privated, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // 뒤로가기 버튼 ID
                finish(); // 현재 액티비티 종료
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
