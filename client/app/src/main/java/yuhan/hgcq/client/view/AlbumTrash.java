package yuhan.hgcq.client.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import yuhan.hgcq.client.R;
import yuhan.hgcq.client.adapter.AlbumTrashAdapter;
import yuhan.hgcq.client.controller.AlbumController;
import yuhan.hgcq.client.localDatabase.Repository.AlbumRepository;
import yuhan.hgcq.client.localDatabase.callback.Callback;
import yuhan.hgcq.client.model.dto.album.AlbumDTO;
import yuhan.hgcq.client.model.dto.album.DeleteCancelAlbumForm;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.dto.team.TeamDTO;

public class AlbumTrash extends AppCompatActivity {
    /* View */
    AppCompatButton recover;
    Button remove;
    TextView empty;
    RecyclerView albumTrashListView;
    BottomNavigationView navi;

    /* Adapter */
    AlbumTrashAdapter ata;

    /* 받아올 값 */
    boolean isPrivate;
    TeamDTO teamDTO;
    MemberDTO loginMember;

    /* http 통신 */
    AlbumController ac;

    /* Room DB */
    AlbumRepository ar;

    /* 메인 스레드 */
    Handler handler = new Handler(Looper.getMainLooper());

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
                }
                albumMainPage.putExtra("loginMember", loginMember);
                startActivity(albumMainPage);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar(); // actionBar 가져오기
        if (actionBar != null) {
            actionBar.setTitle("앨범 휴지통");
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c2dcff")));
            actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 활성화
        }


        EdgeToEdge.enable(this);
        /* Layout */
        setContentView(R.layout.activity_album_trash);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* 초기화 */
        ac = new AlbumController(this);

        ar = new AlbumRepository(this);

        empty = findViewById(R.id.empty);
        albumTrashListView = findViewById(R.id.albumTrashList);
        navi = findViewById(R.id.bottom_navigation_view);
        recover = findViewById(R.id.recover);
        remove =  findViewById(R.id.remove);

        /* 관련된 페이지 */
        Intent groupMainPage = new Intent(this, GroupMain.class);
        Intent albumMainPage = new Intent(this, AlbumMain.class);
        Intent friendListPage = new Intent(this, FriendList.class);
        Intent likePage = new Intent(this, Like.class);
        Intent myPage = new Intent(this, MyPage.class);

        /* 받아 올 값 */
        Intent getIntent = getIntent();
        isPrivate = getIntent.getBooleanExtra("isPrivate", false);
        teamDTO = (TeamDTO) getIntent.getSerializableExtra("teamDTO");
        loginMember = (MemberDTO) getIntent.getSerializableExtra("loginMember");

        /* 초기 설정 */

        /* 개인 */
        if (isPrivate) {
            ar.searchTrash(new Callback<List<AlbumDTO>>() {
                @Override
                public void onSuccess(List<AlbumDTO> result) {
                    if (result != null) {
                        if (result.isEmpty()) {
                            handler.post(() -> {
                                empty.setVisibility(View.VISIBLE);
                            });
                        } else {
                            handler.post(() -> {
                                empty.setVisibility(View.INVISIBLE);
                            });
                        }
                        ata = new AlbumTrashAdapter(result, AlbumTrash.this, isPrivate);
                        handler.post(() -> {
                            albumTrashListView.setAdapter(ata);
                        });
                    } else {
                        /* Toast 메시지 */
                    }
                }

                @Override
                public void onError(Exception e) {
                    /* Toast 메시지 */
                }
            });
        }
        /* 공유! */
        else {
            if (teamDTO != null) {
                Long teamId = teamDTO.getTeamId();
                ac.albumTrashList(teamId, new retrofit2.Callback<List<AlbumDTO>>() {
                    @Override
                    public void onResponse(Call<List<AlbumDTO>> call, Response<List<AlbumDTO>> response) {
                        if (response.isSuccessful()) {
                            List<AlbumDTO> albumTrashList = response.body();
                            if (albumTrashList.isEmpty()) {
                                handler.post(() -> {
                                    empty.setVisibility(View.VISIBLE);
                                });
                            } else {
                                handler.post(() -> {
                                    empty.setVisibility(View.INVISIBLE);
                                });
                            }
                            ata = new AlbumTrashAdapter(albumTrashList, AlbumTrash.this, isPrivate);
                            handler.post(() -> {
                                albumTrashListView.setAdapter(ata);
                            });
                        } else {
                            /* Toast 메시지 */
                        }
                    }

                    @Override
                    public void onFailure(Call<List<AlbumDTO>> call, Throwable t) {
                        /* Toast 메시지 */
                    }
                });
            }
        }
        remove.setOnClickListener(v->{
            List<Long> selectedItems = ata.getSelectedItems();
            onClick_setting_costume_save("정말로 삭제하시겠습니까?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(isPrivate){
                        ar.remove(selectedItems, new Callback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {
                                if (result != null) {
                                    handler.post(() -> {
                                        Toast.makeText(AlbumTrash.this, "삭제하였습니다.", Toast.LENGTH_SHORT).show();
                                        ata.removeAlbumsByIds(selectedItems);

                                        // Clear the selection state
                                        selectedItems.clear();
                                        // Notify adapter to refresh the UI
                                        ata.notifyDataSetChanged();
                                    });
                                } else {
                                    Toast.makeText(AlbumTrash.this, "삭제 실패", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                    }else{
                        ac.removeAlbum(new DeleteCancelAlbumForm(selectedItems), new retrofit2.Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                if (response.isSuccessful()) {
                                    handler.post(() -> {
                                        Toast.makeText(AlbumTrash.this,"삭제했습니다.", Toast.LENGTH_SHORT).show();
                                        ata.removeAlbumsByIds(selectedItems);

                                        // Clear the selection state
                                        selectedItems.clear();
                                        // Notify adapter to refresh the UI
                                        ata.notifyDataSetChanged();
                                    });
                                } else {
                                    handler.post(() -> {
                                        Toast.makeText(AlbumTrash.this,"삭제 실패", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });
                    }
                }

            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(AlbumTrash.this, "취소했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        /* 복구 */
        recover.setOnClickListener(v -> {
            List<Long> selectedItems = ata.getSelectedItems();
            onClick_setting_costume_save("복구하시겠습니까?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    /* 개인 */
                    if (isPrivate) {
                        ar.deleteCancel(selectedItems, new Callback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {
                                if (result != null) {
                                    Intent albumTrashPage = new Intent(AlbumTrash.this, AlbumTrash.class);
                                    albumTrashPage.putExtra("isPrivate", isPrivate);
                                    albumTrashPage.putExtra("loginMember", loginMember);
                                    albumTrashPage.putExtra("teamDTO", teamDTO);
                                    handler.post(() -> {
                                        Toast.makeText(AlbumTrash.this, "복구했습니다.", Toast.LENGTH_SHORT).show();
                                    });
                                    startActivity(albumTrashPage);
                                } else {
                                    Toast.makeText(AlbumTrash.this, "복구 실패", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e("recover Error", "Failed Recover: "+e );
                                Log.d("AlbumSelection", "Selected album IDs: " + selectedItems);

                            }
                        });
                    }
                    /* 공유 */
                    else {
                        ac.deleteCancelAlbum(new DeleteCancelAlbumForm(selectedItems), new retrofit2.Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    Intent AlbumTrashPage = new Intent(AlbumTrash.this, AlbumTrash.class);
                                    AlbumTrashPage.putExtra("isPrivate", isPrivate);
                                    AlbumTrashPage.putExtra("loginMember", loginMember);
                                    AlbumTrashPage.putExtra("teamDTO", teamDTO);
                                    handler.post(() -> {
                                        Toast.makeText(AlbumTrash.this, "복구했습니다.", Toast.LENGTH_SHORT).show();
                                    });
                                    startActivity(AlbumTrashPage);
                                } else {
                                    /* Toast 메시지 */
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                /* Toast 메시지 */
                            }
                        });
                    }
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(AlbumTrash.this, "취소했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        });

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
                        Toast.makeText(AlbumTrash.this, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AlbumTrash.this, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
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