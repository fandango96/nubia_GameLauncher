package cn.nubia.commonui.actionbar.internal.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.util.LruCache;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import cn.nubia.commonui.R;

public final class TintManager {
    private static final ColorFilterLruCache COLOR_FILTER_CACHE = new ColorFilterLruCache(6);
    private static final int[] CONTAINERS_WITH_TINT_CHILDREN = {R.drawable.abc_cab_background_top_material};
    private static final boolean DEBUG = false;
    static final Mode DEFAULT_MODE = Mode.SRC_IN;
    static final boolean SHOULD_BE_USED = (VERSION.SDK_INT < 21);
    private static final String TAG = "NubiaWidget";
    private static final int[] TINT_COLOR_BACKGROUND_MULTIPLY = {R.drawable.abc_popup_background_mtrl_mult, R.drawable.abc_cab_background_internal_bg, R.drawable.abc_menu_hardkey_panel_mtrl_mult};
    private static final int[] TINT_COLOR_CONTROL_ACTIVATED = {R.drawable.abc_textfield_activated_mtrl_alpha, R.drawable.abc_textfield_search_activated_mtrl_alpha, R.drawable.abc_cab_background_top_mtrl_alpha, R.drawable.abc_text_cursor_mtrl_alpha};
    private static final int[] TINT_COLOR_CONTROL_NORMAL = {R.drawable.abc_ic_ab_back_mtrl_am_alpha, R.drawable.abc_ic_go_search_api_mtrl_alpha, R.drawable.abc_ic_search_api_mtrl_alpha, R.drawable.abc_ic_commit_search_api_mtrl_alpha, R.drawable.abc_ic_clear_mtrl_alpha, R.drawable.abc_ic_menu_share_mtrl_alpha, R.drawable.abc_ic_menu_copy_mtrl_am_alpha, R.drawable.abc_ic_menu_cut_mtrl_alpha, R.drawable.abc_ic_menu_selectall_mtrl_alpha, R.drawable.abc_ic_menu_paste_mtrl_am_alpha, R.drawable.abc_ic_menu_moreoverflow_mtrl_alpha, R.drawable.abc_ic_voice_search_api_mtrl_alpha, R.drawable.abc_textfield_search_default_mtrl_alpha, R.drawable.abc_textfield_default_mtrl_alpha, R.drawable.abc_ab_share_pack_mtrl_alpha};
    private static final int[] TINT_COLOR_CONTROL_STATE_LIST = {R.drawable.abc_edit_text_material, R.drawable.abc_tab_indicator_material, R.drawable.abc_textfield_search_material, R.drawable.abc_spinner_mtrl_am_alpha, R.drawable.abc_btn_check_material, R.drawable.abc_btn_radio_material, R.drawable.abc_spinner_textfield_background_material, R.drawable.abc_ratingbar_full_material, R.drawable.abc_switch_track_mtrl_alpha, R.drawable.abc_switch_thumb_material, R.drawable.abc_btn_default_mtrl_shape, R.drawable.abc_btn_borderless_material};
    private final SparseArray<ColorStateList> mColorStateLists = new SparseArray<>();
    private final Context mContext;
    private ColorStateList mDefaultColorStateList;
    private final Resources mResources;
    private final TypedValue mTypedValue;

    private static class ColorFilterLruCache extends LruCache<Integer, PorterDuffColorFilter> {
        public ColorFilterLruCache(int maxSize) {
            super(maxSize);
        }

        /* access modifiers changed from: 0000 */
        public PorterDuffColorFilter get(int color, Mode mode) {
            return (PorterDuffColorFilter) get(Integer.valueOf(generateCacheKey(color, mode)));
        }

        /* access modifiers changed from: 0000 */
        public PorterDuffColorFilter put(int color, Mode mode, PorterDuffColorFilter filter) {
            return (PorterDuffColorFilter) put(Integer.valueOf(generateCacheKey(color, mode)), filter);
        }

        private static int generateCacheKey(int color, Mode mode) {
            return ((color + 31) * 31) + mode.hashCode();
        }
    }

