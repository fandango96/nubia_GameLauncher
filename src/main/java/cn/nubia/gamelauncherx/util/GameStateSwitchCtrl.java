package cn.nubia.gamelauncherx.util;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings.Global;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import cn.nubia.gamelauncherx.R;
import cn.nubia.gamelauncherx.activity.GameSpaceActivity;
import cn.nubia.gamelauncherx.activity.RedMagicDoubleHandleActivity;
import cn.nubia.gamelauncherx.adapter.GuildPageAdapter;
import cn.nubia.gamelauncherx.processmanager.IProcessManagerService;
import cn.nubia.gamelauncherx.processmanager.IProcessManagerService.Stub;
import cn.nubia.gamelauncherx.recycler.BannerManager;
import cn.nubia.gamelauncherx.service.TimerService.Callbacks;
import cn.nubia.gamelauncherx.service.TimerService.Status;
import cn.nubia.gamelauncherx.service.TimerServiceStatus;
import java.util.ArrayList;

public class GameStateSwitchCtrl implements OnClickListener, Callbacks {
    public static final String ACTION_ADD_SHORTCUT = "cn.nubia.launcher.gamespace.action.INSTALL_SHORTCUT";
    private static final String DB_LIGHTNAME = "lightName";
    private static final int DEFAULT_LAMP_NUM = 7;
    private static final int FIRST_LIGHT_VISIBLE_GAMETIME = 0;
    private static final String GAME_FIRST_START_APP = "firstStartApp";
    private static final int RIGHT_LOWER_BUTTON_WIDTH = 276;
    private static final int SECOND_LIGHT_VISIBLE_GAMETIME = 3;
    private static final String SHARED_PREFERENCES_NAME = "data";
    private static final String TAG = "GameStateSwitchCtrl";
    private static final int THREE_LIGHT_VISIBLE_GAMETIME = 6;
    private static final String VIRTUAL_GAME_KEY = "virtual_game_key";
    private static final int mBtnDefaultBgID80 = 2130903056;
    private static final int mBtnLightBgID80 = 2130903055;
    private static final int mDefaultInterBgID = 2130903058;
    private static final int mLightInterBgID = 2130903059;
    /* access modifiers changed from: private */
    public GameSpaceActivity mActivity;
    private TextView mAddShortcutTips_627;
    private ImageView mAddShortcut_627;
    private Context mContext;
    private TextView mCoolingFan80;
    private boolean mCoolingFanOpen = true;
    private Button mGameCenterHome80;
    private Button mGameExit;
    private GameFanContentObserver mGameFanContentObserver;
    private GameKeySOffOnContentObserver mGameKeySOffOnContentObserver;
    private Button mGamePlaza80;
    private Button mGameSearch80;
    private GamekeysLampContentObserver mGamekeysLampContentObserver;
    private GuildPageAdapter mGuildPageAdapter;
    private RecyclerView mGuildPageRecyclerList;
    private Button mHandleSettings80;
    private boolean mHighPerformOpen = false;
    /* access modifiers changed from: private */
    public IProcessManagerService mIProcessManagerService;
    private boolean mIsTimeOut4H = true;
    private boolean mIsTimeOutShowDialog = false;
    /* access modifiers changed from: private */
    public TextView mLaserWave80;
    /* access modifiers changed from: private */
    public boolean mLaserWaveOpen = true;
    /* access modifiers changed from: private */
    public boolean mLastLaserWaveOpen = true;
    private ImageView mMagicLogo80;
    private MainLampContentObserver mMainLampContentObserver;
    private TextView mNoDisturb80;
    private boolean mNoDisturbOpen = false;
    private Button mPersonalCenter80;
    private final ServiceConnection mProcessServiceConnection = new ServiceConnection() {
        public void onServiceDisconnected(ComponentName componentName) {
            GameStateSwitchCtrl.this.mIProcessManagerService = null;
        }

        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            GameStateSwitchCtrl.this.mIProcessManagerService = Stub.asInterface(binder);
            new AsyncTask<Void, Void, Void>() {
                public Void doInBackground(Void... args) {
                    GameStateSwitchCtrl.this.clearAllunLockTaskExcludeTopTask("gamelauncher#cn.nubia.gamelauncher");
                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null});
        }

