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
import cn.nubia.commonui.widget.DatePickerView.OnDateChangeListener;
import java.util.ArrayList;

public class DatePickerDialog extends AlertDialog {
    public static int DEFAULT_END_YEAR = 2037;
    public static int DEFAULT_START_YEAR = 1970;
    /* access modifiers changed from: private */
    public static boolean mIsCN = false;
    private boolean isCurrentMonthLeap;
    private final OnDateSetListener mCallBack;
    /* access modifiers changed from: private */
    public int mCurrday;
    /* access modifiers changed from: private */
    public int mCurrmonth;
    /* access modifiers changed from: private */
    public int mCurryear;
    /* access modifiers changed from: private */
    public final DatePickerView mDatePicker;
    /* access modifiers changed from: private */
    public String mFormatdateString;
    private int mLoadDayVlaue;
    private int mLoadMonthVlaue;
    private int mLoadYearVlaue;
    private View mLunarDateDivider;
    private View mLunarDateLayout;
    private int mLunarDay;
    private String mLunarDayLabel;
    private TextView mLunarLabel;
    private int mLunarMonth;
    private String mLunarMonthLabel;
    private int mLunarYear;
    private String mLunarYearLabel;
    /* access modifiers changed from: private */
    public NubiaSwitch mNubiaSwitch;
    private DatePickerOnClickListener mOnClickListener;
    /* access modifiers changed from: private */
    public DatePickerOnDateChangeListener mOnDateChangeListener;
    /* access modifiers changed from: private */
    public int mSolarDay;
    /* access modifiers changed from: private */
    public int mSolarMonth;
    /* access modifiers changed from: private */
    public int mSolarYear;
    /* access modifiers changed from: private */
    public boolean mSwtichEnabled;

    private class DatePickerOnClickListener implements OnClickListener {
        private DatePickerOnClickListener() {
        }

        public void onClick(DialogInterface dialog, int which) {
            if (which == -1) {
                DatePickerDialog.this.saveSwitchState();
                DatePickerDialog.this.tryNotifyDateSet();
            } else if (which == -2) {
                dialog.dismiss();
            }
        }
    }

    private class DatePickerOnDateChangeListener implements OnDateChangeListener {
        private DatePickerOnDateChangeListener() {
        }

        public void onDateChanged(DatePickerView view, int year, int monthOfYear, int dayOfMonth) {
            DatePickerDialog.this.mCurryear = DatePickerDialog.this.mDatePicker.getYear();
            DatePickerDialog.this.mCurrmonth = DatePickerDialog.this.mDatePicker.getMonth();
            DatePickerDialog.this.mCurrday = DatePickerDialog.this.mDatePicker.getDayOfMonth();
            if (DatePickerDialog.this.mSwtichEnabled) {
                DatePickerDialog.this.mDatePicker.isLunarMode = true;
                DatePickerDialog.this.setLunarTitleByValues(DatePickerDialog.this.mCurryear, DatePickerDialog.this.mCurrmonth + 1, DatePickerDialog.this.mCurrday + 1);
                DatePickerDialog.this.mDatePicker.setDayLabelVisible(false);
            } else if (DatePickerDialog.mIsCN) {
                DatePickerDialog.this.mDatePicker.isLunarMode = false;
                DatePickerDialog.this.setSolarTitle(DatePickerDialog.this.mCurryear, DatePickerDialog.this.mCurrmonth, DatePickerDialog.this.mCurrday);
                DatePickerDialog.this.mDatePicker.setDayLabelVisible(true);
            } else {
                DatePickerDialog.this.setSolarTitle(DatePickerDialog.this.mCurryear, DatePickerDialog.this.mCurrmonth, DatePickerDialog.this.mCurrday);
            }
        }
    }

    public interface OnDateSetListener {
        void onDateSet(DatePickerView datePickerView, int i, int i2, int i3);
    }

