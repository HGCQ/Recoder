package yuhan.hgcq.client.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import yuhan.hgcq.client.R;
import yuhan.hgcq.client.localDatabase.entity.Album;

public class CreateAlbum extends AppCompatActivity {

    Context context;
    ImageButton back;
    Button save;
    EditText createAlbumName;
    Intent goToAlbumMain=new Intent(this, AlbumMain.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("앨범 생성");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_album);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        back=(ImageButton) findViewById(R.id.back);
        save=(Button) findViewById(R.id.save);
        createAlbumName=(EditText)findViewById(R.id.createAlbumText);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goToAlbumMain);
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"앨범 저장", Toast.LENGTH_SHORT).show();
                //앨범 저장하고 추가되게 만들어야됨
                startActivity(goToAlbumMain);
            }
        });

    }
    public void onClick_setting_costume_save(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Recoder")
                .setMessage("앨범 생성하시겠습니까?")
                .setIcon(R.drawable.album)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(CreateAlbum.this, "저장을 완료했습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // 취소 로직
                        Toast.makeText(CreateAlbum.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
}
