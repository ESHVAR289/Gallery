package com.einfoplanet.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

public class LauncherActivity extends AppCompatActivity implements View.OnClickListener {
    private AppCompatButton openCustomGallery;
    private static GridView selectedImageGridView;
    private static final int CustomGallerySelectId = 1;//Set Intent Id
    public static final String CustomGalleryIntentKey = "ImageArray";//Set Intent Key Value
    public static final String KEY_IMAGE_DATA_FROM_LAUNCHER_SCREEN = "launcher_screen_data";
    private ArrayList<ImgDetailDO> selectedImagesToSendOnGridActivity;
    private TextView txtNoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setListeners();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Gallery");
        }
        selectedImagesToSendOnGridActivity = new ArrayList<>();
    }

    //Init all views
    private void initViews() {
        openCustomGallery = (AppCompatButton) findViewById(R.id.openCustomGallery);
        selectedImageGridView = (GridView) findViewById(R.id.selectedImagesGridView);
        txtNoData = findViewById(R.id.txt_no_data);
    }

    //set Listeners
    private void setListeners() {
        openCustomGallery.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.openCustomGallery:
                //Start Custom Gallery Activity by passing intent id
                Intent gridActivityIntent = new Intent(LauncherActivity.this, GalleryActivity.class);
                gridActivityIntent.putExtra(KEY_IMAGE_DATA_FROM_LAUNCHER_SCREEN, selectedImagesToSendOnGridActivity);//Convert Array into string to pass data
                startActivityForResult(gridActivityIntent, CustomGallerySelectId);
                break;
        }
    }

    protected void onActivityResult(int requestcode, int resultcode,
                                    Intent imagereturnintent) {
        super.onActivityResult(requestcode, resultcode, imagereturnintent);
        switch (requestcode) {
            case CustomGallerySelectId:
                if (resultcode == RESULT_OK) {

                    selectedImagesToSendOnGridActivity = (ArrayList<ImgDetailDO>) imagereturnintent.getSerializableExtra(CustomGalleryIntentKey);
                    if (selectedImagesToSendOnGridActivity.size() > 0) {
                        selectedImageGridView.setVisibility(View.VISIBLE);
                        txtNoData.setVisibility(View.GONE);
                        loadGridView(selectedImagesToSendOnGridActivity);//call load gridview method by passing converted list into arrayList
                    } else {
                        selectedImageGridView.setVisibility(View.GONE);
                        txtNoData.setVisibility(View.VISIBLE);
                    }
                }
                break;

        }
    }

    //Load GridView
    private void loadGridView(ArrayList<ImgDetailDO> imageDetailData) {
        GridAdapter adapter = new GridAdapter(LauncherActivity.this, imageDetailData, false,new SparseBooleanArray());
        selectedImageGridView.setAdapter(adapter);
    }
}
