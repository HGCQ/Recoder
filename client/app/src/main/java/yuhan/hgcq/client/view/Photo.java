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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yuhan.hgcq.client.R;
import yuhan.hgcq.client.adapter.ServerPhotoAdapter;
import yuhan.hgcq.client.controller.LikedController;
import yuhan.hgcq.client.controller.PhotoController;
import yuhan.hgcq.client.localDatabase.Repository.PhotoRepository;
import yuhan.hgcq.client.model.dto.album.AlbumDTO;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.dto.photo.LikedDTO;
import yuhan.hgcq.client.model.dto.photo.PhotoDTO;
import yuhan.hgcq.client.model.dto.team.TeamDTO;

public class Photo extends AppCompatActivity {

    /* View */
    ImageButton like, move, photoDelete;
    ViewPager2 photoView;
    BottomNavigationView navi;

    /* Adapter */
    ServerPhotoAdapter spa;

    /* 개인, 공유 확인 */
    boolean isPrivate;

    /* 받아올 값 */
    boolean isLike;
    MemberDTO loginMember;
    TeamDTO teamDTO;
    AlbumDTO albumDTO;
    PhotoDTO photoDTO;
    int position;

    /* 서버와 통신 */
    PhotoController pc;
    LikedController lc;

    /* 로컬 DB */
    PhotoRepository pr;

    /* Toast */
    Handler handler = new Handler(Looper.getMainLooper());

