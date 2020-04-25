package cn.nubia.gamelauncherx.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import cn.nubia.gamelauncherx.R;
import cn.nubia.gamelauncherx.adapter.HandleAdapter;
import cn.nubia.gamelauncherx.gamehandle.GameHandleConnectionStateChangeListener;
import cn.nubia.gamelauncherx.gamehandle.GameHandleConstant;
import cn.nubia.gamelauncherx.gamehandle.GameHandleDataReceiver;
import cn.nubia.gamelauncherx.gamehandle.GameHandleScanCallback;
import cn.nubia.gamelauncherx.gamehandle.GameHandleService;
import cn.nubia.gamelauncherx.gamehandle.GameHandleService.LocalBinder;
import cn.nubia.gamelauncherx.gamehandle.HandShankMoveAreaFloatView;
import cn.nubia.gamelauncherx.gamehandle.NubiaCTAPermissionUtils;
import cn.nubia.gamelauncherx.gamehandle.ScanHelper;
import cn.nubia.gamelauncherx.util.LogUtil;
import cn.nubia.gamelauncherx.util.NubiaTrackManager;
import java.util.ArrayList;
import java.util.List;

public class RedMagicDoubleHandleActivity extends BaseActivity implements OnClickListener {
    private static final int CONNECTING_OVER = 4004;
    private static final int CONN_TIME_OUT = 4002;
    private static final int COUNTDOWN_OVER = 4003;
    private static final String REDMAGICHANDLE_NAME = "GH1001";
    private static final int REQUEST_PERMISSION_ACCESS_LOCATION = 4001;
    private static final String TAG = "RedMagicDoubleHandleActivity";
    /* access modifiers changed from: private */
    public AlertDialog.Builder builder;
    /* access modifiers changed from: private */
    public int connectingCountDown;
    /* access modifiers changed from: private */
    public Runnable connectingTask = new Runnable() {
        public void run() {
            RedMagicDoubleHandleActivity.this.connectingCountDown = RedMagicDoubleHandleActivity.this.connectingCountDown - 1;
            if (RedMagicDoubleHandleActivity.this.connectingCountDown <= 0) {
                Message msg = new Message();
                msg.what = RedMagicDoubleHandleActivity.CONNECTING_OVER;
                RedMagicDoubleHandleActivity.this.mUiHandle.sendMessage(msg);
            }
            RedMagicDoubleHandleActivity.this.mUiHandle.postDelayed(this, 1000);
        }
    };
    /* access modifiers changed from: private */
    public int countDown;
    /* access modifiers changed from: private */
    public TextView countdownView;
    /* access modifiers changed from: private */
    public HandleAdapter handleAdapter;
    /* access modifiers changed from: private */
    public Runnable handleCalibrationTask = new Runnable() {
        public void run() {
            RedMagicDoubleHandleActivity.this.countDown = RedMagicDoubleHandleActivity.this.countDown - 1;
            if (RedMagicDoubleHandleActivity.this.countDown <= 0) {
                Message msg = new Message();
                msg.what = RedMagicDoubleHandleActivity.COUNTDOWN_OVER;
                RedMagicDoubleHandleActivity.this.mUiHandle.sendMessage(msg);
                return;
            }
            RedMagicDoubleHandleActivity.this.countdownView.setText(RedMagicDoubleHandleActivity.this.getString(R.string.handle_calibration_countdown) + RedMagicDoubleHandleActivity.this.countDown + ")");
            RedMagicDoubleHandleActivity.this.mUiHandle.postDelayed(this, 1000);
        }
    };
    /* access modifiers changed from: private */
    public boolean isLeftHandle = true;
    private boolean isStart = true;
    private String leftAddreBEBig;
    /* access modifiers changed from: private */
    public List<BluetoothDevice> mBlueList = new ArrayList();
    private boolean mBlueListDisplay = true;
    private Button mBluetoothConnectOne;
    private Button mBluetoothConnectTwo;
    private TextView mChargeBGOne;
    private TextView mChargeBGTwo;
    /* access modifiers changed from: private */
    public TextView mChargeIconOne;
    /* access modifiers changed from: private */
    public TextView mChargeIconTwo;
    /* access modifiers changed from: private */
    public GameHandleConnectionStateChangeListener mConnectionStateChangeListener = new GameHandleConnectionStateChangeListener() {
        public void onConnectionStateChange(String address, int state) {
            LogUtil.d(RedMagicDoubleHandleActivity.TAG, "game handle connection state change: " + state);
            switch (state) {
                case 0:
                    if (RedMagicDoubleHandleActivity.this.handleAdapter != null) {
                        RedMagicDoubleHandleActivity.this.handleAdapter.setCheckedState(RedMagicDoubleHandleActivity.this.mGameHandleService.isConnected(address));
                        RedMagicDoubleHandleActivity.this.handleAdapter.notifyDataSetChanged();
                    }
                    RedMagicDoubleHandleActivity.this.mUiHandle.removeCallbacks(RedMagicDoubleHandleActivity.this.connectingTask);
                    RedMagicDoubleHandleActivity.this.mGameHandleService.removeDataReceiver(RedMagicDoubleHandleActivity.this.mDataReceiver);
                    RedMagicDoubleHandleActivity.this.mGameHandleService.addDataReceiver(RedMagicDoubleHandleActivity.this.mDataReceiver);
                    RedMagicDoubleHandleActivity.this.mHandleOneAddress = RedMagicDoubleHandleActivity.this.mGameHandleService.getLeftAddress();
                    RedMagicDoubleHandleActivity.this.mHandleTwoAddress = RedMagicDoubleHandleActivity.this.mGameHandleService.getRightAddress();
                    RedMagicDoubleHandleActivity.this.isConnectedOne(RedMagicDoubleHandleActivity.this.mHandleOneAddress);
                    RedMagicDoubleHandleActivity.this.isConnectedTwo(RedMagicDoubleHandleActivity.this.mHandleTwoAddress);
                    RedMagicDoubleHandleActivity.this.dialogDismiss();
                    RedMagicDoubleHandleActivity.this.isConn2Handle();
                    Toast.makeText(RedMagicDoubleHandleActivity.this, RedMagicDoubleHandleActivity.this.getString(R.string.handle_gamehandle_connected), Toast.LENGTH_SHORT).show();
                    return;
                case 1:
                    if (RedMagicDoubleHandleActivity.this.handleAdapter != null) {
                        RedMagicDoubleHandleActivity.this.handleAdapter.setCheckedState(RedMagicDoubleHandleActivity.this.mGameHandleService.isConnected(address));
                        RedMagicDoubleHandleActivity.this.handleAdapter.notifyDataSetChanged();
                        return;
                    }
                    return;
                case 3:
                    RedMagicDoubleHandleActivity.this.isConnectedOne(RedMagicDoubleHandleActivity.this.mHandleOneAddress);
                    RedMagicDoubleHandleActivity.this.isConnectedTwo(RedMagicDoubleHandleActivity.this.mHandleTwoAddress);
                    return;
                case 4:
                    RedMagicDoubleHandleActivity.this.connectingCountDown = 30;
                    RedMagicDoubleHandleActivity.this.mUiHandle.postDelayed(RedMagicDoubleHandleActivity.this.connectingTask, 1000);
                    if (RedMagicDoubleHandleActivity.this.handleAdapter != null) {
                        RedMagicDoubleHandleActivity.this.handleAdapter.setCheckedState(RedMagicDoubleHandleActivity.this.mGameHandleService.isConnected(address));
                        RedMagicDoubleHandleActivity.this.handleAdapter.notifyDataSetChanged();
                    }
                    Toast.makeText(RedMagicDoubleHandleActivity.this, RedMagicDoubleHandleActivity.this.getString(R.string.handle_waiting_for_binding), Toast.LENGTH_SHORT).show();
                    return;
                default:
                    return;
            }
        }
    };
    private Context mContext;
    /* access modifiers changed from: private */
    public GameHandleDataReceiver mDataReceiver = new GameHandleDataReceiver() {
        public void onReceiveData(String address, byte[] data) {
        }

        public void onBatteryChange(String address, int batteryLevel) {
            LogUtil.d(RedMagicDoubleHandleActivity.TAG, "[RedMagicDoubleHandleActivity] onBatteryChange address --> " + address + " batteryLevel --> " + batteryLevel);
            if (address != null) {
                if (address.equals(RedMagicDoubleHandleActivity.this.mHandleOneAddress)) {
                    LogUtil.d(RedMagicDoubleHandleActivity.TAG, "[RedMagicDoubleHandleActivity] one batteryLevel --> " + batteryLevel);
                    RedMagicDoubleHandleActivity.this.mRestChargeOne.setText(batteryLevel + "%");
                    LayoutParams params = RedMagicDoubleHandleActivity.this.mChargeIconOne.getLayoutParams();
                    params.height = (batteryLevel * 66) / 100;
                    if (batteryLevel > 10) {
                        RedMagicDoubleHandleActivity.this.mChargeIconOne.setBackground(RedMagicDoubleHandleActivity.this.getDrawable(R.drawable.battery_green));
                        RedMagicDoubleHandleActivity.this.mChargeIconOne.setLayoutParams(params);
                    } else {
                        RedMagicDoubleHandleActivity.this.mChargeIconOne.setBackground(RedMagicDoubleHandleActivity.this.getDrawable(R.drawable.battery_red));
                        RedMagicDoubleHandleActivity.this.mChargeIconOne.setLayoutParams(params);
                    }
                    LogUtil.d(RedMagicDoubleHandleActivity.TAG, "onBatteryChange " + batteryLevel);
                } else if (address.equals(RedMagicDoubleHandleActivity.this.mHandleTwoAddress)) {
                    LogUtil.d(RedMagicDoubleHandleActivity.TAG, "[RedMagicDoubleHandleActivity] two batteryLevel --> " + batteryLevel);
                    RedMagicDoubleHandleActivity.this.mRestChargeTwo.setText(batteryLevel + "%");
                    LayoutParams params2 = RedMagicDoubleHandleActivity.this.mChargeIconTwo.getLayoutParams();
                    params2.height = (batteryLevel * 66) / 100;
                    if (batteryLevel > 10) {
                        RedMagicDoubleHandleActivity.this.mChargeIconTwo.setBackground(RedMagicDoubleHandleActivity.this.getDrawable(R.drawable.battery_green));
                        RedMagicDoubleHandleActivity.this.mChargeIconTwo.setLayoutParams(params2);
                    } else {
                        RedMagicDoubleHandleActivity.this.mChargeIconTwo.setBackground(RedMagicDoubleHandleActivity.this.getDrawable(R.drawable.battery_red));
                        RedMagicDoubleHandleActivity.this.mChargeIconTwo.setLayoutParams(params2);
                    }
                    LogUtil.d(RedMagicDoubleHandleActivity.TAG, "onBatteryChange " + batteryLevel);
                }
            }
        }

        public void onCalibrateResult(String address, boolean result) {
            if (result) {
                Message msg = new Message();
                msg.what = RedMagicDoubleHandleActivity.COUNTDOWN_OVER;
                RedMagicDoubleHandleActivity.this.mUiHandle.sendMessage(msg);
            }
            LogUtil.d(RedMagicDoubleHandleActivity.TAG, "onCalibrateResult " + result);
        }
    };
    /* access modifiers changed from: private */
    public GameHandleService mGameHandleService;
    private ImageView mHandleBottomButtonOne_1;
    private ImageView mHandleBottomButtonOne_2;
    private ImageView mHandleBottomButtonTwo_1;
    private ImageView mHandleBottomButtonTwo_2;
    private Button mHandleCalibrationOne;
    private Button mHandleCalibrationTwo;
    /* access modifiers changed from: private */
    public int mHandleCount;
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
    private TextView mHandleHelper;
    private TextView mHandleHelperTwo;
    private TextView mHandleOne;
    /* access modifiers changed from: private */
    public String mHandleOneAddress;
    private TextView mHandleOneElectricText;
    private RelativeLayout mHandleOneFrontView;
    private LinearLayout mHandleOneTopView;
    private LinearLayout mHandleOneUpwardView;
    private HandShankMoveAreaFloatView mHandleRockerOne;
    private HandShankMoveAreaFloatView mHandleRockerTwo;
    private TextView mHandleStareOne;
    private TextView mHandleStareTwo;
    private ImageView mHandleTagBGImg;
    private RelativeLayout mHandleThreeViewOne;
    private RelativeLayout mHandleThreeViewTwo;
    private TextView mHandleTwo;
    /* access modifiers changed from: private */
    public String mHandleTwoAddress;
    private TextView mHandleTwoElectricText;
    private RelativeLayout mHandleTwoFrontView;
    private LinearLayout mHandleTwoTopView;
    private LinearLayout mHandleTwoUpwardView;
    private ImageView mHandleUpperButtonOne_1;
    private ImageView mHandleUpperButtonOne_2;
    private ImageView mHandleUpperButtonTwo_1;
    private ImageView mHandleUpperButtonTwo_2;
    private TextView mLeftRightCalibrationBtn;
    private ImageView mLeft_icon;
    private TextView mLeft_name;
    /* access modifiers changed from: private */
    public ListView mListView;
    /* access modifiers changed from: private */
    public AlertDialog mNubiaCenterAlertDialog;
    /* access modifiers changed from: private */
    public TextView mRestChargeOne;
    /* access modifiers changed from: private */
    public TextView mRestChargeTwo;
    private GameHandleScanCallback mScanCallback = new GameHandleScanCallback() {
        public void onScanResult(BluetoothDevice device) {
            if (!RedMagicDoubleHandleActivity.this.mBlueList.contains(device)) {
                LogUtil.d(RedMagicDoubleHandleActivity.TAG, "[GameHandleScanCallback] device = " + device);
                if (device.getName() != null && device.getName().length() >= 6 && RedMagicDoubleHandleActivity.REDMAGICHANDLE_NAME.equals(device.getName().substring(0, 6))) {
                    RedMagicDoubleHandleActivity.this.mBlueList.add(device);
                }
            }
            RedMagicDoubleHandleActivity.this.mHandleCount = RedMagicDoubleHandleActivity.this.mBlueList.size();
        }

        public void onBatchScanResults(List<BluetoothDevice> devices) {
            for (BluetoothDevice device : devices) {
                if (!RedMagicDoubleHandleActivity.this.mBlueList.contains(device)) {
                    LogUtil.d(RedMagicDoubleHandleActivity.TAG, "[onBatchScanResults] device = " + device);
                    if (device.getName() != null && device.getName().length() >= 6 && !RedMagicDoubleHandleActivity.REDMAGICHANDLE_NAME.equals(device.getName().substring(0, 6))) {
                    }
                }
            }
        }

        public void onScanFailed(int errorCode) {
            if (RedMagicDoubleHandleActivity.this.mHandleCount > 0) {
                RedMagicDoubleHandleActivity.this.dialogDismiss();
                RedMagicDoubleHandleActivity.this.view = LayoutInflater.from(RedMagicDoubleHandleActivity.this).inflate(R.layout.handle_choose_list, null);
                RedMagicDoubleHandleActivity.this.mListView = (ListView) RedMagicDoubleHandleActivity.this.view.findViewById(R.id.choose_list);
                RedMagicDoubleHandleActivity.this.handleAdapter = new HandleAdapter(RedMagicDoubleHandleActivity.this, RedMagicDoubleHandleActivity.this.mBlueList);
                LogUtil.d(RedMagicDoubleHandleActivity.TAG, "[GameHandleScanCallback] onScanFailed");
                RedMagicDoubleHandleActivity.this.mListView.setAdapter(RedMagicDoubleHandleActivity.this.handleAdapter);
                RedMagicDoubleHandleActivity.this.builder.setView(RedMagicDoubleHandleActivity.this.view);
                RedMagicDoubleHandleActivity.this.builder.setPositiveButton(R.string.exit_ok, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).create().show();
            }
        }
    };
    /* access modifiers changed from: private */
    public ScanHelper mScanHelper = new ScanHelper();
    /* access modifiers changed from: private */
    public int mSearchCountDown;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            RedMagicDoubleHandleActivity.this.mGameHandleService = ((LocalBinder) service).getService();
            RedMagicDoubleHandleActivity.this.mGameHandleService.addConnectionStateChangeListener(RedMagicDoubleHandleActivity.this.mConnectionStateChangeListener);
            RedMagicDoubleHandleActivity.this.mGameHandleService.addDataReceiver(RedMagicDoubleHandleActivity.this.mDataReceiver);
            boolean isFirst = RedMagicDoubleHandleActivity.this.mGameHandleService.isFirst();
            Log.d("kong", "   isFirst == " + isFirst);
            if (isFirst) {
                RedMagicDoubleHandleActivity.this.mGameHandleService.getSystemConnected();
            }
            RedMagicDoubleHandleActivity.this.refreshClient();
            RedMagicDoubleHandleActivity.this.isConnDoubleHandle();
        }

        public void onServiceDisconnected(ComponentName name) {
            RedMagicDoubleHandleActivity.this.mGameHandleService = null;
        }
    };
    Handler mUiHandle = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RedMagicDoubleHandleActivity.CONN_TIME_OUT /*4002*/:
                    RedMagicDoubleHandleActivity.this.onSearchResult();
                    return;
                case RedMagicDoubleHandleActivity.COUNTDOWN_OVER /*4003*/:
                    LogUtil.d(RedMagicDoubleHandleActivity.TAG, "[mUihandle] COUNTDOWN_OVER countDown = " + RedMagicDoubleHandleActivity.this.countDown);
                    if (!RedMagicDoubleHandleActivity.this.mNubiaCenterAlertDialog.isShowing()) {
                        return;
                    }
                    if (RedMagicDoubleHandleActivity.this.countDown == 0) {
                        Toast.makeText(RedMagicDoubleHandleActivity.this, RedMagicDoubleHandleActivity.this.getString(R.string.handle_overtime_cancel_calibration), Toast.LENGTH_LONG).show();
                        RedMagicDoubleHandleActivity.this.mUiHandle.removeCallbacks(RedMagicDoubleHandleActivity.this.handleCalibrationTask);
                        RedMagicDoubleHandleActivity.this.dialogDismiss();
                        return;
                    }
                    Toast.makeText(RedMagicDoubleHandleActivity.this, RedMagicDoubleHandleActivity.this.getString(R.string.handle_calibration_complete), Toast.LENGTH_SHORT).show();
                    RedMagicDoubleHandleActivity.this.mUiHandle.removeCallbacks(RedMagicDoubleHandleActivity.this.handleCalibrationTask);
                    RedMagicDoubleHandleActivity.this.dialogDismiss();
                    return;
                case RedMagicDoubleHandleActivity.CONNECTING_OVER /*4004*/:
                    Toast.makeText(RedMagicDoubleHandleActivity.this, RedMagicDoubleHandleActivity.this.getString(R.string.handle_connecting_fail), Toast.LENGTH_SHORT).show();
                    RedMagicDoubleHandleActivity.this.mUiHandle.removeCallbacks(RedMagicDoubleHandleActivity.this.connectingTask);
                    if (RedMagicDoubleHandleActivity.this.handleAdapter != null) {
                        RedMagicDoubleHandleActivity.this.handleAdapter.setCheckedItem(-1);
                        RedMagicDoubleHandleActivity.this.handleAdapter.notifyDataSetChanged();
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    };
    private String rightAddreBEBig;
    /* access modifiers changed from: private */
    public Runnable searchDeviceTask = new Runnable() {
        public void run() {
            RedMagicDoubleHandleActivity.this.mSearchCountDown = RedMagicDoubleHandleActivity.this.mSearchCountDown - 1;
            Message msg = new Message();
            msg.what = RedMagicDoubleHandleActivity.CONN_TIME_OUT;
            RedMagicDoubleHandleActivity.this.mUiHandle.sendMessage(msg);
            RedMagicDoubleHandleActivity.this.mUiHandle.postDelayed(this, 1000);
        }
    };
    /* access modifiers changed from: private */
    public View view;

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogUtil.d(TAG, "onConfigurationChanged");
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.redmagic_double_handle_main);
        this.mContext = this;
        checkCTA();
        bindService(new Intent(this, GameHandleService.class), this.mServiceConnection, Context.BIND_AUTO_CREATE);
        initView();
    }

    private void checkCTA() {
        if (!NubiaCTAPermissionUtils.isCTAOK(this.mContext)) {
            NubiaCTAPermissionUtils.showPermissionDialog(this.mContext, null);
        }
    }

    /* access modifiers changed from: protected */
    public void refreshClient() {
        if (this.mGameHandleService != null) {
            if (getIntent().getBooleanExtra(GameHandleConstant.CALBRATION_ACTIVITY, false)) {
                this.mGameHandleService.refreshClient();
            }
            this.mHandleOneAddress = this.mGameHandleService.getLeftAddress();
            this.mHandleTwoAddress = this.mGameHandleService.getRightAddress();
            isConnectedOne(this.mHandleOneAddress);
            isConnectedTwo(this.mHandleTwoAddress);
        }
    }

    private void initView() {
        this.mLeft_icon = (ImageView) findViewById(R.id.left_icon);
        this.mLeft_name = (TextView) findViewById(R.id.left_name);
        this.mHandleOne = (TextView) findViewById(R.id.handle_one_button);
        this.mHandleTwo = (TextView) findViewById(R.id.handle_two_button);
        this.mLeftRightCalibrationBtn = (TextView) findViewById(R.id.handle_left_right_btn);
        this.mHandleThreeViewOne = (RelativeLayout) findViewById(R.id.handle_one_three_view);
        this.mHandleThreeViewTwo = (RelativeLayout) findViewById(R.id.handle_two_three_view);
        this.mHandleOneFrontView = (RelativeLayout) findViewById(R.id.handle_front_view);
        this.mHandleOneTopView = (LinearLayout) findViewById(R.id.handle_top_view);
        this.mHandleOneUpwardView = (LinearLayout) findViewById(R.id.handle_upward_view);
        this.mHandleOneElectricText = (TextView) findViewById(R.id.handle_electric_text);
        this.mHandleTwoElectricText = (TextView) findViewById(R.id.handle_electric_text_2);
        this.mHandleTwoFrontView = (RelativeLayout) findViewById(R.id.handle_front_view_2);
        this.mHandleTwoTopView = (LinearLayout) findViewById(R.id.handle_top_view_2);
        this.mHandleTwoUpwardView = (LinearLayout) findViewById(R.id.handle_upward_view_2);
        this.mHandleRockerOne = (HandShankMoveAreaFloatView) findViewById(R.id.handle_rocker_img);
        this.mHandleRockerTwo = (HandShankMoveAreaFloatView) findViewById(R.id.handle_rocker_img_2);
        this.mHandleDirectionUpOne = (ImageView) findViewById(R.id.handle_direction_up_img);
        this.mHandleDirectionLeftOne = (ImageView) findViewById(R.id.handle_direction_left_img);
        this.mHandleDirectionBottomOne = (ImageView) findViewById(R.id.handle_direction_bottom_img);
        this.mHandleDirectionRightOne = (ImageView) findViewById(R.id.handle_direction_right_img);
        this.mHandleDirectionStartOne = (ImageView) findViewById(R.id.handle_direction_start_img);
        this.mHandleDirectionUpTwo = (ImageView) findViewById(R.id.handle_direction_up_img_2);
        this.mHandleDirectionLeftTwo = (ImageView) findViewById(R.id.handle_direction_left_img_2);
        this.mHandleDirectionBottomTwo = (ImageView) findViewById(R.id.handle_direction_bottom_img_2);
        this.mHandleDirectionRightTwo = (ImageView) findViewById(R.id.handle_direction_right_img_2);
        this.mHandleDirectionStartTwo = (ImageView) findViewById(R.id.handle_direction_start_img_2);
        this.mHandleUpperButtonOne_1 = (ImageView) findViewById(R.id.handle_upper_button_1_img);
        this.mHandleUpperButtonOne_2 = (ImageView) findViewById(R.id.handle_upper_button_2_img);
        this.mHandleBottomButtonOne_1 = (ImageView) findViewById(R.id.handle_bottom_button_1_img);
        this.mHandleBottomButtonOne_2 = (ImageView) findViewById(R.id.handle_bottom_button_2_img);
        this.mHandleUpperButtonTwo_1 = (ImageView) findViewById(R.id.handle_upper_button_1_img_2);
        this.mHandleUpperButtonTwo_2 = (ImageView) findViewById(R.id.handle_upper_button_2_img_2);
        this.mHandleBottomButtonTwo_1 = (ImageView) findViewById(R.id.handle_bottom_button_1_img_2);
        this.mHandleBottomButtonTwo_2 = (ImageView) findViewById(R.id.handle_bottom_button_2_img_2);
        this.mHandleStareOne = (TextView) findViewById(R.id.handle_one_state);
        this.mHandleStareTwo = (TextView) findViewById(R.id.handle_two_state);
        this.mHandleTagBGImg = (ImageView) findViewById(R.id.handle_tag_bg_img);
        this.mHandleHelper = (TextView) findViewById(R.id.handle_helper);
        this.mHandleHelperTwo = (TextView) findViewById(R.id.handle_helper_2);
        this.mChargeBGOne = (TextView) findViewById(R.id.charge_bg);
        this.mChargeIconOne = (TextView) findViewById(R.id.charge_icon);
        this.mRestChargeOne = (TextView) findViewById(R.id.rest_charge);
        this.mChargeBGTwo = (TextView) findViewById(R.id.charge_bg_2);
        this.mChargeIconTwo = (TextView) findViewById(R.id.charge_icon_2);
        this.mRestChargeTwo = (TextView) findViewById(R.id.rest_charge_2);
        this.mBluetoothConnectOne = (Button) findViewById(R.id.bluetooth_connect);
        this.mHandleCalibrationOne = (Button) findViewById(R.id.handle_calibration);
        this.mBluetoothConnectTwo = (Button) findViewById(R.id.bluetooth_connect_2);
        this.mHandleCalibrationTwo = (Button) findViewById(R.id.handle_calibration_2);
        this.mLeft_icon.setOnClickListener(this);
        this.mLeft_name.setOnClickListener(this);
        this.mHandleOne.setOnClickListener(this);
        this.mHandleTwo.setOnClickListener(this);
        this.mLeftRightCalibrationBtn.setOnClickListener(this);
        this.mHandleHelper.setOnClickListener(this);
        this.mHandleHelperTwo.setOnClickListener(this);
        this.mBluetoothConnectOne.setOnClickListener(this);
        this.mHandleCalibrationOne.setOnClickListener(this);
        this.mBluetoothConnectTwo.setOnClickListener(this);
        this.mHandleCalibrationTwo.setOnClickListener(this);
        this.mLeft_name.setText(getString(R.string.handle_redmagic_handle));
        this.builder = new AlertDialog.Builder(this);
    }

    /* access modifiers changed from: private */
    public void isConnDoubleHandle() {
        if (this.mGameHandleService != null) {
            LogUtil.d(TAG, "isDoubleHandle = " + this.mGameHandleService.getConnDoubleHandle());
            Log.d("kong", "isConnDoubleHandle isStart = " + this.isStart);
            if (this.mGameHandleService.getConnDoubleHandle() && this.isStart && this.mGameHandleService.isConnected(this.mHandleOneAddress) && this.mGameHandleService.isConnected(this.mHandleTwoAddress)) {
                this.isStart = false;
                this.mUiHandle.postDelayed(new Runnable() {
                    public void run() {
                        Intent intent_LRCalibration = new Intent(RedMagicDoubleHandleActivity.this, RedMagicHandleLeftRightCalibrationActivity.class);
                        if (RedMagicDoubleHandleActivity.this.mGameHandleService.isConnected(RedMagicDoubleHandleActivity.this.mHandleOneAddress) && RedMagicDoubleHandleActivity.this.mGameHandleService.isConnected(RedMagicDoubleHandleActivity.this.mHandleTwoAddress)) {
                            RedMagicDoubleHandleActivity.this.startActivity(intent_LRCalibration);
                            RedMagicDoubleHandleActivity.this.mGameHandleService.setConnDoubleHandle();
                            RedMagicDoubleHandleActivity.this.finish();
                        }
                    }
                }, 2000);
            }
        }
    }

    /* access modifiers changed from: private */
    public void isConn2Handle() {
        if (this.mGameHandleService != null) {
            LogUtil.d(TAG, "isDoubleHandle = " + this.mGameHandleService.getConnDoubleHandle());
            boolean isCalActivity = getIntent().getBooleanExtra(GameHandleConstant.CALBRATION_ACTIVITY, false);
            Log.d("kong", "[isConn2Handle] isCalActivity = " + isCalActivity + " isStart = " + this.isStart);
            if (!isCalActivity && this.isStart && this.mGameHandleService.isConnected(this.mHandleOneAddress) && this.mGameHandleService.isConnected(this.mHandleTwoAddress)) {
                this.mUiHandle.postDelayed(new Runnable() {
                    public void run() {
                        Intent intent_LRCalibration = new Intent(RedMagicDoubleHandleActivity.this, RedMagicHandleLeftRightCalibrationActivity.class);
                        if (RedMagicDoubleHandleActivity.this.mGameHandleService.isConnected(RedMagicDoubleHandleActivity.this.mHandleOneAddress) && RedMagicDoubleHandleActivity.this.mGameHandleService.isConnected(RedMagicDoubleHandleActivity.this.mHandleTwoAddress)) {
                            RedMagicDoubleHandleActivity.this.startActivity(intent_LRCalibration);
                            RedMagicDoubleHandleActivity.this.mGameHandleService.setConnDoubleHandle();
                            RedMagicDoubleHandleActivity.this.finish();
                        }
                    }
                }, 2500);
            }
        }
    }

    /* access modifiers changed from: private */
    public void isConnectedOne(String address) {
        LogUtil.d(TAG, "[RedMagicDoubleHandleActivity] isConnectOne --> " + this.mGameHandleService.isConnected(this.mHandleOneAddress));
        if (this.mGameHandleService.isConnected(address)) {
            isConnDoubleHandle();
            this.mHandleTwo.setVisibility(View.VISIBLE);
            this.mHandleStareOne.setText(getString(R.string.handle_one_button_text));
            this.mHandleStareOne.setAlpha(0.85f);
            this.mRestChargeOne.setAlpha(0.85f);
            this.mHandleOne.setAlpha(0.85f);
            this.mHandleTagBGImg.setVisibility(View.VISIBLE);
            if (this.mHandleThreeViewOne.getVisibility() == View.VISIBLE) {
                this.mHandleOne.setAlpha(1.0f);
            } else {
                this.mHandleOne.setAlpha(0.5f);
            }
            this.mHandleOneFrontView.setBackground(getDrawable(R.drawable.handle_front));
            this.mHandleOneTopView.setBackground(getDrawable(R.drawable.handle_topview));
            this.mHandleOneUpwardView.setBackground(getDrawable(R.drawable.handle_upwardview));
            this.mHandleRockerOne.setAlpha(1.0f);
            this.mHandleOneElectricText.setAlpha(0.85f);
            this.mChargeBGOne.setAlpha(1.0f);
            this.mChargeIconOne.setVisibility(View.VISIBLE);
            this.mRestChargeOne.setVisibility(View.VISIBLE);
            this.mGameHandleService.getBattery(this.mHandleOneAddress);
            this.mBluetoothConnectOne.setText(getString(R.string.handle_cancel_pair_button));
            this.mHandleCalibrationOne.setEnabled(true);
            this.mHandleCalibrationOne.setTextColor(getColor(R.color.handle_white));
            this.mHandleCalibrationOne.setBackground(getDrawable(R.drawable.handle_button_bg));
            this.isLeftHandle = false;
            if (this.mGameHandleService.isConnected(this.mHandleTwoAddress) && this.mBluetoothConnectTwo.getText().equals(getString(R.string.handle_connect_button))) {
                isConnectedTwo(this.mHandleTwoAddress);
            }
            if (this.mGameHandleService.isConnected(this.mHandleOneAddress) && this.mGameHandleService.isConnected(this.mHandleTwoAddress)) {
                this.mLeftRightCalibrationBtn.setVisibility(View.VISIBLE);
                this.mHandleOne.setText(getString(R.string.handle_left_handle));
                this.mHandleTwo.setText(getString(R.string.handle_right_handle));
                return;
            }
            return;
        }
        this.mLeftRightCalibrationBtn.setVisibility(View.GONE);
        this.mHandleTwo.setVisibility(View.GONE);
        this.mHandleStareOne.setText(getString(R.string.handle_unconnected));
        this.mHandleStareOne.setAlpha(0.3f);
        this.mRestChargeOne.setAlpha(0.3f);
        if (!this.mGameHandleService.isConnected(this.mHandleTwoAddress)) {
            this.mHandleTagBGImg.setVisibility(View.GONE);
        }
        if (this.mHandleThreeViewOne.getVisibility() == View.VISIBLE) {
            this.mHandleOne.setAlpha(1.0f);
        } else {
            this.mHandleOne.setAlpha(0.5f);
        }
        this.mHandleOne.setText(getString(R.string.handle_one_button_text));
        this.mHandleTwo.setText(getString(R.string.handle_two_button_text));
        this.mHandleOneFrontView.setBackground(getDrawable(R.drawable.handle_front_default));
        this.mHandleOneTopView.setBackground(getDrawable(R.drawable.handle_topview_default));
        this.mHandleOneUpwardView.setBackground(getDrawable(R.drawable.handle_upwardview_default));
        this.mHandleRockerOne.setAlpha(0.5f);
        this.mHandleOneElectricText.setTextColor(getColor(R.color.handle_text_default_color));
        this.mChargeBGOne.setAlpha(0.3f);
        this.mChargeIconOne.setVisibility(View.GONE);
        this.mRestChargeOne.setText("--%");
        this.mBluetoothConnectOne.setText(getString(R.string.handle_connect_button));
        this.mHandleCalibrationOne.setEnabled(false);
        this.mHandleCalibrationOne.setTextColor(getColor(R.color.handle_text_default_color));
        this.mHandleCalibrationOne.setBackground(getDrawable(R.drawable.button_unpress));
        this.isLeftHandle = true;
    }

    /* access modifiers changed from: private */
    public void isConnectedTwo(String address) {
        LogUtil.d(TAG, "[RedMagicDoubleHandleActivity] isConnectTwo --> " + this.mGameHandleService.isConnected(this.mHandleTwoAddress));
        if (this.mGameHandleService.isConnected(this.mHandleTwoAddress)) {
            isConnDoubleHandle();
            this.mHandleTwo.setVisibility(View.VISIBLE);
            this.mHandleStareTwo.setText(getString(R.string.handle_two_button_text));
            this.mHandleStareTwo.setAlpha(0.85f);
            this.mRestChargeTwo.setAlpha(0.85f);
            this.mHandleTwo.setAlpha(0.85f);
            this.mHandleTagBGImg.setVisibility(View.VISIBLE);
            if (this.mHandleThreeViewTwo.getVisibility() == View.VISIBLE) {
                this.mHandleTwo.setAlpha(1.0f);
            } else {
                this.mHandleTwo.setAlpha(0.5f);
            }
            this.mHandleTwoFrontView.setBackground(getDrawable(R.drawable.handle_front));
            this.mHandleTwoTopView.setBackground(getDrawable(R.drawable.handle_topview));
            this.mHandleTwoUpwardView.setBackground(getDrawable(R.drawable.handle_upwardview));
            this.mHandleRockerTwo.setAlpha(1.0f);
            this.mHandleTwoElectricText.setAlpha(0.85f);
            this.mChargeBGTwo.setAlpha(1.0f);
            this.mChargeIconTwo.setVisibility(View.VISIBLE);
            this.mRestChargeTwo.setVisibility(View.VISIBLE);
            this.mGameHandleService.getBattery(this.mHandleTwoAddress);
            this.mBluetoothConnectTwo.setText(getString(R.string.handle_cancel_pair_button));
            this.mHandleCalibrationTwo.setEnabled(true);
            this.mHandleCalibrationTwo.setTextColor(getColor(R.color.handle_white));
            this.mHandleCalibrationTwo.setBackground(getDrawable(R.drawable.handle_button_bg));
            if (this.mGameHandleService.isConnected(this.mHandleOneAddress) && this.mBluetoothConnectOne.getText().equals(getString(R.string.handle_connect_button))) {
                isConnectedOne(this.mHandleOneAddress);
            }
            if (this.mGameHandleService.isConnected(this.mHandleOneAddress) && this.mGameHandleService.isConnected(this.mHandleTwoAddress)) {
                this.mLeftRightCalibrationBtn.setVisibility(View.VISIBLE);
                this.mHandleOne.setText(getString(R.string.handle_left_handle));
                this.mHandleTwo.setText(getString(R.string.handle_right_handle));
                return;
            }
            return;
        }
        this.mLeftRightCalibrationBtn.setVisibility(View.GONE);
        this.mHandleStareTwo.setText(getString(R.string.handle_unconnected));
        this.mHandleStareTwo.setAlpha(0.3f);
        this.mRestChargeTwo.setAlpha(0.3f);
        if (!this.mGameHandleService.isConnected(this.mHandleOneAddress)) {
            this.mHandleTagBGImg.setVisibility(View.GONE);
        }
        if (this.mHandleThreeViewTwo.getVisibility() == View.VISIBLE) {
            this.mHandleTwo.setAlpha(1.0f);
        } else {
            this.mHandleTwo.setAlpha(0.5f);
        }
        this.mHandleOne.setText(getString(R.string.handle_one_button_text));
        this.mHandleTwo.setText(getString(R.string.handle_two_button_text));
        this.mHandleTwoFrontView.setBackground(getDrawable(R.drawable.handle_front_default));
        this.mHandleTwoTopView.setBackground(getDrawable(R.drawable.handle_topview_default));
        this.mHandleTwoUpwardView.setBackground(getDrawable(R.drawable.handle_upwardview_default));
        this.mHandleRockerTwo.setAlpha(0.5f);
        this.mHandleTwoElectricText.setTextColor(getColor(R.color.handle_text_default_color));
        this.mChargeBGTwo.setAlpha(0.3f);
        this.mChargeIconTwo.setVisibility(View.GONE);
        this.mRestChargeTwo.setText("--%");
        this.mBluetoothConnectTwo.setText(getString(R.string.handle_connect_button));
        this.mHandleCalibrationTwo.setEnabled(false);
        this.mHandleCalibrationTwo.setTextColor(getColor(R.color.handle_text_default_color));
        this.mHandleCalibrationTwo.setBackground(getDrawable(R.drawable.button_unpress));
    }

    public void onClick(View view2) {
        switch (view2.getId()) {
            case R.id.left_icon /*2131689576*/:
            case R.id.left_name /*2131689577*/:
                finish();
                return;
            case R.id.handle_one_button /*2131689784*/:
                LogUtil.d(TAG, "[RedMagicDoubleHandleActivity]  mHandleOne = " + this.mHandleOneAddress);
                NubiaTrackManager.getInstance().sendEvent("cn.nubia.gamelauncher", "redmagic_handle_valuable_assistant_setting", "左右手", "左手");
                this.mHandleThreeViewOne.setVisibility(View.VISIBLE);
                this.mHandleThreeViewTwo.setVisibility(View.GONE);
                this.mHandleOne.setAlpha(1.0f);
                this.mHandleTwo.setAlpha(0.5f);
                return;
            case R.id.handle_two_button /*2131689785*/:
                LogUtil.d(TAG, "[RedMagicDoubleHandleActivity]  mHandleTwo = " + this.mHandleTwoAddress);
                NubiaTrackManager.getInstance().sendEvent("cn.nubia.gamelauncher", "redmagic_handle_valuable_assistant_setting", "左右手", "右手");
                this.mHandleThreeViewOne.setVisibility(View.GONE);
                this.mHandleThreeViewTwo.setVisibility(View.VISIBLE);
                this.mHandleOne.setAlpha(0.5f);
                this.mHandleTwo.setAlpha(1.0f);
                return;
            case R.id.handle_left_right_btn /*2131689786*/:
                startActivity(new Intent(this, RedMagicHandleLeftRightCalibrationActivity.class));
                finish();
                return;
            case R.id.handle_helper /*2131689795*/:
            case R.id.handle_helper_2 /*2131689819*/:
                startActivity(new Intent(this, RedMagicHandleHelperActivity.class));
                return;
            case R.id.bluetooth_connect /*2131689803*/:
                bluetoothConnect(R.id.bluetooth_connect);
                return;
            case R.id.handle_calibration /*2131689804*/:
                NubiaTrackManager.getInstance().sendEvent("cn.nubia.gamelauncher", "redmagic_handle_calibration");
                handleCalibration(this.mHandleOneAddress);
                return;
            case R.id.bluetooth_connect_2 /*2131689827*/:
                bluetoothConnect(R.id.bluetooth_connect_2);
                return;
            case R.id.handle_calibration_2 /*2131689828*/:
                handleCalibration(this.mHandleTwoAddress);
                return;
            default:
                return;
        }
    }

    private void bluetoothConnect(int butId) {
        this.mBlueList.clear();
        if (butId == R.id.bluetooth_connect && this.mBluetoothConnectOne.getText().equals(getString(R.string.handle_cancel_pair_button))) {
            disConnect(this.mHandleOneAddress);
            LogUtil.d(TAG, "[RedMagicDoubleHandleActivity] bluetoothConnect addressOne = " + this.mHandleOneAddress);
        } else if (butId != R.id.bluetooth_connect_2 || !this.mBluetoothConnectTwo.getText().equals(getString(R.string.handle_cancel_pair_button))) {
            startDiscovery();
        } else {
            disConnect(this.mHandleTwoAddress);
            LogUtil.d(TAG, "[RedMagicDoubleHandleActivity] bluetoothConnect addressTwo = " + this.mHandleTwoAddress);
        }
    }

    private void disConnect(String address) {
        if (this.mGameHandleService != null) {
            this.mGameHandleService.unBond(address);
        }
    }

    private void startDiscovery() {
        LogUtil.d(TAG, " LocationEnable = " + isLocationEnable());
        if (!isLocationEnable()) {
            Toast.makeText(this, getString(R.string.handle_open_gps), Toast.LENGTH_LONG).show();
        } else if (requestPermission()) {
            startScanBluth();
        }
    }

    private void startScanBluth() {
        LogUtil.d(TAG, "[startScanBluth] start search");
        this.mBlueListDisplay = true;
        this.mSearchCountDown = 30;
        this.mScanHelper.stopScan();
        this.mScanHelper.scan(this.mScanCallback);
        this.view = LayoutInflater.from(this).inflate(R.layout.bluetooth_conn_search, null);
        this.builder = new AlertDialog.Builder(this);
        this.builder.setView(this.view);
        this.builder.setPositiveButton(R.string.exit_cancel, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                RedMagicDoubleHandleActivity.this.mScanHelper.stopScan();
                RedMagicDoubleHandleActivity.this.mUiHandle.removeCallbacks(RedMagicDoubleHandleActivity.this.searchDeviceTask);
                dialog.dismiss();
            }
        });
        this.mNubiaCenterAlertDialog = this.builder.show();
        this.mUiHandle.postDelayed(this.searchDeviceTask, 1000);
    }

    /* access modifiers changed from: private */
    public void onSearchResult() {
        if (this.mGameHandleService.isConnected(this.mHandleOneAddress) && this.mGameHandleService.isConnected(this.mHandleTwoAddress)) {
            dialogDismiss();
        }
        if (this.mSearchCountDown <= 0) {
            this.mScanHelper.stopScan();
            this.mUiHandle.removeCallbacks(this.searchDeviceTask);
            if (this.mBlueList.size() <= 0) {
                showNoHandleDialog();
            }
        }
        if (this.mBlueList.size() <= 0) {
            return;
        }
        if (this.mBlueListDisplay) {
            showHandleListDialog();
            this.mBlueListDisplay = false;
            return;
        }
        this.handleAdapter.notifyDataSetChanged();
    }

    private void showHandleListDialog() {
        LogUtil.d(TAG, "[showHandleListDialog] Handle search complete, display results.");
        dialogDismiss();
        this.view = LayoutInflater.from(this).inflate(R.layout.handle_choose_list, null);
        this.mListView = (ListView) this.view.findViewById(R.id.choose_list);
        this.handleAdapter = new HandleAdapter(this, this.mBlueList);
        this.mListView.setAdapter(this.handleAdapter);
        this.builder.setView(this.view);
        this.builder.setPositiveButton(R.string.exit_ok, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                RedMagicDoubleHandleActivity.this.mScanHelper.stopScan();
                RedMagicDoubleHandleActivity.this.mUiHandle.removeCallbacks(RedMagicDoubleHandleActivity.this.searchDeviceTask);
                dialog.dismiss();
            }
        });
        this.mNubiaCenterAlertDialog = this.builder.show();
        this.mListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                HandleAdapter adapter = (HandleAdapter) RedMagicDoubleHandleActivity.this.mListView.getAdapter();
                BluetoothDevice device = (BluetoothDevice) adapter.getItem(index);
                LogUtil.d(RedMagicDoubleHandleActivity.TAG, "[showHandleListDialog] HandleName = " + device.getName());
                if (RedMagicDoubleHandleActivity.this.mGameHandleService != null) {
                    RedMagicDoubleHandleActivity.this.mGameHandleService.connect(device.getAddress(), RedMagicDoubleHandleActivity.this.isLeftHandle);
                    LogUtil.d(RedMagicDoubleHandleActivity.TAG, "[RedMagicDoubleHandleActivity] onItemClickListener addressOne = " + RedMagicDoubleHandleActivity.this.mHandleOneAddress + " addressTwo = " + RedMagicDoubleHandleActivity.this.mHandleTwoAddress);
                    if (RedMagicDoubleHandleActivity.this.mHandleOneAddress != null && RedMagicDoubleHandleActivity.this.mHandleOneAddress.equals(RedMagicDoubleHandleActivity.this.mHandleTwoAddress)) {
                        RedMagicDoubleHandleActivity.this.mHandleTwoAddress = null;
                        RedMagicDoubleHandleActivity.this.mGameHandleService.setResetLR(RedMagicDoubleHandleActivity.this.mHandleOneAddress, RedMagicDoubleHandleActivity.this.mHandleTwoAddress);
                    }
                    RedMagicDoubleHandleActivity.this.mUiHandle.removeCallbacks(RedMagicDoubleHandleActivity.this.searchDeviceTask);
                    RedMagicDoubleHandleActivity.this.mScanHelper.stopScan();
                    adapter.setCheckedItem(index);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        HandleAdapter adapter = (HandleAdapter) this.mListView.getAdapter();
        for (int i = 0; i < this.mBlueList.size(); i++) {
            boolean connectState = this.mGameHandleService.isConnected(((BluetoothDevice) adapter.getItem(i)).getAddress());
            Log.d("kong", "connectState = " + connectState);
            if (connectState) {
                adapter.setCheckedItem(i);
                adapter.setCheckedState(connectState);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void showNoHandleDialog() {
        if (this.mNubiaCenterAlertDialog != null) {
            this.mNubiaCenterAlertDialog.dismiss();
        }
        this.view = LayoutInflater.from(this).inflate(R.layout.bluetooth_no_connectable_device, null);
        this.builder.setView(this.view);
        this.builder.setPositiveButton(R.string.exit_ok, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        }).create().show();
    }

    private void handleCalibration(String address) {
        this.view = LayoutInflater.from(this).inflate(R.layout.handle_calibration_countdown, null);
        this.countdownView = (TextView) this.view.findViewById(R.id.handle_calibration_explain);
        this.countDown = 60;
        this.countdownView.setText(getString(R.string.handle_calibration_countdown) + this.countDown + ")");
        this.builder.setView(this.view);
        this.builder.setPositiveButton(R.string.exit_cancel, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                RedMagicDoubleHandleActivity.this.mUiHandle.removeCallbacks(RedMagicDoubleHandleActivity.this.handleCalibrationTask);
                dialog.dismiss();
            }
        });
        this.mNubiaCenterAlertDialog = this.builder.show();
        this.mUiHandle.postDelayed(this.handleCalibrationTask, 1000);
        if (this.mGameHandleService != null) {
            LogUtil.d(TAG, "[RedMagicDoubleHandleActivity] handleCalibration address --> " + address);
            this.mGameHandleService.calibrate(address);
        }
    }

    /* access modifiers changed from: private */
    public void dialogDismiss() {
        if (this.mNubiaCenterAlertDialog != null && this.mNubiaCenterAlertDialog.isShowing()) {
            LogUtil.d(TAG, "[RedMagicDoubleHandleActivity] dialogDismiss");
            this.mNubiaCenterAlertDialog.dismiss();
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        LogUtil.e(TAG, "onDestroy");
        dialogDismiss();
        this.mUiHandle.removeCallbacks(this.searchDeviceTask);
        this.mUiHandle.removeCallbacks(this.handleCalibrationTask);
        this.mUiHandle.removeCallbacks(this.connectingTask);
        if (this.mScanHelper.isScanning()) {
            this.mScanHelper.stopScan();
        }
        if (this.mGameHandleService != null) {
            this.mGameHandleService.removeConnectionStateChangeListener(this.mConnectionStateChangeListener);
            this.mGameHandleService.removeDataReceiver(this.mDataReceiver);
        }
        unbindService(this.mServiceConnection);
    }

    private boolean isLocationEnable() {
        return ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled("gps");
    }

    private boolean requestPermission() {
        if (VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != 0) {
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, REQUEST_PERMISSION_ACCESS_LOCATION);
                LogUtil.d(TAG, getPackageName() + "No permissions, request permissions");
                return false;
            }
            LogUtil.d(TAG, getPackageName() + "Location permissions opens");
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_ACCESS_LOCATION /*4001*/:
                if (grantResults.length > 0 && grantResults[0] == 0) {
                    LogUtil.e(TAG, "permission granted!");
                    break;
                } else {
                    Toast.makeText(this, getString(R.string.handle_permission_position), Toast.LENGTH_SHORT).show();
                    LogUtil.e(TAG, "Location permissions need to be turned on!");
                    break;
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        int deviceId = event.getDeviceId();
        int leftDeviceId = -1;
        int rightDeviceId = -1;
        if (this.mGameHandleService != null) {
            changeMAC();
            Context mContext2 = this.mGameHandleService.getApplicationContext();
            leftDeviceId = GameHandleService.getGamePadDeviceId(mContext2, this.leftAddreBEBig);
            rightDeviceId = GameHandleService.getGamePadDeviceId(mContext2, this.rightAddreBEBig);
        }
        switch (event.getAction()) {
            case 2:
                float axisX = event.getAxisValue(0);
                float axisY = event.getAxisValue(1);
                if (deviceId == leftDeviceId) {
                    this.mHandleRockerOne.setJoyStickMoveXY(axisX, axisY);
                    return true;
                } else if (deviceId != rightDeviceId) {
                    return true;
                } else {
                    this.mHandleRockerTwo.setJoyStickMoveXY(axisX, axisY);
                    return true;
                }
            default:
                return super.onGenericMotionEvent(event);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int deviceId = event.getDeviceId();
        int leftDeviceId = -1;
        int rightDeviceId = -1;
        if (this.mGameHandleService != null) {
            changeMAC();
            Context mContext2 = this.mGameHandleService.getApplicationContext();
            leftDeviceId = GameHandleService.getGamePadDeviceId(mContext2, this.leftAddreBEBig);
            rightDeviceId = GameHandleService.getGamePadDeviceId(mContext2, this.rightAddreBEBig);
        }
        switch (keyCode) {
            case 96:
                if (deviceId != leftDeviceId) {
                    if (deviceId == rightDeviceId) {
                        this.mHandleDirectionBottomTwo.setVisibility(View.VISIBLE);
                        break;
                    }
                } else {
                    this.mHandleDirectionBottomOne.setVisibility(View.VISIBLE);
                    break;
                }
                break;
            case 97:
                if (deviceId != leftDeviceId) {
                    if (deviceId == rightDeviceId) {
                        this.mHandleDirectionRightTwo.setVisibility(View.VISIBLE);
                        break;
                    }
                } else {
                    this.mHandleDirectionRightOne.setVisibility(View.VISIBLE);
                    break;
                }
                break;
            case 99:
                if (deviceId != leftDeviceId) {
                    if (deviceId == rightDeviceId) {
                        this.mHandleDirectionLeftTwo.setVisibility(View.VISIBLE);
                        break;
                    }
                } else {
                    this.mHandleDirectionLeftOne.setVisibility(View.VISIBLE);
                    break;
                }
                break;
            case 100:
                if (deviceId != leftDeviceId) {
                    if (deviceId == rightDeviceId) {
                        this.mHandleDirectionUpTwo.setVisibility(View.VISIBLE);
                        break;
                    }
                } else {
                    this.mHandleDirectionUpOne.setVisibility(View.VISIBLE);
                    break;
                }
                break;
            case 102:
                if (deviceId != leftDeviceId) {
                    if (deviceId == rightDeviceId) {
                        this.mHandleUpperButtonTwo_1.setVisibility(View.VISIBLE);
                        break;
                    }
                } else {
                    this.mHandleUpperButtonOne_1.setVisibility(View.VISIBLE);
                    break;
                }
                break;
            case 103:
                if (deviceId != leftDeviceId) {
                    if (deviceId == rightDeviceId) {
                        this.mHandleBottomButtonTwo_1.setVisibility(View.VISIBLE);
                        break;
                    }
                } else {
                    this.mHandleBottomButtonOne_1.setVisibility(View.VISIBLE);
                    break;
                }
                break;
            case 104:
                if (deviceId != leftDeviceId) {
                    if (deviceId == rightDeviceId) {
                        this.mHandleUpperButtonTwo_2.setVisibility(View.VISIBLE);
                        break;
                    }
                } else {
                    this.mHandleUpperButtonOne_2.setVisibility(View.VISIBLE);
                    break;
                }
                break;
            case 105:
                if (deviceId != leftDeviceId) {
                    if (deviceId == rightDeviceId) {
                        this.mHandleBottomButtonTwo_2.setVisibility(View.VISIBLE);
                        break;
                    }
                } else {
                    this.mHandleBottomButtonOne_2.setVisibility(View.VISIBLE);
                    break;
                }
                break;
            case 108:
                if (deviceId == leftDeviceId) {
                    this.mHandleDirectionStartOne.setVisibility(View.VISIBLE);
                } else if (deviceId == rightDeviceId) {
                    this.mHandleDirectionStartTwo.setVisibility(View.VISIBLE);
                }
                Toast.makeText(this, getString(R.string.handle_quick_start_handle_setting), Toast.LENGTH_SHORT).show();
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        int deviceId = event.getDeviceId();
        int leftDeviceId = -1;
        int rightDeviceId = -1;
        if (this.mGameHandleService != null) {
            changeMAC();
            Context mContext2 = this.mGameHandleService.getApplicationContext();
            leftDeviceId = GameHandleService.getGamePadDeviceId(mContext2, this.leftAddreBEBig);
            rightDeviceId = GameHandleService.getGamePadDeviceId(mContext2, this.rightAddreBEBig);
        }
        Log.d("yuan", "deviceId -->" + deviceId + "  leftDeviceId --> " + leftDeviceId + "  rightDeviceId --> " + rightDeviceId);
        switch (keyCode) {
            case 96:
                if (deviceId != leftDeviceId) {
                    if (deviceId == rightDeviceId) {
                        this.mHandleDirectionBottomTwo.setVisibility(View.INVISIBLE);
                        break;
                    }
                } else {
                    this.mHandleDirectionBottomOne.setVisibility(View.INVISIBLE);
                    break;
                }
                break;
            case 97:
                if (deviceId != leftDeviceId) {
                    if (deviceId == rightDeviceId) {
                        this.mHandleDirectionRightTwo.setVisibility(View.INVISIBLE);
                        break;
                    }
                } else {
                    this.mHandleDirectionRightOne.setVisibility(View.INVISIBLE);
                    break;
                }
                break;
            case 99:
                if (deviceId != leftDeviceId) {
                    if (deviceId == rightDeviceId) {
                        this.mHandleDirectionLeftTwo.setVisibility(View.INVISIBLE);
                        break;
                    }
                } else {
                    this.mHandleDirectionLeftOne.setVisibility(View.INVISIBLE);
                    break;
                }
                break;
            case 100:
                if (deviceId != leftDeviceId) {
                    if (deviceId == rightDeviceId) {
                        this.mHandleDirectionUpTwo.setVisibility(View.INVISIBLE);
                        break;
                    }
                } else {
                    this.mHandleDirectionUpOne.setVisibility(View.INVISIBLE);
                    break;
                }
                break;
            case 102:
                if (deviceId != leftDeviceId) {
                    if (deviceId == rightDeviceId) {
                        this.mHandleUpperButtonTwo_1.setVisibility(View.INVISIBLE);
                        break;
                    }
                } else {
                    this.mHandleUpperButtonOne_1.setVisibility(View.INVISIBLE);
                    break;
                }
                break;
            case 103:
                if (deviceId != leftDeviceId) {
                    if (deviceId == rightDeviceId) {
                        this.mHandleBottomButtonTwo_1.setVisibility(View.INVISIBLE);
                        break;
                    }
                } else {
                    this.mHandleBottomButtonOne_1.setVisibility(View.INVISIBLE);
                    break;
                }
                break;
            case 104:
                if (deviceId != leftDeviceId) {
                    if (deviceId == rightDeviceId) {
                        this.mHandleUpperButtonTwo_2.setVisibility(View.INVISIBLE);
                        break;
                    }
                } else {
                    this.mHandleUpperButtonOne_2.setVisibility(View.INVISIBLE);
                    break;
                }
                break;
            case 105:
                if (deviceId != leftDeviceId) {
                    if (deviceId == rightDeviceId) {
                        this.mHandleBottomButtonTwo_2.setVisibility(View.INVISIBLE);
                        break;
                    }
                } else {
                    this.mHandleBottomButtonOne_2.setVisibility(View.INVISIBLE);
                    break;
                }
                break;
            case 108:
                if (deviceId != leftDeviceId) {
                    if (deviceId == rightDeviceId) {
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
