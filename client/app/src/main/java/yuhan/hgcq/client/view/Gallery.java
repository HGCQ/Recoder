package yuhan.hgcq.client.view;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;


import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import yuhan.hgcq.client.localDatabase.Repository.AlbumRepository;
import yuhan.hgcq.client.localDatabase.Repository.PhotoRepository;
import yuhan.hgcq.client.localDatabase.callback.Callback;
import yuhan.hgcq.client.model.dto.album.AlbumDTO;
import yuhan.hgcq.client.model.dto.member.MemberDTO;
import yuhan.hgcq.client.model.dto.photo.MovePhotoForm;
import yuhan.hgcq.client.model.dto.photo.PhotoDTO;
import yuhan.hgcq.client.model.dto.team.TeamDTO;

public class Gallery extends AppCompatActivity {
    /* View */
    TextView empty, albumListViewTop;
    Button chat, move, photoPlus, photoTrash,calendar;
    RecyclerView photoListView, albumList;
    BottomNavigationView navi;
    Button moveOk;
    ImageView albumListView;

    /* Adapter */
    GalleryAdapter ga;
    AlbumAdapter aa;

    /* 받아올 값 */
    private boolean isPrivate;
    TeamDTO teamDTO;
    AlbumDTO albumDTO;
    MemberDTO loginMember;

    /* http 통신 */
    PhotoController pc;
    AlbumController ac;

    /* Room DB */
    PhotoRepository pr;
    AlbumRepository ar;

    /* 메인 스레드 */
    Handler handler = new Handler(Looper.getMainLooper());

    /* Intent 요청 코드 */
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
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar(); // actionBar 가져오기
        if (actionBar != null) {
            actionBar.setTitle("Recoder");
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c2dcff")));
            actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 활성화
        }


        EdgeToEdge.enable(this);
        /* Layout */
        setContentView(R.layout.activity_gallery);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* 초기화 */
        pc = new PhotoController(this);
        ac = new AlbumController(this);

        pr = new PhotoRepository(this);
        ar = new AlbumRepository(this);

        albumList = findViewById(R.id.albumList);
        albumListViewTop = findViewById(R.id.albumListViewTop);
        empty = findViewById(R.id.empty);
        chat = findViewById(R.id.chat);
        move = findViewById(R.id.move);
        photoPlus = findViewById(R.id.photoPlus);
        photoTrash = findViewById(R.id.photoTrash);
        moveOk = findViewById(R.id.moveOk);
        photoListView = findViewById(R.id.photoList);
        albumListView = findViewById(R.id.albumListView);
        navi = findViewById(R.id.bottom_navigation_view);
        calendar=findViewById(R.id.calendar);

        /* 갤러리 */
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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

        /* 받아올 값 */
        Intent getIntent = getIntent();
        isPrivate = getIntent.getBooleanExtra("isPrivate", false);
        teamDTO = (TeamDTO) getIntent.getSerializableExtra("teamDTO");
        albumDTO = (AlbumDTO) getIntent.getSerializableExtra("albumDTO");
        loginMember = (MemberDTO) getIntent.getSerializableExtra("loginMember");

        /* 제목 */
        if (albumDTO != null) {
            if (isPrivate) {
                getSupportActionBar().setTitle("[개인] " + albumDTO.getName());
                chat.setVisibility(View.INVISIBLE);
            } else {
                getSupportActionBar().setTitle("[공유] " + albumDTO.getName());
            }
        }

