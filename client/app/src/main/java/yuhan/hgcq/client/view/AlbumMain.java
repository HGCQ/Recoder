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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import yuhan.hgcq.client.R;
import yuhan.hgcq.client.adapter.AlbumAdapter;
import yuhan.hgcq.client.controller.AlbumController;
import yuhan.hgcq.client.controller.PhotoController;
import yuhan.hgcq.client.localDatabase.Repository.AlbumRepository;
import yuhan.hgcq.client.localDatabase.Repository.PhotoRepository;
import yuhan.hgcq.client.localDatabase.callback.Callback;
import yuhan.hgcq.client.model.dto.album.AlbumDTO;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.dto.team.TeamDTO;

public class AlbumMain extends AppCompatActivity {

    /* View */ ImageButton search, auto, albumPlus, albumTrash;
    EditText searchText;
    TextView empty;
    RecyclerView albumListView;
    BottomNavigationView navi;

    /* Adapter */ AlbumAdapter aa;

    /* 개인, 공유 확인 */
    private boolean isPrivate;

    /* 받아올 값 */ TeamDTO teamDTO;
    MemberDTO loginMember;

    /* 서버와 통신 */ AlbumController ac;
    PhotoController pc;

    /* 로컬 DB */ AlbumRepository ar;
    PhotoRepository pr;

    /* Toast */ Handler handler = new Handler(Looper.getMainLooper());

    /* Request Code */
    private static final int GALLERY = 1000;
    private static final int REQUEST_PERMISSION = 1111;

