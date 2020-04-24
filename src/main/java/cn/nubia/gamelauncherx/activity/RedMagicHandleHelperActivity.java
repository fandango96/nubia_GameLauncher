package cn.nubia.gamelauncherx.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import cn.nubia.gamelauncherx.R;
import cn.nubia.gamelauncherx.util.CommonUtil;
import java.util.ArrayList;
import java.util.List;

public class RedMagicHandleHelperActivity extends BaseActivity {
    private static final String TAG = "RedMagicHandleHelperActivity";
    private Context mContext;
    private BroadcastReceiver mFinishActivityReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.CLOSE_SYSTEM_DIALOGS")) {
                String reason = intent.getStringExtra("reason");
                if (reason != null && "homekey".equals(reason)) {
                    RedMagicHandleHelperActivity.this.finish();
                }
            }
        }
    };
    private ImageView mHandleHelp0;
    private ImageView mHandleHelp1;
    private ImageView mHandleHelp2;
    private ImageView mHandleHelp3;
    /* access modifiers changed from: private */
    public ImageView mIndication0;
    /* access modifiers changed from: private */
    public ImageView mIndication1;
    /* access modifiers changed from: private */
    public ImageView mIndication2;
    /* access modifiers changed from: private */
    public ImageView mIndication3;
    private TextView mLeftName;
    private View mRedMagicHandleSlide0;
    private View mRedMagicHandleSlide1;
    private View mRedMagicHandleSlide2;
    private View mRedMagicHandleSlide3;
    /* access modifiers changed from: private */
    public List<View> mRedMagicHandleSlideViewList = new ArrayList();
    private ImageView mReturnBtn;
    private ViewPager mViewPaper;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.redmagic_handle_helper_activity);
        this.mReturnBtn = (ImageView) findViewById(R.id.left_icon);
        this.mReturnBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                RedMagicHandleHelperActivity.this.finish();
            }
        });
        this.mLeftName = (TextView) findViewById(R.id.left_name);
        this.mLeftName.setText(getString(R.string.handle_helper));
        this.mLeftName.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                RedMagicHandleHelperActivity.this.finish();
            }
        });
        initImageViewList();
        initViewPaper();
        initIndication();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        registerReceiver(this.mFinishActivityReceiver, filter);
    }

    private void initImageViewList() {
        LayoutInflater inflater = LayoutInflater.from(this);
        this.mRedMagicHandleSlide0 = inflater.inflate(R.layout.redmagic_handle_slide0, null);
        this.mRedMagicHandleSlide1 = inflater.inflate(R.layout.redmagic_handle_slide1, null);
        this.mRedMagicHandleSlide2 = inflater.inflate(R.layout.redmagic_handle_slide2, null);
        this.mRedMagicHandleSlide3 = inflater.inflate(R.layout.redmagic_handle_slide3, null);
        if (CommonUtil.isInternalVersion()) {
            this.mHandleHelp0 = (ImageView) this.mRedMagicHandleSlide0.findViewById(R.id.redmagic_slide_image0);
            this.mHandleHelp1 = (ImageView) this.mRedMagicHandleSlide1.findViewById(R.id.redmagic_slide_image1);
            this.mHandleHelp2 = (ImageView) this.mRedMagicHandleSlide2.findViewById(R.id.redmagic_slide_image2);
            this.mHandleHelp3 = (ImageView) this.mRedMagicHandleSlide3.findViewById(R.id.redmagic_slide_image3);
            if (CommonUtil.isNX651J_Project()) {
                this.mHandleHelp0.setBackground(getDrawable(R.drawable.redmagic_handle_helper_v2b0_651));
            } else {
                this.mHandleHelp0.setBackground(getDrawable(R.drawable.redmagic_handle_helper_v2b0));
            }
            this.mHandleHelp1.setBackground(getDrawable(R.drawable.redmagic_handle_helper_v2b1));
            this.mHandleHelp2.setBackground(getDrawable(R.drawable.redmagic_handle_helper_v2b2));
            this.mHandleHelp3.setBackground(getDrawable(R.drawable.redmagic_handle_helper_v2b3));
        }
        this.mRedMagicHandleSlideViewList.add(this.mRedMagicHandleSlide0);
        this.mRedMagicHandleSlideViewList.add(this.mRedMagicHandleSlide1);
        this.mRedMagicHandleSlideViewList.add(this.mRedMagicHandleSlide2);
        this.mRedMagicHandleSlideViewList.add(this.mRedMagicHandleSlide3);
    }

    private void initViewPaper() {
        this.mViewPaper = (ViewPager) findViewById(R.id.helper_pager);
        this.mViewPaper.setAdapter(new PagerAdapter() {
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) RedMagicHandleHelperActivity.this.mRedMagicHandleSlideViewList.get(position));
            }

            public Object instantiateItem(ViewGroup container, int position) {
                container.addView((View) RedMagicHandleHelperActivity.this.mRedMagicHandleSlideViewList.get(position));
                return RedMagicHandleHelperActivity.this.mRedMagicHandleSlideViewList.get(position);
            }

            public int getCount() {
                return RedMagicHandleHelperActivity.this.mRedMagicHandleSlideViewList.size();
            }

            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }
        });
    }

    private void initIndication() {
        this.mIndication0 = (ImageView) findViewById(R.id.handle_page_indicator0);
        this.mIndication1 = (ImageView) findViewById(R.id.handle_page_indicator1);
        this.mIndication2 = (ImageView) findViewById(R.id.handle_page_indicator2);
        this.mIndication3 = (ImageView) findViewById(R.id.handle_page_indicator3);
        this.mIndication0.setBackgroundResource(R.drawable.gamespace_navigation_light);
        this.mIndication1.setBackgroundResource(R.drawable.gamespace_navigation_default);
        this.mIndication2.setBackgroundResource(R.drawable.gamespace_navigation_default);
        this.mIndication3.setBackgroundResource(R.drawable.gamespace_navigation_default);
        this.mViewPaper.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageSelected(int position) {
                switch (position % 4) {
                    case 0:
                        RedMagicHandleHelperActivity.this.mIndication0.setBackgroundResource(R.drawable.gamespace_navigation_light);
                        RedMagicHandleHelperActivity.this.mIndication1.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        RedMagicHandleHelperActivity.this.mIndication2.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        RedMagicHandleHelperActivity.this.mIndication3.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        return;
                    case 1:
                        RedMagicHandleHelperActivity.this.mIndication0.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        RedMagicHandleHelperActivity.this.mIndication1.setBackgroundResource(R.drawable.gamespace_navigation_light);
                        RedMagicHandleHelperActivity.this.mIndication2.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        RedMagicHandleHelperActivity.this.mIndication3.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        return;
                    case 2:
                        RedMagicHandleHelperActivity.this.mIndication0.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        RedMagicHandleHelperActivity.this.mIndication1.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        RedMagicHandleHelperActivity.this.mIndication2.setBackgroundResource(R.drawable.gamespace_navigation_light);
                        RedMagicHandleHelperActivity.this.mIndication3.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        return;
                    case 3:
                        RedMagicHandleHelperActivity.this.mIndication0.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        RedMagicHandleHelperActivity.this.mIndication1.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        RedMagicHandleHelperActivity.this.mIndication2.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        RedMagicHandleHelperActivity.this.mIndication3.setBackgroundResource(R.drawable.gamespace_navigation_light);
                        return;
                    default:
                        RedMagicHandleHelperActivity.this.mIndication0.setBackgroundResource(R.drawable.gamespace_navigation_light);
                        RedMagicHandleHelperActivity.this.mIndication1.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        RedMagicHandleHelperActivity.this.mIndication2.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        RedMagicHandleHelperActivity.this.mIndication3.setBackgroundResource(R.drawable.gamespace_navigation_default);
                        return;
                }
            }

            public void onPageScrolled(int position, float state, int num) {
            }

            public void onPageScrollStateChanged(int position) {
            }
        });
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
}
