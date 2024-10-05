package yuhan.hgcq.client.view;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
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
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import yuhan.hgcq.client.R;
import yuhan.hgcq.client.adapter.AlbumAdapter;
import yuhan.hgcq.client.adapter.GalleryAdapter;
import yuhan.hgcq.client.controller.AlbumController;
import yuhan.hgcq.client.controller.PhotoController;
import yuhan.hgcq.client.localDatabase.Repository.PhotoRepository;
import yuhan.hgcq.client.localDatabase.callback.Callback;
import yuhan.hgcq.client.model.dto.album.AlbumDTO;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.dto.photo.MovePhotoForm;
import yuhan.hgcq.client.model.dto.photo.PhotoDTO;
import yuhan.hgcq.client.model.dto.team.TeamDTO;

public class Gallery extends AppCompatActivity {

    TextView empty, date;
    AppCompatButton chat, move, photoPlus, photoTrash;
    RecyclerView photoListView, albumListView;
    BottomNavigationView navi;
    Button moveOk;

    GalleryAdapter ga;
    AlbumAdapter aa;

    private boolean isPrivate;
    TeamDTO teamDTO;
    AlbumDTO albumDTO;
    MemberDTO loginMember;

    PhotoController pc;
    AlbumController ac;

    PhotoRepository pr;

    Handler handler = new Handler(Looper.getMainLooper());

    private static final int GALLERY = 1000;
    private static final int REQUEST_PERMISSION = 1111;

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

        pc = new PhotoController(this);
        ac = new AlbumController(this);

        pr = new PhotoRepository(this);

        empty = findViewById(R.id.empty);
        date = findViewById(R.id.date);

        chat = findViewById(R.id.chat);
        move = findViewById(R.id.move);
        photoPlus = findViewById(R.id.photoPlus);
        photoTrash = findViewById(R.id.photoTrash);
        moveOk = findViewById(R.id.moveOk);

        photoListView = findViewById(R.id.photoList);
        albumListView = findViewById(R.id.albumList);

        navi = findViewById(R.id.bottom_navigation_view);

        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        gallery.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        gallery.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent groupMainPage = new Intent(this, GroupMain.class);
        Intent albumMainPage = new Intent(this, AlbumMain.class);
        Intent friendListPage = new Intent(this, FriendList.class);
        Intent photoTrashPage = new Intent(this, PhotoTrash.class);
        Intent likePage = new Intent(this, Like.class);
        Intent chatPage = new Intent(this, Chat.class);
        Intent myPage = new Intent(this, MyPage.class);

        Intent getIntent = getIntent();
        isPrivate = getIntent.getBooleanExtra("isPrivate", false);

        teamDTO = (TeamDTO) getIntent.getSerializableExtra("teamDTO");
        albumDTO = (AlbumDTO) getIntent.getSerializableExtra("albumDTO");
        loginMember = (MemberDTO) getIntent.getSerializableExtra("loginMember");

        if (albumDTO != null) {
            if (isPrivate) {
                getSupportActionBar().setTitle("[개인] " + albumDTO.getName());
                chat.setVisibility(View.INVISIBLE);
            } else {
                getSupportActionBar().setTitle("[공유] " + albumDTO.getName());
            }
            date.setText(albumDTO.getStartDate() + " ~ " + albumDTO.getEndDate());
        }

        if (albumDTO != null) {
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
            } else {
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
            if (isPrivate) {

            } else {
                ga.enableSelectionMode();
                moveOk.setVisibility(View.VISIBLE);

                moveOk.setOnClickListener(v1 -> {
                    if (teamDTO != null) {
                        Long teamId = teamDTO.getTeamId();
                        List<PhotoDTO> selectedItems = ga.getSelectedPhotos();
                        ac.albumList(teamId, new retrofit2.Callback<List<AlbumDTO>>() {
                            @Override
                            public void onResponse(Call<List<AlbumDTO>> call, Response<List<AlbumDTO>> response) {
                                if (response.isSuccessful()) {
                                    List<AlbumDTO> albumList = response.body();
                                    aa = new AlbumAdapter(albumList, Gallery.this, isPrivate);
                                    handler.post(() -> {
                                        albumListView.setVisibility(View.VISIBLE);

                                        albumListView.setAdapter(aa);
                                        aa.notifyDataSetChanged(); // 데이터 변경 알림
                                    });

                                    aa.setOnItemClickListener(new AlbumAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position) {
                                            onClick_setting_costume_save("이동하시겠습니까?", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    AlbumDTO albumDTO = albumList.get(position);
                                                    MovePhotoForm form = new MovePhotoForm(albumDTO.getAlbumId(), selectedItems);
                                                    pc.moveAlbumPhoto(form, new retrofit2.Callback<ResponseBody>() {
                                                        @Override
                                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                            if (response.isSuccessful()) {
                                                                Intent galleryPage = new Intent(Gallery.this, Gallery.class);
                                                                galleryPage.putExtra("isPrivate", isPrivate);
                                                                galleryPage.putExtra("loginMember", loginMember);
                                                                galleryPage.putExtra("teamDTO", teamDTO);
                                                                galleryPage.putExtra("albumDTO", albumDTO);
                                                                handler.post(() -> {
                                                                    Toast.makeText(Gallery.this, "앨범 이동 했습니다.", Toast.LENGTH_SHORT).show();
                                                                });
                                                                startActivity(galleryPage);
                                                                Log.i("앨범 이동 성공", "Success");
                                                            } else {
                                                                Log.i("앨범 이동 실패", "Fail");
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                            Log.e("앨범 이동 실패", t.getMessage());
                                                        }
                                                    });
                                                }
                                            }, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Toast.makeText(Gallery.this, "취소했습니다.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
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
                    ga.disableSelectionMode();
                });
            }
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
            if (data != null) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();

                    List<Uri> uriList = new ArrayList<>(count);
                    List<String> paths = new ArrayList<>(count);
                    List<LocalDateTime> creates = new ArrayList<>(count);
                    List<String> regions = new ArrayList<>(count);

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

                    if (isPrivate) {
                        for (Uri uri : uriList) {
                            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }

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
                    } else {
                        if (albumDTO != null) {
                            pc.uploadPhoto(albumDTO.getAlbumId(), uriList, creates, regions, new retrofit2.Callback<ResponseBody>() {
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

                    if (isPrivate) {
                        getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
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
                    } else {
                        if (albumDTO != null) {
                            pc.uploadPhoto(albumDTO.getAlbumId(), uriList, creates, regions, new retrofit2.Callback<ResponseBody>() {
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

            GpsDirectory directory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
            if (directory != null) {
                latitude = directory.getGeoLocation().getLatitude();
                longitude = directory.getGeoLocation().getLongitude();
            }

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
            e.printStackTrace();
        }

        return null;
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
            } else {
                Rect rect = new Rect();
                albumListView.getGlobalVisibleRect(rect);
                if (!rect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    handler.post(() -> {
                        albumListView.setVisibility(View.INVISIBLE);
                        moveOk.setVisibility(View.INVISIBLE);
                    });
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}