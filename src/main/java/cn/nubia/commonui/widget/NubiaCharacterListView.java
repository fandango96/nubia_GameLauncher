package cn.nubia.commonui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.nubia.commonui.R;
import java.util.ArrayList;
import java.util.HashMap;

public class NubiaCharacterListView extends View {
    private final String FAVORITE_CHAR = "*";
    private final String TAG = "NubiaWidget";
    private boolean isChinese;
    private MyAdapter mAdapter;
    private ImageView mBottom;
    private int mChoose = -1;
    /* access modifiers changed from: private */
    public RelativeLayout mContainer;
    private Context mContext;
    private int mDiplayHeightPixels;
    /* access modifiers changed from: private */
    public ArrayList<String> mEntries = new ArrayList<>();
    private boolean mFavFlag = false;
    private boolean mFlag;
    protected String[] mFullSet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};
    /* access modifiers changed from: private */
    public Handler mHander = new Handler();
    protected String[] mHasFavFullSet = {"*", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};
    private String[] mHighLightCharacterList = null;
    /* access modifiers changed from: private */
    public boolean mIsInCharcterListView;
    private LayoutParams mLayoutParams;
    private MyListView mListView;
    private HashMap<String, ArrayList<String>> mMap;
    private String[] mNullSet = {""};
    OnTouchingLetterChangedListener mOnTouchingLetterChangedListener;
    private float mPaintTextSize;
    private Paint mPaintTxt = new Paint();
    /* access modifiers changed from: private */
    public PopupWindow mPopup = null;
    private TextView mPopupText = null;
    private int mPopupXLoc;
    private int mPreHeight = 0;
    /* access modifiers changed from: private */
    public Runnable mRunnable;
    private float mSingleLetterHeight;
    private boolean mSingleMode = false;
    private TextView mTitle;
    private WindowManager mWindow;
    private float mXPos;
    private float mYPos;
    private String[] temp = this.mFullSet;

    class MyAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<String> mList;

        public MyAdapter(Context context, ArrayList<String> list) {
            this.mContext = context;
            this.mList = list;
        }

        public int getCount() {
            if (this.mList == null) {
                return 0;
            }
            return this.mList.size();
        }

        public Object getItem(int position) {
            return this.mList.get(position);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(this.mContext, R.layout.nubia_char_list_view_item, null);
            }
            ((TextView) convertView.findViewById(R.id.name)).setText((String) getItem(position));
            return convertView;
        }

        public void setListView(ArrayList<String> list) {
            this.mList = list;
            notifyDataSetChanged();
        }
    }

    public interface OnTouchingLetterChangedListener {
        boolean onTouchingLetterChanged(String str);
    }

    public NubiaCharacterListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
    }

    public NubiaCharacterListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public NubiaCharacterListView(Context context) {
        super(context);
        this.mContext = context;
    }

    public void initContext(Context context) {
        this.mContext = context;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        init(this.mContext);
    }

    private void init(Context context) {
        View popupContent = LayoutInflater.from(context).inflate(R.layout.nubia_letterdialog, null);
        this.mPopupText = (TextView) popupContent.findViewById(R.id.nubia_lettertext);
        this.mPopup = new PopupWindow(popupContent, -2, -2);
        this.mPopup.setFocusable(false);
        this.mPopup.setOutsideTouchable(true);
        this.mPopupXLoc = context.getResources().getDimensionPixelSize(R.dimen.nubia_character_popup_x_location);
        this.mDiplayHeightPixels = getResources().getDisplayMetrics().heightPixels;
        this.isChinese = getResources().getConfiguration().locale.getLanguage().endsWith("zh");
    }

    public void setSingleMode(boolean mode) {
        if (this.mSingleMode != mode) {
            this.mSingleMode = mode;
            if (this.mWindow != null) {
                this.mContainer.removeView(this.mBottom);
                this.mWindow.removeView(this.mContainer);
                this.mAdapter = null;
                this.mWindow = null;
            }
        }
    }

    public boolean getSingleMode() {
        return this.mSingleMode;
    }

    public void setLableNamesMap(HashMap<String, ArrayList<String>> map) {
        this.mMap = map;
    }

    /* access modifiers changed from: protected */
    public boolean makeLettersFitSplitScreen(int currentHeight) {
        String preString;
        if (this.mPreHeight != currentHeight) {
            if (this.mChoose < 0 || this.mChoose >= this.temp.length) {
                preString = this.temp[0];
            } else {
                preString = this.temp[this.mChoose];
            }
            this.temp = this.mFullSet;
            this.mPaintTextSize = getContext().getResources().getDimension(R.dimen.nubia_character_list_view_text_size);
            this.mChoose = -1;
            setCurrentLetter(preString);
            this.mPreHeight = currentHeight;
        }
        return true;
    }

    private void drawFav(Canvas canvas, int drawableResID) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableResID);
        canvas.drawBitmap(bm, (float) ((getWidth() / 2) - (bm.getWidth() / 2)), 0.0f, null);
    }

    private void drawFavorite(Canvas canvas) {
        drawFav(canvas, R.drawable.nubia_char_star_small);
    }

    public void setFavFlag() {
        this.mFavFlag = true;
    }

    public String getFavoriteLetter() {
        return "*";
    }

    /* access modifiers changed from: protected */
    public void drawLetter(Canvas canvas) {
        if (makeLettersFitSplitScreen(getHeight())) {
            this.mPaintTxt.setAntiAlias(true);
            this.mPaintTxt.setTextSize(this.mPaintTextSize);
            if (this.mFavFlag) {
                this.temp = this.mHasFavFullSet;
            }
            this.mSingleLetterHeight = getSingleHeight();
            for (int i = 0; i < this.temp.length; i++) {
                if (!this.mFavFlag || i != 0) {
                    this.mPaintTxt.setColor(getContext().getResources().getColor(R.color.nubia_character_normal_color));
                    this.mXPos = ((float) (getWidth() / 2)) - (this.mPaintTxt.measureText(this.temp[i]) / 2.0f);
                    this.mYPos = this.mSingleLetterHeight * ((float) (i + 1));
                    if (this.mFlag && isInHighLightCharacterList(this.mHighLightCharacterList, this.temp[i])) {
                        this.mPaintTxt.setColor(getContext().getResources().getColor(R.color.nubia_character_normal_color));
                    }
                    if (i == this.mChoose) {
                        this.mPaintTxt.setColor(getContext().getResources().getColor(R.color.nubia_character_normal_color));
                    }
                    canvas.drawText(this.temp[i], this.mXPos, this.mYPos, this.mPaintTxt);
                } else {
                    drawFavorite(canvas);
                }
            }
        }
    }

    public float getSingleHeight() {
        return (float) (getHeight() / this.temp.length);
    }

    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (this.mOnTouchingLetterChangedListener != null) {
            this.mOnTouchingLetterChangedListener.onTouchingLetterChanged(null);
        }
        if (this.mContainer != null && !hasWindowFocus && this.mWindow != null) {
            this.mContainer.removeView(this.mBottom);
            this.mWindow.removeView(this.mContainer);
            this.mAdapter = null;
            this.mWindow = null;
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLetter(canvas);
    }

    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float y = event.getY();
        int oldChoose = this.mChoose;
        OnTouchingLetterChangedListener listener = this.mOnTouchingLetterChangedListener;
        int c = (int) ((y / ((float) getHeight())) * ((float) this.temp.length));
        switch (action) {
            case 0:
                this.mIsInCharcterListView = true;
                this.mFlag = true;
                if (oldChoose != c && listener != null && c >= 0 && c < this.temp.length && ((this.temp[c].compareTo("A") >= 0 || this.temp[c].equals("#") || this.temp[c].equals("*")) && !this.temp[c].equals("•"))) {
                    if (!this.isChinese) {
                        showPopupWindow(this.temp[c]);
                    } else {
                        this.mHander.removeCallbacks(this.mRunnable);
                        showFloatingView(this.temp[c]);
                    }
                    if (true == listener.onTouchingLetterChanged(this.temp[c])) {
                        this.mChoose = c;
                    }
                }
                postInvalidate();
                break;
            case 1:
                this.mIsInCharcterListView = false;
                this.mFlag = false;
                if (!this.isChinese) {
                    hidePopupWindow();
                } else {
                    hideFloatingView();
                }
                if (listener != null) {
                    listener.onTouchingLetterChanged(null);
                }
                postInvalidate();
                break;
            case 2:
                if (oldChoose != c && listener != null && c >= 0 && c < this.temp.length && ((this.temp[c].compareTo("A") >= 0 || this.temp[c].equals("#") || this.temp[c].equals("*")) && !this.temp[c].equals("•"))) {
                    if (this.mFlag && isInHighLightCharacterList(this.mHighLightCharacterList, this.temp[c])) {
                        if (!this.isChinese) {
                            showPopupWindow(this.temp[c]);
                        } else {
                            showFloatingView(this.temp[c]);
                        }
                    }
                    if (true == listener.onTouchingLetterChanged(this.temp[c])) {
                        this.mChoose = c;
                        postInvalidate();
                        break;
                    }
                }
                break;
            case 3:
                this.mIsInCharcterListView = false;
                if (!this.isChinese) {
                    hidePopupWindow();
                } else {
                    hideFloatingView();
                }
                if (listener != null) {
                    listener.onTouchingLetterChanged(null);
                }
                postInvalidate();
                break;
        }
        return true;
    }

    public void setCurrentLetter(String letter) {
        if (letter == null || this.mChoose >= this.temp.length) {
            return;
        }
        if (this.mChoose == -1 || (!letter.equals(this.temp[this.mChoose].toLowerCase()) && !letter.equals(this.temp[this.mChoose]))) {
            for (int i = 0; i < this.temp.length; i++) {
                if (letter.equals(this.temp[i].toLowerCase()) || letter.equals(this.temp[i])) {
                    this.mChoose = i;
                    postInvalidate();
                    return;
                }
            }
        }
    }

    private void show(String letter) {
        Drawable drawable;
        TextView showText = this.isChinese ? this.mTitle : this.mPopupText;
        if (this.isChinese) {
            drawable = getResources().getDrawable(R.drawable.nubia_char_popup_star);
        } else {
            drawable = getResources().getDrawable(R.drawable.nubia_char_float_star);
        }
        if (letter.equals(this.mHasFavFullSet[0])) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumWidth());
            showText.setCompoundDrawables(null, null, drawable, null);
            showText.setText("");
            return;
        }
        drawable.setBounds(0, 0, 0, 0);
        showText.setCompoundDrawables(null, null, null, null);
        showText.setText(letter);
    }

    private void showPopupWindow(String letter) {
        if (this.mPopup != null) {
            show(letter);
            if (getLayoutDirection() == 1) {
                this.mPopup.showAtLocation(this, GravityCompat.START, this.mPopupXLoc, 0);
            } else {
                this.mPopup.showAtLocation(this, GravityCompat.END, this.mPopupXLoc, 0);
            }
        }
    }

    private boolean isInHighLightCharacterList(String[] highLightCharacterList, String currentCharacter) {
        if (highLightCharacterList == null) {
            return false;
        }
        for (String s : highLightCharacterList) {
            if (s.equals(currentCharacter)) {
                return true;
            }
        }
        return false;
    }

    public void setHighLightCharacterList(String[] characterList) {
        this.mHighLightCharacterList = characterList;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        if (this.mPopup != null) {
            this.mPopup.dismiss();
        }
        if (this.mWindow != null) {
            this.mWindow.removeView(this.mContainer);
            this.mWindow = null;
        }
        super.onDetachedFromWindow();
    }

    private void hidePopupWindow() {
        getHandler().postDelayed(new Runnable() {
            public void run() {
                if (NubiaCharacterListView.this.mPopup != null) {
                    NubiaCharacterListView.this.mPopup.dismiss();
                }
            }
        }, 300);
    }

    public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.mOnTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    private int getScreenWidth() {
        DisplayMetrics metric = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService("window")).getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    private void initFloatView(Context context) {
        this.mWindow = (WindowManager) context.getSystemService("window");
        this.mLayoutParams = new LayoutParams();
        this.mLayoutParams.type = 2;
        this.mLayoutParams.gravity = 8388659;
        this.mLayoutParams.width = getResources().getDimensionPixelSize(R.dimen.nubia_character_list_item_height);
        this.mLayoutParams.height = getResources().getDimensionPixelSize(R.dimen.nubia_character_window_height);
        if (this.mSingleMode) {
            this.mLayoutParams.x = (getScreenWidth() / 2) - (this.mLayoutParams.width / 2);
        } else {
            this.mLayoutParams.x = 747;
        }
        this.mLayoutParams.y = 330;
        this.mLayoutParams.flags = 8;
        this.mLayoutParams.flags |= 262144;
        this.mLayoutParams.format = 1;
        this.mContainer = (RelativeLayout) View.inflate(context, R.layout.nubia_floatging_view, null);
        this.mContainer.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() != 4) {
                    return false;
                }
                if (!NubiaCharacterListView.this.mIsInCharcterListView) {
                    NubiaCharacterListView.this.mContainer.setVisibility(8);
                }
                return true;
            }
        });
        this.mListView = (MyListView) this.mContainer.findViewById(R.id.my_list_view_2);
        this.mListView.setOverScrollMode(2);
        this.mTitle = (TextView) this.mContainer.findViewById(R.id.title);
        this.mBottom = (ImageView) this.mContainer.findViewById(R.id.bottom);
        this.mListView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case 0:
                    case 2:
                        NubiaCharacterListView.this.mHander.removeCallbacks(NubiaCharacterListView.this.mRunnable);
                        break;
                    case 1:
                        NubiaCharacterListView.this.hideFloatingView();
                        break;
                }
                return false;
            }
        });
        this.mRunnable = new Runnable() {
            public void run() {
                NubiaCharacterListView.this.mContainer.setVisibility(8);
            }
        };
        this.mListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
                if (NubiaCharacterListView.this.mOnTouchingLetterChangedListener != null) {
                    NubiaCharacterListView.this.mOnTouchingLetterChangedListener.onTouchingLetterChanged((String) NubiaCharacterListView.this.mEntries.get(arg2));
                }
            }
        });
        this.mContainer.setVisibility(8);
    }

    private void showFloatingView(String c) {
        this.mEntries = this.mMap != null ? (ArrayList) this.mMap.get(c) : this.mEntries;
        if (this.mWindow == null) {
            this.mAdapter = new MyAdapter(getContext(), this.mEntries);
            initFloatView(this.mContext);
            this.mWindow.addView(this.mContainer, this.mLayoutParams);
            this.mContainer.setVisibility(0);
        } else {
            this.mContainer.setVisibility(0);
        }
        if (this.mEntries == null || this.mEntries.isEmpty()) {
            this.mTitle.setBackground(getResources().getDrawable(R.drawable.nubia_char_background));
            this.mBottom.setVisibility(8);
        } else {
            this.mTitle.setBackground(getResources().getDrawable(R.drawable.nubia_char_head));
            this.mBottom.setVisibility(0);
        }
        if (!(this.mAdapter == null || this.mListView == null)) {
            this.mAdapter.setListView(this.mEntries);
            this.mListView.setAdapter(this.mAdapter);
            this.mListView.setSelection(0);
        }
        show(c);
    }

    /* access modifiers changed from: private */
    public void hideFloatingView() {
        this.mHander.postDelayed(this.mRunnable, 3000);
    }

    public void hideCharList() {
        if (this.mContainer != null) {
            this.mContainer.setVisibility(8);
        }
    }
}
