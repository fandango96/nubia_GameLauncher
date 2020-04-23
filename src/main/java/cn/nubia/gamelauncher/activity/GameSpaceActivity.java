package cn.nubia.gamelauncher.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Global;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewStub;
import android.widget.Toast;
import cn.nubia.gamelauncher.GameLauncherApplication;
import cn.nubia.gamelauncher.R;
import cn.nubia.gamelauncher.aimhelper.AimService;
import cn.nubia.gamelauncher.commoninterface.ConstantVariable;
import cn.nubia.gamelauncher.gamehandle.NubiaCTAPermissionUtils;
import cn.nubia.gamelauncher.receiver.HomeWatcherReceiver;
import cn.nubia.gamelauncher.recycler.BannerManager;
import cn.nubia.gamelauncher.service.TimerServiceStatus;
import cn.nubia.gamelauncher.util.BuildDeviceUtil;
import cn.nubia.gamelauncher.util.CommonUtil;
import cn.nubia.gamelauncher.util.GameKeysConstant;
import cn.nubia.gamelauncher.util.GameStateSwitchCtrl;
import cn.nubia.gamelauncher.util.LogUtil;
import cn.nubia.gamelauncher.util.NubiaTrackManager;
import cn.nubia.gamelauncher.view.BgRotateAnimView;
import cn.nubia.gamelauncher.view.MyVideoView;

