package com.einfoplanet.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ImgDetailDO> imageDetailData;
    private SparseBooleanArray mSparseBooleanArray;//Variable to store selected Images
    private DisplayImageOptions options;
    private boolean isCustomGalleryActivity;//Variable to check if gridview is to setup for Custom Gallery or not

    public GridAdapter(Context context, ArrayList<ImgDetailDO> imgDetailData, boolean isCustomGalleryActivity,SparseBooleanArray sparseBooleanArray) {
        this.context = context;
        this.imageDetailData = imgDetailData;
        this.isCustomGalleryActivity = isCustomGalleryActivity;
        mSparseBooleanArray = sparseBooleanArray;

        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .resetViewBeforeLoading(true).cacheOnDisk(true)
                .considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    //Method to return selected Images
    public ArrayList<ImgDetailDO> getCheckedItems() {
        ArrayList<ImgDetailDO> mTempArry = new ArrayList<ImgDetailDO>();

        for (int i = 0; i < imageDetailData.size(); i++) {
            if (mSparseBooleanArray.get(i)) {
                ImgDetailDO imgDetailDO = imageDetailData.get(i);
                imgDetailDO.tickStatus = true;
                mTempArry.add(imgDetailDO);
            }
        }
        return mTempArry;
    }

    @Override
    public int getCount() {
        return imageDetailData.size();
    }

    @Override
    public Object getItem(int i) {
        return imageDetailData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ImgDetailDO imgDetailDO = imageDetailData.get(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null)
            view = inflater.inflate(R.layout.custom_grid_view_single_item, viewGroup, false);//Inflate layout

        final RelativeLayout rlContainer = view.findViewById(R.id.rl_container);
        final LinearLayout llImgSelectContainer = view.findViewById(R.id.ll_img_select_true);
        final CheckBox mCheckBox = (CheckBox) view.findViewById(R.id.selectCheckBox);
        final ImageView imageView = (ImageView) view.findViewById(R.id.galleryImageView);

        //If Context is MainActivity then hide checkbox
        if (!isCustomGalleryActivity)
            mCheckBox.setVisibility(View.INVISIBLE);

        ImageLoader.getInstance().displayImage("file://" + imgDetailDO.imgURI, imageView, options);//Load Images over ImageView

        mCheckBox.setTag(position);//Set Tag for CheckBox
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);//Insert selected checkbox value inside boolean array
                ((GalleryActivity) context).showSelectButton();//call custom gallery activity method
                if (isChecked) {
                    llImgSelectContainer.setVisibility(View.VISIBLE);
                } else {
                    llImgSelectContainer.setVisibility(View.GONE);
                }
            }
        });

        if (isCustomGalleryActivity) {
            if (mSparseBooleanArray.get((Integer) mCheckBox.getTag())){
                llImgSelectContainer.setVisibility(View.VISIBLE);
                mCheckBox.setChecked(true);
            }
            else{
                llImgSelectContainer.setVisibility(View.GONE);
                mCheckBox.setChecked(false);
            }

            rlContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCheckBox.performClick();
                }
            });
        }
        return view;
    }
}