    public static Drawable getDrawable(Context context, int resId) {
        if (!isInTintList(resId)) {
            return ContextCompat.getDrawable(context, resId);
        }
        return (context instanceof TintContextWrapper ? ((TintContextWrapper) context).getTintManager() : new TintManager(context)).getDrawable(resId);
    }

    public TintManager(Context context) {
        this.mContext = context;
        this.mTypedValue = new TypedValue();
        this.mResources = new TintResources(context.getResources(), this);
    }

    /* access modifiers changed from: 0000 */
    public Resources getResources() {
        return this.mResources;
    }

    public Drawable getDrawable(int resId) {
        Drawable drawable = ContextCompat.getDrawable(this.mContext, resId);
        if (drawable == null) {
            return drawable;
        }
        Drawable drawable2 = drawable.mutate();
        if (arrayContains(TINT_COLOR_CONTROL_STATE_LIST, resId)) {
            ColorStateList colorStateList = getColorStateListForKnownDrawableId(resId);
            Mode tintMode = DEFAULT_MODE;
            if (resId == R.drawable.abc_switch_thumb_material) {
                tintMode = Mode.MULTIPLY;
            }
            if (colorStateList == null) {
                return drawable2;
            }
            DrawableCompat.setTintList(drawable2, colorStateList);
            DrawableCompat.setTintMode(drawable2, tintMode);
            return drawable2;
        } else if (arrayContains(CONTAINERS_WITH_TINT_CHILDREN, resId)) {
            return this.mResources.getDrawable(resId);
        } else {
            tintDrawable(resId, drawable2);
            return drawable2;
        }
    }

    /* access modifiers changed from: 0000 */
    public void tintDrawable(int resId, Drawable drawable) {
        Mode tintMode = null;
        boolean colorAttrSet = false;
        int colorAttr = 0;
        int alpha = -1;
        if (arrayContains(TINT_COLOR_CONTROL_NORMAL, resId)) {
            colorAttr = R.attr.colorControlNormal;
            colorAttrSet = true;
        } else if (arrayContains(TINT_COLOR_CONTROL_ACTIVATED, resId)) {
            colorAttr = R.attr.colorControlActivated;
            colorAttrSet = true;
        } else if (arrayContains(TINT_COLOR_BACKGROUND_MULTIPLY, resId)) {
            colorAttr = 16842801;
            colorAttrSet = true;
            tintMode = Mode.MULTIPLY;
        } else if (resId == R.drawable.abc_list_divider_mtrl_alpha) {
            colorAttr = 16842800;
            colorAttrSet = true;
            alpha = Math.round(40.8f);
        }
        if (colorAttrSet) {
            if (tintMode == null) {
                tintMode = DEFAULT_MODE;
            }
            tintDrawableUsingColorFilter(drawable, ThemeUtils.getThemeAttrColor(this.mContext, colorAttr), tintMode);
            if (alpha != -1) {
                drawable.setAlpha(alpha);
            }
        }
    }

    private static boolean arrayContains(int[] array, int value) {
        for (int id : array) {
            if (id == value) {
                return true;
            }
        }
        return false;
    }

    private static boolean isInTintList(int drawableId) {
        return arrayContains(TINT_COLOR_BACKGROUND_MULTIPLY, drawableId) || arrayContains(TINT_COLOR_CONTROL_NORMAL, drawableId) || arrayContains(TINT_COLOR_CONTROL_ACTIVATED, drawableId) || arrayContains(TINT_COLOR_CONTROL_STATE_LIST, drawableId) || arrayContains(CONTAINERS_WITH_TINT_CHILDREN, drawableId);
    }

    /* access modifiers changed from: 0000 */
    public ColorStateList getColorStateList(int resId) {
        if (arrayContains(TINT_COLOR_CONTROL_STATE_LIST, resId)) {
            return getColorStateListForKnownDrawableId(resId);
        }
        return null;
    }