    /* 뒤로 가기 */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isLike) {
                    Intent likePage = new Intent(this, Like.class);
                    if (isPrivate) {
                        likePage.putExtra("isPrivate", true);
                    }
                    likePage.putExtra("loginMember", loginMember);
                    startActivity(likePage);
                } else {
                    Intent galleryPage = new Intent(this, Gallery.class);
                    if (isPrivate) {
                        galleryPage.putExtra("isPrivate", true);
                    }
                    galleryPage.putExtra("loginMember", loginMember);
                    galleryPage.putExtra("teamDTO", teamDTO);
                    galleryPage.putExtra("albumDTO", albumDTO);
                    startActivity(galleryPage);
                }
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("Recoder");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_photo);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* 서버와 연결할 Controller 생성 */
        pc = new PhotoController(this);
        lc = new LikedController(this);

        /* 로컬 DB 연결할 Repository 생성 */
        pr = new PhotoRepository(this);

        /* View와 Layout 연결 */
        like = findViewById(R.id.like);
        move = findViewById(R.id.move);
        photoDelete = findViewById(R.id.photoDelete);

        photoView = findViewById(R.id.viewPager);

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
        isLike = getIntent.getBooleanExtra("isLike", false);
        loginMember = (MemberDTO) getIntent.getSerializableExtra("loginMember");
        teamDTO = (TeamDTO) getIntent.getSerializableExtra("teamDTO");
        albumDTO = (AlbumDTO) getIntent.getSerializableExtra("albumDTO");
        photoDTO = (PhotoDTO) getIntent.getSerializableExtra("photoDTO");
        position = getIntent.getIntExtra("position", 0);

        /* 초기 설정 */
        if (isLike) {
            if (isPrivate) {
                getSupportActionBar().setTitle("개인 좋아요");
                pr.searchByLike(new yuhan.hgcq.client.localDatabase.callback.Callback<List<PhotoDTO>>() {
                    @Override
                    public void onSuccess(List<PhotoDTO> result) {
                        if (result != null) {
                            Log.i("Found Private LikeList", "Success");
                        } else {
                            Log.i("Found Private LikeList", "Fail");
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("Found Private LikeList Error", e.getMessage());
                    }
                });
            } else {
                getSupportActionBar().setTitle("공유 좋아요");
                lc.likedList(new Callback<List<PhotoDTO>>() {
                    @Override
                    public void onResponse(Call<List<PhotoDTO>> call, Response<List<PhotoDTO>> response) {
                        if (response.isSuccessful()) {
                            List<PhotoDTO> likeList = response.body();
                            spa = new ServerPhotoAdapter(likeList, Photo.this);
                            photoView.setAdapter(spa);
                            photoView.setCurrentItem(position, false);
                            Log.i("Found Shared LikeList", "Success");
                        } else {
                            Log.i("Found Shared LikeList", "Fail");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<PhotoDTO>> call, Throwable t) {
                        Log.i("Found Shared LikeList Error", t.getMessage());
                    }
                });
            }
        } else {
            if (albumDTO != null) {
                if (isPrivate) {
                    getSupportActionBar().setTitle("개인 사진");
                } else {
                    getSupportActionBar().setTitle("공유 사진");
                    pc.photoList(albumDTO.getAlbumId(), new Callback<List<PhotoDTO>>() {
                        @Override
                        public void onResponse(Call<List<PhotoDTO>> call, Response<List<PhotoDTO>> response) {
                            if (response.isSuccessful()) {
                                List<PhotoDTO> photoList = response.body();
                                int index = 0;
                                int size = photoList.size();
                                for (int i = 0; i < size; i++) {
                                    Long photo = photoList.get(i).getPhotoId();
                                    Long dto = photoDTO.getPhotoId();
                                    if (photo.equals(dto)) {
                                        index = i;
                                        break;
                                    }
                                }
                                spa = new ServerPhotoAdapter(photoList, Photo.this);
                                photoView.setAdapter(spa);
                                photoView.setCurrentItem(index, false);
                                Log.i("Found Shared PhotoList", "Success");
                            } else {
                                Log.i("Found Shared PhotoList", "Fail");
                            }
                        }

                        @Override
                        public void onFailure(Call<List<PhotoDTO>> call, Throwable t) {
                            Log.e("Found Shared PhotoList Error", t.getMessage());
                        }
                    });
                }
            }
        }

        /* 사진 초기 설정 */
        photoView.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                PhotoDTO dto;

                if (isPrivate) {
                    List<PhotoDTO> photoList = spa.getPhotoList();
                    dto = photoList.get(position);
                } else {
                    List<PhotoDTO> photoList = spa.getPhotoList();
                    dto = photoList.get(position);
                }

                if (dto.getLiked()) {
                    like.setImageResource(R.drawable.love);
                } else {
                    like.setImageResource(R.drawable.unlove);
                }
            }
        });

        /* 좋아요 눌림 */
        like.setOnClickListener(v -> {
            int index = photoView.getCurrentItem();

            if (isPrivate) {

            } else {
                List<PhotoDTO> likeList = spa.getPhotoList();
                PhotoDTO dto = likeList.get(index);
                if (dto.getLiked()) {
                    lc.deleteLiked(new LikedDTO(dto.getPhotoId()), new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                like.post(() -> {
                                    like.setImageResource(R.drawable.unlove);
                                });
                                handler.post(() -> {
                                    Toast.makeText(Photo.this, "좋아요를 취소했습니다.", Toast.LENGTH_SHORT).show();
                                });
                                dto.setLiked(false);
                                Log.i("Delete Like Shared Photo", "Success");
                            } else {
                                Log.i("Delete Like Shared Photo", "Fail");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.i("Delete Like Shared Photo Error", t.getMessage());
                        }
                    });
                } else {
                    lc.addLiked(new LikedDTO(dto.getPhotoId()), new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                like.post(() -> {
                                    like.setImageResource(R.drawable.love);
                                });
                                handler.post(() -> {
                                    Toast.makeText(Photo.this, "좋아요 했습니다.", Toast.LENGTH_SHORT).show();
                                });
                                dto.setLiked(true);
                                Log.i("Add Like Shared Photo", "Success");
                            } else {
                                Log.i("Add Like Shared Photo", "Fail");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.i("Add Like Shared Photo Error", t.getMessage());
                        }
                    });
                }
            }
        });

        /* 앨범 이동 눌림 */
        move.setOnClickListener(v -> {
            
        });

        /* 사진 삭제 눌림 */
        photoDelete.setOnClickListener(v -> {
            onClick_setting_costume_save("삭제하시겠습니까?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int index = photoView.getCurrentItem();

                    if (isPrivate) {

                    } else {
                        List<PhotoDTO> photoList = spa.getPhotoList();
                        PhotoDTO dto = photoList.get(index);
                        pc.deletePhoto(dto, new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    photoList.remove(index);
                                    photoView.getAdapter().notifyItemRemoved(index);
                                    photoView.getAdapter().notifyItemRangeChanged(index, photoList.size());

                                    int newItemPosition = index;
                                    if (index == photoList.size()) {
                                        newItemPosition = index - 1;
                                    }
                                    if (!photoList.isEmpty()) {
                                        photoView.setCurrentItem(newItemPosition, true);
                                    }
                                    handler.post(() -> {
                                        Toast.makeText(Photo.this, "사진을 삭제했습니다.", Toast.LENGTH_SHORT).show();
                                    });
                                    Log.i("Delete Shared Photo", "Success");
                                } else {
                                    handler.post(() -> {
                                        Toast.makeText(Photo.this, "사진 삭제가 실패했습니다.", Toast.LENGTH_SHORT).show();
                                    });
                                    Log.i("Delete Shared Photo", "Fail");
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                handler.post(() -> {
                                    Toast.makeText(Photo.this, "사진 삭제가 실패했습니다.", Toast.LENGTH_SHORT).show();
                                });
                                Log.e("Delete Shared Photo Error", t.getMessage());
                            }
                        });
                    }
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(Photo.this, "취소했습니다.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Photo.this, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Photo.this, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
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