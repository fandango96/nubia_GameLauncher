package cn.nubia.commonui.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import cn.nubia.commonui.R;
import cn.nubia.commonui.app.AlertDialog;
import cn.nubia.commonui.widget.TimePickerView.OnTimeChangeListener;

public class TimePickerDialog extends AlertDialog {
    private final onTimeSetListener mCallBack;
    private TimePickerOnClickListener mOnClickListener;
    private TimePickerOnTimeChangeListener mOnTimeChangeListener;
    private final TimePickerView mTimePickerView;

    private class TimePickerOnClickListener implements OnClickListener {
        private TimePickerOnClickListener() {
        }

        public void onClick(DialogInterface dialog, int which) {
            if (which == -1) {
                TimePickerDialog.this.tryNotifyTimeSet();
            } else if (which == -2) {
                dialog.cancel();
            }
        }
    }

    private class TimePickerOnTimeChangeListener implements OnTimeChangeListener {
        private TimePickerOnTimeChangeListener() {
        }

        public void onTimeChanged(TimePickerView view, int hourOfDay, int minute) {
        }
    }

    public interface onTimeSetListener {
        void onTimeSet(TimePickerView timePickerView, int i, int i2);
    }

    public TimePickerDialog(Context context, onTimeSetListener callBack, int hourOfDay, int minute) {
        this(context, 0, callBack, hourOfDay, minute);
    }

    public TimePickerDialog(Context context, int theme, onTimeSetListener callBack, int hourOfDay, int minute) {
        super(context, theme);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        setTitle(R.string.nubia_time_picker_dialog_title);
        Context themeContext = getContext();
        if (this.mOnClickListener == null) {
            this.mOnClickListener = new TimePickerOnClickListener();
        }
        setButton(-1, themeContext.getText(17039379), (OnClickListener) this.mOnClickListener);
        setButton(-2, themeContext.getText(17039360), (OnClickListener) this.mOnClickListener);
        View view = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.nubia_time_picker_dialog, null);
        setView(view);
        this.mCallBack = callBack;
        this.mTimePickerView = (TimePickerView) view.findViewById(R.id.nubia_timePickerView);
        this.mTimePickerView.setCurrentHour(Integer.valueOf(hourOfDay));
        this.mTimePickerView.setCurrentMinute(Integer.valueOf(minute));
        if (this.mOnTimeChangeListener == null) {
            this.mOnTimeChangeListener = new TimePickerOnTimeChangeListener();
        }
        this.mTimePickerView.setOnTimeChangedListener(this.mOnTimeChangeListener);
    }

    public final void updateTime(int hourOfDay, int minute) {
        this.mTimePickerView.setCurrentHour(Integer.valueOf(hourOfDay));
        this.mTimePickerView.setCurrentMinute(Integer.valueOf(minute));
    }

    /* access modifiers changed from: private */
    public void tryNotifyTimeSet() {
        if (this.mCallBack != null) {
            this.mTimePickerView.clearFocus();
            this.mCallBack.onTimeSet(this.mTimePickerView, this.mTimePickerView.getCurrentHour(), this.mTimePickerView.getCurrentMinute());
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.mTimePickerView.onRestoreInstanceState(savedInstanceState.getParcelable("PICKER"));
    }

    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putParcelable("PICKER", this.mTimePickerView.onSaveInstanceState());
        return state;
    }
}
