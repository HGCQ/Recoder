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
import android.view.Menu;
import android.view.MenuItem;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yuhan.hgcq.client.R;
import yuhan.hgcq.client.adapter.AlbumAdapter;
import yuhan.hgcq.client.adapter.PhotoAdapter;
import yuhan.hgcq.client.controller.AlbumController;
import yuhan.hgcq.client.controller.LikedController;
import yuhan.hgcq.client.controller.PhotoController;
import yuhan.hgcq.client.localDatabase.Repository.AlbumRepository;
import yuhan.hgcq.client.localDatabase.Repository.PhotoRepository;
import yuhan.hgcq.client.model.dto.album.AlbumDTO;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.dto.photo.LikedDTO;
import yuhan.hgcq.client.model.dto.photo.MovePhotoForm;
import yuhan.hgcq.client.model.dto.photo.PhotoDTO;
import yuhan.hgcq.client.model.dto.team.TeamDTO;

public class Photo extends AppCompatActivity {
    /* View */
    ImageButton like;
    Button move, photoDelete;
    ViewPager2 photoView;
    BottomNavigationView navi;
    RecyclerView albumList;
    TextView albumListViewTop;
    ImageView albumListView;

    /* Adapter */
    PhotoAdapter pa;

    /* 받아올 값 */
    boolean isPrivate;
    boolean isLike;
    MemberDTO loginMember;
    TeamDTO teamDTO;
    AlbumDTO albumDTO;
    PhotoDTO photoDTO;
    int position;

    /* Adapter */
    AlbumAdapter aa;

    /* http 통신 */
    PhotoController pc;
    LikedController lc;
    AlbumController ac;

    /* Room DB */
    PhotoRepository pr;
    AlbumRepository ar;

    /* 메인 스레드 */
    Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(isPrivate){
            getMenuInflater().inflate(R.menu.menu_actionbar_icon_share, menu);
        }else {
            getMenuInflater().inflate(R.menu.menu_actionbar_icon_privated, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    /* 뒤로 가기 */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
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
        }else{
            Intent albumMainPage = new Intent(this, AlbumMain.class);
            albumMainPage.putExtra("isPrivate", true);
            if (loginMember != null) {
                albumMainPage.putExtra("loginMember", loginMember);
            }
            startActivity(albumMainPage);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar(); // actionBar 가져오기
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true); // 커스텀 뷰 사용 허용
            actionBar.setDisplayShowTitleEnabled(false); // 기본 제목 비활성화
            actionBar.setDisplayHomeAsUpEnabled(true);
            // 액션바 배경 색상 설정
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c2dcff")));

            // 커스텀 타이틀 텍스트뷰 설정
            TextView customTitle = new TextView(this);
            customTitle.setText("채팅"); // 제목 텍스트 설정
            customTitle.setTextSize(20); // 텍스트 크기 조정
            customTitle.setTypeface(ResourcesCompat.getFont(this, R.font.hangle_l)); // 폰트 설정
            customTitle.setTextColor(getResources().getColor(R.color.white)); // 텍스트 색상 설정

            // 커스텀 뷰 설정
            actionBar.setCustomView(customTitle);
        }

        EdgeToEdge.enable(this);
        /* Layout */
        setContentView(R.layout.activity_photo);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* 초기화 */
        pc = new PhotoController(this);
        lc = new LikedController(this);
        ac = new AlbumController(this);

        pr = new PhotoRepository(this);
        ar = new AlbumRepository(this);

        like = findViewById(R.id.like);
        move = findViewById(R.id.move);
        photoDelete = findViewById(R.id.photoDelete);
        photoView = findViewById(R.id.viewPager);
        albumList = findViewById(R.id.albumList);
        navi = findViewById(R.id.bottom_navigation_view);
        albumListViewTop = findViewById(R.id.albumListViewTop);
        albumListView = findViewById(R.id.albumListView);

        /* 관련된 페이지 */
        Intent groupMainPage = new Intent(this, GroupMain.class);
        Intent albumMainPage = new Intent(this, AlbumMain.class);
        Intent friendListPage = new Intent(this, FriendList.class);
        Intent likePage = new Intent(this, Like.class);
        Intent myPage = new Intent(this, MyPage.class);

        /* 받아 올 값 */
        Intent getIntent = getIntent();
        isPrivate = getIntent.getBooleanExtra("isPrivate", false);
        isLike = getIntent.getBooleanExtra("isLike", false);
        loginMember = (MemberDTO) getIntent.getSerializableExtra("loginMember");
        teamDTO = (TeamDTO) getIntent.getSerializableExtra("teamDTO");
        albumDTO = (AlbumDTO) getIntent.getSerializableExtra("albumDTO");
        photoDTO = (PhotoDTO) getIntent.getSerializableExtra("photoDTO");
        position = getIntent.getIntExtra("position", 0);

