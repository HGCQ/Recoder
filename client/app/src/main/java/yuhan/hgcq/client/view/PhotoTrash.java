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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import yuhan.hgcq.client.R;
import yuhan.hgcq.client.adapter.PhotoTrashAdapter;
import yuhan.hgcq.client.controller.PhotoController;
import yuhan.hgcq.client.localDatabase.Repository.PhotoRepository;
import yuhan.hgcq.client.localDatabase.callback.Callback;
import yuhan.hgcq.client.model.dto.album.AlbumDTO;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.dto.photo.DeleteCancelPhotoForm;
import yuhan.hgcq.client.model.dto.photo.PhotoDTO;
import yuhan.hgcq.client.model.dto.team.TeamDTO;

public class PhotoTrash extends AppCompatActivity {

    /* View */
    TextView empty;
    ImageButton recover;
    RecyclerView photoTrashListView;
    BottomNavigationView navi;

    /* Adapter */
    PhotoTrashAdapter pa;

    /* 개인, 공유 확인 */
    boolean isPrivate;

    /* 받아올 값 */
    MemberDTO loginMember;
    TeamDTO teamDTO;
    AlbumDTO albumDTO;

    /* 서버와 통신 */
    PhotoController pc;

    /* 로컬 DB */
    PhotoRepository pr;

    /* Toast */
    Handler handler = new Handler(Looper.getMainLooper());

    /* 뒤로 가기 */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent galleryPage = new Intent(this, Gallery.class);
                if (isPrivate) {
                    galleryPage.putExtra("isPrivate", isPrivate);
                }
                galleryPage.putExtra("loginMember", loginMember);
                galleryPage.putExtra("teamDTO", teamDTO);
                galleryPage.putExtra("albumDTO", albumDTO);
                startActivity(galleryPage);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("사진 휴지통");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_photo_trash);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* 서버와 연결할 Controller 생성 */
        pc = new PhotoController(this);

        /* 로컬 DB 연결할 Repository 생성 */
        pr = new PhotoRepository(this);

        /* View와 Layout 연결 */
        empty = findViewById(R.id.empty);

        recover = findViewById(R.id.recover);

        photoTrashListView = findViewById(R.id.photoTrashList);

        navi = findViewById(R.id.bottom_navigation_view);

        /* 관련된 페이지 */
        Intent groupMainPage = new Intent(this, GroupMain.class);
        Intent albumMainPage = new Intent(this, AlbumMain.class);
        Intent friendListPage = new Intent(this, FriendList.class);
        Intent likePage = new Intent(this, Like.class);
        Intent myPage = new Intent(this, MyPage.class);

        Intent getIntent = getIntent();
        /* 개인, 공유 확인 */
        isPrivate = getIntent.getBooleanExtra("isPrivate", false);

        /* 받아 올 값 */
        loginMember = (MemberDTO) getIntent.getSerializableExtra("loginMember");
        teamDTO = (TeamDTO) getIntent.getSerializableExtra("teamDTO");
        albumDTO = (AlbumDTO) getIntent.getSerializableExtra("albumDTO");

        if (albumDTO != null) {
            getSupportActionBar().setTitle("휴지통 : " + albumDTO.getName());
            /* 개인 초기 설정 */
            if (isPrivate) {
                pr.searchTrash(new Callback<List<PhotoDTO>>() {
                    @Override
                    public void onSuccess(List<PhotoDTO> result) {
                        if (result != null) {
                            pa = new PhotoTrashAdapter(result, PhotoTrash.this, isPrivate);
                            handler.post(() -> {
                                photoTrashListView.setAdapter(pa);
                            });
                            Log.i("Found Private PhotoTrashList", "Success");
                        } else {
                            Log.i("Found Private PhotoTrashList", "Fail");
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("Found Private PhotoTrashList Error", e.getMessage());
                    }
                });
            }
            /* 공유 초기 설정 */
            else {
                pc.photoTrashList(albumDTO.getAlbumId(), new retrofit2.Callback<List<PhotoDTO>>() {
                    @Override
                    public void onResponse(Call<List<PhotoDTO>> call, Response<List<PhotoDTO>> response) {
                        if (response.isSuccessful()) {
                            List<PhotoDTO> photoTrashList = response.body();
                            pa = new PhotoTrashAdapter(photoTrashList, PhotoTrash.this, isPrivate);
                            handler.post(() -> {
                                photoTrashListView.setAdapter(pa);
                            });
                            Log.i("Found Shared PhotoTrashList", "Success");
                        } else {
                            Log.i("Found Shared PhotoTrashList", "Fail");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<PhotoDTO>> call, Throwable t) {
                        Log.e("Found Shared PhotoTrashList Error", t.getMessage());
                    }
                });
            }
        }

        /* 복구 버튼 눌림 */
        recover.setOnClickListener(v -> {
            List<Long> selectedItems = pa.getSelectedItems();
            onClick_setting_costume_save("복구하시겠습니까?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (isPrivate) {
                        pr.deleteCancel(selectedItems, new Callback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {
                                if (result != null) {
                                    Intent photoTrashPage = new Intent(PhotoTrash.this, PhotoTrash.class);
                                    photoTrashPage.putExtra("isPrivate", isPrivate);
                                    photoTrashPage.putExtra("loginMember", loginMember);
                                    photoTrashPage.putExtra("teamDTO", teamDTO);
                                    photoTrashPage.putExtra("albumDTO", albumDTO);
                                    handler.post(() -> {
                                        Toast.makeText(PhotoTrash.this, "복구했습니다.", Toast.LENGTH_SHORT).show();
                                    });
                                    startActivity(photoTrashPage);
                                    Log.i("Delete Cancel Private Photo", "Success");
                                } else {
                                    Log.i("Delete Cancel Private Photo", "Fail");
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e("Delete Cancel Private Photo Error", e.getMessage());
                            }
                        });
                    } else {
                        pc.cancelDeletePhoto(new DeleteCancelPhotoForm(selectedItems), new retrofit2.Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    Intent photoTrashPage = new Intent(PhotoTrash.this, PhotoTrash.class);
                                    photoTrashPage.putExtra("isPrivate", isPrivate);
                                    photoTrashPage.putExtra("loginMember", loginMember);
                                    photoTrashPage.putExtra("teamDTO", teamDTO);
                                    photoTrashPage.putExtra("albumDTO", albumDTO);
                                    handler.post(() -> {
                                        Toast.makeText(PhotoTrash.this, "복구했습니다.", Toast.LENGTH_SHORT).show();
                                    });
                                    startActivity(photoTrashPage);
                                    Log.i("Delete Cancel Shared Photo Error", "Success");
                                } else {
                                    Log.i("Delete Cancel Shared Photo Error", "Fail");
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Log.e("Delete Cancel Shared Photo Error", t.getMessage());
                            }
                        });
                    }
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(PhotoTrash.this, "취소했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        });

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
                        Toast.makeText(PhotoTrash.this, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(PhotoTrash.this, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
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