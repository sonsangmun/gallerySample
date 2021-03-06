package com.baeflower.sol.tourlist_gallerysample;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import com.baeflower.sol.tourlist_gallerysample.adapter.GridAdapter;
import com.baeflower.sol.tourlist_gallerysample.filecache.FileCache;
import com.baeflower.sol.tourlist_gallerysample.filecache.FileCacheFactory;
import com.baeflower.sol.tourlist_gallerysample.imagecache.ImageCache;
import com.baeflower.sol.tourlist_gallerysample.imagecache.ImageCacheFactory;
import com.baeflower.sol.tourlist_gallerysample.model.TourImage;
import com.baeflower.sol.tourlist_gallerysample.util.BitmapScaleSetting;
import com.baeflower.sol.tourlist_gallerysample.util.Constant;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends ActionBarActivity implements View.OnClickListener {

    // 상수
    private static final String TAG = GalleryActivity.class.getSimpleName();
    private static final int SELECT_FROM_GALLERY = 1;

    // Resource
    private Button mBtnGetImg;
    private GridView mGridView;

    // 폰에서 로딩할 수 있는 최대 픽셀 수(ex. 갤S4 : 4096)
    // private int mImageSizeBoundary;

    private BitmapScaleSetting mBitmapScaleSetting;

    // Data
    private GridAdapter mGridAdapter;


    private List<TourImage> mTourListImgList;
    private List<Uri> mUriList;


    // Cache
    private FileCache mFileCache;
    private ImageCache mImageCache;

    private void init() {
        mBtnGetImg = (Button) findViewById(R.id.btn_get_img);
        mGridView = (GridView) findViewById(R.id.gv_each_image);

        // Data
        mTourListImgList = new ArrayList<>();
        mUriList = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        init();
        mBitmapScaleSetting = new BitmapScaleSetting(getPackageName(), Constant.getMaxTextureSize(), getApplicationContext());


        // Cache
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        String cacheName = "gallery";
        FileCacheFactory.initialize(this);
        if (!FileCacheFactory.getInstance().has(cacheName)) {
            FileCacheFactory.getInstance().create(cacheName, cacheSize);
        }
        mFileCache = FileCacheFactory.getInstance().get(cacheName);

        // 이미지 캐시 초기화
        int memoryImageMaxCounts = 20;
        ImageCacheFactory.getInstance().createTwoLevelCache(cacheName, memoryImageMaxCounts);
        mImageCache = ImageCacheFactory.getInstance().get(cacheName);


        // Adapter
        // View
        mGridAdapter = new GridAdapter(getApplicationContext(), mUriList, mBitmapScaleSetting);
        mGridView.setAdapter(mGridAdapter);

        mBtnGetImg.setOnClickListener(this);

    } // onCreate

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, SELECT_FROM_GALLERY);

    } // onClick


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SELECT_FROM_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData(); // Received Data from the intent
                // String path = uri.getPath();
                // Log.d(TAG, "path : " + path);

                mUriList.add(uri);
                mBitmapScaleSetting.setTempImageFile();

                mGridAdapter.setmShowBtns(false);
                mGridAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDestroy() {
        mUriList = null;
    }

    /*
        private void addBitmapToImgList(String path) {
            // sample size 를 적용하여 bitmap load.
            Bitmap bitmap = mBitmapScaleSetting.loadImageWithSampleSize();
            // Bitmap bitmap =BitmapSetting.decodeSampledBitmapFromFile(getTempImageFile(), 100, 100);
            // Bitmap bitmap =BitmapSetting.decodeSampledBitmapFromFile(getTempImageFile(), mImageSizeBoundary);

            // image boundary size 에 맞도록 이미지 축소.
            bitmap = mBitmapScaleSetting.resizeImageWithinBoundary(bitmap);

            // 결과 file 을 얻어갈 수 있는 메서드 제공.
            // saveBitmapToFile(bitmap);

            // show image on ImageView (저장한 파일 읽어와서 출력하는 듯)
            // Bitmap bm = BitmapFactory.decodeFile(getTempImageFile().getAbsolutePath());
            // mImageView.setImageBitmap(bitmap);

            TourImage tourImage = new TourImage(path, bitmap);
            mImgList.add(tourImage);
        }

    public void addBitmapToImgListUsingCache(String path) {

        // 이미지 캐시 사용 부분
        Bitmap bitmapInCache = mImageCache.getBitmap(path);

        if (bitmapInCache == null) {
            Bitmap loadedBitmap = mBitmapScaleSetting.loadImageWithSampleSize();
            loadedBitmap = mBitmapScaleSetting.resizeImageWithinBoundary(loadedBitmap);
            bitmapInCache = addBitmapToCache(path, loadedBitmap);
        }

        TourImage tourImage = new TourImage(path, bitmapInCache);
        mImgList.add(tourImage);
    }

    private Bitmap addBitmapToCache(String path, Bitmap bitmap) {
        mImageCache.addBitmap(path, bitmap);
        return mImageCache.getBitmap(path);
    }

    */

}
