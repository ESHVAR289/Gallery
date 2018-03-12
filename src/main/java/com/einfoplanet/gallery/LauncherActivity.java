package com.einfoplanet.gallery;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
    private ArrayList<DataDetailDO> selectedImagesToSendOnGridActivity;
    private TextView txtNoData;
    private static final int READ_EXTERNAL_STORAGE_PERMISSION_CONST = 102;

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

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {

                    if (ContextCompat.checkSelfPermission(LauncherActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(LauncherActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            buildAlertMessageForPermission();
                        } else {
                            ActivityCompat.requestPermissions(
                                    LauncherActivity.this, new String[]{
                                            Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION_CONST);
                        }
                    } else {
                        //Start Custom Gallery Activity by passing intent id
                        Intent gridActivityIntent = new Intent(LauncherActivity.this, GalleryActivity.class);
                        gridActivityIntent.putExtra(KEY_IMAGE_DATA_FROM_LAUNCHER_SCREEN, selectedImagesToSendOnGridActivity);//Convert Array into string to pass data
                        startActivityForResult(gridActivityIntent, CustomGallerySelectId);
                    }
                } else {
                    //Start Custom Gallery Activity by passing intent id
                    Intent gridActivityIntent = new Intent(LauncherActivity.this, GalleryActivity.class);
                    gridActivityIntent.putExtra(KEY_IMAGE_DATA_FROM_LAUNCHER_SCREEN, selectedImagesToSendOnGridActivity);//Convert Array into string to pass data
                    startActivityForResult(gridActivityIntent, CustomGallerySelectId);
                }
                break;
        }
    }

    protected void onActivityResult(int requestcode, int resultcode,
                                    Intent imagereturnintent) {
        super.onActivityResult(requestcode, resultcode, imagereturnintent);
        switch (requestcode) {
            case CustomGallerySelectId:
                if (resultcode == RESULT_OK) {

                    selectedImagesToSendOnGridActivity = (ArrayList<DataDetailDO>) imagereturnintent.getSerializableExtra(CustomGalleryIntentKey);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE_PERMISSION_CONST:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //Start Custom Gallery Activity by passing intent id
                        Intent gridActivityIntent = new Intent(LauncherActivity.this, GalleryActivity.class);
                        gridActivityIntent.putExtra(KEY_IMAGE_DATA_FROM_LAUNCHER_SCREEN, selectedImagesToSendOnGridActivity);//Convert Array into string to pass data
                        startActivityForResult(gridActivityIntent, CustomGallerySelectId);
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(LauncherActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    buildAlertMessageForPermission();
                                }
                            });
                        } else {
                            showForcefullPermissionDialog();
                        }
                    }
                }
                break;
        }
    }

    public void showForcefullPermissionDialog() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Permissions Required").setMessage("You have forcefully denied some of the required permissions " +
                "for this action. Please open settings, go to permissions and allow them.")
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    //Load GridView
    private void loadGridView(ArrayList<DataDetailDO> imageDetailData) {
        GridAdapter adapter = new GridAdapter(LauncherActivity.this, imageDetailData, false, new SparseBooleanArray());
        selectedImageGridView.setAdapter(adapter);
    }

    public void buildAlertMessageForPermission() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(LauncherActivity.this);
        builder.setMessage("Gallery app requires read STORAGE permissions.\nWithout which you can not proceed.");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                dialog.cancel();
                ActivityCompat.requestPermissions(LauncherActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION_CONST);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

            }
        });
        AlertDialog alert = builder.create();
        if (!isFinishing())
            alert.show();

    }
}
