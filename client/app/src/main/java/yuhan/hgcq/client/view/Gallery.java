package yuhan.hgcq.client.view;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import yuhan.hgcq.client.R;
import yuhan.hgcq.client.adapter.GalleryAdapter;
import yuhan.hgcq.client.controller.PhotoController;
import yuhan.hgcq.client.localDatabase.Repository.PhotoRepository;
import yuhan.hgcq.client.localDatabase.callback.Callback;
import yuhan.hgcq.client.model.dto.album.AlbumDTO;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.dto.photo.PhotoDTO;
import yuhan.hgcq.client.model.dto.team.TeamDTO;

public class Gallery extends AppCompatActivity {

    /* View */
    TextView empty, date;
    ImageButton chat, move, photoPlus, photoTrash;
    RecyclerView photoListView, albumListView;
    BottomNavigationView navi;

    /* Adapter */
    GalleryAdapter ga;

    /* 받아올 값 */
    private boolean isPrivate;
    TeamDTO teamDTO;
    AlbumDTO albumDTO;
    MemberDTO loginMember;

    /* 서버와 통신 */
    PhotoController pc;

    /* 로컬 DB */
    PhotoRepository pr;

    /* Toast */
    Handler handler = new Handler(Looper.getMainLooper());

    /* Request Code */
    private static final int GALLERY = 1000;
    private static final int REQUEST_PERMISSION = 1111;

    /* 뒤로 가기 */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent albumMainPage = new Intent(this, AlbumMain.class);
                if (isPrivate) {
                    albumMainPage.putExtra("isPrivate", true);
                    if (loginMember != null) {
                        albumMainPage.putExtra("loginMember", loginMember);
                    }
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
        getSupportActionBar().setTitle("Recoder");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gallery);

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
        date = findViewById(R.id.date);

        chat = findViewById(R.id.chat);
        move = findViewById(R.id.move);
        photoPlus = findViewById(R.id.photoPlus);
        photoTrash = findViewById(R.id.photoTrash);

        photoListView = findViewById(R.id.photoList);
        albumListView = findViewById(R.id.albumList);

        navi = findViewById(R.id.bottom_navigation_view);

        /* 갤러리 */
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        gallery.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        gallery.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        /* 관련된 페이지 */
        Intent groupMainPage = new Intent(this, GroupMain.class);
        Intent albumMainPage = new Intent(this, AlbumMain.class);
        Intent friendListPage = new Intent(this, FriendList.class);
        Intent photoTrashPage = new Intent(this, PhotoTrash.class);
        Intent likePage = new Intent(this, Like.class);
        Intent chatPage = new Intent(this, Chat.class);
        Intent myPage = new Intent(this, MyPage.class);

        Intent getIntent = getIntent();
        /* 개인, 공유 확인 */
        isPrivate = getIntent.getBooleanExtra("isPrivate", false);

        /* 받아 올 값 */
        teamDTO = (TeamDTO) getIntent.getSerializableExtra("teamDTO");
        albumDTO = (AlbumDTO) getIntent.getSerializableExtra("albumDTO");
        loginMember = (MemberDTO) getIntent.getSerializableExtra("loginMember");

        if (albumDTO != null) {
            if (isPrivate) {
                getSupportActionBar().setTitle("개인 앨범 : " + albumDTO.getName());
                chat.setVisibility(View.INVISIBLE);
            } else {
                getSupportActionBar().setTitle("공유 앨범 : " + albumDTO.getName());
            }
            date.setText(albumDTO.getStartDate() + " ~ " + albumDTO.getEndDate());
        }

