package cn.nubia.gamelauncherx.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.nubia.gamelauncherx.R;
import cn.nubia.gamelauncherx.gamehandle.GameHandleConnectionStateChangeListener;
import cn.nubia.gamelauncherx.gamehandle.GameHandleConstant;
import cn.nubia.gamelauncherx.gamehandle.GameHandleService;
import cn.nubia.gamelauncherx.gamehandle.GameHandleService.LocalBinder;
import cn.nubia.gamelauncherx.gamehandle.HandShankMoveAreaFloatView;
import cn.nubia.gamelauncherx.util.LogUtil;

public class RedMagicHandleLeftRightCalibrationActivity extends BaseActivity implements OnClickListener {
    private static final String GAME_PACKAGE_NAME = "startGameAppName";
    private static final String IS_FROM_GAME_MAP = "yes";
    private static final String IS_FROM_GAME_MAP_ACTION = "isFromGamepadMapView";
    private static final String TAG = "RedMagicHandleLeftRightCalibrationActivity";
    private boolean flag = true;
    private String leftAddreBEBig;
    private int leftDeviceId = -1;
    private TextView mCalibrationSuccessed;
    /* access modifiers changed from: private */
    public GameHandleConnectionStateChangeListener mConnectionStateChangeListener = new GameHandleConnectionStateChangeListener() {
        public void onConnectionStateChange(String address, int state) {
        }
    };
    private int mEventDeviceId = -1;
    /* access modifiers changed from: private */
    public GameHandleService mGameHandleService;
    private ImageView mHandleDirectionBottomOne;
    private ImageView mHandleDirectionBottomTwo;
    private ImageView mHandleDirectionLeftOne;
    private ImageView mHandleDirectionLeftTwo;
    private ImageView mHandleDirectionRightOne;
    private ImageView mHandleDirectionRightTwo;
    private ImageView mHandleDirectionStartOne;
    private ImageView mHandleDirectionStartTwo;
    private ImageView mHandleDirectionUpOne;
    private ImageView mHandleDirectionUpTwo;
    private String mHandleOneAddress;
    private HandShankMoveAreaFloatView mHandleRockerOne;
    private HandShankMoveAreaFloatView mHandleRockerTwo;
    private String mHandleTwoAddress;
    private ImageView mLeftArrowImg;
    private RelativeLayout mRelativeLayout;
    private Button mResetCalibration;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            RedMagicHandleLeftRightCalibrationActivity.this.mGameHandleService = ((LocalBinder) service).getService();
            RedMagicHandleLeftRightCalibrationActivity.this.mGameHandleService.addConnectionStateChangeListener(RedMagicHandleLeftRightCalibrationActivity.this.mConnectionStateChangeListener);
        }

        public void onServiceDisconnected(ComponentName name) {
            RedMagicHandleLeftRightCalibrationActivity.this.mGameHandleService = null;
        }
    };
    private ImageView mSetSuccessIcon;
    private SharedPreferences mSharedPref;
    private Button mStartExperiencing;
    private String rightAddreBEBig;
    private int rightDeviceId = -1;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreate RedMagicHandleLeftRightCalibrationActivity");
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.handle_left_right_calibration_layout);
        bindService(new Intent(this, GameHandleService.class), this.mServiceConnection,
                Context.BIND_AUTO_CREATE);
        initView();
    }

    private void initView() {
        this.mCalibrationSuccessed = (TextView) findViewById(R.id.handle_left_right_describe);
        this.mResetCalibration = (Button) findViewById(R.id.handle_reset_calibration_btn);
        this.mStartExperiencing = (Button) findViewById(R.id.handle_start_experiencing_btn);
        this.mRelativeLayout = (RelativeLayout) findViewById(R.id.handle_left_right_confirm_bg);
        this.mLeftArrowImg = (ImageView) findViewById(R.id.left_arrow_img);
        this.mSetSuccessIcon = (ImageView) findViewById(R.id.setting_success_icon);
        this.mHandleRockerOne = (HandShankMoveAreaFloatView) findViewById(R.id.handle_rocker_img);
        this.mHandleDirectionUpOne = (ImageView) findViewById(R.id.handle_direction_up_img);
        this.mHandleDirectionLeftOne = (ImageView) findViewById(R.id.handle_direction_left_img);
        this.mHandleDirectionBottomOne = (ImageView) findViewById(R.id.handle_direction_bottom_img);
        this.mHandleDirectionRightOne = (ImageView) findViewById(R.id.handle_direction_right_img);
        this.mHandleDirectionStartOne = (ImageView) findViewById(R.id.handle_direction_start_img);
        this.mHandleRockerTwo = (HandShankMoveAreaFloatView) findViewById(R.id.handle_rocker_img_right);
        this.mHandleDirectionUpTwo = (ImageView) findViewById(R.id.handle_direction_up_img_right);
        this.mHandleDirectionLeftTwo = (ImageView) findViewById(R.id.handle_direction_left_img_right);
        this.mHandleDirectionBottomTwo = (ImageView) findViewById(R.id.handle_direction_bottom_img_right);
        this.mHandleDirectionRightTwo = (ImageView) findViewById(R.id.handle_direction_right_img_right);
        this.mHandleDirectionStartTwo = (ImageView) findViewById(R.id.handle_direction_start_img_right);
        this.mResetCalibration.setOnClickListener(this);
        this.mStartExperiencing.setOnClickListener(this);
        this.mSharedPref = getSharedPreferences(GameHandleConstant.PREF_FILE_NAME, 0);
        this.mHandleOneAddress = this.mSharedPref.getString(GameHandleConstant.LEFT_HANDLE_ADDRESS, null);
        this.mHandleTwoAddress = this.mSharedPref.getString(GameHandleConstant.RIGHT_HANDLE_ADDRESS, null);
        this.mResetCalibration.setVisibility(View.INVISIBLE);
        this.mStartExperiencing.setVisibility(View.INVISIBLE);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.handle_reset_calibration_btn /*2131689659*/:
                resetLR();
                return;
            case R.id.handle_start_experiencing_btn /*2131689660*/:
                LogUtil.d(TAG, "[RedMagicHandleLeftRightCalibrationActivity]  left = " + this.mHandleOneAddress + "  right = " + this.mHandleTwoAddress);
                Intent gameMapIntent = getIntent();
                if (IS_FROM_GAME_MAP.equals(gameMapIntent.getStringExtra(IS_FROM_GAME_MAP_ACTION))) {
                    String gamePackName = gameMapIntent.getStringExtra(GAME_PACKAGE_NAME);
                    if (this.mGameHandleService != null) {
                        this.mGameHandleService.refreshClient();
                        this.mGameHandleService.sendCalibrationState();
                    }
                    startActivity(getPackageManager().getLaunchIntentForPackage(gamePackName));
                } else {
                    Intent intent = new Intent(this, RedMagicDoubleHandleActivity.class);
                    intent.putExtra(GameHandleConstant.CALBRATION_ACTIVITY, true);
                    startActivity(intent);
                }
                finish();
                return;
            default:
                return;
        }
    }

    private void resetLR() {
        this.mRelativeLayout.setBackground(getDrawable(R.drawable.handle_left_right_confirm_bg_default));
        this.mSetSuccessIcon.setVisibility(View.GONE);
        this.mLeftArrowImg.setVisibility(View.VISIBLE);
        this.mHandleRockerOne.setVisibility(View.GONE);
        this.mHandleRockerTwo.setVisibility(View.GONE);
        this.mResetCalibration.setVisibility(View.INVISIBLE);
        this.mStartExperiencing.setVisibility(View.INVISIBLE);
        this.mCalibrationSuccessed.setText(getString(R.string.handle_left_right_describe_1));
        this.mEventDeviceId = -1;
        this.leftDeviceId = -1;
        this.rightDeviceId = -1;
        this.flag = true;
    }

    private void initLR(int eventDeviceId) {
        changeMAC();
        this.mRelativeLayout.setBackground(getDrawable(R.drawable.handle_left_right_confirm_bg));
        if (this.flag) {
            this.leftDeviceId = eventDeviceId;
            this.flag = false;
            if (this.leftDeviceId == GameHandleService.getGamePadDeviceId(getBaseContext(), this.leftAddreBEBig)) {
                this.rightDeviceId = GameHandleService.getGamePadDeviceId(getBaseContext(), this.rightAddreBEBig);
            } else {
                this.rightDeviceId = GameHandleService.getGamePadDeviceId(getBaseContext(), this.leftAddreBEBig);
                String tempAddress = this.mHandleOneAddress;
                this.mHandleOneAddress = this.mHandleTwoAddress;
                this.mHandleTwoAddress = tempAddress;
            }
        }
        this.mSharedPref.edit().putString(GameHandleConstant.LEFT_HANDLE_ADDRESS, this.mHandleOneAddress).apply();
        this.mSharedPref.edit().putString(GameHandleConstant.RIGHT_HANDLE_ADDRESS, this.mHandleTwoAddress).apply();
        this.mHandleRockerOne.setVisibility(View.VISIBLE);
        this.mHandleRockerTwo.setVisibility(View.VISIBLE);
        this.mResetCalibration.setVisibility(View.VISIBLE);
        this.mStartExperiencing.setVisibility(View.VISIBLE);
        this.mLeftArrowImg.setVisibility(View.GONE);
        this.mSetSuccessIcon.setVisibility(View.VISIBLE);
        this.mCalibrationSuccessed.setText(getString(R.string.handle_left_right_describe_2));
    }

    public void changeMAC() {
        if (this.mHandleOneAddress != null) {
            String[] left = this.mHandleOneAddress.split(":");
            this.leftAddreBEBig = left[5] + ":" + left[4] + ":" + left[3] + ":" + left[2] + ":" + left[1] + ":" + left[0];
        }
        if (this.mHandleTwoAddress != null) {
            String[] right = this.mHandleTwoAddress.split(":");
            this.rightAddreBEBig = right[5] + ":" + right[4] + ":" + right[3] + ":" + right[2] + ":" + right[1] + ":" + right[0];
        }
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        this.mEventDeviceId = event.getDeviceId();
        initLR(this.mEventDeviceId);
        switch (event.getAction()) {
            case 2:
                float axisX = event.getAxisValue(0);
                float axisY = event.getAxisValue(1);
                if (this.mEventDeviceId == this.leftDeviceId) {
                    this.mHandleRockerOne.setJoyStickMoveXY(axisX, axisY);
                    return true;
                } else if (this.mEventDeviceId != this.rightDeviceId) {
                    return true;
                } else {
                    this.mHandleRockerTwo.setJoyStickMoveXY(-axisX, -axisY);
                    return true;
                }
            default:
                return super.onGenericMotionEvent(event);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        this.mEventDeviceId = event.getDeviceId();
        switch (keyCode) {
            case 96:
                if (this.mEventDeviceId != this.leftDeviceId) {
                    if (this.mEventDeviceId == this.rightDeviceId) {
                        this.mHandleDirectionUpTwo.setVisibility(View.VISIBLE);
                        break;
                    }
                } else {
                    this.mHandleDirectionBottomOne.setVisibility(View.VISIBLE);
                    break;
                }
                break;
            case 97:
                if (this.mEventDeviceId != this.leftDeviceId) {
                    if (this.mEventDeviceId == this.rightDeviceId) {
                        this.mHandleDirectionLeftTwo.setVisibility(View.VISIBLE);
                        break;
                    }
                } else {
                    this.mHandleDirectionRightOne.setVisibility(View.VISIBLE);
                    break;
                }
                break;
            case 99:
                if (this.mEventDeviceId != this.leftDeviceId) {
                    if (this.mEventDeviceId == this.rightDeviceId) {
                        this.mHandleDirectionRightTwo.setVisibility(View.VISIBLE);
                        break;
                    }
                } else {
                    this.mHandleDirectionLeftOne.setVisibility(View.VISIBLE);
                    break;
                }
                break;
            case 100:
                if (this.mEventDeviceId != this.leftDeviceId) {
                    if (this.mEventDeviceId == this.rightDeviceId) {
                        this.mHandleDirectionBottomTwo.setVisibility(View.VISIBLE);
                        break;
                    }
                } else {
                    this.mHandleDirectionUpOne.setVisibility(View.VISIBLE);
                    break;
                }
                break;
            case 108:
                if (this.mEventDeviceId != this.leftDeviceId) {
                    if (this.mEventDeviceId == this.rightDeviceId) {
                        this.mHandleDirectionStartTwo.setVisibility(View.VISIBLE);
                        break;
                    }
                } else {
                    this.mHandleDirectionStartOne.setVisibility(View.VISIBLE);
                    break;
                }
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        this.mEventDeviceId = event.getDeviceId();
        initLR(this.mEventDeviceId);
        switch (keyCode) {
            case 96:
                if (this.mEventDeviceId != this.leftDeviceId) {
                    if (this.mEventDeviceId == this.rightDeviceId) {
                        this.mHandleDirectionUpTwo.setVisibility(View.INVISIBLE);
                        break;
                    }
                } else {
                    this.mHandleDirectionBottomOne.setVisibility(View.INVISIBLE);
                    break;
                }
                break;
            case 97:
                if (this.mEventDeviceId != this.leftDeviceId) {
                    if (this.mEventDeviceId == this.rightDeviceId) {
                        this.mHandleDirectionLeftTwo.setVisibility(View.INVISIBLE);
                        break;
                    }
                } else {
                    this.mHandleDirectionRightOne.setVisibility(View.INVISIBLE);
                    break;
                }
                break;
            case 99:
                if (this.mEventDeviceId != this.leftDeviceId) {
                    if (this.mEventDeviceId == this.rightDeviceId) {
                        this.mHandleDirectionRightTwo.setVisibility(View.INVISIBLE);
                        break;
                    }
                } else {
                    this.mHandleDirectionLeftOne.setVisibility(View.INVISIBLE);
                    break;
                }
                break;
            case 100:
                if (this.mEventDeviceId != this.leftDeviceId) {
                    if (this.mEventDeviceId == this.rightDeviceId) {
                        this.mHandleDirectionBottomTwo.setVisibility(View.INVISIBLE);
                        break;
                    }
                } else {
                    this.mHandleDirectionUpOne.setVisibility(View.INVISIBLE);
                    break;
                }
                break;
            case 108:
                if (this.mEventDeviceId != this.leftDeviceId) {
                    if (this.mEventDeviceId == this.rightDeviceId) {
                        this.mHandleDirectionStartTwo.setVisibility(View.INVISIBLE);
                        break;
                    }
                } else {
                    this.mHandleDirectionStartOne.setVisibility(View.INVISIBLE);
                    break;
                }
                break;
            default:
                return super.onKeyUp(keyCode, event);
        }
        return true;
    }
}