        /* 초기 설정 */

        /* 좋아요 */
        if (isLike) {
            /* 개인 */
            if (isPrivate) {
                getSupportActionBar().setTitle("[개인] 좋아요");
                pr.searchByLike(new yuhan.hgcq.client.localDatabase.callback.Callback<List<PhotoDTO>>() {
                    @Override
                    public void onSuccess(List<PhotoDTO> result) {
                        if (result != null) {
                            pa = new PhotoAdapter(result, Photo.this, isPrivate);
                            handler.post(() -> {
                                photoView.setAdapter(pa);
                                photoView.setCurrentItem(position, false);
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
            /* 공유 */
            else {
                getSupportActionBar().setTitle("[공유] 좋아요");
                lc.likedList(new Callback<List<PhotoDTO>>() {
                    @Override
                    public void onResponse(Call<List<PhotoDTO>> call, Response<List<PhotoDTO>> response) {
                        if (response.isSuccessful()) {
                            List<PhotoDTO> likeList = response.body();
                            pa = new PhotoAdapter(likeList, Photo.this, isPrivate);
                            handler.post(() -> {
                                photoView.setAdapter(pa);
                                photoView.setCurrentItem(position, false);
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
        }
        /* 갤러리 */
        else {
            if (albumDTO != null) {
                /* 개인 */
                if (isPrivate) {
                    getSupportActionBar().setTitle("[개인] " + albumDTO.getName());
                    pr.searchByAlbum(albumDTO.getAlbumId(), new yuhan.hgcq.client.localDatabase.callback.Callback<List<PhotoDTO>>() {
                        @Override
                        public void onSuccess(List<PhotoDTO> result) {
                            if (result != null) {
                                int index = 0;
                                int size = result.size();
                                for (int i = 0; i < size; i++) {
                                    Long photo = result.get(i).getPhotoId();
                                    Long dto = photoDTO.getPhotoId();
                                    if (photo.equals(dto)) {
                                        index = i;
                                        break;
                                    }
                                }
                                pa = new PhotoAdapter(result, Photo.this, isPrivate);
                                int finalIndex = index;
                                handler.post(() -> {
                                    photoView.setAdapter(pa);
                                    photoView.setCurrentItem(finalIndex, false);
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
                /* 공유 */
                else {
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
                                pa = new PhotoAdapter(photoList, Photo.this, isPrivate);
                                int finalIndex = index;
                                handler.post(() -> {
                                    photoView.setAdapter(pa);
                                    photoView.setCurrentItem(finalIndex, false);
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
            }
        }

        /* 사진 초기 설정 */
        photoView.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                PhotoDTO dto = null;

                if (isPrivate) {
                    List<PhotoDTO> photoList = pa.getPhotoList();
                    if (!photoList.isEmpty()) {
                        dto = photoList.get(position);
                    } else {
                        like.setImageResource(R.drawable.unlove);
                    }
                } else {
                    List<PhotoDTO> photoList = pa.getPhotoList();
                    if (!photoList.isEmpty()) {
                        dto = photoList.get(position);
                    } else {
                        like.setImageResource(R.drawable.unlove);
                    }
                }

                if (dto != null) {
                    ActionBar actionBar = getSupportActionBar();
                    if (actionBar != null && actionBar.getCustomView() != null) {
                        TextView customTitle = (TextView) actionBar.getCustomView(); // 커스텀 뷰에서 TextView 가져오기
                        customTitle.setText("[공유자] " + dto.getMember());
                    }
                    if (dto.getLiked()) {
                        like.setImageResource(R.drawable.love);
                    } else {
                        like.setImageResource(R.drawable.unlove);
                    }
                }

            }
        });

        /* 좋아요 */
        like.setOnClickListener(v -> {
            int index = photoView.getCurrentItem();

            /* 개인 */
            if (isPrivate) {
                List<PhotoDTO> photoList = pa.getPhotoList();
                PhotoDTO dto = photoList.get(index);
                if (dto.getLiked()) {
                    pr.Liked(dto.getPhotoId(), new yuhan.hgcq.client.localDatabase.callback.Callback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            if (result != null) {
                                if (result) {
                                    handler.post(() -> {
                                        like.setImageResource(R.drawable.unlove);
                                        Toast.makeText(Photo.this, "좋아요를 취소했습니다.", Toast.LENGTH_SHORT).show();
                                    });
                                    dto.setLiked(false);
                                } else {
                                    /* Toast 메시지 */
                                }
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            /* Toast 메시지 */
                        }
                    });
                } else {
                    pr.Liked(dto.getPhotoId(), new yuhan.hgcq.client.localDatabase.callback.Callback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            if (result != null) {
                                if (result) {
                                    handler.post(() -> {
                                        like.setImageResource(R.drawable.love);
                                        Toast.makeText(Photo.this, "좋아요 했습니다.", Toast.LENGTH_SHORT).show();
                                    });
                                    dto.setLiked(true);
                                } else {
                                    /* Toast 메시지 */
                                }
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            /* Toast 메시지 */
                        }
                    });
                }
            }
            /* 공유 */
            else {
                List<PhotoDTO> photoList = pa.getPhotoList();
                PhotoDTO dto = photoList.get(index);
                if (dto.getLiked()) {
                    lc.deleteLiked(new LikedDTO(dto.getPhotoId()), new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                handler.post(() -> {
                                    like.setImageResource(R.drawable.unlove);
                                    Toast.makeText(Photo.this, "좋아요를 취소했습니다.", Toast.LENGTH_SHORT).show();
                                });
                                dto.setLiked(false);
                            } else {
                                /* Toast 메시지 */
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            /* Toast 메시지 */
                        }
                    });
                } else {
                    lc.addLiked(new LikedDTO(dto.getPhotoId()), new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                handler.post(() -> {
                                    like.setImageResource(R.drawable.love);
                                    Toast.makeText(Photo.this, "좋아요 했습니다.", Toast.LENGTH_SHORT).show();
                                });
                                dto.setLiked(true);
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
        });

        /* 앨범 이동 */
        move.setOnClickListener(v -> {
            /* 개인 */
            if (isPrivate) {
                ar.searchMove(albumDTO.getAlbumId(), new yuhan.hgcq.client.localDatabase.callback.Callback<List<AlbumDTO>>() {
                    @Override
                    public void onSuccess(List<AlbumDTO> result) {
                        if (result != null) {
                            aa = new AlbumAdapter(result, Photo.this, isPrivate);
                            aa.setPhoto();
                            handler.post(() -> {
                                albumList.setAdapter(aa);
                                albumListView.setVisibility(View.VISIBLE);
                                albumListViewTop.setVisibility(View.VISIBLE);
                                albumList.setVisibility(View.VISIBLE);
                            });

                            aa.setOnItemClickListener(new AlbumAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    onClick_setting_costume_save("앨범 이동 하시겠습니까?", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            int index = photoView.getCurrentItem();
                                            AlbumDTO albumDTO1 = result.get(position);
                                            List<PhotoDTO> photoList = pa.getPhotoList();
                                            PhotoDTO dto = photoList.get(index);
                                            List<PhotoDTO> pls = new ArrayList<>();
                                            pls.add(dto);

                                            MovePhotoForm form = new MovePhotoForm(albumDTO1.getAlbumId(), pls);
                                            pr.move(form, new yuhan.hgcq.client.localDatabase.callback.Callback<Boolean>() {
                                                @Override
                                                public void onSuccess(Boolean result) {
                                                    if (result != null) {
                                                        photoList.remove(index);
                                                        handler.post(() -> {
                                                            photoView.getAdapter().notifyItemRemoved(index);
                                                            photoView.getAdapter().notifyItemRangeChanged(index, photoList.size());
                                                            Toast.makeText(Photo.this, "앨범 이동 했습니다.", Toast.LENGTH_SHORT).show();
                                                            albumListView.setVisibility(View.INVISIBLE);
                                                            albumList.setVisibility(View.INVISIBLE);
                                                            albumListViewTop.setVisibility(View.INVISIBLE);
                                                        });
                                                        int newItemPosition = index;
                                                        if (index == photoList.size()) {
                                                            if (index != 0) {
                                                                newItemPosition = index - 1;
                                                            }
                                                        }
                                                        if (!photoList.isEmpty()) {
                                                            int finalNewItemPosition = newItemPosition;
                                                            handler.post(() -> {
                                                                photoView.setCurrentItem(finalNewItemPosition, true);
                                                            });
                                                        }
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
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(Photo.this, "취소했습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
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
            /* 공유 */
            else {
                if (teamDTO != null) {
                    Long teamId = teamDTO.getTeamId();
                    ac.moveAlbumList(teamId, albumDTO.getAlbumId(), new retrofit2.Callback<List<AlbumDTO>>() {
                        @Override
                        public void onResponse(Call<List<AlbumDTO>> call, Response<List<AlbumDTO>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                List<AlbumDTO> albumList1 = response.body();
                                aa = new AlbumAdapter(albumList1, Photo.this, isPrivate);
                                aa.setPhoto();
                                handler.post(() -> {
                                    albumList.setAdapter(aa);
                                    albumList.setVisibility(View.VISIBLE);
                                    albumListViewTop.setVisibility(View.VISIBLE);
                                    albumListView.setVisibility(View.VISIBLE);
                                });

                                aa.setOnItemClickListener(new AlbumAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        onClick_setting_costume_save("앨범 이동 하시겠습니까?", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                int index = photoView.getCurrentItem();
                                                AlbumDTO albumDTO1 = albumList1.get(position);
                                                List<PhotoDTO> photoList = pa.getPhotoList();
                                                PhotoDTO dto = photoList.get(index);
                                                List<PhotoDTO> pls = new ArrayList<>();
                                                pls.add(dto);

                                                MovePhotoForm form = new MovePhotoForm(albumDTO1.getAlbumId(), pls);
                                                pc.moveAlbumPhoto(form, new Callback<ResponseBody>() {
                                                    @Override
                                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                        if (response.isSuccessful()) {
                                                            photoList.remove(index);
                                                            handler.post(() -> {
                                                                photoView.getAdapter().notifyItemRemoved(index);
                                                                photoView.getAdapter().notifyItemRangeChanged(index, photoList.size());
                                                                Toast.makeText(Photo.this, "앨범 이동 했습니다.", Toast.LENGTH_SHORT).show();
                                                                albumListView.setVisibility(View.INVISIBLE);
                                                                albumListViewTop.setVisibility(View.INVISIBLE);
                                                                albumList.setVisibility(View.INVISIBLE);
                                                            });
                                                            int newItemPosition = index;
                                                            if (index == photoList.size()) {
                                                                if (index != 0) {
                                                                    newItemPosition = index - 1;
                                                                }
                                                            }
                                                            if (!photoList.isEmpty()) {
                                                                int finalNewItemPosition = newItemPosition;
                                                                handler.post(() -> {
                                                                    photoView.setCurrentItem(finalNewItemPosition, true);
                                                                });
                                                            }
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
                                        }, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Toast.makeText(Photo.this, "취소했습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
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
        });


        /* 사진 삭제 */
        photoDelete.setOnClickListener(v -> {
            onClick_setting_costume_save("삭제하시겠습니까?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int index = photoView.getCurrentItem();

                    /* 개인 */
                    if (isPrivate) {
                        List<PhotoDTO> photoList = pa.getPhotoList();
                        PhotoDTO dto = photoList.get(index);

                        pr.delete(dto.getPhotoId(), new yuhan.hgcq.client.localDatabase.callback.Callback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {
                                if (result != null) {
                                    photoList.remove(index);
                                    handler.post(() -> {
                                        photoView.getAdapter().notifyItemRemoved(index);
                                        photoView.getAdapter().notifyItemRangeChanged(index, photoList.size());
                                    });

                                    int newItemPosition = index;
                                    if (index == photoList.size()) {
                                        if (index != 0) {
                                            newItemPosition = index - 1;
                                        }
                                    }
                                    if (!photoList.isEmpty()) {
                                        int finalNewItemPosition = newItemPosition;
                                        handler.post(() -> {
                                            photoView.setCurrentItem(finalNewItemPosition, true);
                                        });
                                    }
                                } else {
                                    handler.post(() -> {
                                        Toast.makeText(Photo.this, "사진 삭제가 실패했습니다.", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                handler.post(() -> {
                                    Toast.makeText(Photo.this, "사진 삭제가 실패했습니다.", Toast.LENGTH_SHORT).show();
                                });
                            }
                        });
                    }
                    /* 공유 */
                    else {
                        List<PhotoDTO> photoList = pa.getPhotoList();
                        PhotoDTO dto = photoList.get(index);
                        pc.deletePhoto(dto, new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    photoList.remove(index);
                                    handler.post(() -> {
                                        photoView.getAdapter().notifyItemRemoved(index);
                                        photoView.getAdapter().notifyItemRangeChanged(index, photoList.size());
                                    });

                                    int newItemPosition = index;
                                    if (index == photoList.size()) {
                                        newItemPosition = index - 1;
                                    }
                                    if (!photoList.isEmpty()) {
                                        int finalNewItemPosition = newItemPosition;
                                        handler.post(() -> {
                                            photoView.setCurrentItem(finalNewItemPosition, true);
                                        });
                                    }
                                    handler.post(() -> {
                                        Toast.makeText(Photo.this, "사진을 삭제했습니다.", Toast.LENGTH_SHORT).show();
                                    });
                                } else {
                                    handler.post(() -> {
                                        Toast.makeText(Photo.this, "사진 삭제가 실패했습니다.", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                handler.post(() -> {
                                    Toast.makeText(Photo.this, "사진 삭제가 실패했습니다.", Toast.LENGTH_SHORT).show();
                                });
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
            } else {
                Rect rect = new Rect();
                albumListView.getGlobalVisibleRect(rect);
                if (!rect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    handler.post(() -> {
                        albumListView.setVisibility(View.INVISIBLE);
                        albumList.setVisibility(View.INVISIBLE);
                        albumListViewTop.setVisibility(View.INVISIBLE);
                    });
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}