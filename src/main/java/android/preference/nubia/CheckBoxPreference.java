package android.preference.nubia;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import cn.nubia.commonui.ReflectUtils;

public class CheckBoxPreference extends TwoStatePreference {
    private final Listener mListener;

    public class Listener implements OnCheckedChangeListener {
        private PreferenceManager preferenceManager;
        private PreferenceScreen preferenceScreen;

        public Listener() {
        }

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            boolean z = true;
            if (!CheckBoxPreference.this.callChangeListener(Boolean.valueOf(isChecked))) {
                if (isChecked) {
                    z = false;
                }
                buttonView.setChecked(z);
                return;
            }
            CheckBoxPreference.this.setChecked(isChecked);
            this.preferenceManager = CheckBoxPreference.this.getPreferenceManager();
            if (this.preferenceManager == null) {
                return;
            }
            if (CheckBoxPreference.this.getOnPreferenceClickListener() == null || !CheckBoxPreference.this.getOnPreferenceClickListener().onPreferenceClick(CheckBoxPreference.this)) {
                this.preferenceScreen = (PreferenceScreen) ReflectUtils.invoke(CheckBoxPreference.this.getPreferenceManager(), "getPreferenceScreen", true, false);
                if (this.preferenceScreen != null) {
                    handlePreferenceTreeClick();
                }
            }
        }

        private void handlePreferenceTreeClick() {
            Object listener = ReflectUtils.invoke(CheckBoxPreference.this.getPreferenceManager(), "getOnPreferenceTreeClickListener", true, false);
            if (listener != null) {
                ReflectUtils.invoke(listener, "onPreferenceTreeClick", false, false, new Object[]{this.preferenceScreen, CheckBoxPreference.this}, PreferenceScreen.class, Preference.class);
            }
        }
    }

    public CheckBoxPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CheckBoxPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mListener = new Listener();
    }

    public CheckBoxPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 16842895);
    }

    public CheckBoxPreference(Context context) {
        this(context, null);
    }

    /* access modifiers changed from: protected */
    public void onBindView(View view) {
        super.onBindView(view);
        View checkboxView = view.findViewById(16908289);
        if (checkboxView != null && (checkboxView instanceof Checkable)) {
            if (!(checkboxView instanceof CompoundButton) || (checkboxView instanceof CheckBox)) {
                ((Checkable) checkboxView).setChecked(isChecked());
                return;
            }
            CompoundButton switchView = (CompoundButton) checkboxView;
            switchView.setOnCheckedChangeListener(null);
            ((Checkable) checkboxView).setChecked(isChecked());
            switchView.setOnCheckedChangeListener(this.mListener);
        }
    }
}
