package com.einfoplanet.gallery;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
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
    private static ArrayList<ImgDetailDO> galleryDataURI;
    private static GridAdapter imagesAdapter;
    private ArrayList<ImgDetailDO> launcherScreenImageData;
    private android.support.v7.widget.Toolbar toolbar;
    private TextView txtCount;
    private ImageView imgBack;
    private SparseBooleanArray mSparseBooleanArray;//Variable to store selected Images
    private int j = 0;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        launcherScreenImageData = (ArrayList<ImgDetailDO>) getIntent().getSerializableExtra(KEY_IMAGE_DATA_FROM_LAUNCHER_SCREEN);
        mSparseBooleanArray = new SparseBooleanArray();
        initViews();
        setListeners();
        galleryDataURI = new ArrayList<ImgDetailDO>();//Init array
        fetchGalleryData();
        setUpGridView();
    }

    //Init all views
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        txtCount = toolbar.findViewById(R.id.txt_count);
        imgBack = toolbar.findViewById(R.id.img_back);
        selectImages = (Button) findViewById(R.id.btn_return);
        galleryImagesGridView = (GridView) findViewById(R.id.galleryImagesGridView);

        if (launcherScreenImageData.size() > 0) {
            txtCount.setText((launcherScreenImageData.size()) + " Selected");
            txtCount.setVisibility(View.VISIBLE);
            txtCount.setTextColor(getResources().getColor(android.R.color.white));
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            selectImages.setVisibility(View.VISIBLE);
            imgBack.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back_white));
        } else {
            toolbar.setBackgroundColor(getResources().getColor(android.R.color.white));
            txtCount.setTextColor(getResources().getColor(android.R.color.black));
            txtCount.setText("All Media");
            selectImages.setVisibility(View.VISIBLE);
            imgBack.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back_black));
        }
    }

    //fetch all images from gallery
    private ArrayList<ImgDetailDO> fetchGalleryData() {
        Cursor imageCursor = null;
        Cursor videoCursor = null;
        ArrayList<ImgDetailDO> imgDetailData = new ArrayList<>();
        try {
            final String[] imagesColumns = {
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATE_TAKEN};//get all columns of type images
            final String imagesOrderBy = MediaStore.Images.Media.DATE_TAKEN;//order data by date
            imageCursor = this.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imagesColumns, null,
                    null, imagesOrderBy + " DESC");//get all data in Cursor by sorting in DESC order

            final String[] videoColumns = {MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID};//get all columns of type videos
            final String videoOrderBy = MediaStore.Video.Media.DATE_TAKEN;//order data by date
            videoCursor = this.getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoColumns, null,
                    null, videoOrderBy + " DESC");//get all data in Cursor by sorting in DESC order
            int count = 0;

            if (imageCursor.getCount() > videoCursor.getCount()) {
                count = imageCursor.getCount();
                getDataIfImageCountIsGreater(count, imageCursor, videoCursor);
            } else {
                count = videoCursor.getCount();
                getDataIfVideoCountIsGreater(count, imageCursor, videoCursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (imageCursor != null) {
                imageCursor.close();
            }

            if (videoCursor != null) {
                videoCursor.close();
            }
        }
        return imgDetailData;
    }

    private void getDataIfVideoCountIsGreater(int count, Cursor imageCursor, Cursor videoCursor) {
        //Loop to cursor count
        for (int i = 0; i < count; i++) {
            ImgDetailDO videoDetailDO = new ImgDetailDO();
            videoCursor.moveToPosition(i);
            int videoDataColumnIndex = videoCursor.getColumnIndex(MediaStore.Video.Media.DATA);//get column index
            String videoUri = videoCursor.getString(videoDataColumnIndex);

            videoDetailDO.imgURI = videoUri;
            videoDetailDO.countNoInArrayList = i;
            for (ImgDetailDO imgDetailDO1 : launcherScreenImageData) {
                if (imgDetailDO1.imgURI.equalsIgnoreCase(videoUri)) {
                    videoDetailDO.tickStatus = true;
                    mSparseBooleanArray.put(imgDetailDO1.countNoInArrayList, true);//Insert selected checkbox value inside boolean array
                }
            }
            galleryDataURI.add(videoDetailDO);//get Image from column index

            if (j < imageCursor.getCount()) {
                ImgDetailDO imgDetailDO = new ImgDetailDO();
                imageCursor.moveToPosition(j);
                int dataColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);//get column index
                String imgUri = imageCursor.getString(dataColumnIndex);
                j++;
                i++;
                imgDetailDO.imgURI = imgUri;
                imgDetailDO.countNoInArrayList = i;
                for (ImgDetailDO imgDetailDO1 : launcherScreenImageData) {
                    if (imgDetailDO1.imgURI.equalsIgnoreCase(imgUri)) {
                        imgDetailDO.tickStatus = true;
                        mSparseBooleanArray.put(imgDetailDO1.countNoInArrayList, true);//Insert selected checkbox value inside boolean array
                    }
                }
                galleryDataURI.add(imgDetailDO);//get Image from column index
            }
        }
    }

    private void getDataIfImageCountIsGreater(int count, Cursor imageCursor, Cursor videoCursor) {
        //Loop to cursor count
        for (int i = 0; i < count; i++) {
            ImgDetailDO imgDetailDO = new ImgDetailDO();
            imageCursor.moveToPosition(i);
            int dataColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);//get column index
            String imgUri = imageCursor.getString(dataColumnIndex);

            imgDetailDO.imgURI = imgUri;
            imgDetailDO.countNoInArrayList = i;
            for (ImgDetailDO imgDetailDO1 : launcherScreenImageData) {
                if (imgDetailDO1.imgURI.equalsIgnoreCase(imgUri)) {
                    imgDetailDO.tickStatus = true;
                    mSparseBooleanArray.put(imgDetailDO1.countNoInArrayList, true);//Insert selected checkbox value inside boolean array
                }
            }
            galleryDataURI.add(imgDetailDO);//get Image from column index
            if (j < videoCursor.getCount()) {
                ImgDetailDO videoDetailDO = new ImgDetailDO();
                videoCursor.moveToPosition(j);
                int videoDataColumnIndex = videoCursor.getColumnIndex(MediaStore.Video.Media.DATA);//get column index
                String videoUri = videoCursor.getString(videoDataColumnIndex);

                videoDetailDO.imgURI = videoUri;
                j++;
                i++;
                videoDetailDO.countNoInArrayList = i;
                for (ImgDetailDO imgDetailDO1 : launcherScreenImageData) {
                    if (imgDetailDO1.imgURI.equalsIgnoreCase(videoUri)) {
                        videoDetailDO.tickStatus = true;
                        mSparseBooleanArray.put(imgDetailDO1.countNoInArrayList, true);//Insert selected checkbox value inside boolean array
                    }
                }
                galleryDataURI.add(videoDetailDO);//get Image from column index
            }
        }
    }

    //Set Up GridView method
    private void setUpGridView() {
        imagesAdapter = new GridAdapter(GalleryActivity.this, galleryDataURI, true, mSparseBooleanArray);
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
            imgBack.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back_white));
        } else {
            toolbar.setBackgroundColor(getResources().getColor(android.R.color.white));
            txtCount.setTextColor(getResources().getColor(android.R.color.black));
            txtCount.setText("All Media");
            selectImages.setVisibility(View.VISIBLE);
            imgBack.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back_black));
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
