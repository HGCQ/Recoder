package yuhan.hgcq.client.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
}