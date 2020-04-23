package cn.nubia.commonui.actionbar.internal.app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.View;
import cn.nubia.commonui.actionbar.internal.widget.TintAutoCompleteTextView;
import cn.nubia.commonui.actionbar.internal.widget.TintButton;
import cn.nubia.commonui.actionbar.internal.widget.TintCheckBox;
import cn.nubia.commonui.actionbar.internal.widget.TintCheckedTextView;
import cn.nubia.commonui.actionbar.internal.widget.TintEditText;
import cn.nubia.commonui.actionbar.internal.widget.TintMultiAutoCompleteTextView;
import cn.nubia.commonui.actionbar.internal.widget.TintRadioButton;
import cn.nubia.commonui.actionbar.internal.widget.TintRatingBar;
import cn.nubia.commonui.actionbar.internal.widget.TintSpinner;
import cn.nubia.commonui.actionbar.internal.widget.ViewUtils;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TintViewInflater {
    private static final Map<String, Constructor<? extends View>> sConstructorMap = new HashMap();
    static final Class<?>[] sConstructorSignature = {Context.class, AttributeSet.class};
    private final Object[] mConstructorArgs = new Object[2];
    private final Context mContext;

    public TintViewInflater(Context context) {
        this.mContext = context;
    }

    public final View createView(View parent, String name, @NonNull Context context, @NonNull AttributeSet attrs, boolean inheritContext, boolean themeContext) {
        Context originalContext = context;
        List<String> names = new ArrayList<String>() {
            {
                add("EditText");
                add("Spinner");
                add("CheckBox");
                add("RadioButton");
                add("CheckedTextView");
                add("AutoCompleteTextView");
                add("MultiAutoCompleteTextView");
                add("RatingBar");
                add("Button");
            }
        };
        if (inheritContext && parent != null) {
            context = parent.getContext();
        }
        if (themeContext) {
            context = ViewUtils.themifyContext(context, attrs, true, true);
        }
        switch (names.indexOf(name)) {
            case 0:
                return new TintEditText(context, attrs);
            case 1:
                return new TintSpinner(context, attrs);
            case 2:
                return new TintCheckBox(context, attrs);
            case 3:
                return new TintRadioButton(context, attrs);
            case 4:
                return new TintCheckedTextView(context, attrs);
            case 5:
                return new TintAutoCompleteTextView(context, attrs);
            case 6:
                return new TintMultiAutoCompleteTextView(context, attrs);
            case 7:
                return new TintRatingBar(context, attrs);
            case 8:
                return new TintButton(context, attrs);
            default:
                if (originalContext != context) {
                    return createViewFromTag(context, name, attrs);
                }
                return null;
        }
    }

    /* JADX INFO: finally extract failed */
    private View createViewFromTag(Context context, String name, AttributeSet attrs) {
        if (name.equals("view")) {
            name = attrs.getAttributeValue(null, "class");
        }
        try {
            this.mConstructorArgs[0] = context;
            this.mConstructorArgs[1] = attrs;
            if (-1 == name.indexOf(46)) {
                View createView = createView(name, "android.widget.");
                this.mConstructorArgs[0] = null;
                this.mConstructorArgs[1] = null;
                return createView;
            }
            View createView2 = createView(name, null);
            this.mConstructorArgs[0] = null;
            this.mConstructorArgs[1] = null;
            return createView2;
        } catch (Exception e) {
            this.mConstructorArgs[0] = null;
            this.mConstructorArgs[1] = null;
            return null;
        } catch (Throwable th) {
            this.mConstructorArgs[0] = null;
            this.mConstructorArgs[1] = null;
            throw th;
        }
    }

    private View createView(String name, String prefix) throws ClassNotFoundException, InflateException {
        Constructor<? extends View> constructor = (Constructor) sConstructorMap.get(name);
        if (constructor == null) {
            try {
                constructor = this.mContext.getClassLoader().loadClass(prefix != null ? prefix + name : name).asSubclass(View.class).getConstructor(sConstructorSignature);
                sConstructorMap.put(name, constructor);
            } catch (Exception e) {
                return null;
            }
        }
        constructor.setAccessible(true);
        return (View) constructor.newInstance(this.mConstructorArgs);
    }
}
