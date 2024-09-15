package yuhan.hgcq.client.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import yuhan.hgcq.client.R;
import yuhan.hgcq.client.controller.AlbumController;

public class AlbumMain extends AppCompatActivity {
    private ImageButton back, search, albumPlus, trashAll;
    private EditText searchText;
    private Boolean isPersonal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("Recoder");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_album_main);

        // Set window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        back=(ImageButton)findViewById(R.id.back);
        search=(ImageButton)findViewById(R.id.search);
        albumPlus=(ImageButton)findViewById(R.id.albumplus);
        trashAll=(ImageButton)findViewById(R.id.trashall);

        searchText = (EditText) findViewById(R.id.searchText);
        Intent goToSelect=new Intent(this,Select.class);
        Intent goToTrashAll=new Intent(this,Trash.class);
        Intent goToCreateAlbum=new Intent(this,CreateAlbum.class);

        Intent getIntent = getIntent();
        isPersonal = getIntent.getBooleanExtra("isPersonal", true);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goToSelect);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //사용자가 입력한 값을 text에 받음
                String text = searchText.getText().toString();
                //여기서 뭘 검색하나요??

            }
        });

        albumPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goToCreateAlbum);
            }
        });

        trashAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goToTrashAll);
            }
        });

        if (isPersonal) {
            // isPersonal이 true일 때 동작 수행
            // 여기에서 필요한 동작 수행 (예: 개인 앨범 화면 로드)
        }
    }
}
