package cn.nubia.commonui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View.BaseSavedState;
import android.widget.FrameLayout;
import cn.nubia.commonui.R;
import cn.nubia.commonui.widget.WheelView.OnValueChangeListener;
import java.util.Locale;

public class NubiaLunarTimePickerView extends FrameLayout {
    private static final int DEFAULT_END_YEAR = 2037;
    private static final int DEFAULT_START_YEAR = 1970;
    private static final int HOURS_IN_HALF_DAY = 12;
    public boolean isLunarMode;
    private String[] m12HourArray;
    protected Locale mCurrentLocale;
    /* access modifiers changed from: private */
    public WheelView mHourView;
    /* access modifiers changed from: private */
    public boolean mIs24HourView;
    /* access modifiers changed from: private */
    public boolean mIsAm;
    /* access modifiers changed from: private */
    public boolean mIsCN;
    /* access modifiers changed from: private */
    public int mMaxMonthDay;
    private WheelView mMinuteView;
    /* access modifiers changed from: private */
    public int mMonthDay;
    /* access modifiers changed from: private */
    public WheelView mMonthDayView;
    private OnTimeChangeListener mOnTimeChangeListener;
    private Paint mPaint;
    /* access modifiers changed from: private */
    public int mYear;

    public interface OnTimeChangeListener {
        void onTimeChanged(NubiaLunarTimePickerView nubiaLunarTimePickerView, int i, int i2, int i3, int i4);
    }

    private static class SaveState extends BaseSavedState {
        public static final Creator<SaveState> CREATOR = new Creator<SaveState>() {
            public SaveState createFromParcel(Parcel in) {
                return new SaveState(in);
            }

            public SaveState[] newArray(int size) {
                return new SaveState[size];
            }
        };
        final int mHour;
        final int mMinute;

        SaveState(Parcel in) {
            super(in);
            this.mHour = in.readInt();
            this.mMinute = in.readInt();
        }

