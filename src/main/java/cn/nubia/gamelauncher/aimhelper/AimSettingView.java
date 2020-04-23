package cn.nubia.gamelauncher.aimhelper;

import android.content.Context;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import cn.nubia.gamelauncher.R;

public class AimSettingView extends LinearLayout {
    /* access modifiers changed from: private */
    public static final String TAG = AimSettingView.class.getSimpleName();
    /* access modifiers changed from: private */
    public static int[] colorResArr = {R.id.white, R.id.red, R.id.yellow, R.id.green, R.id.blue};
    public static int[] colors = {-1, -3339749, -1711815, -16200683, -16722987};
    /* access modifiers changed from: private */
    public static int[] styleResArr = {R.id.style1, R.id.style2, R.id.style3, R.id.style4, R.id.style5};
    private OnClickListener mColorClickListener = new OnClickListener() {
        public void onClick(View v) {
            for (int i = 0; i < AimSettingView.colorResArr.length; i++) {
                ((SelectableImageView) AimSettingView.this.findViewById(AimSettingView.colorResArr[i])).setSelect(AimSettingView.colorResArr[i] == v.getId());
                if (AimSettingView.colorResArr[i] == v.getId()) {
                    Log.d(AimSettingView.TAG, "color:" + AimSettingView.colors[i]);
                    AimConfigs.getInstance(AimSettingView.this.getContext()).setColor(AimSettingView.this.mGameHelperController.getTopApplication(), AimSettingView.colors[i]);
                }
            }
            AimSettingView.this.mGameHelperController.refreshAimCenter();
        }
    };
    /* access modifiers changed from: private */
    public GameHelperController mGameHelperController;
    private OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            AimSettingView.this.onModeChange(checkedId == R.id.rb_auto);
        }
    };
    private RadioGroup mRadioGroup;
    private RadioButton mRbAuto;
    private RadioButton mRbManual;
    private SeekBar mSeekBar;
    private OnClickListener mStyleClickListener = new OnClickListener() {
        public void onClick(View view) {
            for (int i = 0; i < AimSettingView.styleResArr.length; i++) {
                ((SelectableImageView) AimSettingView.this.findViewById(AimSettingView.styleResArr[i])).setSelect(AimSettingView.styleResArr[i] == view.getId());
                if (AimSettingView.styleResArr[i] == view.getId()) {
                    AimConfigs.getInstance(AimSettingView.this.getContext()).setStyle(AimSettingView.this.mGameHelperController.getTopApplication(), i + 1);
                }
            }
            AimSettingView.this.mGameHelperController.refreshAimCenter();
        }
    };
    private TextView mTvTip;

    public AimSettingView(Context context) {
        super(context);
        init(context);
    }

    public AimSettingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AimSettingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.aim_setting_view, this);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        for (int id : styleResArr) {
            findViewById(id).setOnClickListener(this.mStyleClickListener);
        }
        for (int id2 : colorResArr) {
            findViewById(id2).setOnClickListener(this.mColorClickListener);
        }
        this.mSeekBar = (SeekBar) findViewById(R.id.seekbar);
        this.mSeekBar.setMax(100);
        if (VERSION.SDK_INT >= 26) {
            this.mSeekBar.setMin(40);
        }
        this.mSeekBar.setPadding(0, 0, 0, 0);
        this.mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    AimSettingView.this.onSizeChange(progress);
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        this.mRadioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        this.mRbAuto = (RadioButton) findViewById(R.id.rb_auto);
        this.mRbManual = (RadioButton) findViewById(R.id.rb_manual);
        this.mRadioGroup.setOnCheckedChangeListener(this.mOnCheckedChangeListener);
        this.mTvTip = (TextView) findViewById(R.id.tv_tip);
    }

    public void setGameHelperController(GameHelperController controller) {
        this.mGameHelperController = controller;
    }

    private void updateStyleChoiceUI() {
        int styleCode = AimConfigs.getInstance(getContext()).getStyle(this.mGameHelperController.getTopApplication());
        for (int i = 0; i < styleResArr.length; i++) {
            ((SelectableImageView) findViewById(styleResArr[i])).setSelect(i + 1 == styleCode);
        }
    }

    private void updateColorChoiceUI() {
        int color = AimConfigs.getInstance(getContext()).getColor(this.mGameHelperController.getTopApplication());
        for (int i = 0; i < colors.length; i++) {
            ((SelectableImageView) findViewById(colorResArr[i])).setSelect(color == colors[i]);
        }
    }

    /* access modifiers changed from: private */
    public void onSizeChange(int size) {
        Log.d(TAG, "onSizeChange size=" + size);
        AimConfigs.getInstance(getContext()).setSize(this.mGameHelperController.getTopApplication(), size);
        this.mGameHelperController.refreshAimCenter();
    }

    /* access modifiers changed from: private */
    public void onModeChange(boolean autoMode) {
        Log.d(TAG, "onModeChange auto=" + autoMode);
        this.mTvTip.setText(autoMode ? R.string.tip_auto : R.string.tip_manual);
        AimConfigs.getInstance(getContext()).setAuto(this.mGameHelperController.getTopApplication(), autoMode);
    }

    public void refreshUI() {
        AimConfigs configs = AimConfigs.getInstance(getContext());
        updateStyleChoiceUI();
        updateColorChoiceUI();
        this.mSeekBar.setProgress(configs.getSize(this.mGameHelperController.getTopApplication()));
        boolean isAutoMode = configs.isAuto(this.mGameHelperController.getTopApplication());
        this.mRadioGroup.setOnCheckedChangeListener(null);
        if (isAutoMode) {
            this.mRbAuto.setChecked(true);
        } else {
            this.mRbManual.setChecked(true);
        }
        this.mRadioGroup.setOnCheckedChangeListener(this.mOnCheckedChangeListener);
        this.mTvTip.setText(isAutoMode ? R.string.tip_auto : R.string.tip_manual);
    }
}
