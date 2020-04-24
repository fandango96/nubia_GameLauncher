package cn.nubia.gamelauncherx.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.nubia.gamelauncherx.R;
import cn.nubia.gamelauncherx.util.CommonUtil;
import cn.nubia.gamelauncherx.util.LogUtil;
import cn.nubia.gamelauncherx.util.ReflectUtilities;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameSpaceKeyHelperActivity extends BaseActivity {
    public static final String ACTION_GAME_DUAL = "cn.nubia.intent.action.TOUCH_GAME_KEY_MAP_OPTION";
    private static final String TAG = "GameSpaceKeyHelperActivity";
    private final String CJZC_CLASS_NAME = "com.epicgames.ue4.SplashActivity";
    private final String HYXD_CLASS_NAME = "com.netease.game.MessiahNativeActivity";
    private final String QJCJ_CLASS_NAME = "com.epicgames.ue4.SplashActivity";
    private final String WZRY_CLASS_NAME = "com.tencent.tmgp.sgame.SGameActivity";
    private final String WZRY_PACKAGE_NAME = "com.tencent.tmgp.sgame";
    private List<String> mEatChickenPkgNameList = new ArrayList(Arrays.asList(new String[]{"com.tencent.tmgp.pubgmhd", "com.tencent.tmgp.pubgm", "com.netease.hyxd"}));
    private BroadcastReceiver mFinishActivityReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.CLOSE_SYSTEM_DIALOGS")) {
                String reason = intent.getStringExtra("reason");
                if (reason != null && "homekey".equals(reason)) {
                    GameSpaceKeyHelperActivity.this.finish();
                }
            }
        }
    };
    private View mGameSpaceSlide0;
    private View mGameSpaceSlide1;
    private View mGameSpaceSlide2;
    private View mGameSpaceSlide3;
    /* access modifiers changed from: private */
    public List<View> mGameSpaceSlideViewList = new ArrayList();
    /* access modifiers changed from: private */
    public ImageView mIndication0;
    /* access modifiers changed from: private */
    public ImageView mIndication1;
    /* access modifiers changed from: private */
    public ImageView mIndication2;
    /* access modifiers changed from: private */
    public ImageView mIndication3;
    private PackageManager mPm;
    private ImageView mReturnBtn;
    private ViewPager mViewPaper;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.gamespace_key_helper_activity);
        this.mPm = getPackageManager();
        initImageViewList();
        initViewPaper();
        initIndication();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        registerReceiver(this.mFinishActivityReceiver, filter);
    }

    private void initImageViewList() {
        LayoutInflater inflater = LayoutInflater.from(this);
        this.mGameSpaceSlide0 = inflater.inflate(R.layout.gamespace_slide0, null);
        this.mGameSpaceSlide1 = inflater.inflate(R.layout.gamespace_slide1, null);
        this.mGameSpaceSlide2 = inflater.inflate(R.layout.gamespace_slide2, null);
        this.mGameSpaceSlide3 = inflater.inflate(R.layout.gamespace_slide3, null);
        if (CommonUtil.isInternalVersion()) {
            this.mGameSpaceSlideViewList.add(this.mGameSpaceSlide2);
            this.mGameSpaceSlideViewList.add(this.mGameSpaceSlide3);
            TextView tail_sub = (TextView) this.mGameSpaceSlide2.findViewById(R.id.tail_text_sub);
            ((TextView) this.mGameSpaceSlide2.findViewById(R.id.tail_text)).setText(R.string.game_space_function_string_slide_inter_0);
            tail_sub.setVisibility(8);
            TextView tail_sub2 = (TextView) this.mGameSpaceSlide3.findViewById(R.id.tail_text_sub);
            ((TextView) this.mGameSpaceSlide3.findViewById(R.id.tail_text)).setText(R.string.game_space_function_string_slide_inter_1);
            tail_sub2.setVisibility(8);
            ImageView slid = (ImageView) this.mGameSpaceSlide2.findViewById(R.id.gamespace_slide2);
            if (CommonUtil.isNX651J_Project()) {
                slid.setBackgroundResource(R.drawable.gamespace_key_help_inter_0_651);
            } else {
                slid.setBackgroundResource(R.drawable.gamespace_key_help_inter_0);
            }
            ((ImageView) this.mGameSpaceSlide3.findViewById(R.id.gamespace_slide3)).setBackgroundResource(R.drawable.gamespace_key_help_0);
        } else {
            this.mGameSpaceSlideViewList.add(this.mGameSpaceSlide0);
            this.mGameSpaceSlideViewList.add(this.mGameSpaceSlide1);
            this.mGameSpaceSlideViewList.add(this.mGameSpaceSlide2);
            this.mGameSpaceSlideViewList.add(this.mGameSpaceSlide3);
        }
        ((Button) this.mGameSpaceSlide3.findViewById(R.id.start_setting)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(GameSpaceKeyHelperActivity.ACTION_GAME_DUAL);
                GameSpaceKeyHelperActivity.this.finish();
                GameSpaceKeyHelperActivity.this.sendBroadcast(intent);
            }
        });
    }

    private void initViewPaper() {
        this.mViewPaper = (ViewPager) findViewById(R.id.pager);
        this.mViewPaper.setAdapter(new PagerAdapter() {
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) GameSpaceKeyHelperActivity.this.mGameSpaceSlideViewList.get(position));
            }

            public Object instantiateItem(ViewGroup container, int position) {
                container.addView((View) GameSpaceKeyHelperActivity.this.mGameSpaceSlideViewList.get(position));
                return GameSpaceKeyHelperActivity.this.mGameSpaceSlideViewList.get(position);
            }

            public int getCount() {
                return GameSpaceKeyHelperActivity.this.mGameSpaceSlideViewList.size();
            }

            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }
        });
    }

    private void initIndication() {
        this.mIndication0 = (ImageView) findViewById(R.id.page_indicator0);
        this.mIndication1 = (ImageView) findViewById(R.id.page_indicator1);
        this.mIndication2 = (ImageView) findViewById(R.id.page_indicator2);
        this.mIndication3 = (ImageView) findViewById(R.id.page_indicator3);
        this.mIndication0.setBackgroundResource(R.drawable.gamespace_navigation_light);
        this.mIndication1.setBackgroundResource(R.drawable.gamespace_navigation_default);
        this.mIndication2.setBackgroundResource(R.drawable.gamespace_navigation_default);
        this.mIndication3.setBackgroundResource(R.drawable.gamespace_navigation_default);
        if (CommonUtil.isInternalVersion()) {
            this.mIndication2.setVisibility(8);
            this.mIndication3.setVisibility(8);
        }
        this.mViewPaper.setOnPageChangeListener(new OnPageChangeListener() {
            public void onPageSelected(int position) {
                switch (position % 4) {
                    case 0:
                        GameSpaceKeyHelperActivity.this.mIndication0.setBackgroundResource(R.drawable.gamespace_navigation_light);
                        GameSpaceKeyHelperActivity.this.mIndication1.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        GameSpaceKeyHelperActivity.this.mIndication2.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        GameSpaceKeyHelperActivity.this.mIndication3.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        return;
                    case 1:
                        GameSpaceKeyHelperActivity.this.mIndication0.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        GameSpaceKeyHelperActivity.this.mIndication1.setBackgroundResource(R.drawable.gamespace_navigation_light);
                        GameSpaceKeyHelperActivity.this.mIndication2.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        GameSpaceKeyHelperActivity.this.mIndication3.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        return;
                    case 2:
                        GameSpaceKeyHelperActivity.this.mIndication0.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        GameSpaceKeyHelperActivity.this.mIndication1.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        GameSpaceKeyHelperActivity.this.mIndication2.setBackgroundResource(R.drawable.gamespace_navigation_light);
                        GameSpaceKeyHelperActivity.this.mIndication3.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        return;
                    case 3:
                        GameSpaceKeyHelperActivity.this.mIndication0.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        GameSpaceKeyHelperActivity.this.mIndication1.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        GameSpaceKeyHelperActivity.this.mIndication2.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        GameSpaceKeyHelperActivity.this.mIndication3.setBackgroundResource(R.drawable.gamespace_navigation_light);
                        return;
                    default:
                        GameSpaceKeyHelperActivity.this.mIndication0.setBackgroundResource(R.drawable.gamespace_navigation_light);
                        GameSpaceKeyHelperActivity.this.mIndication1.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        GameSpaceKeyHelperActivity.this.mIndication2.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        GameSpaceKeyHelperActivity.this.mIndication3.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        return;
                }
            }

            public void onPageScrolled(int position, float state, int num) {
            }

            public void onPageScrollStateChanged(int position) {
            }
        });
    }

    private void startGameActivity(String pkgName, String className) {
        try {
            ReflectUtilities.requestCPUBoost();
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(pkgName, className));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
    }

    public String getAppLabel(String packname) {
        try {
            return (String) this.mPm.getApplicationInfo(packname, 0).loadLabel(this.mPm);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Drawable getAppIcon(String packname) {
        try {
            return this.mPm.getApplicationInfo(packname, 0).loadIcon(this.mPm);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isEngineApkExist(String packageName) {
        ApplicationInfo info = null;
        try {
            info = this.mPm.getApplicationInfo(packageName, 0);
        } catch (NameNotFoundException e) {
            LogUtil.i(TAG, "Apk NameNotFoundException.");
        }
        if (info != null) {
            return true;
        }
        return false;
    }

    public String getAppClassName(String packname) {
        try {
            return this.mPm.getApplicationInfo(packname, 0).className;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
