package cn.nubia.commonui.actionbar.app;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.LayoutInflater.Factory2;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import cn.nubia.commonui.actionbar.internal.view.SupportActionModeWrapper;
import cn.nubia.commonui.actionbar.internal.view.SupportActionModeWrapper.CallbackWrapper;
import cn.nubia.commonui.actionbar.internal.widget.NativeActionModeAwareLayout;
import cn.nubia.commonui.actionbar.internal.widget.NativeActionModeAwareLayout.OnActionModeForChildListener;

@TargetApi(11)
class AppCompatDelegateImplV11 extends AppCompatDelegateImplV7 implements OnActionModeForChildListener {
    private NativeActionModeAwareLayout mNativeActionModeAwareLayout;

    AppCompatDelegateImplV11(Context context, Window window, AppCompatCallback callback) {
        super(context, window, callback);
    }

    /* access modifiers changed from: 0000 */
    public void onSubDecorInstalled(ViewGroup subDecor) {
        this.mNativeActionModeAwareLayout = (NativeActionModeAwareLayout) subDecor.findViewById(16908290);
        if (this.mNativeActionModeAwareLayout != null) {
            this.mNativeActionModeAwareLayout.setActionModeForChildListener(this);
        }
    }

    public ActionMode startActionModeForChild(View originalView, Callback callback) {
        cn.nubia.commonui.actionbar.view.ActionMode supportActionMode = startSupportActionMode(new CallbackWrapper(originalView.getContext(), callback));
        if (supportActionMode != null) {
            return new SupportActionModeWrapper(this.mContext, supportActionMode);
        }
        return null;
    }

    /* access modifiers changed from: 0000 */
    public View callActivityOnCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View view = super.callActivityOnCreateView(parent, name, context, attrs);
        if (view != null) {
            return view;
        }
        if (this.mOriginalWindowCallback instanceof Factory2) {
            return ((Factory2) this.mOriginalWindowCallback).onCreateView(parent, name, context, attrs);
        }
        return null;
    }
}
