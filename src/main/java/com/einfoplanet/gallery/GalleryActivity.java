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
    private static ArrayList<DataDetailDO> galleryDataURI;
    private static GridAdapter imagesAdapter;
    private ArrayList<DataDetailDO> launcherScreenImageData;
    private android.support.v7.widget.Toolbar toolbar;
    private TextView txtCount;
    private ImageView imgBack;
    private SparseBooleanArray mSparseBooleanArray;//Variable to store selected Images
    private int j = 0;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        launcherScreenImageData = (ArrayList<DataDetailDO>) getIntent().getSerializableExtra(KEY_IMAGE_DATA_FROM_LAUNCHER_SCREEN);
        mSparseBooleanArray = new SparseBooleanArray();
        initViews();
        setListeners();
        galleryDataURI = new ArrayList<DataDetailDO>();//Init array
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
            selectImages.setTextColor(getResources().getColor(android.R.color.white));
            imgBack.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back_white));
        } else {
            toolbar.setBackgroundColor(getResources().getColor(android.R.color.white));
            txtCount.setTextColor(getResources().getColor(android.R.color.black));
            txtCount.setText("All Media");
            selectImages.setVisibility(View.VISIBLE);
            selectImages.setTextColor(getResources().getColor(android.R.color.black));
            imgBack.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back_black));
        }
    }

    //fetch all video and image from gallery
    private ArrayList<DataDetailDO> fetchGalleryData() {
        Cursor imageCursor = null;
        Cursor videoCursor = null;
        ArrayList<DataDetailDO> imgDetailData = new ArrayList<>();
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

            //check if there is image data is more or video data
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
            DataDetailDO videoDetailDO = new DataDetailDO();
            videoCursor.moveToPosition(i);
            int videoDataColumnIndex = videoCursor.getColumnIndex(MediaStore.Video.Media.DATA);//get column index
            String videoUri = videoCursor.getString(videoDataColumnIndex);

            videoDetailDO.imgURI = videoUri;
            videoDetailDO.videoIconVisibilityFlag = true;
            videoDetailDO.countNoInArrayList = i;
            for (DataDetailDO dataDetailDO1 : launcherScreenImageData) {
                if (dataDetailDO1.imgURI.equalsIgnoreCase(videoUri)) {
                    videoDetailDO.tickStatus = true;
                    mSparseBooleanArray.put(dataDetailDO1.countNoInArrayList, true);//Insert selected checkbox value inside boolean array
                }
            }
            galleryDataURI.add(videoDetailDO);//get Image from column index

            if (j < imageCursor.getCount()) {
                DataDetailDO dataDetailDO = new DataDetailDO();
                imageCursor.moveToPosition(j);
                int dataColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);//get column index
                String imgUri = imageCursor.getString(dataColumnIndex);
                j++;
                i++;
                dataDetailDO.imgURI = imgUri;
                dataDetailDO.countNoInArrayList = i;
                for (DataDetailDO dataDetailDO1 : launcherScreenImageData) {
                    if (dataDetailDO1.imgURI.equalsIgnoreCase(imgUri)) {
                        dataDetailDO.tickStatus = true;
                        mSparseBooleanArray.put(dataDetailDO1.countNoInArrayList, true);//Insert selected checkbox value inside boolean array
                    }
                }
                galleryDataURI.add(dataDetailDO);//get Image from column index
            }
        }
    }

    private void getDataIfImageCountIsGreater(int count, Cursor imageCursor, Cursor videoCursor) {
        //Loop to cursor count
        for (int i = 0; i < count; i++) {
            DataDetailDO dataDetailDO = new DataDetailDO();
            imageCursor.moveToPosition(i);
            int dataColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);//get column index
            String imgUri = imageCursor.getString(dataColumnIndex);

            dataDetailDO.imgURI = imgUri;
            dataDetailDO.countNoInArrayList = i;
            for (DataDetailDO dataDetailDO1 : launcherScreenImageData) {
                if (dataDetailDO1.imgURI.equalsIgnoreCase(imgUri)) {
                    dataDetailDO.tickStatus = true;
                    mSparseBooleanArray.put(dataDetailDO1.countNoInArrayList, true);//Insert selected checkbox value inside boolean array
                }
            }
            galleryDataURI.add(dataDetailDO);//get Image from column index
            if (j < videoCursor.getCount()) {
                DataDetailDO videoDetailDO = new DataDetailDO();
                videoCursor.moveToPosition(j);
                int videoDataColumnIndex = videoCursor.getColumnIndex(MediaStore.Video.Media.DATA);//get column index
                String videoUri = videoCursor.getString(videoDataColumnIndex);

                videoDetailDO.imgURI = videoUri;
                j++;
                i++;
                videoDetailDO.countNoInArrayList = i;
                videoDetailDO.videoIconVisibilityFlag = true;
                for (DataDetailDO dataDetailDO1 : launcherScreenImageData) {
                    if (dataDetailDO1.imgURI.equalsIgnoreCase(videoUri)) {
                        videoDetailDO.tickStatus = true;
                        mSparseBooleanArray.put(dataDetailDO1.countNoInArrayList, true);//Insert selected checkbox value inside boolean array
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
        ArrayList<DataDetailDO> selectedItems = imagesAdapter.getCheckedItems();
        if (selectedItems.size() > 0) {
            txtCount.setText((selectedItems.size()) + " Selected");
            txtCount.setVisibility(View.VISIBLE);
            txtCount.setTextColor(getResources().getColor(android.R.color.white));
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            selectImages.setVisibility(View.VISIBLE);
            selectImages.setTextColor(getResources().getColor(android.R.color.white));
            imgBack.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back_white));
        } else {
            toolbar.setBackgroundColor(getResources().getColor(android.R.color.white));
            txtCount.setTextColor(getResources().getColor(android.R.color.black));
            txtCount.setText(R.string.label_all_media);
            selectImages.setVisibility(View.VISIBLE);
            selectImages.setTextColor(getResources().getColor(android.R.color.black));
            imgBack.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back_black));
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_return:
                //When button is clicked then fill array with selected images
                ArrayList<DataDetailDO> selectedItems = imagesAdapter.getCheckedItems();

                //Send back result to MainActivity with selected images
                Intent intent = new Intent();
                intent.putExtra(CustomGalleryIntentKey, selectedItems);
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