    public DatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        this(context, 0, callBack, year, monthOfYear, dayOfMonth);
    }

    public DatePickerDialog(Context context, int theme, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, theme);
        this.mSwtichEnabled = false;
        this.isCurrentMonthLeap = false;
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        this.mCallBack = callBack;
        Context themeContext = getContext();
        getCurrentLanguage(context);
        View view = ((LayoutInflater) themeContext.getSystemService("layout_inflater")).inflate(R.layout.nubia_date_picker_dialog, null);
        setView(view);
        this.mLunarDateLayout = view.findViewById(R.id.nubia_lunar_date_layout);
        this.mLunarDateDivider = view.findViewById(R.id.nubia_date_picker_dialog_divider);
        this.mDatePicker = (DatePickerView) view.findViewById(R.id.nubia_date_picker_view);
        this.mLunarLabel = (TextView) view.findViewById(R.id.nubia_lunar_textview);
        this.mLunarLabel.setText(R.string.nubia_lunar);
        this.mLunarLabel.setVisibility(0);
        this.mNubiaSwitch = (NubiaSwitch) view.findViewById(R.id.nubia_switch_button);
        if (!mIsCN) {
            setLunarLayoutVisiable(false);
        }
        this.mLunarDateLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                DatePickerDialog.this.mNubiaSwitch.toggle();
            }
        });
        if (this.mOnDateChangeListener == null) {
            this.mOnDateChangeListener = new DatePickerOnDateChangeListener();
        }
        getTextRes();
        setupNubiaSwitch();
        this.mLoadYearVlaue = year;
        this.mLoadMonthVlaue = monthOfYear + 1;
        this.mLoadDayVlaue = dayOfMonth;
        setup();
        if (this.mOnClickListener == null) {
            this.mOnClickListener = new DatePickerOnClickListener();
        }
        setButton(-1, themeContext.getText(17039379), (OnClickListener) this.mOnClickListener);
        setButton(-2, themeContext.getText(17039360), (OnClickListener) this.mOnClickListener);
        if (mIsCN) {
            this.mSwtichEnabled = getSwitchStateFromSP();
            if (this.mSwtichEnabled) {
                this.mNubiaSwitch.toggle();
            }
        }
    }

    private void getCurrentLanguage(Context context) {
        if (context.getResources().getConfiguration().locale.getLanguage().endsWith("zh")) {
            mIsCN = true;
        } else {
            mIsCN = false;
        }
    }

    public DatePickerView getDatePicker() {
        return this.mDatePicker;
    }

    private void setup() {
        if (mIsCN && !this.mSwtichEnabled) {
            setSolarTitle(this.mLoadYearVlaue, this.mLoadMonthVlaue, this.mLoadDayVlaue);
            this.mDatePicker.update(this.mLoadYearVlaue, this.mLoadMonthVlaue - 1, this.mLoadDayVlaue, this.mOnDateChangeListener);
        } else if (!mIsCN) {
            setSolarTitle(this.mLoadYearVlaue, this.mLoadMonthVlaue, this.mLoadDayVlaue);
            this.mDatePicker.update(this.mLoadYearVlaue, this.mLoadMonthVlaue - 1, this.mLoadDayVlaue, this.mOnDateChangeListener);
        }
    }

    public void updateDate(int year, int monthOfYear, int dayOfMonth) {
        this.mDatePicker.updateDate(year, monthOfYear, dayOfMonth);
    }

    /* access modifiers changed from: private */
    public void tryNotifyDateSet() {
        boolean hasLeapMonth;
        if (this.mCallBack != null) {
            this.mDatePicker.clearFocus();
            if (this.mSwtichEnabled) {
                this.mCurryear = this.mDatePicker.getYear();
                this.mCurrmonth = this.mDatePicker.getMonth();
                this.mCurrday = this.mDatePicker.getDayOfMonth();
                try {
                    int leapMonth = LunarUtil.getLeapMonth(this.mCurryear);
                    if (leapMonth == 0) {
                        hasLeapMonth = false;
                    } else {
                        hasLeapMonth = true;
                    }
                    boolean isCurMonthLeap = false;
                    if (this.mDatePicker.getMonthWheelView().getDisplayedValues()[this.mCurrmonth].startsWith(LunarUtil.LEAP)) {
                        isCurMonthLeap = true;
                    }
                    if (!hasLeapMonth) {
                        this.mCurrmonth++;
                    } else if (this.mCurrmonth < leapMonth) {
                        this.mCurrmonth++;
                    }
                    this.mFormatdateString = LunarUtil.formatDate(this.mCurryear, this.mCurrmonth, this.mCurrday + 1);
                    resolveSolarDate(LunarUtil.lunarToSolar(this.mFormatdateString, isCurMonthLeap));
                } catch (Exception e) {
                }
                this.mCallBack.onDateSet(this.mDatePicker, this.mSolarYear, this.mSolarMonth - 1, this.mSolarDay);
                return;
            }
            this.mCallBack.onDateSet(this.mDatePicker, this.mDatePicker.getYear(), this.mDatePicker.getMonth() - 1, this.mDatePicker.getDayOfMonth());
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
    }

    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putParcelable("PICKER", this.mDatePicker.onSaveInstanceState());
        return state;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.mDatePicker.onRestoreInstanceState(savedInstanceState.getParcelable("PICKER"));
    }

    private void setupNubiaSwitch() {
        if (this.mNubiaSwitch != null) {
            this.mNubiaSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    boolean hasLeapMonth = false;
                    DatePickerDialog.this.mSwtichEnabled = isChecked;
                    DatePickerDialog.this.mCurryear = DatePickerDialog.this.mDatePicker.getYear();
                    DatePickerDialog.this.mCurrmonth = DatePickerDialog.this.mDatePicker.getMonth();
                    DatePickerDialog.this.mCurrday = DatePickerDialog.this.mDatePicker.getDayOfMonth();
                    if (true == DatePickerDialog.this.mSwtichEnabled) {
                        DatePickerDialog.this.mDatePicker.isLunarMode = true;
                        try {
                            DatePickerDialog.this.mFormatdateString = LunarUtil.formatDate(DatePickerDialog.this.mCurryear, DatePickerDialog.this.mCurrmonth, DatePickerDialog.this.mCurrday);
                            DatePickerDialog.this.resolveLunarDate(LunarUtil.solarToLunar(DatePickerDialog.this.mFormatdateString));
                        } catch (Exception e) {
                        }
                        DatePickerDialog.this.updateLunarBySolar();
                        DatePickerDialog.this.mDatePicker.setDayLabelVisible(false);
                        DatePickerDialog.this.mDatePicker.getMonthWheelView().invalidate();
                        DatePickerDialog.this.mDatePicker.getDayWheelView().invalidate();
                        return;
                    }
                    DatePickerDialog.this.mDatePicker.isLunarMode = false;
                    try {
                        int leapMonth = LunarUtil.getLeapMonth(DatePickerDialog.this.mCurryear);
                        if (leapMonth != 0) {
                            hasLeapMonth = true;
                        }
                        boolean isCurMonthLeap = false;
                        if (true == DatePickerDialog.this.mDatePicker.getMonthWheelView().getDisplayedValues()[DatePickerDialog.this.mCurrmonth].startsWith(LunarUtil.LEAP)) {
                            isCurMonthLeap = true;
                        }
                        if (!hasLeapMonth) {
                            DatePickerDialog.this.mCurrmonth = DatePickerDialog.this.mCurrmonth + 1;
                        } else if (DatePickerDialog.this.mCurrmonth < leapMonth) {
                            DatePickerDialog.this.mCurrmonth = DatePickerDialog.this.mCurrmonth + 1;
                        }
                        DatePickerDialog.this.mFormatdateString = LunarUtil.formatDate(DatePickerDialog.this.mCurryear, DatePickerDialog.this.mCurrmonth, DatePickerDialog.this.mCurrday + 1);
                        DatePickerDialog.this.resolveSolarDate(LunarUtil.lunarToSolar(DatePickerDialog.this.mFormatdateString, isCurMonthLeap));
                    } catch (Exception e2) {
                    }
                    DatePickerDialog.this.setSolarTitle(DatePickerDialog.this.mSolarYear, DatePickerDialog.this.mSolarMonth, DatePickerDialog.this.mSolarDay);
                    DatePickerDialog.this.mDatePicker.getYearWheelView().setDisplayedValues(null);
                    DatePickerDialog.this.mDatePicker.getMonthWheelView().setDisplayedValues(null);
                    DatePickerDialog.this.mDatePicker.getDayWheelView().setDisplayedValues(null);
                    if (DatePickerDialog.this.mSolarYear > DatePickerDialog.DEFAULT_END_YEAR) {
                        DatePickerDialog.this.mSolarYear = DatePickerDialog.DEFAULT_END_YEAR;
                    } else if (DatePickerDialog.this.mSolarYear < DatePickerDialog.DEFAULT_START_YEAR) {
                        DatePickerDialog.this.mSolarYear = DatePickerDialog.DEFAULT_START_YEAR;
                    }
                    DatePickerDialog.this.mDatePicker.update(DatePickerDialog.this.mSolarYear, DatePickerDialog.this.mSolarMonth - 1, DatePickerDialog.this.mSolarDay, DatePickerDialog.this.mOnDateChangeListener);
                    DatePickerDialog.this.mDatePicker.setDayLabelVisible(true);
                    DatePickerDialog.this.mDatePicker.getMonthWheelView().invalidate();
                    DatePickerDialog.this.mDatePicker.getDayWheelView().invalidate();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void updateLunarBySolar() {
        boolean curMonthIsLeap = false;
        if (this.mLunarYear < DEFAULT_START_YEAR) {
            this.mCurryear = DEFAULT_START_YEAR;
        } else if (this.mLunarYear > DEFAULT_END_YEAR) {
            this.mCurryear = DEFAULT_END_YEAR;
        } else {
            this.mCurryear = this.mLunarYear;
        }
        this.mCurrmonth = this.mLunarMonth;
        this.mCurrday = this.mLunarDay;
        if (this.mCurrmonth > 20) {
            this.mCurrmonth -= 20;
            curMonthIsLeap = true;
        }
        this.mDatePicker.getYearWheelView().setDisplayedValues(null);
        String[] months = LunarUtil.solarMonthTolunarMonth(this.mCurryear);
        this.mDatePicker.getMonthWheelView().setDisplayedValues(null);
        this.mDatePicker.getMonthWheelView().setMinValue(0);
        this.mDatePicker.getMonthWheelView().setMaxValue(months.length - 1);
        this.mDatePicker.getMonthWheelView().setDisplayedValues(months);
        String[] days = LunarUtil.solarDayTolunarDay(this.mCurryear, this.mCurrmonth, curMonthIsLeap);
        this.mDatePicker.getDayWheelView().setDisplayedValues(null);
        this.mDatePicker.getDayWheelView().setMinValue(0);
        this.mDatePicker.getDayWheelView().setMaxValue(days.length - 1);
        this.mDatePicker.getDayWheelView().setDisplayedValues(days);
        this.mDatePicker.update(this.mCurryear, this.mLunarMonth, this.mCurrday, this.mOnDateChangeListener);
        setLunarTitleByLunarMonth(this.mLunarYear, this.mCurrmonth, this.mLunarDay, curMonthIsLeap);
    }

    /* access modifiers changed from: private */
    public void setLunarTitleByValues(int year, int month, int day) {
        String monthString;
        String str = "";
        boolean isLeap = false;
        int leapMonth = LunarUtil.getLeapMonth(year);
        if (leapMonth > 0) {
            int leapMonth2 = leapMonth + 1;
            if (month == leapMonth2) {
                monthString = LunarUtil.LEAP + LunarUtil.numConvert(month - 1, 1);
                month--;
                isLeap = true;
            } else if (month > leapMonth2) {
                monthString = LunarUtil.numConvert(month - 1, 1);
                month--;
            } else {
                monthString = LunarUtil.numConvert(month, 1);
            }
        } else {
            monthString = LunarUtil.numConvert(month, 1);
        }
        try {
            this.mFormatdateString = LunarUtil.lunarToSolar(LunarUtil.formatDate(year, month, day), isLeap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String weekday = LunarUtil.computeWeekday(this.mFormatdateString);
        String lunarDay = LunarUtil.numConvert(day, 2);
        getAlertController().setTitle(getContext().getResources().getString(R.string.nubia_date_lunar_title, new Object[]{Integer.valueOf(year), monthString, lunarDay, weekday}));
    }

    private void setLunarTitleByLunarMonth(int year, int month, int day, boolean curMonthIsLeap) {
        String monthString;
        String str = "";
        int leapMonth = LunarUtil.getLeapMonth(year);
        if (leapMonth <= 0) {
            monthString = LunarUtil.numConvert(month, 1);
        } else if (month != leapMonth || !curMonthIsLeap) {
            monthString = LunarUtil.numConvert(month, 1);
        } else {
            monthString = LunarUtil.LEAP + LunarUtil.numConvert(month, 1);
        }
        String weekday = LunarUtil.computeWeekday(this.mFormatdateString);
        String lunarDay = LunarUtil.numConvert(day, 2);
        getAlertController().setTitle(getContext().getResources().getString(R.string.nubia_date_lunar_title, new Object[]{Integer.valueOf(year), monthString, lunarDay, weekday}));
    }

    /* access modifiers changed from: private */
    public void setSolarTitle(int year, int month, int day) {
        String weekday = LunarUtil.computeWeekday(LunarUtil.formatDate(year, month, day));
        String monthStr = LunarUtil.MONTHS_LONG[month - 1];
        getAlertController().setTitle(getContext().getResources().getString(R.string.nubia_date_solar_title, new Object[]{Integer.valueOf(year), monthStr, Integer.valueOf(day), weekday}));
    }

    /* access modifiers changed from: private */
    public void resolveLunarDate(String lunarDate) {
        if (8 == lunarDate.length()) {
            this.mLunarYear = Integer.parseInt(lunarDate.substring(0, 4));
            this.mLunarMonth = Integer.parseInt(lunarDate.substring(4, 6));
            this.mLunarDay = Integer.parseInt(lunarDate.substring(6, 8));
            return;
        }
        this.mLunarYear = Integer.parseInt(lunarDate.substring(0, 4));
        this.mLunarMonth = Integer.parseInt(lunarDate.substring(4, 7)) + 20;
        this.mLunarDay = Integer.parseInt(lunarDate.substring(7, 9));
    }

    /* access modifiers changed from: private */
    public void resolveSolarDate(String solarDate) {
        this.mSolarYear = Integer.parseInt(solarDate.substring(0, 4));
        this.mSolarMonth = Integer.parseInt(solarDate.substring(4, 6));
        this.mSolarDay = Integer.parseInt(solarDate.substring(6, 8));
    }

    public boolean getSwitchState() {
        return this.mSwtichEnabled;
    }

    public boolean getCurrentMonthIsLeap() {
        return this.isCurrentMonthLeap;
    }

    public String getDateString(boolean isLuanrMode) {
        boolean hasLeapMonth = false;
        this.isCurrentMonthLeap = false;
        String str = "";
        int curYear = this.mDatePicker.getYear();
        int curMonth = this.mDatePicker.getMonth();
        int curDay = this.mDatePicker.getDayOfMonth();
        if (!isLuanrMode) {
            return LunarUtil.formatDate(curYear, curMonth, curDay);
        }
        int leapMonth = LunarUtil.getLeapMonth(curYear);
        if (leapMonth != 0) {
            hasLeapMonth = true;
        }
        if (this.mDatePicker.getMonthWheelView().getDisplayedValues()[curMonth].startsWith(LunarUtil.LEAP)) {
            this.isCurrentMonthLeap = true;
        }
        if (!hasLeapMonth) {
            curMonth++;
        } else if (curMonth < leapMonth) {
            curMonth++;
        }
        return LunarUtil.formatDate(curYear, curMonth, curDay + 1);
    }

    public void setLunarLayoutVisiable(boolean visiable) {
        if (visiable) {
            this.mLunarDateLayout.setVisibility(0);
            if (this.mLunarDateDivider != null) {
                this.mLunarDateDivider.setVisibility(0);
                return;
            }
            return;
        }
        this.mLunarDateLayout.setVisibility(8);
        if (this.mLunarDateDivider != null) {
            this.mLunarDateDivider.setVisibility(8);
        }
    }

    public boolean getSwitchStateFromSP() {
        return getContext().getSharedPreferences("datePickerSwitch", 0).getBoolean("switchState", false);
    }

    /* access modifiers changed from: private */
    public void saveSwitchState() {
        if (mIsCN) {
            Editor editor = getContext().getSharedPreferences("datePickerSwitch", 0).edit();
            editor.putBoolean("switchState", this.mSwtichEnabled);
            editor.apply();
        }
    }

    public void setSwitchState(boolean checked) {
        if (mIsCN && this.mSwtichEnabled != checked) {
            this.mNubiaSwitch.toggle();
        }
    }

    public void setYearMinMaxValue(int min, int max) {
        ArrayList<String> al = new ArrayList<>();
        for (int i = min; i <= max; i++) {
            al.add(i + "");
        }
        String[] years = (String[]) al.toArray(new String[0]);
        DEFAULT_START_YEAR = min;
        DEFAULT_END_YEAR = max;
        DatePickerView datePickerView = this.mDatePicker;
        DatePickerView.DEFAULT_START_YEAR = min;
        DatePickerView datePickerView2 = this.mDatePicker;
        DatePickerView.DEFAULT_END_YEAR = max;
        this.mDatePicker.getYearWheelView().setDisplayedValues(years);
        this.mDatePicker.getYearWheelView().setMinValue(min);
        this.mDatePicker.getYearWheelView().setMaxValue(max);
    }

    private void getTextRes() {
        LunarUtil.getTextRes(getContext());
        this.mLunarYearLabel = LunarUtil.YEAR;
        this.mLunarMonthLabel = LunarUtil.MONTH;
        this.mLunarDayLabel = LunarUtil.DAY;
    }
}