    /* 뒤로 가기 */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isPrivate) {
                    Intent selectPage = new Intent(this, Select.class);
                    selectPage.putExtra("loginMember", loginMember);
                    startActivity(selectPage);
                } else {
                    Intent groupMainPage = new Intent(this, GroupMain.class);
                    groupMainPage.putExtra("loginMember", loginMember);
                    startActivity(groupMainPage);
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
        setContentView(R.layout.activity_album_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* 서버와 연결할 Controller 생성 */
        ac = new AlbumController(this);
        pc = new PhotoController(this);

        /* 로컬 DB 연결할 Repository 생성 */
        ar = new AlbumRepository(this);
        pr = new PhotoRepository(this);

        /* View와 Layout 연결 */
        search = findViewById(R.id.search);
        auto = findViewById(R.id.auto);
        albumPlus = findViewById(R.id.albumplus);
        albumTrash = findViewById(R.id.albumTrash);

        searchText = findViewById(R.id.searchText);

        empty = findViewById(R.id.empty);

        albumListView = findViewById(R.id.albumList);

        navi = findViewById(R.id.bottom_navigation_view);

        /* 관련된 페이지 */
        Intent groupMainPage = new Intent(this, GroupMain.class);
        Intent albumMainPage = new Intent(this, AlbumMain.class);
        Intent friendListPage = new Intent(this, FriendList.class);
        Intent likePage = new Intent(this, Like.class);
        Intent createAlbumPage = new Intent(this, CreateAlbum.class);
        Intent albumTrashPage = new Intent(this, AlbumTrash.class);
        Intent galleryPage = new Intent(this, Gallery.class);
        Intent myPage = new Intent(this, MyPage.class);

        /* 갤러리 */
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        gallery.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        gallery.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent getIntent = getIntent();
        /* 개인, 공유 확인 */
        isPrivate = getIntent.getBooleanExtra("isPrivate", false);

        /* 받아 올 값 */
        teamDTO = (TeamDTO) getIntent.getSerializableExtra("teamDTO");
        loginMember = (MemberDTO) getIntent.getSerializableExtra("loginMember");

        if (isPrivate) {
            getSupportActionBar().setTitle("개인 앨범");
        } else if (teamDTO != null) {
            getSupportActionBar().setTitle(teamDTO.getName());
        }

        /* 개인 초기 설정 */
        if (isPrivate) {
            ar.searchAll(new Callback<List<AlbumDTO>>() {
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
                        aa = new AlbumAdapter(result, AlbumMain.this, isPrivate);
                        handler.post(() -> {
                            albumListView.setAdapter(aa);
                        });
                        aa.setOnItemClickListener(new AlbumAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                AlbumDTO albumDTO = result.get(position);
                                galleryPage.putExtra("albumDTO", albumDTO);
                                galleryPage.putExtra("isPrivate", true);
                                startActivity(galleryPage);
                            }
                        });
                        Log.i("Found Private AlbumList", "Success");
                    } else {
                        Log.i("Found Private AlbumList", "Fail");
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.e("Found Private AlbumList Error", e.getMessage());
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
                            handler.post(() -> {
                                if (albumList.isEmpty()) {
                                    empty.setVisibility(View.VISIBLE);
                                } else {
                                    empty.setVisibility(View.INVISIBLE);
                                }
                            });

                            aa = new AlbumAdapter(albumList, AlbumMain.this, isPrivate);
                            handler.post(() -> {
                                albumListView.setAdapter(aa);
                            });
                            aa.setOnItemClickListener(new AlbumAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    AlbumDTO albumDTO = albumList.get(position);
                                    galleryPage.putExtra("albumDTO", albumDTO);
                                    galleryPage.putExtra("teamDTO", teamDTO);
                                    galleryPage.putExtra("loginMember", loginMember);
                                    startActivity(galleryPage);
                                }
                            });
                            Log.i("Found Shared AlbumList", "Success");
                        } else {
                            Log.i("Found Shared AlbumList", "Fail");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<AlbumDTO>> call, Throwable t) {
                        Log.e("Found Shared AlbumList Error", t.getMessage());
                    }
                });
            } else {
                Log.e("Intent Error", "teamDTO is Null");
            }
        }

        /* 이름 검색 눌림 */
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = searchText.getText().toString();

                /* 개인 */
                if (isPrivate) {
                    ar.searchByName("%" + text + "%", new Callback<List<AlbumDTO>>() {
                        @Override
                        public void onSuccess(List<AlbumDTO> result) {
                            if (result != null) {
                                handler.post(()->{
                                    aa.updateList(result);
                                });
                                if (result.isEmpty()) {
                                    handler.post(() -> {
                                        empty.setVisibility(View.VISIBLE);
                                    });
                                } else {
                                    handler.post(()->{
                                        empty.setVisibility(View.INVISIBLE);
                                    });
                                }

                                Log.i("Found Private Album By Name", "Success");
                            } else {
                                Log.i("Found Private Album By Name", "Fail");
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            handler.post(() -> {
                                Toast.makeText(AlbumMain.this, "앨범 검색에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            });
                            Log.e("Found Album By Name Error", e.getMessage());
                        }
                    });
                }
                /* 공유 */
                else {
                    if (teamDTO != null) {
                        Long teamId = teamDTO.getTeamId();
                        ac.searchAlbumByName(teamId, text, new retrofit2.Callback<List<AlbumDTO>>() {
                            @Override
                            public void onResponse(Call<List<AlbumDTO>> call, Response<List<AlbumDTO>> response) {
                                if (response.isSuccessful()) {
                                    List<AlbumDTO> albumList = response.body();

                                    if (albumList != null) {
                                        if (albumList.isEmpty()) {
                                            handler.post(()->{
                                                empty.setVisibility(View.VISIBLE);
                                            });
                                        } else {
                                            handler.post(()->{
                                                empty.setVisibility(View.INVISIBLE);
                                            });
                                        }
                                        handler.post(()->{
                                            aa.updateList(albumList);
                                        });
                                    }

                                    Log.i("Found Album By Name", "Success");
                                } else {
                                    handler.post(() -> {
                                        Toast.makeText(AlbumMain.this, "앨범 검색에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                    });
                                    Log.i("Found Album By Name", "Fail");
                                }
                            }

                            @Override
                            public void onFailure(Call<List<AlbumDTO>> call, Throwable t) {
                                handler.post(() -> {
                                    Toast.makeText(AlbumMain.this, "서버와 통신 실패했습니다. 네트워크를 확인해주세요.", Toast.LENGTH_SHORT).show();
                                });
                                Log.e("Found Album By Name Error", t.getMessage());
                            }
                        });
                    } else {
                        Log.e("Intent Error", "teamDTO is Null");
                    }
                }
            }
        });

        /* 사진 자동 분류 버튼 눌림 */
        auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        Toast.makeText(AlbumMain.this, "권한이 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        /* 앨범 생성 버튼 눌림 */
        albumPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 개인 */
                if (isPrivate) {
                    createAlbumPage.putExtra("isPrivate", true);
                }
                /* 공유 */
                else {
                    createAlbumPage.putExtra("teamDTO", teamDTO);
                    createAlbumPage.putExtra("loginMember", loginMember);
                }
                startActivity(createAlbumPage);
            }
        });

        /* 휴지통 버튼 눌림 */
        albumTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 개인 */
                if (isPrivate) {
                    albumTrashPage.putExtra("isPrivate", true);
                }
                /* 공유 */
                else {
                    albumTrashPage.putExtra("loginMember", loginMember);
                    albumTrashPage.putExtra("teamDTO", teamDTO);
                }
                startActivity(albumTrashPage);
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
                        startActivity(albumMainPage);
                    } else {
                        groupMainPage.putExtra("loginMember", loginMember);
                        startActivity(groupMainPage);
                    }
                    return true;
                } else if (itemId == R.id.fragment_friend) {
                    if (loginMember == null) {
                        handler.post(()->{
                            Toast.makeText(AlbumMain.this, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
                        });
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
                    //마이 페이지로 이동시키기
                    if (loginMember == null) {
                        handler.post(()->{
                            Toast.makeText(AlbumMain.this, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
                        });
                    } else {
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

                        PhotoMetaData metadata = getImageMetadata(imageUri);
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
                        pr.autoSave(paths, creates, new Callback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {
                                if (result) {
                                    handler.post(() -> {
                                        Toast.makeText(AlbumMain.this, "사진이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                    });
                                    Log.i("Photo Auto Save In Private Album", "Success");
                                } else {
                                    Log.i("Photo Auto Save In Private Album", "Fail");
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e("Photo Auto Save In Private Album", e.getMessage());
                            }
                        });
                    }
                    /* 공유 */
                    else {
                        if (teamDTO != null) {
                            Long teamId = teamDTO.getTeamId();
                            pc.autoSavePhoto(uriList, teamId, creates, new retrofit2.Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        handler.post(() -> {
                                            Toast.makeText(AlbumMain.this, "사진이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                        });
                                        Log.i("Photo Auto Save In Shared Album", "Success");
                                    } else {
                                        Log.i("Photo Auto Save In Shared Album", "Fail");
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Log.e("Photo Auto Save In Shared Album", t.getMessage());
                                }
                            });
                        } else {
                            Log.e("Intent Error", "teamDTO is Null");
                        }
                    }
                } else if (data.getData() != null) {
                    int count = data.getClipData().getItemCount();

                    List<Uri> uriList = new ArrayList<>(count);
                    List<String> paths = new ArrayList<>(count);
                    List<LocalDateTime> creates = new ArrayList<>(count);

                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        PhotoMetaData metadata = getImageMetadata(imageUri);
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
                        pr.autoSave(paths, creates, new Callback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {
                                if (result) {
                                    handler.post(() -> {
                                        Toast.makeText(AlbumMain.this, "사진이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                    });
                                    Log.i("Photo Auto Save In Private Album", "Success");
                                } else {
                                    Log.i("Photo Auto Save In Private Album", "Fail");
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e("Photo Auto Save In Private Album", e.getMessage());
                            }
                        });
                    }
                    /* 공유 */
                    else {
                        if (teamDTO != null) {
                            Long teamId = teamDTO.getTeamId();
                            pc.autoSavePhoto(uriList, teamId, creates, new retrofit2.Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        handler.post(() -> {
                                            Toast.makeText(AlbumMain.this, "사진이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                        });
                                        Log.i("Photo Auto Save In Shared Album", "Success");
                                    } else {
                                        Log.i("Photo Auto Save In Shared Album", "Fail");
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Log.e("Photo Auto Save In Shared Album", t.getMessage());
                                }
                            });
                        } else {
                            Log.e("Intent Error", "teamDTO is Null");
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

        public static PhotoMetaData create(String photoName, LocalDateTime created) {
            PhotoMetaData photoMetaData = new PhotoMetaData();
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
    private PhotoMetaData getImageMetadata(Uri imageUri) {
        String[] projection = {MediaStore.Images.Media.DISPLAY_NAME, // 사진 이름
                MediaStore.Images.Media.DATE_TAKEN, // 사진 날짜
        };

        try (Cursor cursor = getContentResolver().query(imageUri, projection, null, null, null)) {

            if (cursor != null && cursor.moveToFirst()) {
                String photoName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                long created = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN));

                LocalDateTime createdToLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(created), ZoneId.systemDefault());

                return PhotoMetaData.create(photoName, createdToLocalDateTime);
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