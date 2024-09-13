package yuhan.hgcq.client.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import yuhan.hgcq.client.R;

public class GroupMain extends AppCompatActivity {

    ImageButton back,trashall,groupAdd;
    EditText searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_group_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        back = (ImageButton) findViewById(R.id.back);
        trashall = (ImageButton) findViewById(R.id.trashall);
        groupAdd = (ImageButton) findViewById(R.id.groupAdd);

        searchText=(EditText) findViewById(R.id.searchText);
        //그룹메인에서 뒤로가기하면 로그인창? or Select??
        Intent goToSelect=new Intent(this, Select.class);
        Intent goToTrash=new Intent(this, Trash.class);
        Intent goToCreateGroup=new Intent(this, CreateGroup.class);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goToSelect);
            }
        });
        trashall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goToTrash);
            }
        });
        groupAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goToCreateGroup);
            }
        });


    }
}