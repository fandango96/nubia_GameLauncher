package cn.nubia.commonui.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import cn.nubia.commonui.R;
import cn.nubia.commonui.app.AlertDialog;
import cn.nubia.commonui.widget.NubiaLunarTimePickerView.OnTimeChangeListener;
import java.util.Arrays;

public class NubiaLunarTimePickerDialog extends AlertDialog {
    private static final int DEFAULT_END_YEAR = 2037;
    private static final int DEFAULT_START_YEAR = 1970;
    private boolean isCurrentMonthLeap;
    private final onTimeSetListener mCallBack;
    /* access modifiers changed from: private */
    public boolean mFlag;
    private int mHour;
    /* access modifiers changed from: private */
    public boolean mIsCN;
    private int mLunarDay;
    private String mLunarDayLabel;
    private TextView mLunarLabel;
    private int mLunarMonth;
    private String mLunarMonthLabel;
    private String[] mLunarStringArray;
    private View mLunarTimeDivider;
    private View mLunarTimeLayout;
    /* access modifiers changed from: private */
    public final NubiaLunarTimePickerView mLunarTimePickerView;
    private int mLunarYear;
    private String mLunarYearLabel;
    /* access modifiers changed from: private */
    public int mMin;
    /* access modifiers changed from: private */
    public String mMonthDay;
    /* access modifiers changed from: private */
    public NubiaSwitch mNubiaSwitch;
    private TimePickerOnClickListener mOnClickListener;
    private TimePickerOnTimeChangeListener mOnTimeChangeListener;
    private int mRetDay;
    private int mRetMonth;
    private int mRetYear;
    private int mSolarDay;
    private int mSolarMonth;
    /* access modifiers changed from: private */
    public String[] mSolarStringArray;
    private int mSolarYear;
    /* access modifiers changed from: private */
    public boolean mSwitchEnabled;
    /* access modifiers changed from: private */
    public String[] mUSStringArray;
    /* access modifiers changed from: private */
    public String mWeekDay;
    /* access modifiers changed from: private */
    public int mYYYY;
    private String mYearMonthDay;

    private class TimePickerOnClickListener implements OnClickListener {
        private TimePickerOnClickListener() {
        }

        public void onClick(DialogInterface dialog, int which) {
            if (which == -1) {
                NubiaLunarTimePickerDialog.this.tryNotifyTimeSet();
            } else if (which == -2) {
                dialog.cancel();
            }
        }
    }

    private class TimePickerOnTimeChangeListener implements OnTimeChangeListener {
        private TimePickerOnTimeChangeListener() {
        }

