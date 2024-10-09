package yuhan.hgcq.client.view;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    /* View */
    ImageButton albumPlus;
    Button albumTrash, auto;
    EditText searchText;
    TextView empty;
    RecyclerView albumListView;
    BottomNavigationView navi;

    /* Adapter */
    AlbumAdapter aa;

    /* 받아올 값 */
    private boolean isPrivate;
    TeamDTO teamDTO;
    AlbumDTO albumDTO;
    MemberDTO loginMember;

    /* http 통신 */
    AlbumController ac;
    PhotoController pc;

    /* Room DB */
    AlbumRepository ar;
    PhotoRepository pr;

    /* 메인 스레드 */
    Handler handler = new Handler(Looper.getMainLooper());

    /* Intent 요청 코드 */
    private static final int GALLERY = 1000;
    private static final int REQUEST_PERMISSION = 1111;


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
        Intent loginPage = new Intent(this, Login.class);
        Intent selectPage = new Intent(this, Select.class);
        Intent groupMainPage = new Intent(this, GroupMain.class);


        if (item.getItemId() == android.R.id.home) {
            if (isPrivate) {
                selectPage.putExtra("loginMember", loginMember);
                startActivity(selectPage);
            } else {

                groupMainPage.putExtra("loginMember", loginMember);
                startActivity(groupMainPage);
            }
            finish();
            return true;
        }else {
            if (loginMember != null) {
                groupMainPage.putExtra("loginMember", loginMember);
                startActivity(groupMainPage);
            } else {
                startActivity(loginPage);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Recoder");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EdgeToEdge.enable(this);
        /* Layout */
        setContentView(R.layout.activity_album_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* 초기화 */
        ac = new AlbumController(this);
        pc = new PhotoController(this);

        ar = new AlbumRepository(this);
        pr = new PhotoRepository(this);

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
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        gallery.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        gallery.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        /* 받아올 값 */
        Intent getIntent = getIntent();
        isPrivate = getIntent.getBooleanExtra("isPrivate", false);
        teamDTO = (TeamDTO) getIntent.getSerializableExtra("teamDTO");
        loginMember = (MemberDTO) getIntent.getSerializableExtra("loginMember");

        /* 제목 */
        if (isPrivate) {
            getSupportActionBar().setTitle("[개인] 앨범");
        } else if (teamDTO != null) {
            getSupportActionBar().setTitle("[공유] 앨범\n" + teamDTO.getName());
        }

        /* 초기 설정 */

        /* 개인 */
        if (isPrivate) {


            ar.searchAll(new Callback<List<AlbumDTO>>() {
                @Override
                public void onSuccess(List<AlbumDTO> result) {
                    List<AlbumDTO> albumList = result;
                    if (albumList != null) {
                        if (albumList.isEmpty()) {
                            handler.post(() -> {
                                empty.setVisibility(View.VISIBLE);
                                aa.updateList(result);

                            });
                        } else {
                            handler.post(() -> {
                                empty.setVisibility(View.INVISIBLE);
                            });
                        }
                        aa = new AlbumAdapter(result, AlbumMain.this, isPrivate);
                        handler.post(() -> {
                            albumListView.setAdapter(aa);
                            albumListView.post(new Runnable() {
                                @Override
                                public void run() {
                                    int visibleItemCount = 4; // 화면에 보일 아이템 개수
                                    int itemHeight = getResources().getDimensionPixelSize(R.dimen.item_height); // 아이템 높이
                                    ViewGroup.LayoutParams params = albumListView.getLayoutParams();
                                    params.height = itemHeight * (visibleItemCount / 2);
                                    albumListView.setLayoutParams(params);
                                }
                            });
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
                        searchText.addTextChangedListener(new TextWatcher() {

                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                String text = s.toString();
                                ar.searchByName("%" + text + "%", new Callback<List<AlbumDTO>>() {
                                    @Override
                                    public void onSuccess(List<AlbumDTO> result) {
                                        List<AlbumDTO> albumList = result;

                                        // Check if album list is not null
                                        if (albumList != null) {
                                            // Update the empty view based on whether the album list is empty or not
                                            if (albumList.isEmpty()) {
                                                handler.post(() -> {
                                                    empty.setVisibility(View.VISIBLE); // Show empty if no albums found
                                                });
                                            } else {
                                                handler.post(() -> {
                                                    empty.setVisibility(View.INVISIBLE); // Hide empty if albums are found
                                                });
                                            }

                                            // Update the adapter with the new album list
                                            handler.post(() -> {
                                                aa.updateList(albumList);
                                            });
                                        }



                                    }
                                    @Override
                                    public void onError(Exception e) {
                                    }
                                });
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

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

                searchText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // No action needed here
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // Get the updated text every time the text changes
                        String text = s.toString();

                        // Perform the search in real-time whenever the user types
                        ac.searchAlbumByName(teamId, "%" + text + "%", new retrofit2.Callback<List<AlbumDTO>>() {
                            @Override
                            public void onResponse(Call<List<AlbumDTO>> call, Response<List<AlbumDTO>> response) {
                                if (response.isSuccessful()) {
                                    List<AlbumDTO> albumList = response.body();

                                    // Check if album list is not null
                                    if (albumList != null) {
                                        // Update the empty view based on whether the album list is empty or not
                                        if (albumList.isEmpty()) {
                                            handler.post(() -> {
                                                empty.setVisibility(View.VISIBLE); // Show empty if no albums found
                                            });
                                        } else {
                                            handler.post(() -> {
                                                empty.setVisibility(View.INVISIBLE); // Hide empty if albums are found
                                            });
                                        }

                                        // Update the adapter with the new album list
                                        handler.post(() -> {
                                            aa.updateList(albumList);
                                        });
                                    } else {
                                        Log.i("Found Private Album By Name", "Fail");
                                    }
                                } else {
                                    // Handle unsuccessful response
                                    Log.e("Search Error", "Failed to fetch albums: " + response.message());
                                }
                            }

                            @Override
                            public void onFailure(Call<List<AlbumDTO>> call, Throwable t) {
                                // Handle network or request failure
                                Log.e("Search Error", "Request failed: " + t.getMessage());
                                handler.post(() -> {
                                    Toast.makeText(AlbumMain.this, "앨범 검색에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                });
                            }
                        });
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // You can perform any additional actions after text changes if necessary
                    }
                });

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

        /* 자동 분류 */
        auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Android 11 이상 */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        startActivityForResult(Intent.createChooser(gallery, "사진 선택"), GALLERY);
                    } else {
                        Toast.makeText(AlbumMain.this, "권한이 없습니다.", Toast.LENGTH_SHORT).show();
                        Intent permission = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                        permission.addCategory("android.intent.category.DEFAULT");
                        permission.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                        startActivityForResult(permission, REQUEST_PERMISSION);
                    }
                }
                /* Android 11 미만 */
            }
        });

        /* 앨범 생성 */
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

        /* 앨범 휴지통 */
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
                        Toast.makeText(AlbumMain.this, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AlbumMain.this, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
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

        /* 갤러리 */
        if (requestCode == GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                /* 사진 여러 장 */
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();

                    List<Uri> uriList = new ArrayList<>(count);
                    List<String> paths = new ArrayList<>(count);
                    List<LocalDateTime> creates = new ArrayList<>(count);
                    List<String> regions = new ArrayList<>();

                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();

                        PhotoMetaData metadata = getImageMetadata(imageUri);
                        if (metadata != null) {
                            String path = imageUri.toString();
                            LocalDateTime created = metadata.getCreated();
                            String region = metadata.getRegion();

                            uriList.add(imageUri);
                            paths.add(path);
                            creates.add(created);
                            regions.add(region);
                        }
                    }

                    /* 개인 */
                    if (isPrivate) {
                        /* 권한 요청 */
                        for (Uri uri : uriList) {
                            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }

                        pr.autoSave(paths, creates, new Callback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {
                                if (result) {
                                    handler.post(() -> {
                                        Toast.makeText(AlbumMain.this, "사진이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                    });
                                    Intent albumMainPage = new Intent(AlbumMain.this, AlbumMain.class);
                                    albumMainPage.putExtra("isPrivate", true);
                                    albumMainPage.putExtra("loginMember", loginMember);
                                    albumMainPage.putExtra("teamDTO", teamDTO);
                                    startActivity(albumMainPage);
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
                            pc.autoSavePhoto(uriList, teamId, creates, regions, new retrofit2.Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        handler.post(() -> {
                                            Toast.makeText(AlbumMain.this, "사진이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                        });
                                        Intent albumMainPage = new Intent(AlbumMain.this, AlbumMain.class);
                                        albumMainPage.putExtra("loginMember", loginMember);
                                        albumMainPage.putExtra("teamDTO", teamDTO);
                                        startActivity(albumMainPage);
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
                }
                /* 사진 한 장 */
                else if (data.getData() != null) {
                    Uri imageUri = data.getData();

                    List<Uri> uriList = new ArrayList<>();
                    List<String> paths = new ArrayList<>();
                    List<LocalDateTime> creates = new ArrayList<>();
                    List<String> regions = new ArrayList<>();

                    PhotoMetaData metadata = getImageMetadata(imageUri);

                    if (metadata != null) {
                        String path = imageUri.toString();
                        LocalDateTime created = metadata.getCreated();
                        String region = metadata.getRegion();

                        uriList.add(imageUri);
                        paths.add(path);
                        creates.add(created);
                        regions.add(region);
                    }

                    /* 개인 */
                    if (isPrivate) {
                        /* 권한 요청 */
                        getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        pr.autoSave(paths, creates, new Callback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {
                                if (result) {
                                    handler.post(() -> {
                                        Toast.makeText(AlbumMain.this, "사진이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                    });
                                    Intent albumMainPage = new Intent(AlbumMain.this, AlbumMain.class);
                                    albumMainPage.putExtra("isPrivate", true);
                                    albumMainPage.putExtra("loginMember", loginMember);
                                    albumMainPage.putExtra("teamDTO", teamDTO);
                                    startActivity(albumMainPage);
                                } else {
                                    /* Toast 메시지 추가 */
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                /* Toast 메시지 추가 */
                            }
                        });
                    }
                    /* 공유 */
                    else {
                        if (teamDTO != null) {
                            Long teamId = teamDTO.getTeamId();
                            pc.autoSavePhoto(uriList, teamId, creates, regions, new retrofit2.Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        handler.post(() -> {
                                            Toast.makeText(AlbumMain.this, "사진이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                        });
                                        Intent albumMainPage = new Intent(AlbumMain.this, AlbumMain.class);
                                        albumMainPage.putExtra("loginMember", loginMember);
                                        albumMainPage.putExtra("teamDTO", teamDTO);
                                        startActivity(albumMainPage);
                                    } else {
                                        /* Toast 메시지 추가 */
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    /* Toast 메시지 추가 */
                                }
                            });
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
        private String region;

        public static PhotoMetaData create(String photoName, LocalDateTime created, String region) {
            PhotoMetaData photoMetaData = new PhotoMetaData();
            photoMetaData.setPhotoName(photoName);
            photoMetaData.setCreated(created);
            photoMetaData.setRegion(region);
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

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }
    }

    /* 사진 metadata 추출 */
    private PhotoMetaData getImageMetadata(Uri imageUri) {
        String photoName = null;
        LocalDateTime createdToLocalDateTime = null;
        Double latitude = null;
        Double longitude = null;
        String cityName = null;

        String[] projection = {MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
        };

        ContentResolver resolver = getContentResolver();

        /* 날짜 추출 */
        try (Cursor cursor = resolver.query(imageUri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                photoName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                long created = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN));

                createdToLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(created), ZoneId.systemDefault());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (InputStream inputStream = resolver.openInputStream(imageUri)) {
            Metadata metadata = ImageMetadataReader.readMetadata(inputStream);

            /* 위도 경도 추출 */
            GpsDirectory directory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
            if (directory != null) {
                latitude = directory.getGeoLocation().getLatitude();
                longitude = directory.getGeoLocation().getLongitude();
            }

            /* 도시 추출 */
            if (latitude != null && longitude != null) {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.KOREAN);
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    cityName = address.getLocality();
                    if (cityName == null) {
                        cityName = address.getSubLocality();
                    }
                }
            }

            if (photoName != null && createdToLocalDateTime != null) {
                if (cityName == null) {
                    cityName = "null";
                }
                return PhotoMetaData.create(photoName, createdToLocalDateTime, cityName);
            }
        } catch (Exception e) {

        }

        return null;
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