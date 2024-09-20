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
import android.widget.EditText;
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
import retrofit2.Response;
import yuhan.hgcq.client.R;
import yuhan.hgcq.client.adapter.AlbumAdapter;
import yuhan.hgcq.client.controller.AlbumController;
import yuhan.hgcq.client.localDatabase.Repository.AlbumRepository;
import yuhan.hgcq.client.localDatabase.callback.Callback;
import yuhan.hgcq.client.model.dto.album.AlbumDTO;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.dto.team.TeamDTO;

public class AlbumTrash extends AppCompatActivity {

    /* View */
    TextView empty;
    RecyclerView albumTrashListView;
    BottomNavigationView navi;

    /* Adapter */
    AlbumAdapter aa;

    /* 개인, 공유 확인 */
    boolean isPrivate;

    /* 받아올 값 */
    TeamDTO teamDTO;
    MemberDTO loginMember;

    /* 서버와 통신 */
    AlbumController ac;

    /* 로컬 DB */
    AlbumRepository ar;

    /* Toast */
    Handler handler = new Handler(Looper.getMainLooper());

    /* Request Code */

    /* 뒤로 가기 */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent albumMainPage = new Intent(this, AlbumMain.class);
                if (isPrivate) {
                    albumMainPage.putExtra("isPrivate", isPrivate);
                } else {
                    albumMainPage.putExtra("teamDTO", teamDTO);
                    albumMainPage.putExtra("loginMember", loginMember);
                }
                startActivity(albumMainPage);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("앨범 휴지통");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_album_trash);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* 서버와 연결할 Controller 생성 */
        ac = new AlbumController(this);

        /* 로컬 DB 연결할 Repository 생성 */
        ar = new AlbumRepository(this);

        /* View와 Layout 연결 */
        empty = findViewById(R.id.empty);

        albumTrashListView = findViewById(R.id.albumTrashList);

        navi = findViewById(R.id.bottom_navigation_view);

        /* 관련된 페이지 */
        Intent groupMainPage = new Intent(this, GroupMain.class);
        Intent albumMainPage = new Intent(this, AlbumMain.class);
        Intent friendListPage = new Intent(this, FriendList.class);
        Intent likePage = new Intent(this, Like.class);

        Intent getIntent = getIntent();
        /* 개인, 공유 확인 */
        isPrivate = getIntent.getBooleanExtra("isPrivate", false);

        /* 받아 올 값 */
        teamDTO = (TeamDTO) getIntent.getSerializableExtra("teamDTO");
        loginMember = (MemberDTO) getIntent.getSerializableExtra("loginMember");

        /* 개인 초기 설정 */
        if (isPrivate) {
            ar.searchTrash(new Callback<List<AlbumDTO>>() {
                @Override
                public void onSuccess(List<AlbumDTO> result) {
                    if (result != null) {
                        if (result.isEmpty()) {
                            empty.setVisibility(View.VISIBLE);
                        } else {
                            empty.setVisibility(View.INVISIBLE);
                        }
                        aa = new AlbumAdapter(result);
                        albumTrashListView.setAdapter(aa);
                        aa.setOnItemClickListener(new AlbumAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                            }
                        });
                        Log.i("Found Private AlbumTrashList", "Success");
                    } else {
                        Log.i("Found Private AlbumTrashList", "Fail");
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.e("Found Private AlbumTrashList Error", e.getMessage());
                }
            });
        }

        /* 공유 초기 설정 */
        else {
            if (teamDTO != null) {
                Long teamId = teamDTO.getTeamId();
                ac.albumList(teamId, new retrofit2.Callback<List<AlbumDTO>>() {
                    @Override
                    public void onResponse(Call<List<AlbumDTO>> call, Response<List<AlbumDTO>> response) {
                        if (response.isSuccessful()) {
                            List<AlbumDTO> albumList = response.body();
                            if (albumList.isEmpty()) {
                                empty.setVisibility(View.VISIBLE);
                            } else {
                                empty.setVisibility(View.INVISIBLE);
                            }
                            aa = new AlbumAdapter(albumList);
                            albumTrashListView.setAdapter(aa);
                            aa.setOnItemClickListener(new AlbumAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {

                                }
                            });
                            Log.i("Found Shared AlbumTrashList", "Success");
                        } else {
                            Log.i("Found Shared AlbumTrashList", "Fail");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<AlbumDTO>> call, Throwable t) {
                        Log.e("Found Shared AlbumTrashList Error", t.getMessage());
                    }
                });
            } else {
                Log.e("Intent Error", "teamDTO is Null");
            }
        }


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
                        Toast.makeText(AlbumTrash.this, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        if (isPrivate) {
                            friendListPage.putExtra("isPrivate", true);
                            friendListPage.putExtra("loginMember", loginMember);
                            startActivity(friendListPage);
                        } else {
                            friendListPage.putExtra("loginMember", loginMember);
                            startActivity(friendListPage);
                        }
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