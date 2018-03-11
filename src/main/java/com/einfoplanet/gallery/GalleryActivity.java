package com.einfoplanet.gallery;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.einfoplanet.gallery.LauncherActivity.CustomGalleryIntentKey;
import static com.einfoplanet.gallery.LauncherActivity.KEY_IMAGE_DATA_FROM_LAUNCHER_SCREEN;

public class GalleryActivity extends AppCompatActivity implements View.OnClickListener {

    private static Button selectImages;
    private static GridView galleryImagesGridView;
    private static ArrayList<ImgDetailDO> galleryImageUrls;
    private static GridAdapter imagesAdapter;
    private ArrayList<ImgDetailDO> launcherScreenImageData;
    private android.support.v7.widget.Toolbar toolbar;
    private TextView txtCount;
    private ImageView imgBack;
    private SparseBooleanArray mSparseBooleanArray;//Variable to store selected Images

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        launcherScreenImageData = (ArrayList<ImgDetailDO>) getIntent().getSerializableExtra(KEY_IMAGE_DATA_FROM_LAUNCHER_SCREEN);
        mSparseBooleanArray = new SparseBooleanArray();
        initViews();
        setListeners();
        fetchGalleryImages();
        setUpGridView();
    }

    //Init all views
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        txtCount = toolbar.findViewById(R.id.txt_count);
        imgBack = toolbar.findViewById(R.id.img_back);
        selectImages = (Button) findViewById(R.id.btn_return);
        galleryImagesGridView = (GridView) findViewById(R.id.galleryImagesGridView);

        toolbar.setBackgroundColor(getResources().getColor(android.R.color.white));
        txtCount.setTextColor(getResources().getColor(android.R.color.black));
        txtCount.setText("All Media");
        selectImages.setVisibility(View.VISIBLE);
        imgBack.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
    }

    //fetch all images from gallery
    private void fetchGalleryImages() {
        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};//get all columns of type images
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;//order data by date
        Cursor imagecursor = managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy + " DESC");//get all data in Cursor by sorting in DESC order

        galleryImageUrls = new ArrayList<ImgDetailDO>();//Init array

        //Loop to cursor count
        for (int i = 0; i < imagecursor.getCount(); i++) {
            ImgDetailDO imgDetailDO = new ImgDetailDO();
            imagecursor.moveToPosition(i);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);//get column index
            String imgUri = imagecursor.getString(dataColumnIndex);
            for (ImgDetailDO imgDetailDO1 : launcherScreenImageData) {
                if (imgDetailDO1.imgURI.equalsIgnoreCase(imgUri)){
                    Log.e("GALLERY ACTIVITY", "uri => " + imgUri);
                    imgDetailDO.tickStatus = true;
                    mSparseBooleanArray.put(i, true);//Insert selected checkbox value inside boolean array
                }
            }

            imgDetailDO.imgURI = imgUri;
            galleryImageUrls.add(imgDetailDO);//get Image from column index

        }
    }

    //Set Up GridView method
    private void setUpGridView() {
        imagesAdapter = new GridAdapter(GalleryActivity.this, galleryImageUrls, true,mSparseBooleanArray);
        galleryImagesGridView.setAdapter(imagesAdapter);
    }

    //Set Listeners method
    private void setListeners() {
        selectImages.setOnClickListener(this);
        imgBack.setOnClickListener(this);
    }


    //Show hide select button if images are selected or deselected
    public void showSelectButton() {
        ArrayList<ImgDetailDO> selectedItems = imagesAdapter.getCheckedItems();
        if (selectedItems.size() > 0) {
            txtCount.setText((selectedItems.size()) + " Selected");
            txtCount.setVisibility(View.VISIBLE);
            txtCount.setTextColor(getResources().getColor(android.R.color.white));
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            selectImages.setVisibility(View.VISIBLE);
            imgBack.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
        } else{
            toolbar.setBackgroundColor(getResources().getColor(android.R.color.white));
            txtCount.setTextColor(getResources().getColor(android.R.color.black));
            txtCount.setText("All Media");
            selectImages.setVisibility(View.VISIBLE);
            imgBack.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_return:

                //When button is clicked then fill array with selected images
                ArrayList<ImgDetailDO> selectedItems = imagesAdapter.getCheckedItems();

                //Send back result to MainActivity with selected images
                Intent intent = new Intent();
                intent.putExtra(CustomGalleryIntentKey, selectedItems);
//                intent.putExtra(LauncherActivity.CustomGalleryIntentKey, selectedItems.toString());//Convert Array into string to pass data
                setResult(RESULT_OK, intent);//Set result OK
                finish();//finish activity
                break;
            case R.id.img_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