    private ColorStateList getColorStateListForKnownDrawableId(int resId) {
        ColorStateList colorStateList = (ColorStateList) this.mColorStateLists.get(resId);
        if (colorStateList == null) {
            if (resId == R.drawable.abc_edit_text_material) {
                colorStateList = createEditTextColorStateList();
            } else if (resId == R.drawable.abc_switch_track_mtrl_alpha) {
                colorStateList = createSwitchTrackColorStateList();
            } else if (resId == R.drawable.abc_switch_thumb_material) {
                colorStateList = createSwitchThumbColorStateList();
            } else if (resId == R.drawable.abc_btn_default_mtrl_shape || resId == R.drawable.abc_btn_borderless_material) {
                colorStateList = createButtonColorStateList();
            } else if (resId == R.drawable.abc_spinner_mtrl_am_alpha || resId == R.drawable.abc_spinner_textfield_background_material) {
                colorStateList = createSpinnerColorStateList();
            } else {
                colorStateList = getDefaultColorStateList();
            }
            this.mColorStateLists.append(resId, colorStateList);
        }
        return colorStateList;
    }

    private ColorStateList getDefaultColorStateList() {
        if (this.mDefaultColorStateList == null) {
            int colorControlNormal = ThemeUtils.getThemeAttrColor(this.mContext, R.attr.colorControlNormal);
            int colorControlActivated = ThemeUtils.getThemeAttrColor(this.mContext, R.attr.colorControlActivated);
            int[][] states = new int[7][];
            int[] colors = new int[7];
            states[0] = new int[]{-16842910};
            colors[0] = ThemeUtils.getDisabledThemeAttrColor(this.mContext, R.attr.colorControlNormal);
            int i = 0 + 1;
            states[i] = new int[]{16842908};
            colors[i] = colorControlActivated;
            int i2 = i + 1;
            states[i2] = new int[]{16843518};
            colors[i2] = colorControlActivated;
            int i3 = i2 + 1;
            states[i3] = new int[]{16842919};
            colors[i3] = colorControlActivated;
            int i4 = i3 + 1;
            states[i4] = new int[]{16842912};
            colors[i4] = colorControlActivated;
            int i5 = i4 + 1;
            states[i5] = new int[]{16842913};
            colors[i5] = colorControlActivated;
            int i6 = i5 + 1;
            states[i6] = new int[0];
            colors[i6] = colorControlNormal;
            int i7 = i6 + 1;
            this.mDefaultColorStateList = new ColorStateList(states, colors);
        }
        return this.mDefaultColorStateList;
    }

    private ColorStateList createSwitchTrackColorStateList() {
        int[][] states = new int[3][];
        int[] colors = new int[3];
        states[0] = new int[]{-16842910};
        colors[0] = ThemeUtils.getThemeAttrColor(this.mContext, 16842800, 0.1f);
        int i = 0 + 1;
        states[i] = new int[]{16842912};
        colors[i] = ThemeUtils.getThemeAttrColor(this.mContext, R.attr.colorControlActivated, 0.3f);
        int i2 = i + 1;
        states[i2] = new int[0];
        colors[i2] = ThemeUtils.getThemeAttrColor(this.mContext, 16842800, 0.3f);
        int i3 = i2 + 1;
        return new ColorStateList(states, colors);
    }

    private ColorStateList createSwitchThumbColorStateList() {
        int[][] states = new int[3][];
        int[] colors = new int[3];
        ColorStateList thumbColor = ThemeUtils.getThemeAttrColorStateList(this.mContext, R.attr.colorSwitchThumbNormal);
        if (thumbColor == null || !thumbColor.isStateful()) {
            states[0] = new int[]{-16842910};
            colors[0] = ThemeUtils.getDisabledThemeAttrColor(this.mContext, R.attr.colorSwitchThumbNormal);
            int i = 0 + 1;
            states[i] = new int[]{16842912};
            colors[i] = ThemeUtils.getThemeAttrColor(this.mContext, R.attr.colorControlActivated);
            int i2 = i + 1;
            states[i2] = new int[0];
            colors[i2] = ThemeUtils.getThemeAttrColor(this.mContext, R.attr.colorSwitchThumbNormal);
            int i3 = i2 + 1;
        } else {
            states[0] = new int[]{-16842910};
            colors[0] = thumbColor.getColorForState(states[0], 0);
            int i4 = 0 + 1;
            states[i4] = new int[]{16842912};
            colors[i4] = ThemeUtils.getThemeAttrColor(this.mContext, R.attr.colorControlActivated);
            int i5 = i4 + 1;
            states[i5] = new int[0];
            colors[i5] = thumbColor.getDefaultColor();
            int i6 = i5 + 1;
        }
        return new ColorStateList(states, colors);
    }

