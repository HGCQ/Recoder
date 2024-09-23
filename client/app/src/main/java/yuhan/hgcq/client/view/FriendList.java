package yuhan.hgcq.client.view;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yuhan.hgcq.client.R;
import yuhan.hgcq.client.adapter.FollowAdapter;
import yuhan.hgcq.client.adapter.FollowerAdapter;
import yuhan.hgcq.client.adapter.FollowingAdapter;
import yuhan.hgcq.client.controller.FollowController;
import yuhan.hgcq.client.model.dto.follow.Follower;
import yuhan.hgcq.client.model.dto.member.MemberDTO;

public class FriendList extends AppCompatActivity {

    /* View */
    TextView empty;
    EditText searchText;
    ImageButton search, friendAdd;
    Button follower, following;
    RecyclerView friendListView;
    BottomNavigationView navi;

    /* Adapter */
    FollowerAdapter fra;
    FollowingAdapter fga;

    /* 서버와 통신 */
    FollowController fc;

    /* 개인 공유 확인 */
    boolean isPrivate;

    /* 받아올 값 */
    MemberDTO loginMember;

    /* 팔로워, 팔로우 버튼 구별 */
    boolean isFollower = false;

    /* Toast */
    Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("친구 목록");
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_friend_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* 서버와 연결할 Controller 생성 */
        fc = new FollowController(this);

        /* View와 Layout 연결 */
        empty = findViewById(R.id.empty);

        searchText = findViewById(R.id.searchText);

        search = findViewById(R.id.search);
        friendAdd = findViewById(R.id.friendAdd);

        follower = findViewById(R.id.follower);
        following = findViewById(R.id.following);

        friendListView = findViewById(R.id.friendList);

        navi = findViewById(R.id.bottom_navigation_view);

        /* 관련된 페이지 */
        Intent groupMainPage = new Intent(this, GroupMain.class);
        Intent albumMainPage = new Intent(this, AlbumMain.class);
        Intent friendListPage = new Intent(this, FriendList.class);
        Intent likePage = new Intent(this, Like.class);
        Intent myPage = new Intent(this, MyPage.class);
        Intent friendAddPage = new Intent(this, FriendAdd.class);

        Intent getIntent = getIntent();
        /* 받아 올 값 */
        isPrivate = getIntent.getBooleanExtra("isPrivate", false);
        loginMember = (MemberDTO) getIntent.getSerializableExtra("loginMember");

        /* 초기 설정 */
        fc.followingList(new Callback<List<MemberDTO>>() {
            @Override
            public void onResponse(Call<List<MemberDTO>> call, Response<List<MemberDTO>> response) {
                if (response.isSuccessful()) {
                    List<MemberDTO> followingList = response.body();
                    if (followingList.isEmpty()) {
                        handler.post(() -> {
                            empty.setVisibility(View.VISIBLE);
                        });
                    } else {
                        handler.post(() -> {
                            empty.setVisibility(View.INVISIBLE);
                        });
                    }
                    fga = new FollowingAdapter(FriendList.this, followingList);
                    handler.post(() -> {
                        friendListView.setAdapter(fga);
                    });
                    Log.i("Found FollowingList", "Success");
                } else {
                    Log.i("Found FollowingList", "Fail");
                }
            }

            @Override
            public void onFailure(Call<List<MemberDTO>> call, Throwable t) {
                Log.e("Found FollowingList Error", t.getMessage());
            }
        });

