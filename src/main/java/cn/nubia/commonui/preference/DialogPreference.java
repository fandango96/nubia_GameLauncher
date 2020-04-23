package cn.nubia.commonui.preference;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.preference.Preference;
import android.preference.Preference.BaseSavedState;
import android.preference.PreferenceManager.OnActivityDestroyListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import cn.nubia.commonui.R;
import cn.nubia.commonui.ReflectUtils;
import cn.nubia.commonui.app.AlertDialog.Builder;
import cn.nubia.commonui.app.Dialog;
import java.lang.reflect.Method;

public abstract class DialogPreference extends Preference implements OnClickListener, OnDismissListener, OnActivityDestroyListener {
    private Builder mBuilder;
    private Dialog mDialog;
    private Drawable mDialogIcon;
    private int mDialogLayoutResId;
    private CharSequence mDialogMessage;
    private CharSequence mDialogTitle;
    private CharSequence mNegativeButtonText;
    private CharSequence mPositiveButtonText;
    private int mWhichButtonClicked;

    private static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        Bundle dialogBundle;
        boolean isDialogShowing;

        public SavedState(Parcel source) {
            boolean z = true;
            super(source);
            if (source.readInt() != 1) {
                z = false;
            }
            this.isDialogShowing = z;
            this.dialogBundle = source.readBundle();
        }

        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.isDialogShowing ? 1 : 0);
            dest.writeBundle(this.dialogBundle);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }
    }

    public DialogPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.obtainStyledAttributes(attrs, (int[]) ReflectUtils.getStyleable("DialogPreference"), defStyleAttr, defStyleRes);
        this.mDialogTitle = a.getString(((Integer) ReflectUtils.getStyleable("DialogPreference_dialogTitle")).intValue());
        if (this.mDialogTitle == null) {
            this.mDialogTitle = getTitle();
        }
        this.mDialogTitle = null;
        this.mDialogMessage = a.getString(((Integer) ReflectUtils.getStyleable("DialogPreference_dialogMessage")).intValue());
        this.mDialogIcon = a.getDrawable(((Integer) ReflectUtils.getStyleable("DialogPreference_dialogIcon")).intValue());
        this.mPositiveButtonText = a.getString(((Integer) ReflectUtils.getStyleable("DialogPreference_positiveButtonText")).intValue());
        this.mNegativeButtonText = a.getString(((Integer) ReflectUtils.getStyleable("DialogPreference_negativeButtonText")).intValue());
        this.mDialogLayoutResId = a.getResourceId(((Integer) ReflectUtils.getStyleable("DialogPreference_dialogLayout")).intValue(), this.mDialogLayoutResId);
        a.recycle();
        setPersistent(true);
    }

    public DialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DialogPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 16842897);
    }

    public DialogPreference(Context context) {
        this(context, null);
    }

    public void setDialogTitle(CharSequence dialogTitle) {
        this.mDialogTitle = dialogTitle;
    }

    public void setDialogTitle(int dialogTitleResId) {
        setDialogTitle((CharSequence) getContext().getString(dialogTitleResId));
    }

    public CharSequence getDialogTitle() {
        return this.mDialogTitle;
    }

    public void setDialogMessage(CharSequence dialogMessage) {
        this.mDialogMessage = dialogMessage;
    }

    public void setDialogMessage(int dialogMessageResId) {
        setDialogMessage((CharSequence) getContext().getString(dialogMessageResId));
    }

    public CharSequence getDialogMessage() {
        return this.mDialogMessage;
    }

    public void setDialogIcon(Drawable dialogIcon) {
        this.mDialogIcon = dialogIcon;
    }

    public void setDialogIcon(int dialogIconRes) {
        this.mDialogIcon = getContext().getDrawable(dialogIconRes);
    }

    public Drawable getDialogIcon() {
        return this.mDialogIcon;
    }

    public void setPositiveButtonText(CharSequence positiveButtonText) {
        this.mPositiveButtonText = positiveButtonText;
    }

    public void setPositiveButtonText(int positiveButtonTextResId) {
        setPositiveButtonText((CharSequence) getContext().getString(positiveButtonTextResId));
    }

    public CharSequence getPositiveButtonText() {
        return this.mPositiveButtonText;
    }

    public void setNegativeButtonText(CharSequence negativeButtonText) {
        this.mNegativeButtonText = negativeButtonText;
    }

    public void setNegativeButtonText(int negativeButtonTextResId) {
        setNegativeButtonText((CharSequence) getContext().getString(negativeButtonTextResId));
    }

    public CharSequence getNegativeButtonText() {
        return this.mNegativeButtonText;
    }

    public void setDialogLayoutResource(int dialogLayoutResId) {
        this.mDialogLayoutResId = dialogLayoutResId;
    }

    public int getDialogLayoutResource() {
        return this.mDialogLayoutResId;
    }

    /* access modifiers changed from: protected */
    public void onPrepareDialogBuilder(Builder builder) {
    }

    /* access modifiers changed from: protected */
    public void onClick() {
        if (this.mDialog == null || !this.mDialog.isShowing()) {
            showDialog(null);
        }
    }

    /* access modifiers changed from: protected */
    public void showDialog(Bundle state) {
        Context context = getContext();
        this.mWhichButtonClicked = -2;
        this.mBuilder = new Builder(context).setTitle(this.mDialogTitle).setIcon(this.mDialogIcon).setPositiveButton(this.mPositiveButtonText, (OnClickListener) this).setNegativeButton(this.mNegativeButtonText, (OnClickListener) this);
        View contentView = onCreateDialogView();
        if (contentView != null) {
            onBindDialogView(contentView);
            this.mBuilder.setView(contentView);
        } else {
            this.mBuilder.setMessage(this.mDialogMessage);
        }
        onPrepareDialogBuilder(this.mBuilder);
        invoke(getPreferenceManager(), "registerOnActivityDestroyListener", OnActivityDestroyListener.class);
        Dialog dialog = this.mBuilder.create();
        this.mDialog = dialog;
        if (state != null) {
            dialog.onRestoreInstanceState(state);
        }
        if (needInputMethod()) {
            requestInputMethod(dialog);
        }
        dialog.setOnDismissListener(this);
        dialog.show();
    }

    /* access modifiers changed from: protected */
    public boolean needInputMethod() {
        return false;
    }

    private void requestInputMethod(Dialog dialog) {
        dialog.getWindow().setSoftInputMode(5);
    }

    /* access modifiers changed from: protected */
    public View onCreateDialogView() {
        if (this.mDialogLayoutResId == 0) {
            return null;
        }
        return LayoutInflater.from(this.mBuilder.getContext()).inflate(this.mDialogLayoutResId, null);
    }

    /* access modifiers changed from: protected */
    public void onBindDialogView(View view) {
        View dialogMessageView = view.findViewById(R.id.nubia_message);
        if (dialogMessageView != null) {
            CharSequence message = getDialogMessage();
            int newVisibility = 8;
            if (!TextUtils.isEmpty(message)) {
                if (dialogMessageView instanceof TextView) {
                    ((TextView) dialogMessageView).setText(message);
                }
                newVisibility = 0;
            }
            if (dialogMessageView.getVisibility() != newVisibility) {
                dialogMessageView.setVisibility(newVisibility);
            }
        }
    }

    public void onClick(DialogInterface dialog, int which) {
        this.mWhichButtonClicked = which;
    }

    public void onDismiss(DialogInterface dialog) {
        invoke(getPreferenceManager(), "unregisterOnActivityDestroyListener", OnActivityDestroyListener.class);
        this.mDialog = null;
        onDialogClosed(this.mWhichButtonClicked == -1);
    }

    /* access modifiers changed from: protected */
    public void onDialogClosed(boolean positiveResult) {
    }

    public Dialog getDialog() {
        return this.mDialog;
    }

    public void onActivityDestroy() {
        if (this.mDialog != null && this.mDialog.isShowing()) {
            this.mDialog.dismiss();
        }
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        if (this.mDialog == null || !this.mDialog.isShowing()) {
            return superState;
        }
        SavedState myState = new SavedState(superState);
        myState.isDialogShowing = true;
        myState.dialogBundle = this.mDialog.onSaveInstanceState();
        return myState;
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        if (myState.isDialogShowing) {
            showDialog(myState.dialogBundle);
        }
    }

    private void invoke(Object object, String methodName, Class target) {
        try {
            Method method = object.getClass().getDeclaredMethod(methodName, new Class[]{target});
            method.setAccessible(true);
            method.invoke(object, new Object[]{this});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
