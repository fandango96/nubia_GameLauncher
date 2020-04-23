package cn.nubia.commonui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View.BaseSavedState;
import android.widget.FrameLayout;
import android.widget.TextView;
import cn.nubia.commonui.R;
import cn.nubia.commonui.widget.WheelView.OnValueChangeListener;
import java.text.DateFormatSymbols;
import java.util.Calendar;

public class DatePickerView extends FrameLayout {
    public static int DEFAULT_END_YEAR = 2037;
    public static int DEFAULT_START_YEAR = 1970;
    public boolean hasLeapMonth;
    public boolean isLeapMonth;
    public boolean isLunarMode;
    public int leapMonth;
    /* access modifiers changed from: private */
    public int mDay;
    private TextView mDayLabel;
    private WheelView mDayView;
    private boolean mIsCN;
    private boolean mIs_JA_KO;
    private int mMaxDayOfMonth;
    private int mMaxMonthOfYear;
    /* access modifiers changed from: private */
    public int mMonth;
    private TextView mMonthLabel;
    private WheelView mMonthView;
    private OnDateChangeListener mOnDateChangeListener;
    private Paint mPaint;
    /* access modifiers changed from: private */
    public int mYear;
    private TextView mYearLabel;
    private WheelView mYearView;

    public interface OnDateChangeListener {
        void onDateChanged(DatePickerView datePickerView, int i, int i2, int i3);
    }

    private static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        final int sDay;
        final int sMonth;
        final int sYear;

        SavedState(Parcelable superState, int year, int month, int day) {
            super(superState);
            this.sYear = year;
            this.sMonth = month;
            this.sDay = day;
        }