        /* 팔로워 버튼 눌림 */
        follower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fc.followerList(new Callback<Follower>() {
                    @Override
                    public void onResponse(Call<Follower> call, Response<Follower> response) {
                        if (response.isSuccessful()) {
                            Follower body = response.body();

                            List<MemberDTO> followerList = body.getFollowerList();
                            List<MemberDTO> followingList = body.getFollowingList();

                            for (MemberDTO dto : followingList) {
                                Log.i("followingList", dto.toString());
                            }

                            if (followerList.isEmpty()) {
                                handler.post(() -> {
                                    empty.setVisibility(View.VISIBLE);
                                });
                            } else {
                                handler.post(() -> {
                                    empty.setVisibility(View.INVISIBLE);
                                });
                            }
                            fra = new FollowerAdapter(FriendList.this, followerList, followingList);
                            handler.post(() -> {
                                friendListView.setAdapter(fra);
                            });
                            isFollower = true;
                            Log.i("Found FollowerList", "Success");
                        } else {
                            Log.i("Found FollowerList", "Fail");
                        }
                    }

                    @Override
                    public void onFailure(Call<Follower> call, Throwable t) {
                        Log.i("Found FollowerList Error", t.getMessage());
                    }
                });
            }
        });

        /* 팔로잉 버튼 눌림 */
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fc.followingList(new Callback<List<MemberDTO>>() {
                    @Override
                    public void onResponse(Call<List<MemberDTO>> call, Response<List<MemberDTO>> response) {
                        if (response.isSuccessful()) {
                            List<MemberDTO> followingList = response.body();
                            if (followingList.isEmpty()) {
                                handler.post(() -> {
                                    empty.setVisibility(View.VISIBLE);
                                });
                            } else {
                                handler.post(() -> {
                                    empty.setVisibility(View.INVISIBLE);
                                });
                            }
                            fga = new FollowingAdapter(FriendList.this, followingList);
                            handler.post(() -> {
                                friendListView.setAdapter(fga);
                            });
                            isFollower = false;
                            Log.i("Found FollowingList", "Success");
                        } else {
                            Log.i("Found FollowingList", "Fail");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<MemberDTO>> call, Throwable t) {
                        Log.e("Found FollowingList Error", t.getMessage());
                    }
                });
            }
        });

        /* 검색 눌림 */
        search.setOnClickListener(v -> {
            String name = searchText.getText().toString();
            if (isFollower) {
                fc.searchFollowerByName(name, new Callback<Follower>() {
                    @Override
                    public void onResponse(Call<Follower> call, Response<Follower> response) {
                        if (response.isSuccessful()) {
                            Follower body = response.body();

                            List<MemberDTO> followerList = body.getFollowerList();
                            List<MemberDTO> followingList = body.getFollowingList();

                            if (followerList.isEmpty()) {
                                handler.post(() -> {
                                    empty.setVisibility(View.VISIBLE);
                                });
                            } else {
                                handler.post(() -> {
                                    empty.setVisibility(View.INVISIBLE);
                                });
                            }
                            fra = new FollowerAdapter(FriendList.this, followerList, followingList);
                            handler.post(() -> {
                                friendListView.setAdapter(fra);
                            });
                            Log.i("Found FollowerList By Name", "Success");
                        } else {
                            Log.i("Found FollowerList By Name", "Fail");
                        }
                    }

                    @Override
                    public void onFailure(Call<Follower> call, Throwable t) {
                        Log.i("Found FollowerList By Name Error", t.getMessage());
                    }
                });
            } else {
                fc.searchFollowingByName(name, new Callback<List<MemberDTO>>() {
                    @Override
                    public void onResponse(Call<List<MemberDTO>> call, Response<List<MemberDTO>> response) {
                        if (response.isSuccessful()) {
                            List<MemberDTO> followingList = response.body();
                            if (followingList.isEmpty()) {
                                handler.post(() -> {
                                    empty.setVisibility(View.VISIBLE);
                                });
                            } else {
                                handler.post(() -> {
                                    empty.setVisibility(View.INVISIBLE);
                                });
                            }
                            handler.post(() -> {
                                fga.updateList(followingList);
                            });
                            Log.i("Found FollowingList", "Success");
                        } else {
                            Log.i("Found FollowingList", "Fail");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<MemberDTO>> call, Throwable t) {
                        Log.e("Found FollowingList Error", t.getMessage());
                    }
                });
            }
        });

        /* 친구 추가 버튼 눌림 */
        friendAdd.setOnClickListener(v -> {
            if (isPrivate) {
                friendAddPage.putExtra("isPrivate", isPrivate);
            }
            friendAddPage.putExtra("loginMember", loginMember);
            startActivity(friendAddPage);
        });

        /* 내비게이션 바 */
        navi.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.fragment_home) {
                    if (isPrivate) {
                        albumMainPage.putExtra("isPrivate", true);
                        startActivity(albumMainPage);
                    } else {
                        groupMainPage.putExtra("loginMember", loginMember);
                        startActivity(groupMainPage);
                    }
                    return true;
                } else if (itemId == R.id.fragment_friend) {
                    if (loginMember == null) {
                        Toast.makeText(FriendList.this, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
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
                        startActivity(likePage);
                    } else {
                        likePage.putExtra("loginMember", loginMember);
                        startActivity(likePage);
                    }
                    return true;
                } else if (itemId == R.id.fragment_setting) {
                    if (loginMember == null) {
                        Toast.makeText(FriendList.this, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
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

    /* Confirm 창 */
    public void onClick_setting_costume_save(String message,
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