        public void onTimeChanged(NubiaLunarTimePickerView view, int year, int monthDay, int hourOfDay, int minute) {
            NubiaLunarTimePickerDialog.this.mMin = minute;
            NubiaLunarTimePickerDialog.this.adjustHourOfDay(hourOfDay);
            NubiaLunarTimePickerDialog.this.mYYYY = year;
            if (true == NubiaLunarTimePickerDialog.this.mSwitchEnabled) {
                NubiaLunarTimePickerDialog.this.updateLunarTitle();
            } else if (NubiaLunarTimePickerDialog.this.mIsCN) {
                NubiaLunarTimePickerDialog.this.mSolarStringArray = NubiaLunarTimePickerDialog.this.mLunarTimePickerView.getMonthDayView().getDisplayedValues();
                NubiaLunarTimePickerDialog.this.mMonthDay = NubiaLunarTimePickerDialog.this.mSolarStringArray[NubiaLunarTimePickerDialog.this.mLunarTimePickerView.getMonthDayView().getValue()];
                NubiaLunarTimePickerDialog.this.mFlag = LunarUtil.isSolarLeapYear(NubiaLunarTimePickerDialog.this.mYYYY);
                NubiaLunarTimePickerDialog.this.mWeekDay = LunarUtil.computeWeekday("" + NubiaLunarTimePickerDialog.this.mYYYY + LunarUtil.daysTommdd(NubiaLunarTimePickerDialog.this.mMonthDay, NubiaLunarTimePickerDialog.this.mSolarStringArray, NubiaLunarTimePickerDialog.this.mFlag));
                NubiaLunarTimePickerDialog.this.setSolarTitle();
            } else {
                NubiaLunarTimePickerDialog.this.mUSStringArray = NubiaLunarTimePickerDialog.this.mLunarTimePickerView.getMonthDayView().getDisplayedValues();
                NubiaLunarTimePickerDialog.this.mMonthDay = NubiaLunarTimePickerDialog.this.mUSStringArray[NubiaLunarTimePickerDialog.this.mLunarTimePickerView.getMonthDayView().getValue()];
                NubiaLunarTimePickerDialog.this.mFlag = LunarUtil.isSolarLeapYear(NubiaLunarTimePickerDialog.this.mYYYY);
                NubiaLunarTimePickerDialog.this.mWeekDay = LunarUtil.computeWeekday("" + NubiaLunarTimePickerDialog.this.mYYYY + LunarUtil.daysTommdd(NubiaLunarTimePickerDialog.this.mMonthDay, NubiaLunarTimePickerDialog.this.mUSStringArray, NubiaLunarTimePickerDialog.this.mFlag));
                NubiaLunarTimePickerDialog.this.mMonthDay = NubiaLunarTimePickerDialog.this.mMonthDay.substring(0, NubiaLunarTimePickerDialog.this.mMonthDay.length() - 2) + " " + NubiaLunarTimePickerDialog.this.mMonthDay.substring(NubiaLunarTimePickerDialog.this.mMonthDay.length() - 2, NubiaLunarTimePickerDialog.this.mMonthDay.length());
                NubiaLunarTimePickerDialog.this.mWeekDay = LunarUtil.WEEKDAYS_SHORT[Arrays.asList(LunarUtil.WEEKDAYS_LONG).indexOf(NubiaLunarTimePickerDialog.this.mWeekDay)];
                NubiaLunarTimePickerDialog.this.setSolarTitle();
            }
        }
    }

    public interface onTimeSetListener {
        void onTimeSet(NubiaLunarTimePickerView nubiaLunarTimePickerView, int i, int i2, int i3, int i4, int i5);
    }

    public NubiaLunarTimePickerDialog(Context context, onTimeSetListener callBack, int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minute) {
        this(context, 0, callBack, year, monthOfYear, dayOfMonth, hourOfDay, minute);
    }

    public final void updateTime(int hourOfDay, int minute) {
        this.mLunarTimePickerView.setCurrentHour(Integer.valueOf(hourOfDay));
        this.mLunarTimePickerView.setCurrentMinute(Integer.valueOf(minute));
    }