        public void onBindingDied(ComponentName name) {
            GameStateSwitchCtrl.this.mIProcessManagerService = null;
            GameStateSwitchCtrl.this.binderProcessManagerService();
        }
    };
    private boolean mRedMagicTimeOpen = false;
    private SharedPreferences mSharedPref;
    /* access modifiers changed from: private */
    public boolean mShieldPhoneOpen = false;
    private TextView mShieldphone80;
    private Shock4DContentObserver mShock4DContentObserver;
    private boolean mShock4DOpen = false;
    private int mTimeHour = 0;
    private int mTimeMinute = 0;
    private ImageView mViewMode80;

    private class GameFanContentObserver extends ContentObserver {
        public GameFanContentObserver(Handler handler) {
            super(handler);
        }

        public void register() {
            GameStateSwitchCtrl.this.mActivity.getContentResolver().registerContentObserver(Global.getUriFor(GameKeysConstant.NUBIA_COLLING_FAN_SWITCH), false, this);
        }

        public void unregister() {
            GameStateSwitchCtrl.this.mActivity.getContentResolver().unregisterContentObserver(this);
        }

        public void onChange(boolean selfChange) {
            GameStateSwitchCtrl.this.getCurrentSwitchStateValue();
            GameStateSwitchCtrl.this.setModeBackground();
        }
    }

    private class GameKeySOffOnContentObserver extends ContentObserver {
        public GameKeySOffOnContentObserver(Handler handler) {
            super(handler);
        }

        public void register() {
            GameStateSwitchCtrl.this.mActivity.getContentResolver().registerContentObserver(Global.getUriFor(GameKeysConstant.STR_GAME_KEYS_OFF_ON), false, this);
        }

        public void unregister() {
            GameStateSwitchCtrl.this.mActivity.getContentResolver().unregisterContentObserver(this);
        }

        public void onChange(boolean selfChange) {
            GameStateSwitchCtrl.this.getCurrentSwitchStateValue();
            GameStateSwitchCtrl.this.setModeBackground();
        }
    }

    private class GamekeysLampContentObserver extends ContentObserver {
        public GamekeysLampContentObserver(Handler handler) {
            super(handler);
        }

        public void register() {
            GameStateSwitchCtrl.this.mActivity.getContentResolver().registerContentObserver(Global.getUriFor(GameKeysConstant.NUBIA_GAMEKEYS_LAMP), false, this);
        }

        public void unregister() {
            GameStateSwitchCtrl.this.mActivity.getContentResolver().unregisterContentObserver(this);
        }

        public void onChange(boolean selfChange) {
            GameStateSwitchCtrl.this.setLedEffectsText();
        }
    }

    private class MainLampContentObserver extends ContentObserver {
        public MainLampContentObserver(Handler handler) {
            super(handler);
        }

        public void register() {
            GameStateSwitchCtrl.this.mActivity.getContentResolver().registerContentObserver(Global.getUriFor(GameKeysConstant.SWITCH_MAIN_LAMP_ENABLE), false, this);
        }

        public void unregister() {
            GameStateSwitchCtrl.this.mActivity.getContentResolver().unregisterContentObserver(this);
        }

        public void onChange(boolean selfChange) {
            boolean z = true;
            GameStateSwitchCtrl.this.mLastLaserWaveOpen = GameStateSwitchCtrl.this.getLampChangeFromObserState();
            GameStateSwitchCtrl.this.mLaserWaveOpen = GameStateSwitchCtrl.this.getMainLampSwitch() && GameStateSwitchCtrl.this.getColorLampSwitch() && GameStateSwitchCtrl.this.mLastLaserWaveOpen;
            if (!CommonUtil.isInternalVersion()) {
                GameStateSwitchCtrl.this.setCommonVersionBackground(GameStateSwitchCtrl.this.mLaserWave80, GameStateSwitchCtrl.this.mLaserWaveOpen);
            }
            GameStateSwitchCtrl gameStateSwitchCtrl = GameStateSwitchCtrl.this;
            if (GameStateSwitchCtrl.this.mLaserWaveOpen) {
                z = false;
            }
            gameStateSwitchCtrl.setLaserWaveCompoundDrawables(z);
            GameStateSwitchCtrl.this.setLampChangeFromObserState(GameStateSwitchCtrl.this.mLastLaserWaveOpen);
            if (!GameStateSwitchCtrl.this.mLaserWaveOpen) {
                GameStateSwitchCtrl.this.closeLightMode();
            } else {
                GameStateSwitchCtrl.this.openLightMode();
            }
        }
    }

    private class Shock4DContentObserver extends ContentObserver {
        public Shock4DContentObserver(Handler handler) {
            super(handler);
        }

        public void register() {
            GameStateSwitchCtrl.this.mActivity.getContentResolver().registerContentObserver(Global.getUriFor(GameKeysConstant.NUBIA_4D_SHOCK), false, this);
        }

        public void unregister() {
            GameStateSwitchCtrl.this.mActivity.getContentResolver().unregisterContentObserver(this);
        }

        public void onChange(boolean selfChange) {
        }
    }

    public GameStateSwitchCtrl(GameSpaceActivity activity) {
        this.mActivity = activity;
    }

    public void doResume() {
        if (!getFirstStartGameSpaceValue()) {
            showGameTimeout4hDialog();
            getCurrentSwitchStateValue();
            setModeBackground();
        }
        TimerServiceStatus.getInstance().setCallbacks(this);
    }

    /* access modifiers changed from: private */
    public void setLedEffectsText() {
        if (CommonUtil.isInternalVersion()) {
        }
    }

    private void initView(Context context, View parentContainer) {
        this.mSharedPref = context.getSharedPreferences(GameKeysConstant.IS_FIRST_DIALOG_NAME, 0);
        this.mAddShortcut_627 = (ImageView) parentContainer.findViewById(R.id.add_shortcut);
        this.mAddShortcutTips_627 = (TextView) parentContainer.findViewById(R.id.add_shortcut_tips);
        this.mGameExit = (Button) parentContainer.findViewById(R.id.exit_button80);
        this.mGameExit.setOnClickListener(this);
        this.mAddShortcut_627.setOnClickListener(this);
        this.mAddShortcutTips_627.setOnClickListener(this);
        if ((CommonUtil.isNX627J_Project() || CommonUtil.isNX651J_Project()) && hasShortcut(context)) {
            this.mAddShortcut_627.setVisibility(View.GONE);
            this.mAddShortcutTips_627.setVisibility(View.GONE);
        }
        if (CommonUtil.isNX629J_Project() || CommonUtil.isNX619J_Project() || CommonUtil.isNX659J_Project()) {
            this.mAddShortcut_627.setVisibility(View.GONE);
            this.mAddShortcutTips_627.setVisibility(View.GONE);
            this.mGameExit.setVisibility(View.GONE);
        }
        this.mShieldphone80 = (TextView) parentContainer.findViewById(R.id.shieldphone_text80);
        this.mShieldphone80.setOnClickListener(this);
        this.mCoolingFan80 = (TextView) parentContainer.findViewById(R.id.cooling_fan_text80);
        this.mCoolingFan80.setOnClickListener(this);
        this.mLaserWave80 = (TextView) parentContainer.findViewById(R.id.led_effect_text80);
        this.mLaserWave80.setOnClickListener(this);
        this.mNoDisturb80 = (TextView) parentContainer.findViewById(R.id.no_disturb_text80);
        this.mNoDisturb80.setOnClickListener(this);
        this.mGameSearch80 = (Button) parentContainer.findViewById(R.id.game_search_btn80);
        this.mGameSearch80.setOnClickListener(this);
        this.mViewMode80 = (ImageView) parentContainer.findViewById(R.id.view_mode80);
        this.mViewMode80.setOnClickListener(this);
        this.mMagicLogo80 = (ImageView) parentContainer.findViewById(R.id.magic_logo80);
        this.mMagicLogo80.setOnClickListener(this);
        this.mGamePlaza80 = (Button) parentContainer.findViewById(R.id.game_plaza80);
        this.mGamePlaza80.setOnClickListener(this);
        this.mGameCenterHome80 = (Button) parentContainer.findViewById(R.id.game_center_home80);
        this.mGameCenterHome80.setOnClickListener(this);
        this.mHandleSettings80 = (Button) parentContainer.findViewById(R.id.handle_settings80);
        this.mHandleSettings80.setOnClickListener(this);
        this.mPersonalCenter80 = (Button) parentContainer.findViewById(R.id.personal_center);
        this.mPersonalCenter80.setOnClickListener(this);
        if (CommonUtil.isNX659J_Project() || CommonUtil.isNX629J_Project()) {
            ((MarginLayoutParams) this.mPersonalCenter80.getLayoutParams()).setMargins(0, 48, 90, 0);
        }
        if (CommonUtil.isNX659J_Project() || CommonUtil.isNX651J_Project() || VERSION.SDK_INT >= 29) {
            if (BannerManager.getInstance().getLastGameDisplayMode()) {
                this.mViewMode80.setBackgroundResource(R.mipmap.switch_viewmode_list80);
            } else {
                this.mViewMode80.setBackgroundResource(R.mipmap.switch_viewmode_card80);
            }
            if (CommonUtil.isNX651J_Project()) {
                this.mCoolingFan80.setVisibility(View.GONE);
                this.mShieldphone80.setVisibility(View.VISIBLE);
            }
        } else {
            this.mCoolingFan80.setVisibility(View.GONE);
            this.mLaserWave80.setVisibility(View.GONE);
            this.mGameSearch80.setVisibility(View.GONE);
            this.mViewMode80.setVisibility(View.GONE);
            this.mMagicLogo80.setVisibility(View.GONE);
            this.mGamePlaza80.setVisibility(View.GONE);
            this.mGameCenterHome80.setVisibility(View.GONE);
            this.mHandleSettings80.setVisibility(View.GONE);
            this.mPersonalCenter80.setVisibility(View.GONE);
        }
        if (CommonUtil.isInternalVersion()) {
            interGONE();
            this.mAddShortcut_627.setVisibility(View.GONE);
            this.mAddShortcutTips_627.setVisibility(View.GONE);
            return;
        }
        romGONE();
    }

    private void interGONE() {
        this.mGameSearch80.setVisibility(View.GONE);
        this.mGamePlaza80.setVisibility(View.GONE);
        this.mGameCenterHome80.setVisibility(View.GONE);
    }

    private void romGONE() {
        if (CommonUtil.isNX651J_Project() && !CommonUtil.isInternalVersion()) {
            this.mLaserWave80.setVisibility(View.GONE);
            this.mNoDisturb80.setVisibility(View.VISIBLE);
        }
    }

    public void initGameModeState() {
        openLightMode();
        getCurrentSwitchStateValue();
        closePhoneMode();
        setModeBackground();
    }

    private static boolean hasShortcut(Context context) {
        boolean result = false;
        Cursor cursor = null;
        try {
            Uri CONTENT_URI = Uri.parse("content://cn.nubia.launcher.settings/favorites?notify=true");
            Cursor cursor2 = context.getContentResolver().query(CONTENT_URI, new String[]{"intent"}, null, null, null, null);
            if (cursor2 != null && cursor2.getCount() > 0) {
                while (cursor2.moveToNext()) {
                    String itemIntent = cursor2.getString(cursor2.getColumnIndex("intent"));
                    if (!TextUtils.isEmpty(itemIntent)) {
                        Intent shortCutIntent = Intent.parseUri(itemIntent, 0);
                        if (shortCutIntent != null && shortCutIntent.getComponent() != null && shortCutIntent.getComponent().getPackageName().equals("cn.nubia.gamelauncher") && shortCutIntent.getComponent().getClassName().equals("cn.nubia.gamelauncher.activity.GameSpaceActivity")) {
                            result = true;
                        }
                    }
                }
            }
            if (cursor2 != null) {
                cursor2.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
        return result;
    }

    private void addShortcut() {
        Intent addShortcutIntent = new Intent(ACTION_ADD_SHORTCUT);
        addShortcutIntent.putExtra("android.intent.extra.shortcut.NAME", this.mContext.getString(R.string.app_name));
        addShortcutIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(this.mContext, R.mipmap.ic_launcher));
        Intent launcherIntent = new Intent("android.intent.action.MAIN");
        launcherIntent.setClass(this.mContext, GameSpaceActivity.class);
        launcherIntent.addCategory("android.intent.category.LAUNCHER");
        addShortcutIntent.putExtra("android.intent.extra.shortcut.INTENT", launcherIntent);
        this.mContext.sendBroadcast(addShortcutIntent);
        Toast.makeText(this.mActivity, this.mActivity.getString(R.string.add_shortcut_ok), Toast.LENGTH_SHORT).show();
        this.mAddShortcut_627.setVisibility(View.GONE);
        this.mAddShortcutTips_627.setVisibility(View.GONE);
    }

    public void initGuildpageRecyclerView(Context context, View parentContainer) {
        this.mGuildPageRecyclerList = (RecyclerView) parentContainer.findViewById(R.id.guild_page_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.mActivity);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        this.mGuildPageRecyclerList.setLayoutManager(layoutManager);
        new PagerSnapHelper().attachToRecyclerView(this.mGuildPageRecyclerList);
        ArrayList<Integer> mDatas = new ArrayList<>();
        mDatas.add(Integer.valueOf(R.mipmap.game_space_guide_page_1));
        if (!CommonUtil.isInternalVersion()) {
            mDatas.add(Integer.valueOf(R.mipmap.game_space_guide_page_2));
        }
        mDatas.add(Integer.valueOf(R.mipmap.game_space_guide_page_3));
        ArrayList<Integer> mDataHint = new ArrayList<>();
        mDataHint.add(Integer.valueOf(R.string.guild_page1_text));
        if (!CommonUtil.isInternalVersion()) {
            mDataHint.add(Integer.valueOf(R.string.guild_page2_text));
        }
        mDataHint.add(Integer.valueOf(R.string.guild_page3_text));
        this.mGuildPageAdapter = new GuildPageAdapter(this.mActivity, mDatas, mDataHint);
        this.mGuildPageRecyclerList.setAdapter(this.mGuildPageAdapter);
    }

    public void setGuildPageRecyclerInVisible() {
        this.mGuildPageRecyclerList.setVisibility(View.GONE);
    }

    public void onClick(View v) {
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4 = false;
        boolean z5 = true;
        switch (v.getId()) {
            case R.id.add_shortcut /*2131689609*/:
                addShortcut();
                return;
            case R.id.add_shortcut_tips /*2131689610*/:
                this.mAddShortcutTips_627.setVisibility(View.GONE);
                return;
            case R.id.shieldphone_text80 /*2131689669*/:
                setShieldPhonemode();
                return;
            case R.id.cooling_fan_text80 /*2131689670*/:
                NubiaTrackManager instance = NubiaTrackManager.getInstance();
                String str = "cn.nubia.gamelauncher";
                String str2 = "gamespace_cooling_fan";
                String str3 = "switch_on";
                if (!this.mCoolingFanOpen) {
                    z = true;
                } else {
                    z = false;
                }
                instance.sendEvent(str, str2, str3, z);
                Bundle cv1 = new Bundle();
                cv1.putString("package_name", "cn.nubia.gamelauncher");
                cv1.putString("event_name", "gamespace_cooling_fan_switch");
                cv1.putString("action_type", "switch_status");
                String str4 = "action_value";
                if (!this.mCoolingFanOpen) {
                    z4 = true;
                }
                cv1.putBoolean(str4, z4);
                cv1.putInt("report_interval", 1);
                NubiaTrackManager.getInstance().sendEvent("cn.nubia.gamelauncher", cv1);
                this.mSharedPref.edit().putString(GameKeysConstant.FAN_STATUS, this.mCoolingFanOpen ? "开" : "关");
                setCoolingFanMode();
                return;
            case R.id.led_effect_text80 /*2131689671*/:
                NubiaTrackManager instance2 = NubiaTrackManager.getInstance();
                String str5 = "cn.nubia.gamelauncher";
                String str6 = "gamespace_athletic_atmosphere_light_status";
                String str7 = "switch_on";
                if (!this.mLaserWaveOpen) {
                    z2 = true;
                } else {
                    z2 = false;
                }
                instance2.sendEvent(str5, str6, str7, z2);
                Bundle cv = new Bundle();
                cv.putString("package_name", "cn.nubia.gamelauncher");
                cv.putString("event_name", "gamespace_athletic_atmosphere_light");
                cv.putString("action_type", "switch_on");
                String str8 = "action_value";
                if (!this.mLaserWaveOpen) {
                    z3 = true;
                } else {
                    z3 = false;
                }
                cv.putBoolean(str8, z3);
                cv.putInt("report_interval", 1);
                NubiaTrackManager.getInstance().sendEvent("cn.nubia.gamelauncher", cv);
                this.mSharedPref.edit().putString(GameKeysConstant.LED_STATUS, this.mLaserWaveOpen ? "开" : "关");
                setLedEffectsMode();
                if (this.mLaserWaveOpen) {
                    z5 = false;
                }
                this.mLaserWaveOpen = z5;
                this.mLastLaserWaveOpen = this.mLaserWaveOpen;
                setLampChangeFromObserState(this.mLastLaserWaveOpen);
                return;
            case R.id.no_disturb_text80 /*2131689672*/:
                setChatMode();
                return;
            case R.id.game_search_btn80 /*2131689846*/:
                NubiaTrackManager.getInstance().sendEvent("cn.nubia.gamelauncher", "gamespace_search_click");
                GameCenterHelper.startGameCenterSearchPage(this.mActivity);
                return;
            case R.id.view_mode80 /*2131689847*/:
                setViewMode();
                return;
            case R.id.exit_button80 /*2131689849*/:
                exitGameSpace();
                return;
            case R.id.personal_center /*2131689850*/:
                NubiaTrackManager.getInstance().sendEvent("cn.nubia.gamelauncher", "gamespace_personal_center_click");
                GameCenterHelper.startUsersGameSetttings(this.mActivity, false);
                return;
            case R.id.handle_settings80 /*2131689851*/:
                NubiaTrackManager.getInstance().sendEvent("cn.nubia.gamelauncher", "gamespace_redmagic_handle_click");
                startKeysHelperActivity();
                return;
            case R.id.game_center_home80 /*2131689852*/:
                NubiaTrackManager.getInstance().sendEvent("cn.nubia.gamelauncher", "gamespace_game_recommendation_click");
                GameCenterHelper.startGameCenterHome(this.mActivity);
                return;
            case R.id.game_plaza80 /*2131689853*/:
                NubiaTrackManager.getInstance().sendEvent("cn.nubia.gamelauncher", "gamespace_game_square_click");
                GameCenterHelper.startGameCenterPlaza(this.mActivity);
                return;
            default:
                return;
        }
    }

    private void setViewMode() {
        BannerManager.getInstance().switchViewMode();
        boolean isListMode = BannerManager.getInstance().getLastGameDisplayMode();
        if (isListMode) {
            this.mViewMode80.setBackgroundResource(R.mipmap.switch_viewmode_list80);
        } else {
            this.mViewMode80.setBackgroundResource(R.mipmap.switch_viewmode_card80);
        }
        NubiaTrackManager.getInstance().sendEvent("cn.nubia.gamelauncher", "gamespace_view_switching_click", "option", isListMode ? "List" : "Card");
        Bundle cv = new Bundle();
        cv.putString("package_name", "cn.nubia.gamelauncher");
        cv.putString("event_name", "gamespace_view_switching_status");
        cv.putString("action_type", "option game_number");
        cv.putString("action_value", (isListMode ? "List" : "Card") + " " + BannerManager.getInstance().mGameEntranceAddedList.size());
        cv.putInt("report_interval", 1);
        NubiaTrackManager.getInstance().sendEvent("cn.nubia.gamelauncher", cv);
        Log.d("kkkkk", " ListOrCard = " + (isListMode ? "List" : "Card") + "   number = " + BannerManager.getInstance().mGameEntranceAddedList.size());
        this.mSharedPref.edit().putString(GameKeysConstant.LIST_OR_CARD, isListMode ? "List" : "Card");
        this.mSharedPref.edit().putInt(GameKeysConstant.APPS_NUMBER, BannerManager.getInstance().mGameEntranceAddedList.size());
    }

    private void exitGameSpace() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mActivity, 2131624188);
        builder.setView(LayoutInflater.from(this.mActivity).inflate(R.layout.dialog_text_exit, null));
        builder.setPositiveButton((int) R.string.exit_ok, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            @TargetApi(11)
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                new AsyncTask<Void, Void, Void>() {
                    /* access modifiers changed from: protected */
                    public Void doInBackground(Void... params) {
                        Global.putInt(GameStateSwitchCtrl.this.mActivity.getContentResolver(), GameStateSwitchCtrl.VIRTUAL_GAME_KEY, 0);
                        return null;
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null});
            }
        }).setNegativeButton(R.string.exit_cancel, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setWindowAnimations(R.style.Theme_Nubia_Dialog);
    }

    private void startKeysHelperActivity() {
        ReflectUtilities.requestCPUBoost();
        this.mActivity.startActivity(new Intent(this.mActivity, RedMagicDoubleHandleActivity.class));
    }

    private void setShieldPhonemode() {
        if (this.mShieldPhoneOpen) {
            closePhoneMode();
            this.mShieldPhoneOpen = !this.mShieldPhoneOpen;
            return;
        }
        showShieldPhoneDialog();
    }

    private void showShieldPhoneDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mActivity, 2131624188);
        builder.setView(LayoutInflater.from(this.mActivity).inflate(R.layout.dialog_text_shieldphone, null));
        builder.setPositiveButton(R.string.exit_ok, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                GameStateSwitchCtrl.this.openPhoneMode();
                GameStateSwitchCtrl.this.mShieldPhoneOpen = !GameStateSwitchCtrl.this.mShieldPhoneOpen;
                dialog.dismiss();
            }
        }).setNegativeButton(R.string.exit_cancel, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        }).create().show();
    }

    private void setChatMode() {
        if (this.mNoDisturbOpen) {
            closeNotifyMode();
        } else {
            openNotifyMode();
        }
        this.mNoDisturbOpen = !this.mNoDisturbOpen;
    }

    private void setCoolingFanMode() {
        if (this.mCoolingFanOpen) {
            closeCollingFanMode();
        } else {
            openCollingFanMode();
        }
        this.mCoolingFanOpen = !this.mCoolingFanOpen;
    }

    private void setLedEffectsMode() {
        setCommonVersionBackground(this.mLaserWave80, !this.mLaserWaveOpen);
        setLaserWaveCompoundDrawables(this.mLaserWaveOpen);
        if (this.mLaserWaveOpen) {
            closeLightMode();
        } else {
            openLightMode();
        }
    }

    /* access modifiers changed from: private */
    public void setLaserWaveCompoundDrawables(boolean flag) {
        setBtnTextColor(this.mLaserWave80, Boolean.valueOf(flag));
    }

    /* access modifiers changed from: private */
    public void setCommonVersionBackground(TextView textView, boolean openFlag) {
        if (openFlag) {
            textView.setBackgroundResource(R.mipmap.btn_light80);
        } else {
            textView.setBackgroundResource(R.mipmap.btn_normal80);
        }
    }

    /* access modifiers changed from: private */
    public void openPhoneMode() {
        ReflectUtilities.openSub(this.mActivity, 10);
        setShieldPhoneBackground(R.mipmap.button_background_light_inter, R.mipmap.btn_light80);
    }

    private void closePhoneMode() {
        ReflectUtilities.closeSub(this.mActivity, 10);
        setShieldPhoneBackground(R.mipmap.button_background_default_inter, R.mipmap.btn_normal80);
    }

    private void openNotifyMode() {
        ReflectUtilities.openSub(this.mActivity, 4);
        setNoDisturbBackground(R.mipmap.button_background_light_inter, R.mipmap.btn_light80);
    }

    private void closeNotifyMode() {
        ReflectUtilities.closeSub(this.mActivity, 4);
        setNoDisturbBackground(R.mipmap.button_background_default_inter, R.mipmap.btn_normal80);
    }

    private void setShieldPhoneBackground(int defaultInterId, int defaultId) {
        this.mShieldphone80.setBackgroundResource(defaultId);
        setShieldPhoneCompoundDrawables(this.mShieldPhoneOpen);
    }

    private void setShieldPhoneCompoundDrawables(boolean flag) {
        setBtnTextColor(this.mShieldphone80, Boolean.valueOf(flag));
    }

    private void setNoDisturbCompoundDrawables(boolean flag) {
        setBtnTextColor(this.mNoDisturb80, Boolean.valueOf(flag));
    }

    private void setNoDisturbBackground(int defaultInterId, int defaultId) {
        this.mNoDisturb80.setBackgroundResource(defaultId);
        setNoDisturbCompoundDrawables(this.mNoDisturbOpen);
    }

    private void openRedMagicTimeMode() {
        setRedMagicTimeValue(1);
    }

    private void setRedMagicTimeValue(int value) {
        try {
            Global.putInt(this.mActivity.getContentResolver(), GameKeysConstant.SETTING_REDMAGIC_TIME_SWITCH_KEY, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public void openLightMode() {
        ReflectUtilities.openSub(this.mActivity, 16);
        setMainLampSwitch();
    }

    /* access modifiers changed from: private */
    public void closeLightMode() {
        ReflectUtilities.closeSub(this.mActivity, 16);
    }

    private void openCollingFanMode() {
        setCollingFanValue(1);
        setCollingFanBackground(R.mipmap.button_background_light_inter, R.mipmap.btn_light80);
    }

    private void closeCollingFanMode() {
        setCollingFanValue(0);
        setCollingFanBackground(R.mipmap.button_background_default_inter, R.mipmap.btn_normal80);
    }

    private void setCollingFanValue(int value) {
        try {
            Global.putInt(this.mActivity.getContentResolver(), GameKeysConstant.NUBIA_COLLING_FAN_SWITCH, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCollingFanBackground(int defaultInterId, int defaultId) {
        this.mCoolingFan80.setBackgroundResource(defaultId);
        setCollingFanCompoundDrawables(this.mCoolingFanOpen);
    }

    private void setCollingFanCompoundDrawables(boolean flag) {
        setBtnTextColor(this.mCoolingFan80, Boolean.valueOf(flag));
    }

    private void setBtnTextColor(TextView text, Boolean isOpen) {
        if (isOpen.booleanValue()) {
            text.setTextColor(-2130706433);
        } else {
            text.setTextColor(-1);
        }
    }

    /* access modifiers changed from: private */
    public void getCurrentSwitchStateValue() {
        int gameKeysVaule = ReflectUtilities.getGameKeysDBValue(this.mActivity);
        LogUtil.d(TAG, "gameKeysVaule = " + Integer.toBinaryString(gameKeysVaule));
        this.mShieldPhoneOpen = geGameKeysOffOnShieldPhone(gameKeysVaule);
        this.mNoDisturbOpen = geGameKeysOffOnNoDisturb(gameKeysVaule);
        this.mLaserWaveOpen = getMainLampSwitch() && getColorLampSwitch() && geGameKeysOffOnLight(gameKeysVaule);
        this.mHighPerformOpen = geGameKeysOffOnPerform(gameKeysVaule);
        this.mCoolingFanOpen = getCoolingFan();
        this.mRedMagicTimeOpen = getRedMagicTime();
        this.mShock4DOpen = getShock4DValue();
    }

    /* access modifiers changed from: private */
    public void setModeBackground() {
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4 = true;
        setCommonVersionBackground(this.mCoolingFan80, this.mCoolingFanOpen);
        setCommonVersionBackground(this.mLaserWave80, this.mLaserWaveOpen);
        setCommonVersionBackground(this.mShieldphone80, this.mShieldPhoneOpen);
        setCommonVersionBackground(this.mNoDisturb80, this.mNoDisturbOpen);
        if (!this.mShieldPhoneOpen) {
            z = true;
        } else {
            z = false;
        }
        setShieldPhoneCompoundDrawables(z);
        if (!this.mLaserWaveOpen) {
            z2 = true;
        } else {
            z2 = false;
        }
        setLaserWaveCompoundDrawables(z2);
        if (!this.mCoolingFanOpen) {
            z3 = true;
        } else {
            z3 = false;
        }
        setCollingFanCompoundDrawables(z3);
        if (this.mNoDisturbOpen) {
            z4 = false;
        }
        setNoDisturbCompoundDrawables(z4);
    }

    public void updateTimerView(int time) {
        if (!CommonUtil.isInternalVersion()) {
            refreshTimer(time);
        }
    }

    public void notifyTimeRange(Status status) {
        switch (status) {
        }
    }

    public void refreshTimer(int time) {
        if (getGameTimeRemindState() && TimerServiceUtil.isGameTimeWeeklyRemind(this.mActivity) && TimerServiceUtil.getWeeklyTimeoutValue(this.mActivity)) {
            showGameTimeWeeklyRemindDialog();
            TimerServiceUtil.setWeeklyTimeoutValue(this.mActivity, false);
        }
    }

    private boolean getGameTimeRemindState() {
        return 1 == Global.getInt(this.mActivity.getContentResolver(), GameKeysConstant.DB_GAME_TIME_REMIND, 1);
    }

    private void showGameTimeWeeklyRemindDialog() {
        new AlertDialog.Builder(this.mActivity, 2131624188).setMessage((CharSequence) this.mActivity.getString(R.string.game_time_weekly_message)).setNegativeButton(R.string.exit_cancel, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        }).setPositiveButton((int) R.string.game_time_weekly_positive, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                GameCenterHelper.startUsersGameSetttings(GameStateSwitchCtrl.this.mActivity, true);
                dialog.dismiss();
            }
        }).create().show();
    }

    private void showGameTimeout4hDialog() {
        this.mIsTimeOut4H = TimerServiceUtil.getTimeOut4HValue(this.mActivity);
        if (this.mTimeHour >= 6 && this.mIsTimeOut4H && !this.mIsTimeOutShowDialog) {
            showGameTimeDialog();
        }
    }

    private void showGameTimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mActivity, 2131624188);
        builder.setMessage((CharSequence) this.mActivity.getString(R.string.gametime_overtime_message)).setPositiveButton(R.string.exit_ok, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                TimerServiceUtil.setTimeOut4HValue(GameStateSwitchCtrl.this.mActivity);
                dialog.dismiss();
            }
        }).create();
        if (!this.mActivity.isFinishing()) {
            builder.show();
            this.mIsTimeOutShowDialog = true;
        }
    }

    /* access modifiers changed from: private */
    public boolean getLampChangeFromObserState() {
        return 1 == Global.getInt(this.mActivity.getContentResolver(), GameKeysConstant.GAME_LAMP_CHANGE_FROM_OBSERVER, 1);
    }

    /* access modifiers changed from: private */
    public void setLampChangeFromObserState(boolean isFromObserver) {
        Global.putInt(this.mActivity.getContentResolver(), GameKeysConstant.GAME_LAMP_CHANGE_FROM_OBSERVER, isFromObserver ? 1 : 0);
    }

    public void initGameSpaceSwitchView(Context context, View parentContainer) {
        this.mContext = context;
        binderProcessManagerService();
        initView(context, parentContainer);
        setGameTimeDatas(context);
    }

    private void setGameTimeDatas(Context context) {
        if (TimerServiceUtil.isUpdateDate(context)) {
            updateTimerView(0);
        } else {
            updateTimerView(TimerServiceUtil.readTimerTosharedPrefs(context));
        }
    }

    private boolean geGameKeysOffOnLight(int gameKeysVaule) {
        return (gameKeysVaule & 16) != 0;
    }

    private boolean geGameKeysOffOnNoDisturb(int gameKeysVaule) {
        return (gameKeysVaule & 4) != 0;
    }

    private boolean geGameKeysOffOnShieldPhone(int gameKeysVaule) {
        return (gameKeysVaule & 2) != 0;
    }

    private boolean geGameKeysOffOnPerform(int gameKeysVaule) {
        return (gameKeysVaule & 32) != 0;
    }

    private boolean getCoolingFan() {
        if (Global.getInt(this.mActivity.getContentResolver(), GameKeysConstant.NUBIA_COLLING_FAN_SWITCH, 1) == 1) {
            return true;
        }
        return false;
    }

    private boolean getRedMagicTime() {
        int redmagic_time = Global.getInt(this.mActivity.getContentResolver(), GameKeysConstant.SETTING_REDMAGIC_TIME_SWITCH_KEY, -1);
        if (redmagic_time == 1) {
            this.mSharedPref.edit().putBoolean(GameKeysConstant.IS_FIRST_DIALOG, false).apply();
        }
        if (redmagic_time == 1) {
            return true;
        }
        return false;
    }

    private boolean getShock4DValue() {
        if (Global.getInt(this.mActivity.getContentResolver(), GameKeysConstant.NUBIA_4D_SHOCK, -1) == 1) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public boolean getMainLampSwitch() {
        return Global.getInt(this.mActivity.getContentResolver(), GameKeysConstant.SWITCH_MAIN_LAMP_ENABLE, 1) == 1;
    }

    /* access modifiers changed from: private */
    public boolean getColorLampSwitch() {
        return Global.getInt(this.mActivity.getContentResolver(), GameKeysConstant.SWITCH_COLOR_LAMP_ENABLE, 1) == 1;
    }

    private void setMainLampSwitch() {
        try {
            Global.putInt(this.mActivity.getContentResolver(), GameKeysConstant.SWITCH_MAIN_LAMP_ENABLE, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setLedEffects(TextView laserWave) {
        int gamekeys_lamp = Global.getInt(this.mActivity.getContentResolver(), GameKeysConstant.NUBIA_GAMEKEYS_LAMP, -1);
        LogUtil.i(TAG, "gamekeys_lamp = " + gamekeys_lamp);
        switch (gamekeys_lamp) {
            case -1:
            case 0:
                laserWave.setText(this.mActivity.getString(R.string.gamekeys_lamp_a));
                return;
            case 1:
                laserWave.setText(this.mActivity.getString(R.string.gamekeys_lamp_b));
                return;
            case 2:
                laserWave.setText(this.mActivity.getString(R.string.gamekeys_lamp_c));
                return;
            case 3:
                laserWave.setText(this.mActivity.getString(R.string.gamekeys_lamp_d));
                return;
            case 4:
                laserWave.setText(this.mActivity.getString(R.string.gamekeys_lamp_e));
                return;
            case 5:
                laserWave.setText(this.mActivity.getString(R.string.gamekeys_lamp_f));
                return;
            case 6:
                laserWave.setText(this.mActivity.getString(R.string.gamekeys_lamp_g));
                return;
            default:
                laserWave.setText(getCustomLampName(this.mActivity, gamekeys_lamp));
                return;
        }
    }

    private String getCustomLampName(Context context, int pos) {
        if (context != null) {
            Cursor cursor = context.getContentResolver().query(Uri.parse("content://cn.nubia.lighteditor/light"), null, null, null, null);
            if (cursor != null) {
                int i = 0;
                while (cursor.moveToNext()) {
                    if (i == pos - 7) {
                        return cursor.getString(cursor.getColumnIndex(DB_LIGHTNAME));
                    }
                    i++;
                }
                cursor.close();
            } else {
                LogUtil.d(TAG, "cursor is null, query failed");
            }
        } else {
            LogUtil.d(TAG, "context is null, query failed");
        }
        return "";
    }

    public void registerObserver() {
        this.mGamekeysLampContentObserver = new GamekeysLampContentObserver(new Handler());
        this.mGamekeysLampContentObserver.register();
        this.mShock4DContentObserver = new Shock4DContentObserver(new Handler());
        this.mShock4DContentObserver.register();
        this.mGameKeySOffOnContentObserver = new GameKeySOffOnContentObserver(new Handler());
        this.mGameKeySOffOnContentObserver.register();
        this.mMainLampContentObserver = new MainLampContentObserver(new Handler());
        this.mMainLampContentObserver.register();
        this.mGameFanContentObserver = new GameFanContentObserver(new Handler());
        this.mGameFanContentObserver.register();
    }

    public void unregisterObserverAndService() {
        unregisterObserver();
    }

    private void unregisterObserver() {
        if (this.mGamekeysLampContentObserver != null) {
            this.mGamekeysLampContentObserver.unregister();
        }
        if (this.mShock4DContentObserver != null) {
            this.mShock4DContentObserver.unregister();
        }
        if (this.mGameKeySOffOnContentObserver != null) {
            this.mGameKeySOffOnContentObserver.unregister();
        }
        if (this.mMainLampContentObserver != null) {
            this.mMainLampContentObserver.unregister();
        }
        if (this.mGameFanContentObserver != null) {
            this.mGameFanContentObserver.unregister();
        }
    }

    public void clearAllunLockTaskExcludeTopTask(String topTaskPackageName) {
        try {
            if (this.mIProcessManagerService != null) {
                this.mIProcessManagerService.oneKeyCleanExcludeCurrentApp(topTaskPackageName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public void binderProcessManagerService() {
        try {
            if (this.mIProcessManagerService == null) {
                Intent intent = new Intent();
                intent.setClassName("cn.nubia.processmanager", "cn.nubia.processmanager.service.ProcessManagerService");
                this.mActivity.bindService(intent, this.mProcessServiceConnection, Context.BIND_AUTO_CREATE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean getFirstStartGameSpaceValue() {
        return this.mActivity.getSharedPreferences("data", 0).getBoolean("firstStartApp", true);
    }

    public void setFirstStartGameSpaceValue() {
        Editor editor = this.mActivity.getSharedPreferences("data", 0).edit();
        editor.putBoolean("firstStartApp", false);
        editor.apply();
    }
}