        /* 초기 설정 */
        if (albumDTO != null) {
            /* 개인 */
            if (isPrivate) {
                pr.gallery(albumDTO.getAlbumId(), new Callback<Map<String, List<PhotoDTO>>>() {
                    @Override
                    public void onSuccess(Map<String, List<PhotoDTO>> result) {
                        if (result != null) {
                            ga = new GalleryAdapter(result, Gallery.this, isPrivate, loginMember, albumDTO);
                            handler.post(() -> {
                                photoListView.setAdapter(ga);
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
                pc.galleryList(albumDTO.getAlbumId(), new retrofit2.Callback<Map<String, List<PhotoDTO>>>() {
                    @Override
                    public void onResponse(Call<Map<String, List<PhotoDTO>>> call, Response<Map<String, List<PhotoDTO>>> response) {
                        if (response.isSuccessful()) {
                            Map<String, List<PhotoDTO>> galleryList = response.body();
                            ga = new GalleryAdapter(galleryList, Gallery.this, isPrivate, loginMember, teamDTO, albumDTO);
                            handler.post(() -> {
                                photoListView.setAdapter(ga);
                            });
                        } else {
                            /* Toast 메시지 */
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, List<PhotoDTO>>> call, Throwable t) {
                        /* Toast 메시지 */
                    }
                });
            }
        }

        /* 사진 추가 */
        photoPlus.setOnClickListener(v -> {
            /* Android 11 이상 */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    startActivityForResult(Intent.createChooser(gallery, "사진 선택"), GALLERY);
                } else {
                    Toast.makeText(Gallery.this, "권한이 없습니다.", Toast.LENGTH_SHORT).show();
                    Intent permission = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    permission.addCategory("android.intent.category.DEFAULT");
                    permission.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                    startActivityForResult(permission, REQUEST_PERMISSION);
                }
            }
            /* Android 11 미만 */
        });

        /* 사진 휴지통 */
        photoTrash.setOnClickListener(v -> {
            if (isPrivate) {
                photoTrashPage.putExtra("isPrivate", isPrivate);
            }
            photoTrashPage.putExtra("loginMember", loginMember);
            photoTrashPage.putExtra("teamDTO", teamDTO);
            photoTrashPage.putExtra("albumDTO", albumDTO);
            startActivity(photoTrashPage);
        });

        /* 앨범 이동 */
        move.setOnClickListener(v -> {
            /* 개인 */
            if (isPrivate) {
                ga.enableSelectionMode();
                moveOk.setVisibility(View.VISIBLE);

                moveOk.setOnClickListener(v1 -> {
                    List<PhotoDTO> selectedItems = ga.getSelectedPhotos();
                    ar.searchMove(albumDTO.getAlbumId(), new Callback<List<AlbumDTO>>() {
                        @Override
                        public void onSuccess(List<AlbumDTO> result) {
                            if (result != null) {
                                aa = new AlbumAdapter(result, Gallery.this, isPrivate);
                                aa.setPhoto();
                                handler.post(() -> {
                                    albumListView.setVisibility(View.VISIBLE);
                                    albumList.setVisibility(View.VISIBLE);
                                    albumListViewTop.setVisibility(View.VISIBLE);
                                    albumList.setAdapter(aa);
                                    aa.notifyDataSetChanged(); // 데이터 변경 알림
                                });

                                aa.setOnItemClickListener(new AlbumAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        onClick_setting_costume_save("이동하시겠습니까?", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                AlbumDTO albumDTO1 = result.get(position);
                                                MovePhotoForm form = new MovePhotoForm(albumDTO1.getAlbumId(), selectedItems);
                                                pr.move(form, new Callback<Boolean>() {
                                                    @Override
                                                    public void onSuccess(Boolean result) {
                                                        if (result != null) {
                                                            Intent galleryPage = new Intent(Gallery.this, Gallery.class);
                                                            galleryPage.putExtra("isPrivate", isPrivate);
                                                            galleryPage.putExtra("loginMember", loginMember);
                                                            galleryPage.putExtra("albumDTO", albumDTO);
                                                            handler.post(() -> {
                                                                Toast.makeText(Gallery.this, "앨범 이동 했습니다.", Toast.LENGTH_SHORT).show();
                                                            });
                                                            startActivity(galleryPage);
                                                        } else {
                                                            /* Toast 메시지 */
                                                        }
                                                    }

                                                    @Override
                                                    public void onError(Exception e) {

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
                            } else {
                                /* Toast 메시지 */
                            }
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                });
            }
            /* 공유 !*/
            else {
                ga.enableSelectionMode();
                moveOk.setVisibility(View.VISIBLE);

                moveOk.setOnClickListener(v1 -> {
                    if (teamDTO != null) {
                        Long teamId = teamDTO.getTeamId();
                        List<PhotoDTO> selectedItems = ga.getSelectedPhotos();
                        Long albumId = albumDTO.getAlbumId();
                        ac.moveAlbumList(teamId, albumId, new retrofit2.Callback<List<AlbumDTO>>() {
                            @Override
                            public void onResponse(Call<List<AlbumDTO>> call, Response<List<AlbumDTO>> response) {
                                if (response.isSuccessful()) {
                                    List<AlbumDTO> albumList1 = response.body();
                                    aa = new AlbumAdapter(albumList1, Gallery.this, isPrivate);
                                    aa.setPhoto();
                                    handler.post(() -> {
                                        albumListView.setVisibility(View.VISIBLE);
                                        albumList.setVisibility(View.VISIBLE);
                                        albumListViewTop.setVisibility(View.VISIBLE);
                                        albumList.setAdapter(aa);
                                        aa.notifyDataSetChanged(); // 데이터 변경 알림
                                    });

                                    aa.setOnItemClickListener(new AlbumAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position) {
                                            onClick_setting_costume_save("이동하시겠습니까?", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    AlbumDTO albumDTO1 = albumList1.get(position);
                                                    MovePhotoForm form = new MovePhotoForm(albumDTO1.getAlbumId(), selectedItems);
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
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Toast.makeText(Gallery.this, "취소했습니다.", Toast.LENGTH_SHORT).show();
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
                    ga.disableSelectionMode();
                });
            }
        });
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();

                // Setting title for picker
                builder.setTitleText("Select Date Range");

                // Optional: Restrict selectable dates to future dates
                CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();

                builder.setCalendarConstraints(constraintsBuilder.build());

                final MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();

                // Show the picker
                datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

                // Handle positive button click
                datePicker.addOnPositiveButtonClickListener(selection -> {
                    // Handle date range selection
                    Long startDate = selection.first;
                    Long endDate = selection.second;

                    // Format dates and display them
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String start = sdf.format(new Date(startDate));
                    String end = sdf.format(new Date(endDate));

                    if (isPrivate) {
                        pr.galleryByDate(albumDTO.getAlbumId(), start, end, new Callback<Map<String, List<PhotoDTO>>>() {
                            @Override
                            public void onSuccess(Map<String, List<PhotoDTO>> result) {
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
                                    ga = new GalleryAdapter(result, Gallery.this, isPrivate, loginMember, teamDTO, albumDTO);
                                    handler.post(() -> {
                                        photoListView.setAdapter(ga);
                                        ga.notifyDataSetChanged();
                                    });
                                }
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                    } else {
                        pc.galleryListByDate(albumDTO.getAlbumId(), start, end, new retrofit2.Callback<Map<String, List<PhotoDTO>>>() {
                            @Override
                            public void onResponse(Call<Map<String, List<PhotoDTO>>> call, Response<Map<String, List<PhotoDTO>>> response) {
                                if (response.isSuccessful()) {
                                    Map<String, List<PhotoDTO>> galleryList = response.body();
                                    if (galleryList.isEmpty()) {
                                        handler.post(() -> {
                                            empty.setVisibility(View.VISIBLE);
                                        });
                                    } else {
                                        handler.post(() -> {
                                            empty.setVisibility(View.INVISIBLE);
                                        });
                                    }
                                    ga = new GalleryAdapter(galleryList, Gallery.this, isPrivate, loginMember, teamDTO, albumDTO);
                                    handler.post(() -> {
                                        photoListView.setAdapter(ga);
                                        ga.notifyDataSetChanged();
                                    });
                                }
                            }

                            @Override
                            public void onFailure(Call<Map<String, List<PhotoDTO>>> call, Throwable t) {

                            }
                        });
                    }
                });
            }
        });

        /* 채팅 */
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatPage.putExtra("loginMember", loginMember);
                chatPage.putExtra("teamDTO", teamDTO);
                chatPage.putExtra("albumDTO", albumDTO);
                startActivity(chatPage);
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

        // ProgressDialog 선언
        ProgressDialog progressDialog = new ProgressDialog(Gallery.this);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // 배경을 투명하게
        progressDialog.setCancelable(false); // 다이얼로그 외부 클릭으로 종료되지 않게

        /* 갤러리 */
        if (requestCode == GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                /* 사진 여러 장 */
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

                    /* 개인 */
                    if (isPrivate) {
                        pr.create(albumDTO.getAlbumId(), paths, creates, regions, new Callback<Boolean>() {
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
                                } else {
                                    /* Toast 메시지 */
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                progressDialog.dismiss(); // 로딩 화면 종료
                                /* Toast 메시지 */
                            }
                        });
                    }
                    /* 공유 */
                    else {
                        if (albumDTO != null) {
                            progressDialog.show(); // 로딩 화면 보여주기
                            pc.uploadPhoto(albumDTO.getAlbumId(), uriList, creates, regions, new retrofit2.Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    progressDialog.dismiss(); // 로딩 화면 종료
                                    if (response.isSuccessful()) {
                                        handler.post(() -> {
                                            Toast.makeText(Gallery.this, "사진이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                        });
                                        Intent galleryPage = new Intent(Gallery.this, Gallery.class);
                                        galleryPage.putExtra("loginMember", loginMember);
                                        galleryPage.putExtra("isPrivate", isPrivate);
                                        galleryPage.putExtra("teamDTO", teamDTO);
                                        galleryPage.putExtra("albumDTO", albumDTO);
                                        startActivity(galleryPage);
                                    } else {
                                        /* Toast 메시지 */
                                        progressDialog.dismiss(); // 로딩 화면 종료
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    /* Toast 메시지 */
                                    progressDialog.dismiss(); // 로딩 화면 종료
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

                    if (isPrivate) {
                        pr.create(albumDTO.getAlbumId(), paths, creates, regions, new Callback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {
                                progressDialog.dismiss(); // 로딩 화면 종료
                                if (result) {
                                    handler.post(() -> {
                                        Toast.makeText(Gallery.this, "사진이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                    });
                                    Intent galleryPage = new Intent(Gallery.this, Gallery.class);
                                    galleryPage.putExtra("loginMember", loginMember);
                                    galleryPage.putExtra("isPrivate", isPrivate);
                                    galleryPage.putExtra("teamDTO", teamDTO);
                                    galleryPage.putExtra("albumDTO", albumDTO);
                                    startActivity(galleryPage);
                                } else {
                                    progressDialog.dismiss(); // 로딩 화면 종료
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
                        if (albumDTO != null) {
                            progressDialog.show(); // 로딩 화면 보여주기
                            pc.uploadPhoto(albumDTO.getAlbumId(), uriList, creates, regions, new retrofit2.Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    progressDialog.dismiss(); // 로딩 화면 종료
                                    if (response.isSuccessful()) {
                                        handler.post(() -> {
                                            Toast.makeText(Gallery.this, "사진이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                        });
                                        Intent galleryPage = new Intent(Gallery.this, Gallery.class);
                                        galleryPage.putExtra("loginMember", loginMember);
                                        galleryPage.putExtra("isPrivate", isPrivate);
                                        galleryPage.putExtra("teamDTO", teamDTO);
                                        galleryPage.putExtra("albumDTO", albumDTO);
                                        startActivity(galleryPage);
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
            }
        }
    }

    /* 사진 metadata */
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
                        moveOk.setVisibility(View.INVISIBLE);
                        albumList.setVisibility(View.INVISIBLE);
                        albumListViewTop.setVisibility(View.INVISIBLE);
                    });
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}