        SaveState(Parcelable superState, int hour, int minute) {
            super(superState);
            this.mHour = hour;
            this.mMinute = minute;
        }

        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.mHour);
            dest.writeInt(this.mMinute);
        }
    }

    public NubiaLunarTimePickerView(Context context) {
        super(context);
        this.isLunarMode = false;
        this.m12HourArray = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    }

    public NubiaLunarTimePickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.isLunarMode = false;
        this.m12HourArray = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    }

    public NubiaLunarTimePickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isLunarMode = false;
        this.m12HourArray = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        this.mPaint = new Paint();
        this.mPaint.setColor(context.getResources().getColor(R.color.nubia_wheelview_middle_zone_color));
        this.mIs24HourView = DateFormat.is24HourFormat(getContext());
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.nubia_lunar_time_picker, this, true);
        setBackgroundColor(0);
        if (context.getResources().getConfiguration().locale.getLanguage().endsWith("zh")) {
            this.mIsCN = true;
        } else {
            this.mIsCN = false;
        }
        this.mMonthDayView = (WheelView) findViewById(R.id.nubia_month_day_spinner);
        this.mMonthDayView.setMinValue(1);
        this.mMonthDayView.setOnValueChangedListener(new OnValueChangeListener() {
            public void onValueChange(WheelView wheelView, int oldVal, int newVal) {
                if (oldVal == NubiaLunarTimePickerView.this.mMonthDayView.getMaxValue() && newVal == NubiaLunarTimePickerView.this.mMonthDayView.getMinValue()) {
                    NubiaLunarTimePickerView.this.mYear = NubiaLunarTimePickerView.this.mYear + 1;
                } else if (oldVal == NubiaLunarTimePickerView.this.mMonthDayView.getMinValue() && newVal == NubiaLunarTimePickerView.this.mMonthDayView.getMaxValue()) {
                    NubiaLunarTimePickerView.this.mYear = NubiaLunarTimePickerView.this.mYear - 1;
                }
                if (NubiaLunarTimePickerView.this.mYear > NubiaLunarTimePickerView.DEFAULT_END_YEAR) {
                    NubiaLunarTimePickerView.this.mYear = NubiaLunarTimePickerView.DEFAULT_END_YEAR;
                }
                if (NubiaLunarTimePickerView.this.mYear < NubiaLunarTimePickerView.DEFAULT_START_YEAR) {
                    NubiaLunarTimePickerView.this.mYear = NubiaLunarTimePickerView.DEFAULT_START_YEAR;
                }
                NubiaLunarTimePickerView.this.mMaxMonthDay = NubiaLunarTimePickerView.this.mMonthDayView.getMaxValue();
                if (NubiaLunarTimePickerView.this.isLunarMode) {
                    NubiaLunarTimePickerView.this.mMonthDay = newVal;
                    NubiaLunarTimePickerView.this.adjustLunYear(newVal, oldVal);
                } else if (NubiaLunarTimePickerView.this.mIsCN) {
                    NubiaLunarTimePickerView.this.adjustSolYear(newVal, oldVal);
                } else {
                    NubiaLunarTimePickerView.this.adjustUSYear(newVal, oldVal);
                }
                NubiaLunarTimePickerView.this.noticeTimeChange();
            }
        });
        this.mHourView = (WheelView) findViewById(R.id.nubia_hour_spinner);
        if (!this.mIs24HourView) {
            this.mHourView.setDisplayedValues(null);
            this.mHourView.setMinValue(1);
            this.mHourView.setMaxValue(this.m12HourArray.length);
            this.mHourView.setDisplayedValues(this.m12HourArray);
        } else {
            this.mHourView.setMinValue(0);
            this.mHourView.setMaxValue(23);
        }
        this.mHourView.setFormatter(WheelView.getTwoDigitFormatter());
        this.mHourView.setOnValueChangedListener(new OnValueChangeListener() {
            public void onValueChange(WheelView wheelView, int oldVal, int newVal) {
                if (!NubiaLunarTimePickerView.this.mIs24HourView) {
                    NubiaLunarTimePickerView.this.mIsAm = newVal <= 12;
                }
                NubiaLunarTimePickerView.this.mHourView.setValue(newVal);
                NubiaLunarTimePickerView.this.onTimeChanged();
            }
        });
        this.mMinuteView = (WheelView) findViewById(R.id.nubia_minute_spinner);
        this.mMinuteView.setMinValue(0);
        this.mMinuteView.setMaxValue(59);
        this.mMinuteView.setFormatter(WheelView.getTwoDigitFormatter());
        this.mMinuteView.setOnValueChangedListener(new OnValueChangeListener() {
            public void onValueChange(WheelView wheelView, int oldVal, int newVal) {
                NubiaLunarTimePickerView.this.onTimeChanged();
            }
        });
        setCurrentLocale(Locale.getDefault());
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mMinuteView != null) {
            Canvas canvas2 = canvas;
            canvas2.drawRect(0.0f, (float) this.mMinuteView.getMiddleTop(), (float) getRight(), (float) this.mMinuteView.getMiddleBottom(), this.mPaint);
        }
    }

    public void setCurrentLocale(Locale locale) {
        if (!locale.equals(this.mCurrentLocale)) {
            this.mCurrentLocale = locale;
        }
    }

    /* access modifiers changed from: private */
    public void onTimeChanged() {
        if (this.mOnTimeChangeListener != null) {
            this.mOnTimeChangeListener.onTimeChanged(this, this.mYear, this.mMonthDay, getCurrentHour(), getCurrentMinute());
        }
    }

    public void update(int year) {
        this.mYear = year;
        if (this.isLunarMode) {
            this.mMaxMonthDay = LunarUtil.timePickerLunMonthDay(this.mYear).length - 1;
        } else {
            this.mMaxMonthDay = LunarUtil.timePickerSolMonthDay(this.mYear).length - 1;
        }
    }

    /* access modifiers changed from: private */
    public void adjustLunYear(int newVal, int oldVal) {
        updateTimePickerArray(LunarUtil.timePickerLunMonthDay(this.mYear));
        if (this.mMaxMonthDay == this.mMonthDayView.getMaxValue()) {
            this.mMonthDay = newVal;
            this.mMonthDayView.setValue(this.mMonthDay);
        } else if (oldVal == this.mMaxMonthDay) {
            this.mMaxMonthDay = this.mMonthDayView.getMaxValue();
            this.mMonthDay = 0;
            this.mMonthDayView.setValue(this.mMonthDay);
        } else if (oldVal == 0) {
            this.mMaxMonthDay = this.mMonthDayView.getMaxValue();
            this.mMonthDay = this.mMaxMonthDay;
            this.mMonthDayView.setValue(this.mMonthDay);
        } else {
            this.mMonthDay = newVal;
            this.mMonthDayView.setValue(this.mMonthDay);
        }
    }

    /* access modifiers changed from: private */
    public void adjustSolYear(int newVal, int oldVal) {
        updateTimePickerArray(LunarUtil.timePickerSolMonthDay(this.mYear));
        if (this.mMaxMonthDay == this.mMonthDayView.getMaxValue()) {
            this.mMonthDay = newVal;
            this.mMonthDayView.setValue(this.mMonthDay);
        } else if (oldVal == this.mMaxMonthDay) {
            this.mMaxMonthDay = this.mMonthDayView.getMaxValue();
            this.mMonthDay = 0;
            this.mMonthDayView.setValue(this.mMonthDay);
        } else if (oldVal == 0) {
            this.mMaxMonthDay = this.mMonthDayView.getMaxValue();
            this.mMonthDay = this.mMaxMonthDay;
            this.mMonthDayView.setValue(this.mMonthDay);
        } else {
            this.mMonthDay = newVal;
            this.mMonthDayView.setValue(this.mMonthDay);
        }
    }

    /* access modifiers changed from: private */
    public void adjustUSYear(int newVal, int oldVal) {
        updateTimePickerArray(LunarUtil.timePickerUSMonthDay(this.mYear));
        if (this.mMaxMonthDay == this.mMonthDayView.getMaxValue()) {
            this.mMonthDay = newVal;
            this.mMonthDayView.setValue(this.mMonthDay);
        } else if (oldVal == this.mMaxMonthDay) {
            this.mMaxMonthDay = this.mMonthDayView.getMaxValue();
            this.mMonthDay = 0;
            this.mMonthDayView.setValue(this.mMonthDay);
        } else if (oldVal == 0) {
            this.mMaxMonthDay = this.mMonthDayView.getMaxValue();
            this.mMonthDay = this.mMaxMonthDay;
            this.mMonthDayView.setValue(this.mMonthDay);
        } else {
            this.mMonthDay = newVal;
            this.mMonthDayView.setValue(this.mMonthDay);
        }
    }

    private void updateTimePickerArray(String[] array) {
        this.mMonthDayView.setDisplayedValues(null);
        this.mMonthDayView.setMinValue(0);
        this.mMonthDayView.setMaxValue(array.length - 1);
        this.mMonthDayView.setDisplayedValues(array);
    }

    public int getCurrentYear() {
        return this.mYear;
    }

    public void setCurrentYear(int year) {
        this.mYear = year;
    }

    public final int getCurrentHour() {
        int currentHour = this.mHourView.getValue();
        if (is24HourView()) {
            return currentHour;
        }
        if (this.mIsAm) {
            return currentHour % 12;
        }
        return (currentHour % 12) + 12;
    }

    public final int getCurrentMinute() {
        return this.mMinuteView.getValue();
    }

    public WheelView getMonthDayView() {
        return this.mMonthDayView;
    }

    public void noticeTimeChange() {
        onTimeChanged();
        this.mHourView.invalidate();
        this.mMinuteView.invalidate();
        this.mMonthDayView.invalidate();
    }

    public boolean getmIs24HourView() {
        return this.mIs24HourView;
    }

    public boolean getmIsAm() {
        return this.mIsAm;
    }

    public void setCurrentHour(Integer currentHour) {
        if (currentHour != null || currentHour.intValue() != getCurrentHour()) {
            if (!is24HourView()) {
                if (currentHour.intValue() >= 12) {
                    this.mIsAm = false;
                } else {
                    this.mIsAm = true;
                    if (currentHour.intValue() == 0) {
                        currentHour = Integer.valueOf(12);
                    }
                }
            }
            this.mHourView.setValue(currentHour.intValue());
        }
    }

    public void setCurrentMinute(Integer currentMinute) {
        if (currentMinute != null || currentMinute.intValue() != getCurrentMinute()) {
            this.mMinuteView.setValue(currentMinute.intValue());
        }
    }

    public final void setOnTimeChangedListener(OnTimeChangeListener onTimeChangeListener) {
        this.mOnTimeChangeListener = onTimeChangeListener;
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable state) {
        SaveState ss = (SaveState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setCurrentHour(Integer.valueOf(ss.mHour));
        setCurrentMinute(Integer.valueOf(ss.mMinute));
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        return new SaveState(super.onSaveInstanceState(), getCurrentHour(), getCurrentMinute());
    }

    public static String[] getAmPmStrings(Context context) {
        return new String[]{context.getResources().getString(R.string.nubia_time_am), context.getResources().getString(R.string.nubia_time_pm)};
    }

    public boolean is24HourView() {
        return this.mIs24HourView;
    }
}
