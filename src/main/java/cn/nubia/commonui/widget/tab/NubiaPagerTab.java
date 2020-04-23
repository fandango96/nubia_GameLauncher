package cn.nubia.commonui.widget.tab;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.nubia.commonui.R;
import java.util.ArrayList;

public class NubiaPagerTab extends HorizontalScrollView implements OnPageChangeListener {
    private static final float TAB_ITEM_WIDTH = 80.0f;
    private int mActivatedColor;
    private Context mContext;
    private Rect mIndicatorRect;
    private int mNormalColor;
    /* access modifiers changed from: private */
    public ViewPager mPager;
    private int mPrevSelected;
    private int mTabCount;
    private int mTabItemWidth;
    private LinearLayout mTabStrip;
    private ColorStateList mTabTextColor;
    private ArrayList<TextView> mTextList;

    private class OnTabLongClickListener implements OnLongClickListener {
        final int mPosition;

        public OnTabLongClickListener(int position) {
            this.mPosition = position;
        }

        public boolean onLongClick(View v) {
            NubiaPagerTab.this.mPager.setCurrentItem(NubiaPagerTab.this.getRtlPosition(this.mPosition));
            return true;
        }
    }

    public NubiaPagerTab(Context context) {
        this(context, null);
        this.mContext = context;
        this.mTextList = new ArrayList<>();
    }

    public NubiaPagerTab(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.nubiaPagerTabStyle);
        this.mContext = context;
        this.mTextList = new ArrayList<>();
    }

    public NubiaPagerTab(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mIndicatorRect = new Rect();
        this.mPrevSelected = -1;
        this.mTabCount = 0;
        this.mTabItemWidth = 0;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NubiaPagerTab, defStyle, 0);
        this.mActivatedColor = typedArray.getColor(R.styleable.NubiaPagerTab_tabActivatedColor, R.color.nubia_tab_activated_red);
        this.mNormalColor = typedArray.getColor(R.styleable.NubiaPagerTab_tabNormalColor, R.color.nubia_tab_normal_color);
        this.mTabTextColor = new ColorStateList(new int[][]{new int[]{16842913}, new int[]{0}}, new int[]{this.mActivatedColor, this.mNormalColor});
        typedArray.recycle();
        setFillViewport(true);
        this.mTabItemWidth = dip2px(TAB_ITEM_WIDTH);
        this.mTabStrip = new LinearLayout(context);
        LayoutParams params = new LayoutParams(-2, -1);
        params.gravity = 17;
        addView(this.mTabStrip, params);
    }

    public void setViewPager(ViewPager viewPager) {
        this.mPager = viewPager;
        addTabs((PagerAdapterTab) this.mPager.getAdapter());
    }

    private void addTabs(PagerAdapterTab adapter) {
        this.mTabStrip.removeAllViews();
        int count = adapter.getCount();
        this.mTabCount = count;
        for (int i = 0; i < count; i++) {
            addTab(adapter.getPageImage(i), adapter.getPageTitle(i), i);
        }
    }

    private void addTab(Drawable tabImage, CharSequence tabTitle, final int position) {
        View view = LayoutInflater.from(this.mContext).inflate(R.layout.nubia_tab_item, null);
        ImageView image = (ImageView) view.findViewById(R.id.nubia_tab_image);
        TextView title = (TextView) view.findViewById(R.id.nubia_tab_title);
        TextView label = (TextView) view.findViewById(R.id.nubia_tab_label);
        this.mTextList.add(label);
        image.setImageDrawable(tabImage);
        title.setText(tabTitle);
        if (this.mTabTextColor != null) {
            title.setTextColor(this.mTabTextColor);
        }
        label.setText(((TextView) this.mTextList.get(position)).getText());
        label.setVisibility(((TextView) this.mTextList.get(position)).getVisibility());
        view.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                NubiaPagerTab.this.mPager.setCurrentItem(NubiaPagerTab.this.getRtlPosition(position));
            }
        });
        view.setOnLongClickListener(new OnTabLongClickListener(position));
        this.mTabStrip.addView(view, new LinearLayout.LayoutParams(this.mTabItemWidth, -1));
    }

    public void setViewVisable(int position, int visable) {
        if (position >= 0 && position < this.mTextList.size()) {
            TextView text = (TextView) this.mTextList.get(position);
            if (text != null) {
                text.setVisibility(visable);
            }
        }
    }

    public void setVisableAndValue(int position, int visable, CharSequence value) {
        if (position >= 0 && position < this.mTextList.size()) {
            TextView text = (TextView) this.mTabStrip.getChildAt(position).findViewById(R.id.nubia_tab_label);
            if (text != null) {
                text.setVisibility(visable);
                text.setText(value);
            }
        }
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int position2 = getRtlPosition(position);
        if (this.mPrevSelected == -1 && position2 >= 0) {
            this.mPrevSelected = position2;
            this.mTabStrip.getChildAt(position2).setSelected(true);
        }
        int tabStripChildCount = this.mTabStrip.getChildCount();
        if (tabStripChildCount == 0 || position2 < 0 || position2 >= tabStripChildCount) {
        }
    }

    public void onPageSelected(int position) {
        int position2 = getRtlPosition(position);
        int tabStripChildCount = this.mTabStrip.getChildCount();
        if (this.mPrevSelected >= 0 && tabStripChildCount > this.mPrevSelected) {
            this.mTabStrip.getChildAt(this.mPrevSelected).setSelected(false);
        }
        View selectedChild = this.mTabStrip.getChildAt(position2);
        if (selectedChild != null) {
            selectedChild.setSelected(true);
            smoothScrollTo(selectedChild.getLeft() - ((getWidth() - selectedChild.getWidth()) / 2), 0);
            this.mPrevSelected = position2;
        }
    }

    public void onPageScrollStateChanged(int state) {
    }

    /* access modifiers changed from: private */
    public int getRtlPosition(int position) {
        if (getLayoutDirection() == 1) {
            return (this.mTabStrip.getChildCount() - 1) - position;
        }
        return position;
    }

    public int dip2px(float dpValue) {
        return (int) ((dpValue * getResources().getDisplayMetrics().density) + 0.5f);
    }
}
