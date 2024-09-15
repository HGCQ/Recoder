package yuhan.hgcq.client.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import yuhan.hgcq.client.R;

public class Select extends AppCompatActivity {

    private ImageButton privated,share;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("Recoder");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        privated=(ImageButton) findViewById(R.id.privated);
        share=(ImageButton)findViewById(R.id.share);

        Intent goToLogin=new Intent(this, Login.class);
        Intent goToAlbumMain=new Intent(this,AlbumMain.class);

        privated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAlbumMain.putExtra("isPersonal", true);
                startActivity(goToAlbumMain);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goToLogin);
            }
        });

    }
}