        if (albumDTO != null) {
            /* 개인 초기 설정 */
            if (isPrivate) {
                pr.gallery(albumDTO.getAlbumId(), new Callback<Map<String, List<PhotoDTO>>>() {
                    @Override
                    public void onSuccess(Map<String, List<PhotoDTO>> result) {
                        if (result != null) {
                            ga = new GalleryAdapter(result, Gallery.this, isPrivate, loginMember, albumDTO);
                            handler.post(() -> {
                                photoListView.setAdapter(ga);
                            });
                            Log.i("Found Private Gallery", "Success");
                        } else {
                            Log.i("Found Private Gallery", "Fail");
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("Found Private Gallery Error", e.getMessage());
                    }
                });
            }
            /* 공유 초기 설정 */
            else {
                pc.galleryList(albumDTO.getAlbumId(), new retrofit2.Callback<Map<String, List<PhotoDTO>>>() {
                    @Override
                    public void onResponse(Call<Map<String, List<PhotoDTO>>> call, Response<Map<String, List<PhotoDTO>>> response) {
                        if (response.isSuccessful()) {
                            Map<String, List<PhotoDTO>> galleryList = response.body();
                            ga = new GalleryAdapter(galleryList, Gallery.this, isPrivate, loginMember, teamDTO, albumDTO);
                            handler.post(() -> {
                                photoListView.setAdapter(ga);
                            });
                            Log.i("Found Shared Gallery", "Success");
                        } else {
                            Log.i("Found Shared Gallery", "Fail");
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, List<PhotoDTO>>> call, Throwable t) {
                        Log.i("Found Shared Gallery Error", t.getMessage());
                    }
                });
            }
        }

        /* 사진 추가 눌림 */
        photoPlus.setOnClickListener(v -> {
            /* 권한 확인 */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
                Intent permission = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                permission.addCategory("android.intent.category.DEFAULT");
                permission.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                startActivityForResult(permission, REQUEST_PERMISSION);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    startActivityForResult(Intent.createChooser(gallery, "사진 선택"), GALLERY);
                } else {
                    Toast.makeText(Gallery.this, "권한이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /* 사진 휴지통 눌림 */
        photoTrash.setOnClickListener(v -> {
            if (isPrivate) {
                photoTrashPage.putExtra("isPrivate", isPrivate);
            }
            photoTrashPage.putExtra("loginMember", loginMember);
            photoTrashPage.putExtra("teamDTO", teamDTO);
            photoTrashPage.putExtra("albumDTO", albumDTO);
            startActivity(photoTrashPage);
        });

        /* 앨범 이동 눌림 */
        move.setOnClickListener(v -> {

        });

        /* 채팅 눌림 */
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatPage.putExtra("loginMember", loginMember);
                chatPage.putExtra("teamDTO", teamDTO);
                chatPage.putExtra("albumDTO", albumDTO);
                startActivity(chatPage);
            }
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
                        Toast.makeText(Gallery.this, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Gallery.this, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
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

        if (requestCode == GALLERY && resultCode == RESULT_OK) {
            /* 선택된 이미지 처리 */
            if (data != null) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();

                    List<Uri> uriList = new ArrayList<>(count);
                    List<String> paths = new ArrayList<>(count);
                    List<LocalDateTime> creates = new ArrayList<>(count);

                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        AlbumMain.PhotoMetaData metadata = getImageMetadata(imageUri);
                        String path = imageUri.toString();
                        LocalDateTime created = metadata.getCreated();

                        Log.d("path", path);
                        Log.d("created", created.toString());

                        uriList.add(imageUri);
                        paths.add(path);
                        creates.add(created);
                    }

                    /* 개인 */
                    if (isPrivate) {
                        pr.create(albumDTO.getAlbumId(), paths, creates, new Callback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {
                                if (result) {
                                    handler.post(() -> {
                                        Intent galleryPage = new Intent(Gallery.this, Gallery.class);
                                        galleryPage.putExtra("loginMember", loginMember);
                                        galleryPage.putExtra("isPrivate", isPrivate);
                                        galleryPage.putExtra("teamDTO", teamDTO);
                                        galleryPage.putExtra("albumDTO", albumDTO);
                                        Toast.makeText(Gallery.this, "사진이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                        startActivity(galleryPage);
                                    });
                                    Log.i("Photo Upload In Private Album", "Success");
                                } else {
                                    Log.i("Photo Upload In Private Album", "Fail");
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e("Photo Upload In Private Album Error", e.getMessage());
                            }
                        });
                    }
                    /* 공유 */
                    else {
                        if (albumDTO != null) {
                            pc.uploadPhoto(albumDTO.getAlbumId(), uriList, creates, new retrofit2.Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        handler.post(() -> {
                                            Intent galleryPage = new Intent(Gallery.this, Gallery.class);
                                            galleryPage.putExtra("loginMember", loginMember);
                                            galleryPage.putExtra("isPrivate", isPrivate);
                                            galleryPage.putExtra("teamDTO", teamDTO);
                                            galleryPage.putExtra("albumDTO", albumDTO);
                                            Toast.makeText(Gallery.this, "사진이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                            startActivity(galleryPage);
                                        });
                                        Log.i("Photo Upload In Shared Album", "Success");
                                    } else {
                                        Log.i("Photo Upload In Shared Album", "Fail");
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Log.e("Photo Upload In Shared Album Error", t.getMessage());
                                }
                            });
                        } else {
                            Log.e("Intent Error", "AlbumDTO is Null");
                        }
                    }
                } else if (data.getData() != null) {
                    Uri imageUri = data.getData();
                    getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    List<Uri> uriList = new ArrayList<>();
                    List<String> paths = new ArrayList<>();
                    List<LocalDateTime> creates = new ArrayList<>();

                    AlbumMain.PhotoMetaData metadata = getImageMetadata(imageUri);
                    String path = imageUri.toString();
                    LocalDateTime created = metadata.getCreated();

                    Log.d("path", path);
                    Log.d("created", created.toString());

                    uriList.add(imageUri);
                    paths.add(path);
                    creates.add(created);

                    /* 개인 */
                    if (isPrivate) {
                        pr.create(albumDTO.getAlbumId(), paths, creates, new Callback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {
                                if (result) {
                                    handler.post(() -> {
                                        Intent galleryPage = new Intent(Gallery.this, Gallery.class);
                                        galleryPage.putExtra("loginMember", loginMember);
                                        galleryPage.putExtra("isPrivate", isPrivate);
                                        galleryPage.putExtra("teamDTO", teamDTO);
                                        galleryPage.putExtra("albumDTO", albumDTO);
                                        Toast.makeText(Gallery.this, "사진이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                        startActivity(galleryPage);
                                    });
                                    Log.i("Photo Upload In Private Album", "Success");
                                } else {
                                    Log.i("Photo Upload In Private Album", "Fail");
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e("Photo Upload In Private Album", e.getMessage());
                            }
                        });
                    }
                    /* 공유 */
                    else {
                        if (albumDTO != null) {
                            pc.uploadPhoto(albumDTO.getAlbumId(), uriList, creates, new retrofit2.Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        handler.post(() -> {
                                            Intent galleryPage = new Intent(Gallery.this, Gallery.class);
                                            galleryPage.putExtra("loginMember", loginMember);
                                            galleryPage.putExtra("isPrivate", isPrivate);
                                            galleryPage.putExtra("teamDTO", teamDTO);
                                            galleryPage.putExtra("albumDTO", albumDTO);
                                            Toast.makeText(Gallery.this, "사진이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                            startActivity(galleryPage);
                                        });
                                        Log.i("Photo Upload In Shared Album", "Success");
                                    } else {
                                        Log.i("Photo Upload In Shared Album", "Fail");
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Log.e("Photo Upload In Shared Album Error", t.getMessage());
                                }
                            });
                        } else {
                            Log.e("Intent Error", "albumDTO is Null");
                        }
                    }
                }
            }
        }
    }

    /* 사진 metadata 담을 내부 클래스 */
    static class PhotoMetaData {
        private String photoName;
        private LocalDateTime created;

        public static AlbumMain.PhotoMetaData create(String photoName, LocalDateTime created) {
            AlbumMain.PhotoMetaData photoMetaData = new AlbumMain.PhotoMetaData();
            photoMetaData.setPhotoName(photoName);
            photoMetaData.setCreated(created);
            return photoMetaData;
        }

        public String getPhotoName() {
            return photoName;
        }

        public void setPhotoName(String photoName) {
            this.photoName = photoName;
        }

        public LocalDateTime getCreated() {
            return created;
        }

        public void setCreated(LocalDateTime created) {
            this.created = created;
        }
    }

    /* 사진의 metadata 추출 */
    private AlbumMain.PhotoMetaData getImageMetadata(Uri imageUri) {
        String[] projection = {
                MediaStore.Images.Media.DISPLAY_NAME, // 사진 이름
                MediaStore.Images.Media.DATE_TAKEN, // 사진 날짜
        };

        try (Cursor cursor = getContentResolver().query(
                imageUri,
                projection,
                null,
                null,
                null)) {

            if (cursor != null && cursor.moveToFirst()) {
                String photoName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                long created = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN));

                LocalDateTime createdToLocalDateTime = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(created),
                        ZoneId.systemDefault()
                );

                return AlbumMain.PhotoMetaData.create(photoName, createdToLocalDateTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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