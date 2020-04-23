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
import android.widget.TextView;
import cn.nubia.commonui.R;
import cn.nubia.commonui.widget.WheelView.OnValueChangeListener;
import java.util.Locale;

public class TimePickerView extends FrameLayout {
    private static final int HOURS_IN_HALF_DAY = 12;
    private WheelView mAmPmView;
    protected Locale mCurrentLocale;
    private WheelView mHourView;
    private boolean mIs24HourView;
    /* access modifiers changed from: private */
    public boolean mIsAm;
    private WheelView mMinuteView;
    private OnTimeChangeListener mOnTimeChangeListener;
    private Paint mPaint;

    public interface OnTimeChangeListener {
        void onTimeChanged(TimePickerView timePickerView, int i, int i2);
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

    public TimePickerView(Context context) {
        super(context);
    }

    public TimePickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TimePickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mPaint = new Paint();
        this.mPaint.setColor(context.getResources().getColor(R.color.nubia_wheelview_middle_zone_color));
        this.mIs24HourView = DateFormat.is24HourFormat(getContext());
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.nubia_time_picker, this, true);
        setBackgroundColor(0);
        this.mHourView = (WheelView) findViewById(R.id.nubia_hour_spinner);
        if (!this.mIs24HourView) {
            this.mHourView.setMinValue(1);
            this.mHourView.setMaxValue(12);
        } else {
            this.mHourView.setMinValue(0);
            this.mHourView.setMaxValue(23);
        }
        this.mHourView.setFormatter(WheelView.getTwoDigitFormatter());
        this.mHourView.setOnValueChangedListener(new OnValueChangeListener() {
            public void onValueChange(WheelView wheelView, int oldVal, int newVal) {
                TimePickerView.this.onTimeChanged();
            }
        });
        this.mMinuteView = (WheelView) findViewById(R.id.nubia_minute_spinner);
        this.mMinuteView.setMinValue(0);
        this.mMinuteView.setMaxValue(59);
        this.mMinuteView.setFormatter(WheelView.getTwoDigitFormatter());
        this.mMinuteView.setOnValueChangedListener(new OnValueChangeListener() {
            public void onValueChange(WheelView wheelView, int oldVal, int newVal) {
                TimePickerView.this.onTimeChanged();
            }
        });
        if (!this.mIs24HourView) {
            String[] amPmStrings = getAmPmStrings(context);
            this.mAmPmView = (WheelView) findViewById(R.id.nubia_ampm_spinner);
            this.mAmPmView.setMinValue(0);
            this.mAmPmView.setMaxValue(1);
            this.mAmPmView.setDisplayedValues(amPmStrings);
            this.mAmPmView.setOnValueChangedListener(new OnValueChangeListener() {
                public void onValueChange(WheelView wheelView, int oldVal, int newVal) {
                    TimePickerView.this.mIsAm = !TimePickerView.this.mIsAm;
                    TimePickerView.this.updateAmPmControl();
                    TimePickerView.this.onTimeChanged();
                }
            });
        } else {
            this.mAmPmView = (WheelView) findViewById(R.id.nubia_ampm_spinner);
            this.mAmPmView.setVisibility(8);
            ((TextView) findViewById(R.id.nubia_time_hour_textview)).setVisibility(8);
        }
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
            this.mOnTimeChangeListener.onTimeChanged(this, getCurrentHour(), getCurrentMinute());
        }
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

    public void setCurrentHour(Integer currentHour) {
        if (currentHour != null || currentHour.intValue() != getCurrentHour()) {
            if (!is24HourView()) {
                if (currentHour.intValue() >= 12) {
                    this.mIsAm = false;
                    if (currentHour.intValue() > 12) {
                        currentHour = Integer.valueOf(currentHour.intValue() - 12);
                    }
                } else {
                    this.mIsAm = true;
                    if (currentHour.intValue() == 0) {
                        currentHour = Integer.valueOf(12);
                    }
                }
                updateAmPmControl();
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

    /* access modifiers changed from: private */
    public void updateAmPmControl() {
        if (!is24HourView()) {
            int index = this.mIsAm ? 0 : 1;
            if (this.mAmPmView != null) {
                this.mAmPmView.setValue(index);
                this.mAmPmView.setVisibility(0);
            }
        } else if (this.mAmPmView != null) {
            this.mAmPmView.setVisibility(8);
        }
    }
}
