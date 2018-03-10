package com.einfoplanet.gallery;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.GridView;

import java.util.ArrayList;

public class LauncherActivity extends AppCompatActivity implements View.OnClickListener {
    private AppCompatButton openCustomGallery;
    private static GridView selectedImageGridView;
    private static final int CustomGallerySelectId = 1;//Set Intent Id
    public static final String CustomGalleryIntentKey = "ImageArray";//Set Intent Key Value
    public static final String KEY_IMAGE_DATA_FROM_LAUNCHER_SCREEN = "launcher_screen_data";
    ArrayList<ImgDetailDO> selectedImagesToSendOnGridActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setListeners();
        selectedImagesToSendOnGridActivity = new ArrayList<>();
//        getSharedImages();
    }

    //Init all views
    private void initViews() {
        openCustomGallery = (AppCompatButton) findViewById(R.id.openCustomGallery);
        selectedImageGridView = (GridView) findViewById(R.id.selectedImagesGridView);
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
                startActivityForResult(gridActivityIntent,CustomGallerySelectId);
                break;
        }
    }

    protected void onActivityResult(int requestcode, int resultcode,
                                    Intent imagereturnintent) {
        super.onActivityResult(requestcode, resultcode, imagereturnintent);
        switch (requestcode) {
            case CustomGallerySelectId:
                if (resultcode == RESULT_OK) {
//                    String imagesArray = imagereturnintent.getStringExtra(CustomGalleryIntentKey);//get Intent data
//                    //Convert string array into List by splitting by ',' and substring after '[' and before ']'
//                    List<String> selectedImages = Arrays.asList(imagesArray.substring(1, imagesArray.length() - 1).split(", "));
//                    selectedImagesToSendOnGridActivity = new ArrayList<String>(selectedImages);
                    selectedImagesToSendOnGridActivity = (ArrayList<ImgDetailDO>) imagereturnintent.getSerializableExtra(CustomGalleryIntentKey);
                    loadGridView(selectedImagesToSendOnGridActivity);//call load gridview method by passing converted list into arrayList
                }
                break;

        }
    }

    //Load GridView
    private void loadGridView(ArrayList<ImgDetailDO> imageDetailData) {
        GridAdapter adapter = new GridAdapter(LauncherActivity.this, imageDetailData, false);
        selectedImageGridView.setAdapter(adapter);
    }

//    //Read Shared Images
//    private void getSharedImages() {
//
//        //If Intent Action equals then proceed
//        if (Intent.ACTION_SEND_MULTIPLE.equals(getIntent().getAction())
//                && getIntent().hasExtra(Intent.EXTRA_STREAM)) {
//            ArrayList<Parcelable> list =
//                    getIntent().getParcelableArrayListExtra(Intent.EXTRA_STREAM);//get Parcelabe list
//            ArrayList<String> selectedImages = new ArrayList<>();
//
//            //Loop to all parcelable list
//            for (Parcelable parcel : list) {
//                Uri uri = (Uri) parcel;//get URI
//                String sourcepath = getPath(uri);//Get Path of URI
//                selectedImages.add(sourcepath);//add images to arraylist
//            }
//            loadGridView(selectedImages);//call load gridview
//        }
//    }


    //get actual path of uri
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
