package cn.nubia.gamelauncherx.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cn.nubia.gamelauncherx.R;
import cn.nubia.gamelauncherx.adapter.AstoPhereMapAdapter;
import cn.nubia.gamelauncherx.bean.AppListItemBean;
import cn.nubia.gamelauncherx.gamelist.GameItemDecoration;
import cn.nubia.gamelauncherx.model.AppAddModel;
import cn.nubia.gamelauncherx.recycler.BannerManager;
import cn.nubia.gamelauncherx.view.SimpleEditImageView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CustomAstophereMapActivity extends BaseActivity {
    private static final String CUSTOM_IMAGE_DIR_NAME = "custom_image";
    private static final int OPEN_GALLERY_REQUEST_CODE = 100;
    private static String[] PERMISSIONS_STORAGE = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private TextView mAapplyText;
    private ArrayList<AppListItemBean> mAppAddedList;
    private AppListItemBean mAppListItemBean;
    private File mAstophereMapFile;
    private ArrayList<String> mAstophereMapLists;
    private RecyclerView mAstophereRecyclerView;
    private TextView mCancelText;
    /* access modifiers changed from: private */
    public Canvas mCanvas = null;
    /* access modifiers changed from: private */
    public int mCropBitmapHeight;
    private int mCropBitmapHeightOffSet;
    /* access modifiers changed from: private */
    public int mCropBitmapStartX;
    /* access modifiers changed from: private */
    public int mCropBitmapStartY;
    /* access modifiers changed from: private */
    public int mCropBitmapWidth;
    private int mDeviceHeight;
    private int mDeviceWidth;
    /* access modifiers changed from: private */
    public SimpleEditImageView mEditImageView;
    private Bitmap mGalleryBitmap = null;
    private TextView mGalleryButton;
    /* access modifiers changed from: private */
    public Paint mPaint = null;
    /* access modifiers changed from: private */
    public Bitmap savedBitmap;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.astophere_layout);
        initView();
        initDisplayInfo();
        initPaintAndCanvas();
        initAppListItemBean();
        initAstophereMapFile();
        initAstophereMapListData();
        initRecyclerView();
    }

    private void showPermission() {
        if (ActivityCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1);
        }
    }

    private void initAppListItemBean() {
        this.mAppAddedList = BannerManager.getInstance().getGameAddedList();
        int position = getIntent().getIntExtra("position", 0);
        if (position < this.mAppAddedList.size() - 1) {
            this.mAppListItemBean = (AppListItemBean) this.mAppAddedList.get(position);
        }
    }

    @TargetApi(8)
    private void initAstophereMapFile() {
        this.mAstophereMapFile = getApplicationContext().getExternalFilesDir(CUSTOM_IMAGE_DIR_NAME);
    }

    private void initAstophereMapListData() {
        try {
            AssetManager assets = getAssets();
            String[] images = assets.list("");
            this.mEditImageView.setBitmap(BitmapFactory.decodeStream(assets.open(images[0])));
            if (images != null && images.length > 0) {
                this.mAstophereMapLists = new ArrayList<>();
                for (int index = 0; index < images.length; index++) {
                    if ((images[index].endsWith("jpg") || images[index].endsWith("png")) && !images[index].startsWith("minority_boutique") && !images[index].startsWith("classic_masterpiece") && !images[index].startsWith("card_add")) {
                        this.mAstophereMapLists.add(images[index]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initDisplayInfo() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            Class.forName("android.view.Display").getMethod("getRealMetrics", new Class[]{DisplayMetrics.class}).invoke(display, new Object[]{dm});
            this.mDeviceWidth = dm.widthPixels;
            this.mDeviceHeight = dm.heightPixels;
        } catch (Exception e) {
            DisplayMetrics dm2 = getResources().getDisplayMetrics();
            this.mDeviceWidth = dm2.widthPixels;
            this.mDeviceHeight = dm2.heightPixels;
            e.printStackTrace();
        }
        this.mCropBitmapWidth = getResources().getDimensionPixelOffset(R.dimen.crop_bitmap_width);
        this.mCropBitmapHeight = getResources().getDimensionPixelOffset(R.dimen.crop_bitmap_height);
        this.mCropBitmapHeightOffSet = getResources().getDimensionPixelOffset(R.dimen.crop_bitmap_height_offset);
        this.mCropBitmapStartX = (this.mDeviceWidth - this.mCropBitmapWidth) / 2;
        this.mCropBitmapStartY = ((this.mDeviceHeight - this.mCropBitmapHeight) / 2) - this.mCropBitmapHeightOffSet;
    }

    private void initPaintAndCanvas() {
        this.mPaint = new Paint();
        this.mPaint.setColor(-16711936);
        this.savedBitmap = Bitmap.createBitmap(this.mDeviceWidth, this.mDeviceHeight, Config.ARGB_8888);
        this.mCanvas = new Canvas(this.savedBitmap);
    }

    private void initView() {
        this.mEditImageView = (SimpleEditImageView) findViewById(R.id.simple_edit);
        this.mCancelText = (TextView) findViewById(R.id.cancel);
        this.mAapplyText = (TextView) findViewById(R.id.apply);
        this.mGalleryButton = (TextView) findViewById(R.id.gallery);
        this.mCancelText.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CustomAstophereMapActivity.this.finish();
            }
        });
        this.mAapplyText.setOnClickListener(new OnClickListener() {
            @SuppressLint({"NewApi"})
            public void onClick(View v) {
                RectF rectF = new RectF();
                CustomAstophereMapActivity.this.mEditImageView.getCurrentMatrix().mapRect(rectF);
                CustomAstophereMapActivity.this.mCanvas.drawBitmap(CustomAstophereMapActivity.this.mEditImageView.getMatrixBitmap(), rectF.left, rectF.top, CustomAstophereMapActivity.this.mPaint);
                CustomAstophereMapActivity.this.savedBitmap = Bitmap.createBitmap(CustomAstophereMapActivity.this.savedBitmap, CustomAstophereMapActivity.this.mCropBitmapStartX, CustomAstophereMapActivity.this.mCropBitmapStartY, CustomAstophereMapActivity.this.mCropBitmapWidth, CustomAstophereMapActivity.this.mCropBitmapHeight);
                CustomAstophereMapActivity.this.saveCropedBitmapToStorage(CustomAstophereMapActivity.this.savedBitmap);
                CustomAstophereMapActivity.this.finish();
            }
        });
        this.mGalleryButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.GET_CONTENT");
                intent.setAction("android.intent.action.PICK");
                intent.setType("image/*");
                intent.putExtra("return-data", true);
                CustomAstophereMapActivity.this.startActivityForResult(intent, 100);
            }
        });
    }

    /* access modifiers changed from: private */
    public void saveCropedBitmapToStorage(Bitmap galleryBitmap) {
        if (!this.mAstophereMapFile.exists()) {
            this.mAstophereMapFile.mkdirs();
        }
        File imageFile = new File(this.mAstophereMapFile, System.currentTimeMillis() + ".png");
        try {
            imageFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            if (galleryBitmap != null) {
                Bitmap.createBitmap(galleryBitmap, 0, 0, galleryBitmap.getWidth(), galleryBitmap.getHeight()).compress(CompressFormat.PNG, 90, outputStream);
            }
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (this.mAppListItemBean != null) {
            this.mAppListItemBean.setImageUrl(imageFile.getAbsolutePath());
            AppAddModel.getInstance().updateAppItemBeanInAppAddDB(this.mAppListItemBean);
        }
    }

    private void initRecyclerView() {
        this.mAstophereRecyclerView = (RecyclerView) findViewById(R.id.picture_list);
        this.mAstophereRecyclerView.setLayoutParams(new LayoutParams(150, -2));
        this.mAstophereRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.mAstophereRecyclerView.addItemDecoration(new GameItemDecoration(0, 10));
        this.mAstophereRecyclerView.setAdapter(new AstoPhereMapAdapter(this, this.mAstophereMapLists, this.mEditImageView));
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data != null && data.getData() != null) {
            try {
                this.mGalleryBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                if (this.mGalleryBitmap != null && (this.mGalleryBitmap.getWidth() > this.mDeviceWidth || this.mGalleryBitmap.getHeight() > this.mDeviceHeight)) {
                    int mRatio = Math.max(this.mGalleryBitmap.getWidth() / this.mDeviceWidth, this.mGalleryBitmap.getHeight() / this.mDeviceHeight);
                    this.mGalleryBitmap = Bitmap.createScaledBitmap(this.mGalleryBitmap, this.mGalleryBitmap.getWidth() / mRatio, this.mGalleryBitmap.getHeight() / mRatio, true);
                }
                this.mEditImageView.setBitmap(this.mGalleryBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
