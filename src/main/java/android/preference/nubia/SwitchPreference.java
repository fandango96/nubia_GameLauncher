package android.preference.nubia;

import android.content.Context;
import android.preference.TwoStatePreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import cn.nubia.commonui.R;
import cn.nubia.commonui.widget.NubiaSwitch;

public class SwitchPreference extends TwoStatePreference {
    private final Listener mListener;

    private class Listener implements OnCheckedChangeListener {
        private Listener() {
        }

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!SwitchPreference.this.callChangeListener(Boolean.valueOf(isChecked))) {
                buttonView.setChecked(!isChecked);
            } else {
                SwitchPreference.this.setChecked(isChecked);
            }
        }
    }

    public SwitchPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mListener = new Listener();
    }

    public SwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SwitchPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 16843629);
    }

    public SwitchPreference(Context context) {
        this(context, null);
    }

    /* access modifiers changed from: protected */
    public void onBindView(View view) {
        super.onBindView(view);
        View checkableView = view.findViewById(R.id.nubia_switchWidget);
        if (checkableView != null && (checkableView instanceof Checkable)) {
            if (checkableView instanceof NubiaSwitch) {
                ((NubiaSwitch) checkableView).setOnCheckedChangeListener(null);
            }
            ((Checkable) checkableView).setChecked(isChecked());
            if (checkableView instanceof NubiaSwitch) {
                ((NubiaSwitch) checkableView).setOnCheckedChangeListener(this.mListener);
            }
        }
    }
}