public class GameSpaceActivity extends BaseActivity {
    public static final String GAME_FIRST_START_APP = "firstStartApp";
    private static final String GAME_NAME = "game_animation=";
    public static final String HAS_PERMISSION = "has_permission";
    private static final String SETTING_LAUNCHER_RESUME = "nubia_gamelauncher_resume";
    public static final String SHARED_PREFERENCES_NAME = "data";
    private static final int START_ANIMATION_STAY_TIME = 0;
    private static final String TAG = "GameSpaceActivity";
    private boolean initBannerManager = true;
    private AudioManager mAudioManager;
    /* access modifiers changed from: private */
    public BgRotateAnimView mBgRotateAnimView;
    private ViewStub mContentPanel;
    /* access modifiers changed from: private */
    public GameStateSwitchCtrl mGameStateSwitchCtrl;
    private Handler mHandler = new Handler();
    private HomeWatcherReceiver mHomeWatcherReceiver;
    private ConstraintLayout mParentContainer;
    /* access modifiers changed from: private */
    public boolean mSplashScreenEnd = false;
    /* access modifiers changed from: private */
    public MyVideoView mVideoView = null;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "---->onCreate()");
        if (GameLauncherApplication.mScreenStatusReceiver != null) {
            GameLauncherApplication.mScreenStatusReceiver.setActivity(this);
        }
        setContentView(R.layout.game_space);
        if (CommonUtil.isNX627J_Project() || CommonUtil.isNX651J_Project()) {
            checkCTA();
        }
        if (CommonUtil.isInternalVersion()) {
            registerHomeKeyReceiver(this);
        }
        this.mGameStateSwitchCtrl = new GameStateSwitchCtrl(this);
        if (!isLaunchFromGameKey(getIntent()) || isReCreate(savedInstanceState)) {
            this.mSplashScreenEnd = true;
            resetStartCenterItemPositionIfNeed(savedInstanceState);
            loadContent();
        } else {
            playStartAnimation();
        }
        int cooling_fan = Global.getInt(getContentResolver(), GameKeysConstant.NUBIA_COLLING_FAN_SWITCH, 1);
        int redmagic_time = Global.getInt(getContentResolver(), GameKeysConstant.SETTING_REDMAGIC_TIME_SWITCH_KEY, -1);
        int led_effect = Global.getInt(getContentResolver(), GameKeysConstant.GAME_LAMP_CHANGE_FROM_OBSERVER, 1);
        NubiaTrackManager.getInstance().init(this);
        NubiaTrackManager.getInstance().sendEvent("cn.nubia.gamelauncher", "gamespace_athletic_atmosphere_light_status", "switch_on", led_effect == 1 ? "开" : "关");
        NubiaTrackManager.getInstance().sendEvent("cn.nubia.gamelauncher", "gamespace_redmagic_time_status", "switch_on", redmagic_time == 1 ? "开" : "关");
        NubiaTrackManager.getInstance().sendEvent("cn.nubia.gamelauncher", "gamespace_cooling_fan_switch", "switch_on", cooling_fan == 1 ? "开" : "关");
        if (BuildDeviceUtil.isAndroidQ()) {
            this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    GameSpaceActivity.this.startService(new Intent(GameSpaceActivity.this, AimService.class));
                }
            }, 1000);
        }
    }

    private void checkCTA() {
        if (!NubiaCTAPermissionUtils.isCTAOK(this)) {
            NubiaCTAPermissionUtils.showPermissionDialogHome(this);
        }
    }

    private void resetStartCenterItemPositionIfNeed(Bundle savedInstanceState) {
        if (isReCreate(savedInstanceState)) {
            int startPosition = getSavedCenterItemPosition(savedInstanceState);
            if (startPosition > 0) {
                BannerManager.getInstance().setStartCenterItemPosition(startPosition);
            }
        }
    }

    private boolean isReCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return false;
        }
        return savedInstanceState.getBoolean("reCreate");
    }

    private int getSavedCenterItemPosition(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return -1;
        }
        return savedInstanceState.getInt("CurrentCenterItemPosition");
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("reCreate", true);
        outState.putInt("CurrentCenterItemPosition", BannerManager.getInstance().getCurrentCenterItemPosition());
        LogUtil.d(TAG, "------------->onSaveInstanceState()");
    }

    private boolean isLaunchFromGameKey(Intent intent) {
        if (intent == null) {
            return false;
        }
        return intent.getBooleanExtra("nubia.intent.extra.FROM_GAME_KEY", false);
    }

    /* access modifiers changed from: private */
    public void loadContent() {
        if (this.mContentPanel == null) {
            LogUtil.d(TAG, "---->loadContent()");
            this.mContentPanel = (ViewStub) findViewById(R.id.content_panel);
            this.mParentContainer = (ConstraintLayout) this.mContentPanel.inflate();
            initGameSpaceMainLayout();
            if (this.mSplashScreenEnd) {
                showContentPanel();
            }
        }
    }

    private void initPermission() {
        if (ConstantVariable.HAS_PERMISSION) {
            return;
        }
        if (!CommonUtil.isNX627J_Project() && !CommonUtil.isNX651J_Project()) {
            ConstantVariable.HAS_PERMISSION = true;
            Editor editor = getSharedPreferences("data", 0).edit();
            editor.putBoolean("has_permission", true);
            editor.apply();
        } else if (NubiaCTAPermissionUtils.isCTAOK(this)) {
            ConstantVariable.HAS_PERMISSION = true;
            Editor editor2 = getSharedPreferences("data", 0).edit();
            editor2.putBoolean("has_permission", true);
            editor2.apply();
        }
    }

    public void initGameSpaceMainLayout() {
        this.mBgRotateAnimView = (BgRotateAnimView) findViewById(R.id.bg_rotate_view);
        BannerManager.getInstance().init(this, this.mParentContainer);
        this.mGameStateSwitchCtrl.initGameSpaceSwitchView(this, this.mParentContainer);
        this.mGameStateSwitchCtrl.registerObserver();
        initPermission();
        if (!CommonUtil.isInternalVersion()) {
            TimerServiceStatus.getInstance().startService(this);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void setGuildPageRecyclerInVisible() {
        this.mGameStateSwitchCtrl.setGuildPageRecyclerInVisible();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            return super.onKeyDown(keyCode, event);
        }
        if (CommonUtil.isNX627J_Project() || CommonUtil.isNX651J_Project()) {
            Toast.makeText(this, R.string.turn_off_game_button, 0).show();
        } else {
            Toast.makeText(this, R.string.turn_off_game_button_redmagic, 0).show();
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "---->onDestroy()");
        NubiaTrackManager.getInstance().unbindServiceInvoked();
        if (GameLauncherApplication.mScreenStatusReceiver != null) {
            GameLauncherApplication.mScreenStatusReceiver.setActivity(null);
        }
        if (CommonUtil.isInternalVersion()) {
            unRegisterHomeKeyReceiver(this);
        }
        this.mGameStateSwitchCtrl.unregisterObserverAndService();
        BannerManager.getInstance().clearBgRotateHandler();
        BannerManager.getInstance().exit();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        TimerServiceStatus.getInstance().setCallbacks(null);
        if (BannerManager.getInstance().mSoundPool != null) {
            LogUtil.d(TAG, "---> mSoundPool.release() <---");
            BannerManager.getInstance().mSoundPool.release();
        }
        LogUtil.d(TAG, "---->onPause()");
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        LogUtil.d(TAG, "---->onResume(s)");
        this.mHandler.post(new Runnable() {
            public void run() {
                GameSpaceActivity.this.doResume();
            }
        });
        LogUtil.d(TAG, "---->onResume(e)");
    }

    /* access modifiers changed from: private */
    public void doResume() {
        LogUtil.d(TAG, "doResume(s)");
        BannerManager.getInstance().refreshBannerText();
        if (!this.initBannerManager) {
            BannerManager.getInstance().initItemChangeSound();
        }
        this.initBannerManager = false;
        if (this.mSplashScreenEnd) {
            this.mGameStateSwitchCtrl.doResume();
        }
        LogUtil.d(TAG, "doResume(e)");
    }

    private void registerHomeKeyReceiver(Context context) {
        if (this.mHomeWatcherReceiver == null) {
            this.mHomeWatcherReceiver = new HomeWatcherReceiver();
        }
        context.registerReceiver(this.mHomeWatcherReceiver, new IntentFilter("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
    }

    private void unRegisterHomeKeyReceiver(Context context) {
        if (this.mHomeWatcherReceiver != null) {
            context.unregisterReceiver(this.mHomeWatcherReceiver);
        }
    }

    public void setFirstStartGameSpaceValue() {
        cleanup();
        this.mGameStateSwitchCtrl.setFirstStartGameSpaceValue();
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        hideNavigationBar(true);
        if (this.mVideoView == null || this.mVideoView.getVisibility() != 0) {
            switch (ev.getAction()) {
                case 0:
                    Log.d("ky", "action_down touch_X = " + ev.getX() + "  touch_Y = " + ev.getY());
                    break;
                case 1:
                    Log.d("ky", "action_up touch_X = " + ev.getX() + "  touch_Y = " + ev.getY());
                    break;
                case 3:
                    Log.d("ky", "action_cancel touch_X = " + ev.getX() + "  touch_Y = " + ev.getY());
                    break;
            }
            return super.dispatchTouchEvent(ev);
        }
        LogUtil.d(TAG, "mVideoView dispatchTouchEvent return true");
        return true;
    }

    private void playStartAnimation() {
        LogUtil.d(TAG, "---->playStartAnimation(s)");
        BannerManager.getInstance().setNeedDoTheFirstScroll(false);
        this.mVideoView = (MyVideoView) findViewById(R.id.start_animation_video_view);
        this.mVideoView.setVisibility(0);
        this.mVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.start_animation));
        this.mVideoView.start();
        setAudioManagerParameters("begin");
        this.mVideoView.setOnCompletionListener(new OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                LogUtil.d(GameSpaceActivity.TAG, "videoView invisible  end playStartAnimation");
                GameSpaceActivity.this.hideVideoViewDelayed(0);
            }
        });
        this.mVideoView.setOnErrorListener(new OnErrorListener() {
            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                LogUtil.d(GameSpaceActivity.TAG, "videoView OnErrorListener");
                return false;
            }
        });
        preLoadContentStub(100);
        hideVideoViewDelayed(3500);
        LogUtil.d(TAG, "---->playStartAnimation(e)");
    }

    @TargetApi(3)
    public void cleanup() {
        new AsyncTask<Void, Void, Void>() {
            /* access modifiers changed from: protected */
            public Void doInBackground(Void... params) {
                GameSpaceActivity.this.getContentResolver().notifyChange(Global.getUriFor(GameSpaceActivity.SETTING_LAUNCHER_RESUME), null);
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null});
    }

    private AudioManager getAudioManager() {
        if (this.mAudioManager == null) {
            this.mAudioManager = (AudioManager) getSystemService("audio");
        }
        return this.mAudioManager;
    }

    /* access modifiers changed from: private */
    public void setAudioManagerParameters(String gameName) {
        String gameName2 = GAME_NAME + gameName;
        LogUtil.d(TAG, "setAudioManagerParameters(" + gameName2 + ")");
        getAudioManager().setParameters(gameName2);
    }

    public void hideVideoViewDelayed(int time) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Log.i("GameSpace", "ensure ViewView setVisible Gone");
                if (GameSpaceActivity.this.mVideoView != null && GameSpaceActivity.this.mVideoView.getVisibility() != 8) {
                    GameSpaceActivity.this.mSplashScreenEnd = true;
                    GameSpaceActivity.this.setAudioManagerParameters("end");
                    GameSpaceActivity.this.mVideoView.setVisibility(8);
                    GameSpaceActivity.this.showContentPanel();
                    if (!GameSpaceActivity.this.mGameStateSwitchCtrl.getFirstStartGameSpaceValue()) {
                        GameSpaceActivity.this.cleanup();
                    }
                }
            }
        }, (long) time);
    }

    private void preLoadContentStub(int time) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                LogUtil.d(GameSpaceActivity.TAG, "------>preLoadContentStub()");
                GameSpaceActivity.this.loadContent();
            }
        }, (long) time);
    }

    /* access modifiers changed from: private */
    public void showContentPanel() {
        LogUtil.d(TAG, "------->showContentPanel()");
        if (this.mGameStateSwitchCtrl.getFirstStartGameSpaceValue()) {
            this.mGameStateSwitchCtrl.initGuildpageRecyclerView(this, this.mParentContainer);
            this.mGameStateSwitchCtrl.initGameModeState();
            return;
        }
        showContentWithAnim();
    }

    private void doBannerStartAnim() {
        LogUtil.d(TAG, "------>doBannerStartAnim()");
        this.mBgRotateAnimView.startDrawThread();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                BannerManager.getInstance().startAnimator();
                GameSpaceActivity.this.mBgRotateAnimView.listenerRecycleScrollState();
            }
        }, 600);
    }

    public void showContentWithAnim() {
        LogUtil.d(TAG, "------>showContentWithAnim()");
        doBannerStartAnim();
        loadContent();
        playContentAnimation();
        this.mGameStateSwitchCtrl.doResume();
    }

    private void playContentAnimation() {
        BannerManager.getInstance().scrollGameRecyclerToStartingPosition();
        if (CommonUtil.isInternalVersion() || VERSION.SDK_INT < 29) {
        }
    }
}