    public NubiaLunarTimePickerDialog(Context context, int theme, onTimeSetListener callBack, int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minute) {
        Object valueOf;
        Object valueOf2;
        super(context, theme);
        this.mSwitchEnabled = false;
        this.isCurrentMonthLeap = false;
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        Context themeContext = getContext();
        if (context.getResources().getConfiguration().locale.getLanguage().endsWith("zh")) {
            this.mIsCN = true;
        } else {
            this.mIsCN = false;
        }
        if (this.mOnClickListener == null) {
            this.mOnClickListener = new TimePickerOnClickListener();
        }
        setButton(-1, themeContext.getText(17039379), (OnClickListener) this.mOnClickListener);
        setButton(-2, themeContext.getText(17039360), (OnClickListener) this.mOnClickListener);
        View view = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.nubia_lunar_time_picker_dialog, null);
        setView(view);
        this.mCallBack = callBack;
        this.mLunarTimePickerView = (NubiaLunarTimePickerView) view.findViewById(R.id.nubia_lunar_time_picker_view);
        this.mLunarTimePickerView.setCurrentHour(Integer.valueOf(hourOfDay));
        this.mLunarTimePickerView.setCurrentMinute(Integer.valueOf(minute));
        if (this.mOnTimeChangeListener == null) {
            this.mOnTimeChangeListener = new TimePickerOnTimeChangeListener();
        }
        this.mLunarTimePickerView.setOnTimeChangedListener(this.mOnTimeChangeListener);
        this.mLunarLabel = (TextView) view.findViewById(R.id.nubia_lunar_textview);
        this.mLunarLabel.setText(R.string.nubia_lunar);
        getTextRes();
        this.mNubiaSwitch = (NubiaSwitch) view.findViewById(R.id.nubia_switch_button);
        this.mLunarTimeLayout = view.findViewById(R.id.nubia_lunar_time_layout);
        this.mLunarTimeDivider = view.findViewById(R.id.nubia_lunar_date_picker_dialog_divider);
        if (!this.mIsCN) {
            setLunarLayoutVisiable(false);
        }
        this.mHour = hourOfDay;
        this.mMin = minute;
        int monthOfYear2 = monthOfYear + 1;
        StringBuilder append = new StringBuilder().append("").append(year);
        if (monthOfYear2 < 10) {
            valueOf = "0" + monthOfYear2;
        } else {
            valueOf = Integer.valueOf(monthOfYear2);
        }
        StringBuilder append2 = append.append(valueOf);
        if (dayOfMonth < 10) {
            valueOf2 = "0" + dayOfMonth;
        } else {
            valueOf2 = Integer.valueOf(dayOfMonth);
        }
        initData(append2.append(valueOf2).toString());
        initTitle();
        setupNubiaSwitch();
        this.mLunarTimeLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                NubiaLunarTimePickerDialog.this.mNubiaSwitch.toggle();
            }
        });
        this.mLunarTimePickerView.update(this.mYYYY);
        if (this.mIsCN) {
            this.mSwitchEnabled = getSwitchStateFromSP();
            if (this.mSwitchEnabled) {
                this.mNubiaSwitch.toggle();
            }
        }
    }

    public void setLunarLayoutVisiable(boolean visiable) {
        if (visiable) {
            this.mLunarTimeLayout.setVisibility(0);
            if (this.mLunarTimeDivider != null) {
                this.mLunarTimeDivider.setVisibility(0);
                return;
            }
            return;
        }
        this.mLunarTimeLayout.setVisibility(8);
        if (this.mLunarTimeDivider != null) {
            this.mLunarTimeDivider.setVisibility(8);
        }
    }

    private void initTitle() {
        if (this.mIsCN) {
            setSolarTitle();
            return;
        }
        this.mMonthDay = new StringBuilder(this.mMonthDay).insert(this.mMonthDay.length() - 2, " ").toString();
        this.mWeekDay = LunarUtil.WEEKDAYS_SHORT[Arrays.asList(LunarUtil.WEEKDAYS_LONG).indexOf(this.mWeekDay)];
        setSolarTitle();
    }

    private void initData(String yearMonthDay) {
        int yyyy = Integer.parseInt(yearMonthDay.substring(0, 4));
        int mm = Integer.parseInt(yearMonthDay.substring(4, 6));
        int dd = Integer.parseInt(yearMonthDay.substring(6, 8));
        this.mYYYY = yyyy;
        this.mYearMonthDay = yearMonthDay;
        this.mFlag = LunarUtil.isSolarLeapYear(this.mYYYY);
        initDisplayedValues();
        int sumDay = LunarUtil.solarSumDays(mm, dd, this.mYYYY);
        LunarUtil.clearSum();
        this.mLunarTimePickerView.getMonthDayView().setValue(sumDay - 1);
        adjustHourOfDay(this.mHour);
        this.mWeekDay = LunarUtil.computeWeekday(this.mYearMonthDay);
        if (this.mIsCN) {
            this.mMonthDay = this.mSolarStringArray[this.mLunarTimePickerView.getMonthDayView().getValue()];
        } else {
            this.mMonthDay = this.mUSStringArray[this.mLunarTimePickerView.getMonthDayView().getValue()];
        }
    }

    private void initDisplayedValues() {
        if (this.mIsCN) {
            this.mLunarLabel.setVisibility(0);
            this.mSolarStringArray = LunarUtil.timePickerSolMonthDay(this.mYYYY);
            updateTimePickerArray(this.mSolarStringArray);
            return;
        }
        this.mLunarLabel.setVisibility(8);
        this.mUSStringArray = LunarUtil.timePickerUSMonthDay(this.mYYYY);
        updateTimePickerArray(this.mUSStringArray);
    }

    private String getAmPm() {
        if (true == this.mLunarTimePickerView.getmIs24HourView()) {
            return "";
        }
        String string = getContext().getResources().getString(R.string.nubia_time_am_upper);
        String pmUpper = getContext().getResources().getString(R.string.nubia_time_pm_upper);
        if (!this.mLunarTimePickerView.getmIsAm()) {
            if (this.mIsCN) {
                pmUpper = LunarUtil.pm;
            }
            return pmUpper;
        } else if (this.mIsCN) {
            return LunarUtil.am;
        } else {
            return string;
        }
    }

    /* access modifiers changed from: private */
    public void tryNotifyTimeSet() {
        if (this.mRetYear > DEFAULT_END_YEAR) {
            this.mRetYear = DEFAULT_END_YEAR;
        } else if (this.mRetYear < DEFAULT_START_YEAR) {
            this.mRetYear = DEFAULT_START_YEAR;
        }
        saveSwitchState();
        if (this.mCallBack != null) {
            this.mLunarTimePickerView.clearFocus();
            setRetDateTime();
            this.mCallBack.onTimeSet(this.mLunarTimePickerView, this.mRetYear, this.mRetMonth, this.mRetDay, this.mLunarTimePickerView.getCurrentHour(), this.mLunarTimePickerView.getCurrentMinute());
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.mLunarTimePickerView.onRestoreInstanceState(savedInstanceState.getParcelable("PICKER"));
    }

    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putParcelable("PICKER", this.mLunarTimePickerView.onSaveInstanceState());
        return state;
    }

    /* access modifiers changed from: private */
    public void adjustHourOfDay(int hourOfDay) {
        if (!this.mLunarTimePickerView.getmIs24HourView()) {
            if (hourOfDay > 12) {
                hourOfDay -= 12;
            }
            if (hourOfDay == 0) {
                hourOfDay = 12;
            }
        }
        this.mHour = hourOfDay;
    }

    private void resolveLunarDate(String lunarDate) {
        if (lunarDate.length() == 8) {
            this.mLunarYear = Integer.parseInt(lunarDate.substring(0, 4));
            this.mLunarMonth = Integer.parseInt(lunarDate.substring(4, 6));
            this.mLunarDay = Integer.parseInt(lunarDate.substring(6, 8));
            return;
        }
        this.mLunarYear = Integer.parseInt(lunarDate.substring(0, 4));
        this.mLunarMonth = Integer.parseInt(lunarDate.substring(4, 7)) + 20;
        this.mLunarDay = Integer.parseInt(lunarDate.substring(7, 9));
    }

    private void resolveSolarDate(String solarDate) {
        this.mSolarYear = Integer.parseInt(solarDate.substring(0, 4));
        this.mSolarMonth = Integer.parseInt(solarDate.substring(4, 6));
        this.mSolarDay = Integer.parseInt(solarDate.substring(6, 8));
    }

    /* access modifiers changed from: private */
    public void updateLunar() {
        adjustYearSolarToLunar();
        boolean isLeapMonth = false;
        if (this.mLunarMonth > 20) {
            this.mLunarMonth -= 20;
            isLeapMonth = true;
        }
        this.mLunarStringArray = LunarUtil.timePickerLunMonthDay(this.mYYYY);
        updateTimePickerArray(this.mLunarStringArray);
        int day = 0;
        int leapMonth = LunarUtil.getLeapMonth(this.mYYYY);
        String mmdd_lun = (isLeapMonth ? LunarUtil.LEAP : "") + LunarUtil.numConvert(this.mLunarMonth, 1) + LunarUtil.MONTH + LunarUtil.numConvert(this.mLunarDay, 2);
        int i = 0;
        while (true) {
            if (i >= this.mLunarStringArray.length) {
                break;
            } else if (mmdd_lun.equals(this.mLunarStringArray[i])) {
                day = i + 1;
                break;
            } else {
                i++;
            }
        }
        this.mLunarTimePickerView.update(this.mYYYY);
        this.mLunarTimePickerView.getMonthDayView().setValue(day - 1);
    }

    private void adjustYearSolarToLunar() {
        this.mMonthDay = this.mSolarStringArray[this.mLunarTimePickerView.getMonthDayView().getValue()];
        this.mFlag = LunarUtil.isSolarLeapYear(this.mYYYY);
        try {
            resolveLunarDate(LunarUtil.solarToLunar("" + this.mYYYY + LunarUtil.daysTommdd(this.mMonthDay, this.mSolarStringArray, this.mFlag)));
        } catch (Exception e) {
        }
        if (this.mYYYY != this.mLunarYear) {
            this.mYYYY = this.mLunarYear;
        }
    }

    /* access modifiers changed from: private */
    public void updateLunarTitle() {
        this.mLunarStringArray = this.mLunarTimePickerView.getMonthDayView().getDisplayedValues();
        this.mMonthDay = this.mLunarStringArray[this.mLunarTimePickerView.getMonthDayView().getValue()];
        String mmdd = LunarUtil.daysToLunmmdd(this.mMonthDay, this.mLunarStringArray, this.mYYYY);
        boolean isLeapMonth = false;
        if (mmdd.length() == 5) {
            isLeapMonth = true;
            mmdd = mmdd.substring(1, 5);
        }
        try {
            this.mWeekDay = LunarUtil.computeWeekday(LunarUtil.lunarToSolar("" + this.mYYYY + mmdd, isLeapMonth));
        } catch (Exception e) {
        }
        adjustHourOfDay(this.mHour);
        setLunarTitle();
    }

    private void setLunarTitle() {
        getAlertController().setTitle(getContext().getResources().getString(R.string.nubia_time_title, new Object[]{Integer.valueOf(this.mYYYY), this.mMonthDay, this.mWeekDay, getAmPm(), setTwoDigital(this.mHour), setTwoDigital(this.mMin)}));
    }

    /* access modifiers changed from: private */
    public void updateSolar() {
        adjustYearLunarToSolar();
        this.mSolarStringArray = LunarUtil.timePickerSolMonthDay(this.mYYYY);
        updateTimePickerArray(this.mSolarStringArray);
        int day = 0;
        String mmdd_sol = setTwoDigital(this.mSolarMonth) + LunarUtil.MONTH + setTwoDigital(this.mSolarDay) + LunarUtil.DAY;
        int i = 0;
        while (true) {
            if (i >= this.mSolarStringArray.length) {
                break;
            } else if (mmdd_sol.equals(this.mSolarStringArray[i])) {
                day = i + 1;
                break;
            } else {
                i++;
            }
        }
        this.mLunarTimePickerView.update(this.mYYYY);
        this.mLunarTimePickerView.getMonthDayView().setValue(day - 1);
    }

    private void adjustYearLunarToSolar() {
        this.mMonthDay = this.mLunarStringArray[this.mLunarTimePickerView.getMonthDayView().getValue()];
        String mmdd = LunarUtil.daysToLunmmdd(this.mMonthDay, this.mLunarStringArray, this.mYYYY);
        boolean isLeapMonth = false;
        if (mmdd.length() == 5) {
            isLeapMonth = true;
            mmdd = mmdd.substring(1, 5);
        }
        try {
            resolveSolarDate(LunarUtil.lunarToSolar("" + this.mYYYY + mmdd, isLeapMonth));
        } catch (Exception e) {
        }
        if (this.mYYYY != this.mSolarYear) {
            this.mYYYY = this.mSolarYear;
        }
    }

    /* access modifiers changed from: private */
    public void updateSolarTitle() {
        this.mSolarStringArray = this.mLunarTimePickerView.getMonthDayView().getDisplayedValues();
        this.mMonthDay = this.mSolarStringArray[this.mLunarTimePickerView.getMonthDayView().getValue()];
        this.mFlag = LunarUtil.isSolarLeapYear(this.mYYYY);
        this.mWeekDay = LunarUtil.computeWeekday("" + this.mYYYY + LunarUtil.daysTommdd(this.mMonthDay, this.mSolarStringArray, this.mFlag));
        adjustHourOfDay(this.mHour);
        setSolarTitle();
    }

    /* access modifiers changed from: private */
    public void setSolarTitle() {
        getAlertController().setTitle(getContext().getResources().getString(R.string.nubia_time_title, new Object[]{Integer.valueOf(this.mYYYY), this.mMonthDay, this.mWeekDay, getAmPm(), setTwoDigital(this.mHour), setTwoDigital(this.mMin)}));
    }

    private String setTwoDigital(int digital) {
        return (digital < 10 ? "0" : "") + digital;
    }

    private void setupNubiaSwitch() {
        if (this.mNubiaSwitch != null) {
            this.mNubiaSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    NubiaLunarTimePickerDialog.this.mSwitchEnabled = isChecked;
                    if (true == NubiaLunarTimePickerDialog.this.mSwitchEnabled) {
                        try {
                            NubiaLunarTimePickerDialog.this.mLunarTimePickerView.isLunarMode = true;
                            NubiaLunarTimePickerDialog.this.updateLunar();
                            NubiaLunarTimePickerDialog.this.updateLunarTitle();
                        } catch (Exception e) {
                        }
                    } else if (!NubiaLunarTimePickerDialog.this.mSwitchEnabled) {
                        NubiaLunarTimePickerDialog.this.mLunarTimePickerView.isLunarMode = false;
                        NubiaLunarTimePickerDialog.this.updateSolar();
                        NubiaLunarTimePickerDialog.this.updateSolarTitle();
                    }
                    NubiaLunarTimePickerDialog.this.mLunarTimePickerView.noticeTimeChange();
                }
            });
        }
    }

    public boolean getSwitchState() {
        return this.mSwitchEnabled;
    }

    public boolean getCurrentMonthIsLeap() {
        return this.isCurrentMonthLeap;
    }

    public int getCurrentAmPm() {
        if (this.mLunarTimePickerView.getmIs24HourView()) {
            int hour = this.mLunarTimePickerView.getCurrentHour();
            if (hour == 0) {
                hour = 24;
            }
            if (hour > 12) {
                return 1;
            }
            return 0;
        } else if (this.mLunarTimePickerView.getmIsAm()) {
            return 0;
        } else {
            return 1;
        }
    }

    public String getDateString(boolean isLuanrMode) {
        String mmdd;
        String str = "";
        int hh = this.mLunarTimePickerView.getCurrentHour();
        if (hh > 12) {
            hh -= 12;
        }
        String hhmm = setTwoDigital(hh) + setTwoDigital(this.mLunarTimePickerView.getCurrentMinute());
        if (isLuanrMode) {
            this.mMonthDay = this.mLunarStringArray[this.mLunarTimePickerView.getMonthDayView().getValue()];
            String mmdd2 = LunarUtil.daysToLunmmdd(this.mMonthDay, this.mLunarStringArray, this.mYYYY);
            if (this.mMonthDay.startsWith(LunarUtil.LEAP)) {
                this.isCurrentMonthLeap = true;
                mmdd = mmdd2.substring(1, mmdd2.length());
            } else {
                this.isCurrentMonthLeap = false;
                mmdd = mmdd2.substring(0, mmdd2.length());
            }
        } else if (this.mIsCN) {
            this.mMonthDay = this.mSolarStringArray[this.mLunarTimePickerView.getMonthDayView().getValue()];
            this.mFlag = LunarUtil.isSolarLeapYear(this.mYYYY);
            mmdd = LunarUtil.daysTommdd(this.mMonthDay, this.mSolarStringArray, this.mFlag);
        } else {
            this.mMonthDay = this.mUSStringArray[this.mLunarTimePickerView.getMonthDayView().getValue()];
            this.mFlag = LunarUtil.isSolarLeapYear(this.mYYYY);
            mmdd = LunarUtil.daysTommdd(this.mMonthDay, this.mUSStringArray, this.mFlag);
        }
        return "" + this.mYYYY + mmdd + hhmm;
    }

    private void setRetDateTime() {
        String mmdd;
        String mmdd2;
        String str = "";
        if (this.mSwitchEnabled) {
            this.mMonthDay = this.mLunarStringArray[this.mLunarTimePickerView.getMonthDayView().getValue()];
            String mmdd3 = LunarUtil.daysToLunmmdd(this.mMonthDay, this.mLunarStringArray, this.mYYYY);
            if (true == this.mMonthDay.startsWith(LunarUtil.LEAP)) {
                this.isCurrentMonthLeap = true;
                mmdd2 = mmdd3.substring(1, mmdd3.length());
            } else {
                this.isCurrentMonthLeap = false;
                mmdd2 = mmdd3.substring(0, mmdd3.length());
            }
            try {
                resolveSolarDate(LunarUtil.lunarToSolar("" + this.mYYYY + mmdd2, this.isCurrentMonthLeap));
            } catch (Exception e) {
            }
        } else {
            if (this.mIsCN) {
                this.mMonthDay = this.mSolarStringArray[this.mLunarTimePickerView.getMonthDayView().getValue()];
                this.mFlag = LunarUtil.isSolarLeapYear(this.mYYYY);
                mmdd = LunarUtil.daysTommdd(this.mMonthDay, this.mSolarStringArray, this.mFlag);
            } else {
                this.mMonthDay = this.mUSStringArray[this.mLunarTimePickerView.getMonthDayView().getValue()];
                this.mFlag = LunarUtil.isSolarLeapYear(this.mYYYY);
                mmdd = LunarUtil.daysTommdd(this.mMonthDay, this.mUSStringArray, this.mFlag);
            }
            resolveSolarDate("" + this.mYYYY + mmdd);
        }
        this.mRetYear = this.mSolarYear;
        this.mRetMonth = this.mSolarMonth - 1;
        this.mRetDay = this.mSolarDay;
    }

    public boolean getSwitchStateFromSP() {
        return getContext().getSharedPreferences("lunarTimePickerSwitch", 0).getBoolean("switchState", false);
    }

    private void saveSwitchState() {
        if (this.mIsCN) {
            Editor editor = getContext().getSharedPreferences("lunarTimePickerSwitch", 0).edit();
            editor.putBoolean("switchState", this.mSwitchEnabled);
            editor.apply();
        }
    }

    private void getTextRes() {
        LunarUtil.getTextRes(getContext());
        this.mLunarYearLabel = LunarUtil.YEAR;
        this.mLunarMonthLabel = LunarUtil.MONTH;
        this.mLunarDayLabel = LunarUtil.DAY;
    }

    private void updateTimePickerArray(String[] array) {
        this.mLunarTimePickerView.getMonthDayView().setDisplayedValues(null);
        this.mLunarTimePickerView.getMonthDayView().setMinValue(0);
        this.mLunarTimePickerView.getMonthDayView().setMaxValue(array.length - 1);
        this.mLunarTimePickerView.getMonthDayView().setDisplayedValues(array);
    }
}
