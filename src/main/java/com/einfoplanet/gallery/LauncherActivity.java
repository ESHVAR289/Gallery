package com.einfoplanet.gallery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class LauncherActivity extends AppCompatActivity {

    private RecyclerView selectedImgRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selectedImgRecyclerView = findViewById(R.id.selected_img_recycler_view);
    }

    public void openGalleryActivity(View view) {
    }
}
