package cn.nubia.commonui.preference;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.preference.Preference.BaseSavedState;
import android.util.AttributeSet;
import cn.nubia.commonui.ReflectUtils;
import cn.nubia.commonui.app.AlertDialog.Builder;
import java.util.HashSet;
import java.util.Set;

public class MultiSelectListPreference extends DialogPreference {
    private CharSequence[] mEntries;
    /* access modifiers changed from: private */
    public CharSequence[] mEntryValues;
    /* access modifiers changed from: private */
    public Set<String> mNewValues;
    /* access modifiers changed from: private */
    public boolean mPreferenceChanged;
    private Set<String> mValues;

    private static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        Set<String> values;

        public SavedState(Parcel source) {
            super(source);
            this.values = new HashSet();
            for (String add : (String[]) ReflectUtils.invoke(source, "readStringArray", true, false)) {
                this.values.add(add);
            }
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeStringArray((String[]) this.values.toArray(new String[0]));
        }
    }

    public MultiSelectListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mValues = new HashSet();
        this.mNewValues = new HashSet();
        TypedArray a = context.obtainStyledAttributes(attrs, (int[]) ReflectUtils.getStyleable("MultiSelectListPreference"), defStyleAttr, defStyleRes);
        this.mEntries = a.getTextArray(((Integer) ReflectUtils.getStyleable("MultiSelectListPreference_entries")).intValue());
        this.mEntryValues = a.getTextArray(((Integer) ReflectUtils.getStyleable("MultiSelectListPreference_entryValues")).intValue());
        a.recycle();
    }

    public MultiSelectListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MultiSelectListPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 16842897);
    }

    public MultiSelectListPreference(Context context) {
        this(context, null);
    }

    public void setEntries(CharSequence[] entries) {
        this.mEntries = entries;
    }

    public void setEntries(int entriesResId) {
        setEntries(getContext().getResources().getTextArray(entriesResId));
    }

    public CharSequence[] getEntries() {
        return this.mEntries;
    }

    public void setEntryValues(CharSequence[] entryValues) {
        this.mEntryValues = entryValues;
    }

    public void setEntryValues(int entryValuesResId) {
        setEntryValues(getContext().getResources().getTextArray(entryValuesResId));
    }

    public CharSequence[] getEntryValues() {
        return this.mEntryValues;
    }

    public void setValues(Set<String> values) {
        this.mValues.clear();
        this.mValues.addAll(values);
        ReflectUtils.invoke(this, "persistStringSet", false, false, new Object[]{values}, Set.class);
    }

    public Set<String> getValues() {
        return this.mValues;
    }

    public int findIndexOfValue(String value) {
        if (!(value == null || this.mEntryValues == null)) {
            for (int i = this.mEntryValues.length - 1; i >= 0; i--) {
                if (this.mEntryValues[i].equals(value)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /* access modifiers changed from: protected */
    public void onPrepareDialogBuilder(Builder builder) {
        super.onPrepareDialogBuilder(builder);
        if (this.mEntries == null || this.mEntryValues == null) {
            throw new IllegalStateException("MultiSelectListPreference requires an entries array and an entryValues array.");
        }
        builder.setMultiChoiceItems(this.mEntries, getSelectedItems(), (OnMultiChoiceClickListener) new OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    MultiSelectListPreference.this.mPreferenceChanged = MultiSelectListPreference.this.mPreferenceChanged | MultiSelectListPreference.this.mNewValues.add(MultiSelectListPreference.this.mEntryValues[which].toString());
                } else {
                    MultiSelectListPreference.this.mPreferenceChanged = MultiSelectListPreference.this.mPreferenceChanged | MultiSelectListPreference.this.mNewValues.remove(MultiSelectListPreference.this.mEntryValues[which].toString());
                }
            }
        });
        this.mNewValues.clear();
        this.mNewValues.addAll(this.mValues);
    }

    private boolean[] getSelectedItems() {
        CharSequence[] entries = this.mEntryValues;
        int entryCount = entries.length;
        Set<String> values = this.mValues;
        boolean[] result = new boolean[entryCount];
        for (int i = 0; i < entryCount; i++) {
            result[i] = values.contains(entries[i].toString());
        }
        return result;
    }

    /* access modifiers changed from: protected */
    public void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult && this.mPreferenceChanged) {
            Set<String> values = this.mNewValues;
            if (callChangeListener(values)) {
                setValues(values);
            }
        }
        this.mPreferenceChanged = false;
    }

    /* access modifiers changed from: protected */
    public Object onGetDefaultValue(TypedArray a, int index) {
        Set<String> result = new HashSet<>();
        for (CharSequence charSequence : a.getTextArray(index)) {
            result.add(charSequence.toString());
        }
        return result;
    }

    /* access modifiers changed from: protected */
    public void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        Set set = (Set) ReflectUtils.invoke(this, "getPersistedStringSet", true, false, new Object[]{this.mValues}, Set.class);
        if (!restoreValue) {
            set = (Set) defaultValue;
        }
        setValues(set);
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            return superState;
        }
        SavedState myState = new SavedState(superState);
        myState.values = getValues();
        return myState;
    }
}