    private ColorStateList createEditTextColorStateList() {
        int[][] states = new int[3][];
        int[] colors = new int[3];
        states[0] = new int[]{-16842910};
        colors[0] = ThemeUtils.getDisabledThemeAttrColor(this.mContext, R.attr.colorControlNormal);
        int i = 0 + 1;
        states[i] = new int[]{-16842919, -16842908};
        colors[i] = ThemeUtils.getThemeAttrColor(this.mContext, R.attr.colorControlNormal);
        int i2 = i + 1;
        states[i2] = new int[0];
        colors[i2] = ThemeUtils.getThemeAttrColor(this.mContext, R.attr.colorControlActivated);
        int i3 = i2 + 1;
        return new ColorStateList(states, colors);
    }

    private ColorStateList createButtonColorStateList() {
        int[][] states = new int[4][];
        int[] colors = new int[4];
        states[0] = new int[]{-16842910};
        colors[0] = ThemeUtils.getDisabledThemeAttrColor(this.mContext, R.attr.colorButtonNormal);
        int i = 0 + 1;
        states[i] = new int[]{16842919};
        colors[i] = ThemeUtils.getThemeAttrColor(this.mContext, R.attr.colorControlHighlight);
        int i2 = i + 1;
        states[i2] = new int[]{16842908};
        colors[i2] = ThemeUtils.getThemeAttrColor(this.mContext, R.attr.colorControlHighlight);
        int i3 = i2 + 1;
        states[i3] = new int[0];
        colors[i3] = ThemeUtils.getThemeAttrColor(this.mContext, R.attr.colorButtonNormal);
        int i4 = i3 + 1;
        return new ColorStateList(states, colors);
    }

    private ColorStateList createSpinnerColorStateList() {
        int[][] states = new int[3][];
        int[] colors = new int[3];
        states[0] = new int[]{-16842910};
        colors[0] = ThemeUtils.getDisabledThemeAttrColor(this.mContext, R.attr.colorControlNormal);
        int i = 0 + 1;
        states[i] = new int[]{-16842919, -16842908};
        colors[i] = ThemeUtils.getThemeAttrColor(this.mContext, R.attr.colorControlNormal);
        int i2 = i + 1;
        states[i2] = new int[0];
        colors[i2] = ThemeUtils.getThemeAttrColor(this.mContext, R.attr.colorControlActivated);
        int i3 = i2 + 1;
        return new ColorStateList(states, colors);
    }

    public static void tintViewBackground(View view, TintInfo tint) {
        Drawable background = view.getBackground();
        if (tint.mTintList != null) {
            tintDrawableUsingColorFilter(background, tint.mTintList.getColorForState(view.getDrawableState(), tint.mTintList.getDefaultColor()), tint.mTintMode != null ? tint.mTintMode : DEFAULT_MODE);
        } else {
            background.clearColorFilter();
        }
    }

    private static void tintDrawableUsingColorFilter(Drawable drawable, int color, Mode mode) {
        PorterDuffColorFilter filter = COLOR_FILTER_CACHE.get(color, mode);
        if (filter == null) {
            filter = new PorterDuffColorFilter(color, mode);
            COLOR_FILTER_CACHE.put(color, mode, filter);
        }
        drawable.setColorFilter(filter);
    }
}
