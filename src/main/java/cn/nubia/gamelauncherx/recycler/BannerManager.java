package cn.nubia.gamelauncherx.recycler;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import cn.nubia.gamelauncherx.R;
import cn.nubia.gamelauncherx.bean.AppListItemBean;
import cn.nubia.gamelauncherx.commoninterface.ConstantVariable;
import cn.nubia.gamelauncherx.commoninterface.IGetAppStatusDataCallBack;
import cn.nubia.gamelauncherx.controller.NeoDownloadManager;
import cn.nubia.gamelauncherx.gamelist.GameEntranceItem;
import cn.nubia.gamelauncherx.gamelist.GameItemDecoration;
import cn.nubia.gamelauncherx.gamelist.GameListPagerAdapter;
import cn.nubia.gamelauncherx.gamelist.GameRecycleViewAdapter;
import cn.nubia.gamelauncherx.gamelist.IndicatorView;
import cn.nubia.gamelauncherx.model.AppAddModel;
import cn.nubia.gamelauncherx.recycler.DefaultChildSelectionListener.OnCenterItemClickListener;
import cn.nubia.gamelauncherx.recycler.LooperLayoutManager.OnCenterItemSelectionListener;
import cn.nubia.gamelauncherx.util.CommonUtil;
import cn.nubia.gamelauncherx.util.GameKeysConstant;
import cn.nubia.gamelauncherx.util.LogUtil;
import cn.nubia.gamelauncherx.util.NubiaTrackManager;
import cn.nubia.gamelauncherx.util.ReflectUtilities;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class BannerManager implements OnCenterItemClickListener, OnCenterItemSelectionListener, IGetAppStatusDataCallBack {
    public static final int GAME_COUNT_ONE_PAGE = 9;
    public static final String GAME_DISPLAY_MODE = "gameDisplayMode";
    public static final int GAME_PAGE_COUNT = 3;
    public static final String GAME_RECORD_DATA_KEY = "lastGameComponent";
    public static final int LEFT_RIGHT_MARGIN = 12;
    private static final int MIN_GAME_ITEM_COUNT = 3;
    public static final String SHARED_PREFERENCES_NAME = "data";
    private static final String TAG = "BannerManager";
    public static final int TOP_BOTTOM_MARGIN = 8;
    public static final String VOL_RECEIVER_ACTION = "android.media.VOLUME_CHANGED_ACTION";
    private boolean isJumpStartPosition;
    private boolean isListMode;
    List<RecyclerView> mAllPageList;
    /* access modifiers changed from: private */
    public BannerRecyclerView mBannerRecyclerView;
    private Handler mBgRotateHandler;
    private HandlerThread mBgRotateThread;
    Callback mCallback;
    /* access modifiers changed from: private */
    public Context mContext;
    private int mCurrentCenterItemPosition;
    private GameAddedContentObserver mGameAddedContentObserver;
    ArrayList<AppListItemBean> mGameAddedList;
    public List<AppListItemBean> mGameEntranceAddedList;
    private List<GameEntranceItem> mGameEntranceList;
    /* access modifiers changed from: private */
    public IndicatorView mGameIndicatorView;
    private BannerListAdapter mGameListAdapter;
    private ViewPager mGameListViewPager;
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private boolean mIsClickBackItem;
    public String mLastGameComponentName;
    /* access modifiers changed from: private */
    public LooperLayoutManager mLayoutManager;
    private ImageView mLogo;
    private boolean mNeedDoTheFirstScroll;
    /* access modifiers changed from: private */
    public boolean mOpenScrollSound;
    List<AppListItemBean> mOperationList;
    private Resources mResources;
    int mScrollSound;
    private SharedPreferences mSharedPref;
    public SoundPool mSoundPool;
    private int mStartCenterItemPosition;
    /* access modifiers changed from: private */
    public boolean mVolIsChanged;
    /* access modifiers changed from: private */
    public float mVolRatio;
    /* access modifiers changed from: private */
    public VolRatioChangeObserver mVolRatioChangeObserver;

    private static class BannerManagerHolder {
        public static final BannerManager INSTANCE = new BannerManager();

        private BannerManagerHolder() {
        }
    }

    public interface Callback {
        void scrollDirectionChanged();
    }

    private class GameAddedContentObserver extends ContentObserver {
        public GameAddedContentObserver(Handler handler) {
            super(handler);
        }

        public void register() {
            BannerManager.this.mContext.getContentResolver().registerContentObserver(ConstantVariable.APPADD_URI, false, this);
        }

        public void unregister() {
            BannerManager.this.mContext.getContentResolver().unregisterContentObserver(this);
        }

        public void onChange(boolean selfChange) {
            BannerManager.this.refreshGameRecycler(true);
        }
    }

    private class PlaySoundRunnable implements Runnable {
        private PlaySoundRunnable() {
        }

        public void run() {
            if (BannerManager.this.mOpenScrollSound) {
                if (BannerManager.this.mSoundPool == null) {
                    BannerManager.this.initItemChangeSound();
                }
                if (BannerManager.this.mVolIsChanged) {
                    BannerManager.this.initVolRatio();
                    BannerManager.this.mVolIsChanged = false;
                }
                BannerManager.this.mSoundPool.play(BannerManager.this.mScrollSound, BannerManager.this.mVolRatio, BannerManager.this.mVolRatio, 0, 0, 1.0f);
            }
        }
    }

    @TargetApi(3)
    private class VolRatioChangeObserver extends BroadcastReceiver {
        private VolRatioChangeObserver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (BannerManager.VOL_RECEIVER_ACTION.equals(intent.getAction())) {
                BannerManager.this.mVolIsChanged = true;
            }
        }
    }

    private BannerManager() {
        this.mNeedDoTheFirstScroll = false;
        this.mIsClickBackItem = false;
        this.mOpenScrollSound = false;
        this.isListMode = true;
        this.isJumpStartPosition = false;
        this.mStartCenterItemPosition = -1;
        this.mCurrentCenterItemPosition = 0;
        this.mLastGameComponentName = null;
        this.mVolRatioChangeObserver = null;
        this.mVolIsChanged = false;
        this.mGameAddedList = new ArrayList<>();
        this.mOperationList = new ArrayList();
        this.mGameEntranceAddedList = new ArrayList();
        this.mGameEntranceList = new ArrayList();
    }

    public static BannerManager getInstance() {
        return BannerManagerHolder.INSTANCE;
    }

    public void init(Context context, View parent) {
        initGlobalValues(context);
        registerVolReceive();
        registerGameAddedObserver();
        registerGetAppStatusDataCallBack();
        readLastGameComponentFromSP();
        initBannerList(parent);
        this.mSharedPref = this.mContext.getSharedPreferences(GameKeysConstant.IS_FIRST_DIALOG_NAME, 0);
    }

    @TargetApi(3)
    private void registerVolReceive() {
        new AsyncTask<Void, Void, Void>() {
            /* access modifiers changed from: protected */
            public Void doInBackground(Void... params) {
                if (BannerManager.this.mVolRatioChangeObserver == null) {
                    BannerManager.this.mVolRatioChangeObserver = new VolRatioChangeObserver();
                }
                IntentFilter filter = new IntentFilter();
                filter.addAction(BannerManager.VOL_RECEIVER_ACTION);
                BannerManager.this.mContext.registerReceiver(BannerManager.this.mVolRatioChangeObserver, filter);
                return null;
            }
        }.execute(new Void[0]);
    }

    private void initGlobalValues(Context context) {
        this.mContext = context;
        this.mResources = this.mContext.getResources();
    }

    private void initBannerList(View parent) {
        clearGameAddedListIfNeed();
        fillGameAddedList();
        initGameListAdpter();
        initLayoutManager();
        initRecyclerView(parent);
        initGameEntranceViewPager(parent);
        setGameEntranceListViewPage();
    }

    private void initGameEntranceViewPager(View parent) {
        this.mGameListViewPager = (ViewPager) parent.findViewById(R.id.main_home_entrance_vp);
        this.mGameIndicatorView = (IndicatorView) parent.findViewById(R.id.main_home_entrance_indicator);
        this.mLogo = (ImageView) parent.findViewById(R.id.magic_logo);
        if (CommonUtil.isNX627J_Project()) {
            this.mLogo.setBackground(this.mContext.getDrawable(R.mipmap.logo_627));
        } else {
            this.mLogo.setBackground(this.mContext.getDrawable(R.mipmap.logo));
        }
        if (VERSION.SDK_INT >= 29) {
            this.mLogo.setVisibility(View.GONE);
        }
        setGameViewMode();
    }

    private void setGameViewMode() {
        this.isListMode = getLastGameDisplayMode();
        switchViewMode();
    }

    private void setGameEntranceListViewPage() {
        initGameEntranceListData();
        int pageCount = (int) Math.ceil((((double) (this.mGameEntranceList.size() + 1)) * 1.0d) / 9.0d);
        if (pageCount == 0) {
            pageCount = 1;
        }
        LayoutParams mLayoutParams = new LayoutParams(this.mResources.getDimensionPixelOffset(R.dimen.view_page_game_list_width), this.mResources.getDimensionPixelOffset(R.dimen.view_page_game_list_height));
        List<RecyclerView> viewList = new ArrayList<>();
        for (int index = 0; index < pageCount; index++) {
            RecyclerView recyclerView = (RecyclerView) LayoutInflater.from(this.mContext).inflate(R.layout.item_home_entrance_vp, this.mGameListViewPager, false);
            recyclerView.setLayoutParams(mLayoutParams);
            recyclerView.setLayoutManager(new GridLayoutManager(this.mContext, 3));
            recyclerView.addItemDecoration(new GameItemDecoration(12, 8));
            recyclerView.setOverScrollMode(2);
            recyclerView.setAdapter(new GameRecycleViewAdapter(this.mContext, this.mGameEntranceList, index, 9));
            viewList.add(recyclerView);
        }
        this.mAllPageList = viewList;
        this.mGameListViewPager.setAdapter(new GameListPagerAdapter(viewList));
        this.mGameListViewPager.setLayoutParams(mLayoutParams);
        this.mGameListViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            public void onPageSelected(int position) {
                BannerManager.this.mGameIndicatorView.setCurrentIndicator(position);
            }
        });
        this.mGameIndicatorView.setIndicatorCount(this.mGameListViewPager.getAdapter().getCount());
        this.mGameIndicatorView.setCurrentIndicator(this.mGameListViewPager.getCurrentItem());
    }

    private void initGameEntranceListData() {
        this.mGameEntranceList.clear();
        for (int i = 0; i < this.mGameEntranceAddedList.size(); i++) {
            this.mGameEntranceList.add(new GameEntranceItem(((AppListItemBean) this.mGameEntranceAddedList.get(i)).getName(), ((AppListItemBean) this.mGameEntranceAddedList.get(i)).getComponetName(), ((AppListItemBean) this.mGameEntranceAddedList.get(i)).getIcon(), ((AppListItemBean) this.mGameEntranceAddedList.get(i)).getDownloadInfo()));
        }
    }

    public void switchViewMode() {
        boolean z = false;
        if (this.isListMode) {
            this.mBannerRecyclerView.setVisibility(View.GONE);
            this.mGameListViewPager.setVisibility(View.VISIBLE);
            this.mGameIndicatorView.setVisibility(View.VISIBLE);
        } else {
            this.mBannerRecyclerView.setVisibility(View.VISIBLE);
            this.mGameListViewPager.setVisibility(View.GONE);
            this.mGameIndicatorView.setVisibility(View.GONE);
            this.mLayoutManager.setTextVisibility();
        }
        setLastGameDisplayMode(this.isListMode);
        if (!this.isListMode) {
            z = true;
        }
        this.isListMode = z;
    }

    private void displayListMode() {
    }

    private void displayCardMode() {
    }

    private void clearGameAddedListIfNeed() {
        if (this.mGameAddedList != null && this.mGameAddedList.size() != 0) {
            this.mGameAddedList.clear();
        }
    }

    private void initGameListAdpter() {
        this.mGameListAdapter = new BannerListAdapter(this.mGameAddedList, this.mContext);
    }

    private void initRecyclerView(View parent) {
        if (parent != null) {
            this.mBannerRecyclerView = (BannerRecyclerView) parent.findViewById(R.id.game_list);
            this.mBannerRecyclerView.setLayoutManager(this.mLayoutManager);
            this.mBannerRecyclerView.setHasFixedSize(true);
            this.mBannerRecyclerView.setAdapter(this.mGameListAdapter);
            this.mBannerRecyclerView.addOnScrollListener(new CenterScrollListener());
            DefaultChildSelectionListener.initCenterItemListener(this, this.mBannerRecyclerView, this.mLayoutManager);
        }
    }

    private void setGameListGone() {
        this.mBannerRecyclerView.setVisibility(View.GONE);
    }

    private void setGameListVisible() {
        this.mBannerRecyclerView.setVisibility(View.VISIBLE);
    }

    private void initLayoutManager() {
        this.mLayoutManager = new LooperLayoutManager(this.mContext, 0, true);
        this.mLayoutManager.setPostLayoutListener(new ZoomPostLayoutListener());
        this.mLayoutManager.setMaxVisibleItems(1);
        this.mLayoutManager.setEdgeTransparentAreaWidth(this.mResources.getDimensionPixelOffset(R.dimen.edge_transparent_area_width));
        this.mLayoutManager.setCardMoveAlongX(this.mResources.getDimensionPixelOffset(R.dimen.game_card_move_along_x));
        this.mLayoutManager.addOnItemSelectionListener(this);
    }

    private void registerGameAddedObserver() {
        this.mGameAddedContentObserver = new GameAddedContentObserver(new Handler());
        this.mGameAddedContentObserver.register();
    }

    private void unRegisterGameAddedObserver() {
        if (this.mGameAddedContentObserver != null) {
            this.mGameAddedContentObserver.unregister();
        }
    }

    private void registerGetAppStatusDataCallBack() {
        AppAddModel.getInstance().resisterGetAppStatusDataCallBack(this);
    }

    public void unRegisterGetAppStatusDataCallBack() {
        AppAddModel.getInstance().unResisterGetAppStatusDataCallBack(this);
    }

    public void initItemChangeSound() {
        loadSound();
        initVolRatio();
    }

    /* access modifiers changed from: private */
    public void initVolRatio() {
        if (this.mContext != null) {
            AudioManager am = (AudioManager) this.mContext.getSystemService(Context.AUDIO_SERVICE);
            this.mVolRatio = ((float) am.getStreamVolume(3)) / ((float) am.getStreamMaxVolume(3));
        }
    }

    private void loadSound() {
        this.mSoundPool = new SoundPool(20, 1, 1);
        if (this.mContext != null) {
            this.mScrollSound = this.mSoundPool.load(this.mContext, R.raw.scrolling_sound, 1);
        }
    }

    private void initPlayHandler() {
        this.mHandlerThread = new HandlerThread("SoundThread");
        this.mHandlerThread.start();
        this.mHandler = new Handler(this.mHandlerThread.getLooper());
    }

    private void playScrollSound() {
        if (this.mHandler == null) {
            initPlayHandler();
        }
        this.mHandler.post(new PlaySoundRunnable());
    }

    private void initBgRotateHandler() {
        this.mBgRotateThread = new HandlerThread("BgRotateThread");
        this.mBgRotateThread.start();
        this.mBgRotateHandler = new Handler(this.mBgRotateThread.getLooper());
    }

    public void postBgRotateRunnableDelayed(Runnable runnable, long delayMillis) {
        if (this.mBgRotateHandler == null) {
            initBgRotateHandler();
        }
        this.mBgRotateHandler.postDelayed(runnable, delayMillis);
    }

    public void clearBgRotateHandler() {
        if (this.mBgRotateHandler != null) {
            this.mBgRotateHandler.removeCallbacksAndMessages(null);
        }
    }

    public void scrollGameRecyclerToStartingPosition() {
        this.mOpenScrollSound = true;
        this.mBannerRecyclerView.scrollToPosition(getStartPosition());
    }

    private void fillGameAddedList() {
        addOperationList();
        addAppAddedList();
        addNeoDownloadList();
        addAddGameList();
    }

    private void addAddGameList() {
        if (3 > this.mGameAddedList.size()) {
            for (int i = this.mGameAddedList.size(); i < 3; i++) {
                this.mGameAddedList.add(getAddGameItemBean());
            }
            return;
        }
        this.mGameAddedList.add(getAddGameItemBean());
    }

    public void addAppAddedList() {
        ArrayList<AppListItemBean> appAddedList = AppAddModel.getInstance().getAppAddedList();
        this.mGameEntranceAddedList = new ArrayList();
        if (!(this.mGameEntranceAddedList == null || appAddedList == null)) {
            this.mGameEntranceAddedList.addAll(appAddedList);
        }
        if (appAddedList == null) {
            return;
        }
        if (addLastExitGameAndAllTheGamesBihindItIfFoundInList(appAddedList)) {
            Iterator it = appAddedList.iterator();
            while (it.hasNext()) {
                AppListItemBean item = (AppListItemBean) it.next();
                if (!item.getComponetName().equals(this.mLastGameComponentName)) {
                    this.mGameAddedList.add(item);
                } else {
                    return;
                }
            }
            return;
        }
        this.mGameAddedList.addAll(appAddedList);
    }

    public void addNeoDownloadList() {
        ArrayList<AppListItemBean> neoDownloadList = AppAddModel.getInstance().getNeoDownloadAppItemList();
        if (neoDownloadList != null && neoDownloadList.size() > 0) {
            this.mGameAddedList.addAll(neoDownloadList);
            Log.i("lsm", " addNeoDownloadList neoDownloadList == " + neoDownloadList);
        }
        if (this.mGameEntranceAddedList != null && neoDownloadList != null) {
            this.mGameEntranceAddedList.addAll(neoDownloadList);
        }
    }

    public void doChangeNeoDownloadApp(AppListItemBean changeBean) {
        if (changeBean != null && this.mBannerRecyclerView != null) {
            if (this.mAllPageList != null && this.mAllPageList.size() > 0) {
                for (RecyclerView recyclerView : this.mAllPageList) {
                    ((GameRecycleViewAdapter) recyclerView.getAdapter()).updateNeoDownloadIcon(changeBean.getDownloadInfo());
                }
            }
            ((BannerListAdapter) this.mBannerRecyclerView.getAdapter()).updateNeoDownloadIcon(changeBean);
        }
    }

    public ArrayList<AppListItemBean> getGameAddedList() {
        return this.mGameAddedList;
    }

    private void addOperationList() {
        if (!CommonUtil.isInternalVersion()) {
            this.mGameAddedList.addAll(getmOperationList());
        }
    }

    private int getAddedOperationListSize() {
        if (!CommonUtil.isInternalVersion()) {
            return this.mOperationList.size();
        }
        return 0;
    }

    private boolean addLastExitGameAndAllTheGamesBihindItIfFoundInList(ArrayList<AppListItemBean> appAddedList) {
        if (this.mLastGameComponentName == null) {
            return false;
        }
        boolean isFound = false;
        Iterator it = appAddedList.iterator();
        while (it.hasNext()) {
            AppListItemBean item = (AppListItemBean) it.next();
            if (item.getComponetName() != null && (isFound || item.getComponetName().equals(this.mLastGameComponentName))) {
                isFound = true;
                this.mGameAddedList.add(item);
            }
        }
        return isFound;
    }

    private void RefillGameAddedList() {
        clearGameAddedListIfNeed();
        fillGameAddedList();
    }

    @NonNull
    private AppListItemBean getAddGameItemBean() {
        return new AppListItemBean(this.mResources.getString(R.string.add_game), "cn.nubia.gamelauncherx,cn.nubia.gamelauncherx.activity.AppAddActivity", "card_add.png");
    }

    private List<AppListItemBean> getmOperationList() {
        if (this.mOperationList.size() == 0) {
            AppListItemBean classicMasterpieceBean = new AppListItemBean(this.mResources.getString(R.string.classic_masterpiece), null, "classic_masterpiece.png");
            AppListItemBean minorityBoutiqueBean = new AppListItemBean(this.mResources.getString(R.string.minority_boutique), null, "minority_boutique.png");
            this.mOperationList.add(classicMasterpieceBean);
            this.mOperationList.add(minorityBoutiqueBean);
        }
        return this.mOperationList;
    }

    public void refreshBannerText() {
        if (this.mLayoutManager != null) {
            this.mLayoutManager.setTextVisibility();
        }
    }

    public void refreshGameRecycler(boolean scrollToLastPosition) {
        int last;
        LogUtil.d(TAG, "refreshGameRecycler(" + this.mGameAddedList.size() + ")");
        AppListItemBean lastCenterBean = null;
        if (scrollToLastPosition && this.mGameAddedList.size() > 0 && this.mCurrentCenterItemPosition > 0) {
            lastCenterBean = (AppListItemBean) this.mGameAddedList.get(this.mGameListAdapter.getRealPosition(this.mCurrentCenterItemPosition));
        }
        if (this.mLayoutManager != null) {
            resetRecordPosition();
            RefillGameAddedList();
            resetDownloadData();
            this.mBannerRecyclerView.getAdapter().notifyDataSetChanged();
            try {
                if (getCurrentCenterItemPosition() > 0) {
                    int last2 = findLastItemRealPosition(lastCenterBean);
                    if (!scrollToLastPosition || last2 < 0) {
                        last = 0;
                    } else {
                        last = last2 - 2;
                    }
                    this.mCurrentCenterItemPosition = getStartPosition() + last;
                    this.mBannerRecyclerView.scrollToPosition(this.mCurrentCenterItemPosition);
                } else {
                    this.mCurrentCenterItemPosition = getStartPosition();
                    this.mBannerRecyclerView.scrollToPosition(this.mCurrentCenterItemPosition);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.mLayoutManager.doAnimator(false);
            setGameEntranceListViewPage();
        }
    }

    private int findLastItemRealPosition(AppListItemBean lastCenterBean) {
        if (lastCenterBean == null) {
            return -1;
        }
        Iterator it = this.mGameAddedList.iterator();
        while (it.hasNext()) {
            AppListItemBean bean = (AppListItemBean) it.next();
            if (bean != null && bean.getName() != null && bean.getImageUrl() != null && bean.getName().equals(lastCenterBean.getName()) && bean.getImageUrl().equals(lastCenterBean.getImageUrl())) {
                return this.mGameAddedList.indexOf(bean);
            }
        }
        return -1;
    }

    /* access modifiers changed from: 0000 */
    public void resetDownloadData() {
        if (this.mAllPageList != null && this.mAllPageList.size() > 0) {
            for (RecyclerView recyclerView : this.mAllPageList) {
                ((GameRecycleViewAdapter) recyclerView.getAdapter()).resetNeoDownloadMap();
            }
        }
        ((BannerListAdapter) this.mBannerRecyclerView.getAdapter()).resetNeoDownloadMap();
    }

    private void resetRecordPosition() {
        this.mStartCenterItemPosition = -1;
        this.mCurrentCenterItemPosition = 0;
    }

    public void startAnimator() {
        LogUtil.d(TAG, "startAnimator()");
        final int width = this.mResources.getDimensionPixelOffset(R.dimen.banner_space);
        this.mLayoutManager.doAnimator(true);
        this.mBannerRecyclerView.setAlpha(1.0f);
        if (this.mLayoutManager.findCenterItemView() == null) {
            this.mLayoutManager.setAdjacentCardSpace(width);
            return;
        }
        this.mLayoutManager.setStartAnimEnd(false);
        this.mLayoutManager.findCenterItemView().setAlpha(0.0f);
        this.mLayoutManager.findCenterItemView().setTranslationY(500.0f);
        this.mLayoutManager.getCenterItemPosition();
        ValueAnimator animator = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
        animator.setDuration(500);
        animator.setInterpolator(Anim3DHelper.PATH_INTERPOLATOR_CARD_ENTER);
        animator.start();
        animator.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                if (BannerManager.this.mLayoutManager.findCenterItemView() != null) {
                    BannerManager.this.mLayoutManager.findCenterItemView().setAlpha(1.0f - value);
                    BannerManager.this.mLayoutManager.findCenterItemView().setTranslationY(100.0f * value);
                }
                BannerManager.this.mLayoutManager.setAdjacentCardSpace(((int) (400.0f * value)) + width);
                BannerManager.this.mBannerRecyclerView.requestLayout();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                onStartAnimEnd();
            }

            private void onStartAnimEnd() {
                LogUtil.d(BannerManager.TAG, "---->onStartAnimEnd()");
                BannerManager.this.mLayoutManager.setStartAnimEnd(true);
                if (BannerManager.this.mLayoutManager.findCenterItemView() != null) {
                    BannerManager.this.mLayoutManager.findCenterItemView().setAlpha(1.0f);
                    BannerManager.this.mLayoutManager.findCenterItemView().setTranslationY(0.0f);
                    BannerManager.this.mLayoutManager.setTextVisibility();
                }
                BannerManager.this.mLayoutManager.setAdjacentCardSpace(width);
                BannerManager.this.mBannerRecyclerView.requestLayout();
            }

            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                onStartAnimEnd();
            }

            public void onAnimationPause(Animator animation) {
                super.onAnimationPause(animation);
                LogUtil.d(BannerManager.TAG, "---->onAnimationPause()");
            }
        });
    }

    private int getStartPosition() {
        int startPosition = this.mGameListAdapter.getItemCount() / 2;
        if (this.mGameAddedList.size() > 0) {
            startPosition = ((startPosition / this.mGameAddedList.size()) * this.mGameAddedList.size()) + getAddedOperationListSize();
        }
        if (this.mStartCenterItemPosition < 0) {
            this.mStartCenterItemPosition = startPosition;
        }
        if (this.mCurrentCenterItemPosition == 0) {
            this.mCurrentCenterItemPosition = startPosition;
        }
        return startPosition;
    }

    public boolean isNeedDoTheFirstScroll() {
        return this.mNeedDoTheFirstScroll;
    }

    public void setNeedDoTheFirstScroll(boolean isNeedScroll) {
        this.mNeedDoTheFirstScroll = isNeedScroll;
    }

    public void recordLastGameComponent(String componentName) {
        Editor editor = this.mContext.getSharedPreferences("data", 0).edit();
        editor.putString(GAME_RECORD_DATA_KEY, componentName);
        editor.apply();
    }

    private void readLastGameComponentFromSP() {
        this.mLastGameComponentName = this.mContext.getSharedPreferences("data", 0).getString(GAME_RECORD_DATA_KEY, null);
    }

    public void exit() {
        unRegisterGameAddedObserver();
        unRegisterGetAppStatusDataCallBack();
    }

    public void onCenterItemClicked(@NonNull RecyclerView recyclerView, @NonNull LooperLayoutManager carouselLayoutManager, @NonNull View v) {
        int position = recyclerView.getChildLayoutPosition(v);
        int realPosition = this.mGameListAdapter.getRealPosition(position);
        BannerViewHolder holder = carouselLayoutManager.getViewHolderByPosition(carouselLayoutManager.getCenterItemPosition());
        if (holder.mModifyAtmosphere.isShown()) {
            holder.mMoreOptionsList.setVisibility(View.GONE);
            return;
        }
        LogUtil.d(TAG, "onCenterItemClicked() position : " + position + String.format(Locale.US, "Item %1$d was clicked", new Object[]{Integer.valueOf(realPosition)}));
        ReflectUtilities.requestCPUBoost();
        if (((AppListItemBean) this.mGameAddedList.get(realPosition)).getComponetName() == null) {
            clickOperation(realPosition);
        } else {
            clickApp(realPosition);
        }
    }

    private void clickOperation(int realPosition) {
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.setData(Uri.parse("gameplace://grid4topicdetail?position=" + (realPosition + 2)));
            this.mContext.startActivity(intent);
            if (realPosition == 0) {
                NubiaTrackManager.getInstance().sendEvent("cn.nubia.gamelauncher", "gamespace_classic_masterpiece_click");
            } else if (realPosition == 1) {
                NubiaTrackManager.getInstance().sendEvent("cn.nubia.gamelauncher", "gamespace_niche_boutique_click");
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this.mContext, this.mContext.getString(R.string.update_game_center_string), Toast.LENGTH_SHORT).show();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void clickApp(int realPosition) {
        if (((AppListItemBean) this.mGameAddedList.get(realPosition)).isDownloadItem()) {
            NeoDownloadManager.getInstance().doClick(((AppListItemBean) this.mGameAddedList.get(realPosition)).getDownloadInfo().app_id);
            return;
        }
        try {
            Intent intent = new Intent();
            String componetName = ((AppListItemBean) this.mGameAddedList.get(realPosition)).getComponetName();
            intent.setComponent(CommonUtil.createComponentName(componetName));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.mContext.startActivity(intent);
            recordLastGameComponent(componetName);
            if ("cn.nubia.gamelauncher,cn.nubia.gamelauncher.activity.AppAddActivity".equals(componetName)) {
                NubiaTrackManager.getInstance().sendEvent("cn.nubia.gamelauncher", "gamespace_management_game");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onBackItemClicked(@NonNull View v) {
        this.mIsClickBackItem = true;
    }

    public void onCenterItemChanged(int adapterPosition) {
        LogUtil.d(TAG, "onCenterItemChanged() adapterPosition : " + adapterPosition);
        notifyScrollDirectionChanged();
        this.mCurrentCenterItemPosition = adapterPosition;
        if (adapterPosition == this.mStartCenterItemPosition && isNeedDoTheFirstScroll() && this.isListMode && this.isJumpStartPosition) {
            setNeedDoTheFirstScroll(false);
        } else if (this.mIsClickBackItem) {
            this.mIsClickBackItem = false;
        }
        playScrollSound();
    }

    private void notifyScrollDirectionChanged() {
        if (this.mCallback != null) {
            this.mCallback.scrollDirectionChanged();
        }
    }

    public void onLoadAddAppListDone(ArrayList<AppListItemBean> arrayList, int hasAddCount) {
        refreshGameRecycler(true);
    }

    public int getCurrentCenterItemPosition() {
        if (this.mLayoutManager == null) {
            return this.mCurrentCenterItemPosition;
        }
        return this.mLayoutManager.getCenterItemPosition();
    }

    public void setStartCenterItemPosition(int startPosition) {
        this.mStartCenterItemPosition = startPosition;
    }

    public void setLastGameDisplayMode(boolean flag) {
        Editor editor = this.mContext.getSharedPreferences("data", 0).edit();
        editor.putBoolean(GAME_DISPLAY_MODE, flag);
        editor.apply();
    }

    public boolean getLastGameDisplayMode() {
        return this.mContext.getSharedPreferences("data", 0).getBoolean(GAME_DISPLAY_MODE, false);
    }

    public void addCallback(Callback callback) {
        this.mCallback = callback;
    }

    public void addBannerScrollListener(RecyclerView.OnScrollListener scrollListener) {
        this.mBannerRecyclerView.addOnScrollListener(scrollListener);
    }
}