        SavedState(Parcel in) {
            super(in);
            this.sYear = in.readInt();
            this.sMonth = in.readInt();
            this.sDay = in.readInt();
        }

        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.sYear);
            dest.writeInt(this.sMonth);
            dest.writeInt(this.sDay);
        }
    }

    public DatePickerView(Context context) {
        super(context);
        this.isLunarMode = false;
        this.hasLeapMonth = false;
        this.leapMonth = 0;
        this.isLeapMonth = false;
    }

    public DatePickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.isLunarMode = false;
        this.hasLeapMonth = false;
        this.leapMonth = 0;
        this.isLeapMonth = false;
    }

    public DatePickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isLunarMode = false;
        this.hasLeapMonth = false;
        this.leapMonth = 0;
        this.isLeapMonth = false;
        this.mPaint = new Paint();
        this.mPaint.setColor(getContext().getResources().getColor(R.color.nubia_wheelview_middle_zone_color));
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.nubia_date_picker, this, true);
        setBackgroundColor(0);
        this.mYearLabel = (TextView) findViewById(R.id.nubia_year_label);
        this.mMonthLabel = (TextView) findViewById(R.id.nubia_month_label);
        this.mDayLabel = (TextView) findViewById(R.id.nubia_day_label);
        String currentLanguage = getResources().getConfiguration().locale.getLanguage();
        if (currentLanguage.endsWith("zh")) {
            this.mYearLabel.setVisibility(0);
            this.mMonthLabel.setVisibility(0);
            this.mDayLabel.setVisibility(0);
            this.mIsCN = true;
        } else {
            this.mIsCN = false;
        }
        if (currentLanguage.endsWith("ja") || currentLanguage.endsWith("ko")) {
            this.mYearLabel.setVisibility(4);
            this.mMonthLabel.setVisibility(4);
            this.mDayLabel.setVisibility(4);
            this.mIs_JA_KO = true;
        } else {
            this.mIs_JA_KO = false;
        }
        if (this.mIsCN || this.mIs_JA_KO) {
            this.mDayView = (WheelView) findViewById(R.id.nubia_day_spinner);
            this.mMonthView = (WheelView) findViewById(R.id.nubia_month_spinner);
            this.mYearView = (WheelView) findViewById(R.id.nubia_year_spinner);
        } else {
            this.mYearView = (WheelView) findViewById(R.id.nubia_day_spinner);
            this.mDayView = (WheelView) findViewById(R.id.nubia_month_spinner);
            this.mMonthView = (WheelView) findViewById(R.id.nubia_year_spinner);
        }
        this.mYearView.setMinValue(DEFAULT_START_YEAR);
        this.mYearView.setMaxValue(DEFAULT_END_YEAR);
        this.mYearView.setOnValueChangedListener(new OnValueChangeListener() {
            public void onValueChange(WheelView wheelView, int oldVal, int newVal) {
                if (DatePickerView.this.isLunarMode) {
                    DatePickerView.this.mYear = newVal;
                    DatePickerView.this.adjustMonthDisplayerValues();
                    DatePickerView.this.adjustDayDisplayerValues();
                    DatePickerView.this.notifyDateChanged();
                    return;
                }
                DatePickerView.this.mYear = newVal;
                DatePickerView.this.adjustMaxDay();
                DatePickerView.this.notifyDateChanged();
                DatePickerView.this.updateSpinners();
            }
        });
        this.mMonthView.setFormatter(WheelView.getTwoDigitFormatter());
        String[] months = new DateFormatSymbols().getShortMonths();
        if (months[0].startsWith("1")) {
            for (int i = 0; i < months.length; i++) {
                months[i] = String.valueOf(i + 1);
            }
            this.mMonthView.setMinValue(1);
            this.mMonthView.setMaxValue(12);
        } else {
            this.mMonthView.setMinValue(1);
            this.mMonthView.setMaxValue(12);
            this.mMonthView.setDisplayedValues(LunarUtil.MONTHS_SHORT);
        }
        this.mMonthView.setOnValueChangedListener(new OnValueChangeListener() {
            public void onValueChange(WheelView wheelView, int oldVal, int newVal) {
                if (DatePickerView.this.isLunarMode) {
                    DatePickerView.this.mMonth = newVal;
                    DatePickerView.this.adjustDayDisplayerValues();
                    DatePickerView.this.notifyDateChanged();
                    return;
                }
                DatePickerView.this.mMonth = newVal - 1;
                DatePickerView.this.adjustMaxDay();
                DatePickerView.this.notifyDateChanged();
                DatePickerView.this.updateSpinners();
            }
        });
        this.mDayView.setFormatter(WheelView.getTwoDigitFormatter());
        this.mDayView.setOnValueChangedListener(new OnValueChangeListener() {
            public void onValueChange(WheelView wheelView, int oldVal, int newVal) {
                DatePickerView.this.mDay = newVal;
                DatePickerView.this.notifyDateChanged();
            }
        });
        Calendar cal = Calendar.getInstance();
        update(cal.get(1), cal.get(2), cal.get(5), null);
    }

    public final void updateDate(int year, int monthOfYear, int dayOfMonth) {
        if (this.mYear != year || this.mMonth != monthOfYear || this.mDay != dayOfMonth) {
            this.mYear = year;
            this.mMonth = monthOfYear;
            this.mDay = dayOfMonth;
            updateSpinners();
            notifyDateChanged();
        }
    }

    private static int getCurrentYear() {
        return Calendar.getInstance().get(1);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Canvas canvas2 = canvas;
        canvas2.drawRect(0.0f, (float) this.mDayView.getMiddleTop(), (float) getRight(), (float) this.mDayView.getMiddleBottom(), this.mPaint);
    }

    public final void adjustMaxDay() {
        Calendar cal = Calendar.getInstance();
        cal.set(1, this.mYear);
        cal.set(5, 1);
        cal.set(2, this.mMonth);
        this.mMaxDayOfMonth = cal.getActualMaximum(5);
        if (this.mDay > this.mMaxDayOfMonth) {
            this.mDay = this.mMaxDayOfMonth;
        }
    }

    public final void adjustMonthDisplayerValues() {
        String[] months = LunarUtil.solarMonthTolunarMonth(this.mYear);
        this.mMaxMonthOfYear = months.length;
        this.mMonthView.setDisplayedValues(null);
        this.mMonthView.setMinValue(0);
        this.mMonthView.setMaxValue(months.length - 1);
        this.mMonthView.setDisplayedValues(months);
        if (this.mMonth > this.mMaxMonthOfYear - 1) {
            this.mMonth = this.mMaxMonthOfYear - 1;
        }
        int leapMonth2 = LunarUtil.getLeapMonth(this.mYear);
        this.mMonthView.setValue(this.mMonth);
    }

    public final void adjustDayDisplayerValues() {
        String[] days;
        String[] values = this.mMonthView.getDisplayedValues();
        int leapMonth2 = LunarUtil.getLeapMonth(this.mYear);
        if (values[this.mMonth].startsWith(LunarUtil.LEAP)) {
            days = LunarUtil.solarDayTolunarDay(this.mYear, this.mMonth, true);
        } else if (leapMonth2 > 0 && this.mMonth >= leapMonth2 + 1) {
            days = LunarUtil.solarDayTolunarDay(this.mYear, this.mMonth, false);
        } else if (leapMonth2 <= 0 || this.mMonth >= leapMonth2) {
            days = LunarUtil.solarDayTolunarDay(this.mYear, this.mMonth + 1, false);
        } else {
            days = LunarUtil.solarDayTolunarDay(this.mYear, this.mMonth + 1, false);
        }
        this.mMaxDayOfMonth = days.length;
        this.mDayView.setDisplayedValues(null);
        this.mDayView.setMinValue(0);
        this.mDayView.setMaxValue(this.mMaxDayOfMonth - 1);
        this.mDayView.setDisplayedValues(days);
        if (this.mDay > this.mMaxDayOfMonth - 1) {
            this.mDay = this.mMaxDayOfMonth - 1;
        }
        this.mDayView.setValue(this.mDay);
    }

    public final void notifyDateChanged() {
        if (this.mOnDateChangeListener != null) {
            this.mOnDateChangeListener.onDateChanged(this, this.mYear, this.mMonth, this.mDay);
            this.mYearView.invalidate();
            this.mMonthView.invalidate();
            this.mDayView.invalidate();
        }
    }

    public final void update(int year, int monthOfYear, int dayOfMonth, OnDateChangeListener onDateChangeListener) {
        this.mYear = year;
        this.mMonth = monthOfYear;
        this.mDay = dayOfMonth;
        this.mOnDateChangeListener = onDateChangeListener;
        updateSpinners();
    }

    public final void updateSpinners() {
        if (this.mMonth >= 20) {
            this.isLeapMonth = true;
            this.mMonth -= 20;
        }
        this.mYearView.setValue(this.mYear);
        updateDaySpinners();
        if (this.isLunarMode) {
            this.leapMonth = LunarUtil.getLeapMonth(this.mYear);
            if (this.leapMonth <= 0) {
                this.mMonth--;
            } else if (this.isLeapMonth) {
                this.isLeapMonth = false;
            } else if (this.mMonth <= this.leapMonth) {
                this.mMonth--;
            }
            this.mMaxMonthOfYear = this.mMonthView.getDisplayedValues().length - 1;
            if (this.mMonth > this.mMaxMonthOfYear) {
                this.mMonth = this.mMaxMonthOfYear;
            }
            this.mMonthView.setValue(this.mMonth);
            return;
        }
        this.mMonthView.setMinValue(1);
        this.mMonthView.setMaxValue(12);
        if (this.mMonth >= 12) {
            this.mMonth = 12;
        }
        this.mMonthView.setValue(this.mMonth + 1);
    }

    public final void updateDaySpinners() {
        if (this.isLunarMode) {
            this.mMaxDayOfMonth = this.mDayView.getDisplayedValues().length - 1;
            this.mDay--;
            if (this.mDay > this.mMaxDayOfMonth) {
                this.mDay = this.mMaxDayOfMonth;
            }
            this.mDayView.setValue(this.mDay);
            return;
        }
        Calendar cal = Calendar.getInstance();
        cal.set(1, this.mYear);
        cal.set(5, 1);
        cal.set(2, this.mMonth);
        this.mMaxDayOfMonth = cal.getActualMaximum(5);
        this.mDayView.setMinValue(1);
        this.mDayView.setMaxValue(this.mMaxDayOfMonth);
        this.mDayView.setValue(this.mDay);
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState(), this.mYear, this.mMonth, this.mDay);
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.mYear = ss.sYear;
        this.mMonth = ss.sMonth;
        this.mDay = ss.sDay;
        updateSpinners();
    }

    public final int getYear() {
        return this.mYear;
    }

    public final int getMonth() {
        return this.mMonthView.getValue();
    }

    public final int getDayOfMonth() {
        return this.mDay;
    }

    public final WheelView getYearWheelView() {
        return this.mYearView;
    }

    public final WheelView getMonthWheelView() {
        return this.mMonthView;
    }

    public final WheelView getDayWheelView() {
        return this.mDayView;
    }

    public void setDayLabelVisible(boolean visible) {
        if (visible) {
            this.mDayLabel.setVisibility(0);
        } else {
            this.mDayLabel.setVisibility(4);
        }
    }
}
