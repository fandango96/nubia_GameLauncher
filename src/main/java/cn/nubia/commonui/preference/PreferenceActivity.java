package cn.nubia.commonui.preference;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentBreadCrumbs;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.BadParcelableException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceFragment.OnPreferenceStartFragmentCallback;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.nubia.commonui.R;
import cn.nubia.commonui.ReflectUtils;
import cn.nubia.commonui.actionbar.app.ActionBarListActivity;
import cn.nubia.gamelauncherx.commoninterface.NeoGameDBColumns;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class PreferenceActivity extends ActionBarListActivity implements OnPreferenceStartFragmentCallback {
    private static final String BACK_STACK_PREFS = ":android:prefs";
    private static final String CUR_HEADER_TAG = ":android:cur_header";
    public static final String EXTRA_NO_HEADERS = ":android:no_headers";
    private static final String EXTRA_PREFS_SET_BACK_TEXT = "extra_prefs_set_back_text";
    private static final String EXTRA_PREFS_SET_NEXT_TEXT = "extra_prefs_set_next_text";
    private static final String EXTRA_PREFS_SHOW_BUTTON_BAR = "extra_prefs_show_button_bar";
    private static final String EXTRA_PREFS_SHOW_SKIP = "extra_prefs_show_skip";
    public static final String EXTRA_SHOW_FRAGMENT = ":android:show_fragment";
    public static final String EXTRA_SHOW_FRAGMENT_ARGUMENTS = ":android:show_fragment_args";
    public static final String EXTRA_SHOW_FRAGMENT_SHORT_TITLE = ":android:show_fragment_short_title";
    public static final String EXTRA_SHOW_FRAGMENT_TITLE = ":android:show_fragment_title";
    private static final int FIRST_REQUEST_CODE = 100;
    private static final String HEADERS_TAG = ":android:headers";
    public static final long HEADER_ID_UNDEFINED = -1;
    private static final int MSG_BIND_PREFERENCES = 1;
    private static final int MSG_BUILD_HEADERS = 2;
    private static final String PREFERENCES_TAG = ":android:preferences";
    private static final String TAG = "NubiaWidget";
    /* access modifiers changed from: private */
    public Header mCurHeader;
    private FragmentBreadCrumbs mFragmentBreadCrumbs;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    PreferenceActivity.this.bindPreferences();
                    return;
                case 2:
                    ArrayList<Header> oldHeaders = new ArrayList<>(PreferenceActivity.this.mHeaders);
                    PreferenceActivity.this.mHeaders.clear();
                    PreferenceActivity.this.onBuildHeaders(PreferenceActivity.this.mHeaders);
                    if (PreferenceActivity.this.mAdapter instanceof BaseAdapter) {
                        ((BaseAdapter) PreferenceActivity.this.mAdapter).notifyDataSetChanged();
                    }
                    Header header = PreferenceActivity.this.onGetNewHeader();
                    if (header != null && header.fragment != null) {
                        Header mappedHeader = PreferenceActivity.this.findBestMatchingHeader(header, oldHeaders);
                        if (mappedHeader == null || PreferenceActivity.this.mCurHeader != mappedHeader) {
                            PreferenceActivity.this.switchToHeader(header);
                            return;
                        }
                        return;
                    } else if (PreferenceActivity.this.mCurHeader != null) {
                        Header mappedHeader2 = PreferenceActivity.this.findBestMatchingHeader(PreferenceActivity.this.mCurHeader, PreferenceActivity.this.mHeaders);
                        if (mappedHeader2 != null) {
                            PreferenceActivity.this.setSelectedHeader(mappedHeader2);
                            return;
                        }
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }
    };
    /* access modifiers changed from: private */
    public final ArrayList<Header> mHeaders = new ArrayList<>();
    private FrameLayout mListFooter;
    private Button mNextButton;
    private int mPreferenceHeaderItemResId = 0;
    private boolean mPreferenceHeaderRemoveEmptyIcon = false;
    private PreferenceManager mPreferenceManager;
    private ViewGroup mPrefsContainer;
    private Bundle mSavedInstanceState;
    private boolean mSinglePane;

    public static final class Header implements Parcelable {
        public static final Creator<Header> CREATOR = new Creator<Header>() {
            public Header createFromParcel(Parcel source) {
                return new Header(source);
            }

            public Header[] newArray(int size) {
                return new Header[size];
            }
        };
        public CharSequence breadCrumbShortTitle;
        public int breadCrumbShortTitleRes;
        public CharSequence breadCrumbTitle;
        public int breadCrumbTitleRes;
        public Bundle extras;
        public String fragment;
        public Bundle fragmentArguments;
        public int iconRes;
        public long id = -1;
        public Intent intent;
        public CharSequence summary;
        public int summaryRes;
        public CharSequence title;
        public int titleRes;

        public Header() {
        }

        public CharSequence getTitle(Resources res) {
            if (this.titleRes != 0) {
                return res.getText(this.titleRes);
            }
            return this.title;
        }

        public CharSequence getSummary(Resources res) {
            if (this.summaryRes != 0) {
                return res.getText(this.summaryRes);
            }
            return this.summary;
        }

        public CharSequence getBreadCrumbTitle(Resources res) {
            if (this.breadCrumbTitleRes != 0) {
                return res.getText(this.breadCrumbTitleRes);
            }
            return this.breadCrumbTitle;
        }

        public CharSequence getBreadCrumbShortTitle(Resources res) {
            if (this.breadCrumbShortTitleRes != 0) {
                return res.getText(this.breadCrumbShortTitleRes);
            }
            return this.breadCrumbShortTitle;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.id);
            dest.writeInt(this.titleRes);
            TextUtils.writeToParcel(this.title, dest, flags);
            dest.writeInt(this.summaryRes);
            TextUtils.writeToParcel(this.summary, dest, flags);
            dest.writeInt(this.breadCrumbTitleRes);
            TextUtils.writeToParcel(this.breadCrumbTitle, dest, flags);
            dest.writeInt(this.breadCrumbShortTitleRes);
            TextUtils.writeToParcel(this.breadCrumbShortTitle, dest, flags);
            dest.writeInt(this.iconRes);
            dest.writeString(this.fragment);
            dest.writeBundle(this.fragmentArguments);
            if (this.intent != null) {
                dest.writeInt(1);
                this.intent.writeToParcel(dest, flags);
            } else {
                dest.writeInt(0);
            }
            dest.writeBundle(this.extras);
        }

        public void readFromParcel(Parcel in) {
            this.id = in.readLong();
            this.titleRes = in.readInt();
            this.title = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
            this.summaryRes = in.readInt();
            this.summary = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
            this.breadCrumbTitleRes = in.readInt();
            this.breadCrumbTitle = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
            this.breadCrumbShortTitleRes = in.readInt();
            this.breadCrumbShortTitle = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
            this.iconRes = in.readInt();
            this.fragment = in.readString();
            this.fragmentArguments = in.readBundle();
            if (in.readInt() != 0) {
                this.intent = (Intent) Intent.CREATOR.createFromParcel(in);
            }
            this.extras = in.readBundle();
        }

        Header(Parcel in) {
            readFromParcel(in);
        }
    }

    private static class HeaderAdapter extends ArrayAdapter<Header> {
        private LayoutInflater mInflater;
        private int mLayoutResId;
        private boolean mRemoveIconIfEmpty;

        private static class HeaderViewHolder {
            ImageView icon;
            TextView summary;
            TextView title;

            private HeaderViewHolder() {
            }
        }

        public HeaderAdapter(Context context, List<Header> objects, int layoutResId, boolean removeIconBehavior) {
            super(context, 0, objects);
            this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
            this.mLayoutResId = layoutResId;
            this.mRemoveIconIfEmpty = removeIconBehavior;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            HeaderViewHolder holder;
            if (convertView == null) {
                view = this.mInflater.inflate(this.mLayoutResId, parent, false);
                holder = new HeaderViewHolder();
                holder.icon = (ImageView) view.findViewById(((Integer) ReflectUtils.getFromInternalR(NeoGameDBColumns._ID, NeoGameDBColumns.ICON)).intValue());
                holder.title = (TextView) view.findViewById(((Integer) ReflectUtils.getFromInternalR(NeoGameDBColumns._ID, NeoGameDBColumns.TITLE)).intValue());
                holder.summary = (TextView) view.findViewById(((Integer) ReflectUtils.getFromInternalR(NeoGameDBColumns._ID, "summary")).intValue());
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (HeaderViewHolder) view.getTag();
            }
            Header header = (Header) getItem(position);
            if (!this.mRemoveIconIfEmpty) {
                holder.icon.setImageResource(header.iconRes);
            } else if (header.iconRes == 0) {
                holder.icon.setVisibility(8);
            } else {
                holder.icon.setVisibility(0);
                holder.icon.setImageResource(header.iconRes);
            }
            holder.title.setText(header.getTitle(getContext().getResources()));
            CharSequence summary = header.getSummary(getContext().getResources());
            if (!TextUtils.isEmpty(summary)) {
                holder.summary.setVisibility(0);
                holder.summary.setText(summary);
            } else {
                holder.summary.setVisibility(8);
            }
            return view;
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypedArray sa = obtainStyledAttributes(null, (int[]) ReflectUtils.getStyleable("PreferenceActivity"), ((Integer) ReflectUtils.getFromInternalR("attr", "preferenceActivityStyle")).intValue(), 0);
        int layoutResId = sa.getResourceId(((Integer) ReflectUtils.getStyleable("PreferenceActivity_layout")).intValue(), ((Integer) ReflectUtils.getFromInternalR("layout", "preference_list_content")).intValue());
        this.mPreferenceHeaderItemResId = sa.getResourceId(((Integer) ReflectUtils.getStyleable("PreferenceActivity_headerLayout")).intValue(), ((Integer) ReflectUtils.getFromInternalR("layout", "preference_header_item")).intValue());
        this.mPreferenceHeaderRemoveEmptyIcon = sa.getBoolean(((Integer) ReflectUtils.getStyleable("PreferenceActivity_headerRemoveIconIfEmpty")).intValue(), false);
        sa.recycle();
        setContentView(layoutResId);
        this.mListFooter = (FrameLayout) findViewById(((Integer) ReflectUtils.getFromInternalR(NeoGameDBColumns._ID, "list_footer")).intValue());
        this.mPrefsContainer = (ViewGroup) findViewById(((Integer) ReflectUtils.getFromInternalR(NeoGameDBColumns._ID, "prefs_frame")).intValue());
        this.mSinglePane = onIsHidingHeaders() || !onIsMultiPane();
        String initialFragment = getIntent().getStringExtra(EXTRA_SHOW_FRAGMENT);
        Bundle initialArguments = getIntent().getBundleExtra(EXTRA_SHOW_FRAGMENT_ARGUMENTS);
        int initialTitle = getIntent().getIntExtra(EXTRA_SHOW_FRAGMENT_TITLE, 0);
        int initialShortTitle = getIntent().getIntExtra(EXTRA_SHOW_FRAGMENT_SHORT_TITLE, 0);
        if (savedInstanceState != null) {
            ArrayList<Header> headers = savedInstanceState.getParcelableArrayList(HEADERS_TAG);
            if (headers != null) {
                this.mHeaders.addAll(headers);
                int curHeader = savedInstanceState.getInt(CUR_HEADER_TAG, -1);
                if (curHeader >= 0 && curHeader < this.mHeaders.size()) {
                    setSelectedHeader((Header) this.mHeaders.get(curHeader));
                }
            }
        } else if (initialFragment == null || !this.mSinglePane) {
            onBuildHeaders(this.mHeaders);
            if (this.mHeaders.size() > 0 && !this.mSinglePane) {
                if (initialFragment == null) {
                    switchToHeader(onGetInitialHeader());
                } else {
                    switchToHeader(initialFragment, initialArguments);
                }
            }
        } else {
            switchToHeader(initialFragment, initialArguments);
            if (initialTitle != 0) {
                showBreadCrumbs(getText(initialTitle), initialShortTitle != 0 ? getText(initialShortTitle) : null);
            }
        }
        if (initialFragment != null && this.mSinglePane) {
            findViewById(((Integer) ReflectUtils.getFromInternalR(NeoGameDBColumns._ID, "headers")).intValue()).setVisibility(8);
            this.mPrefsContainer.setVisibility(0);
            if (initialTitle != 0) {
                showBreadCrumbs(getText(initialTitle), initialShortTitle != 0 ? getText(initialShortTitle) : null);
            }
        } else if (this.mHeaders.size() > 0) {
            HeaderAdapter headerAdapter = new HeaderAdapter(this, this.mHeaders, this.mPreferenceHeaderItemResId, this.mPreferenceHeaderRemoveEmptyIcon);
            setListAdapter(headerAdapter);
            if (!this.mSinglePane) {
                getListView().setChoiceMode(1);
                if (this.mCurHeader != null) {
                    setSelectedHeader(this.mCurHeader);
                }
                this.mPrefsContainer.setVisibility(0);
            }
        } else {
            setContentView(R.layout.nubia_preference_list_content_single);
            this.mListFooter = (FrameLayout) findViewById(((Integer) ReflectUtils.getFromInternalR(NeoGameDBColumns._ID, "list_footer")).intValue());
            this.mPrefsContainer = (ViewGroup) findViewById(((Integer) ReflectUtils.getFromInternalR(NeoGameDBColumns._ID, "prefs")).intValue());
            this.mPreferenceManager = (PreferenceManager) ReflectUtils.newInstanceByConstructor("android.preference.PreferenceManager", new Object[]{this, Integer.valueOf(100)}, Activity.class, Integer.TYPE);
        }
        Intent intent = getIntent();
        if (intent.getBooleanExtra(EXTRA_PREFS_SHOW_BUTTON_BAR, false)) {
            findViewById(((Integer) ReflectUtils.getFromInternalR(NeoGameDBColumns._ID, "button_bar")).intValue()).setVisibility(0);
            Button backButton = (Button) findViewById(((Integer) ReflectUtils.getFromInternalR(NeoGameDBColumns._ID, "back_button")).intValue());
            AnonymousClass2 r0 = new OnClickListener() {
                public void onClick(View v) {
                    PreferenceActivity.this.setResult(0);
                    PreferenceActivity.this.finish();
                }
            };
            backButton.setOnClickListener(r0);
            Button skipButton = (Button) findViewById(((Integer) ReflectUtils.getFromInternalR(NeoGameDBColumns._ID, "skip_button")).intValue());
            AnonymousClass3 r02 = new OnClickListener() {
                public void onClick(View v) {
                    PreferenceActivity.this.setResult(-1);
                    PreferenceActivity.this.finish();
                }
            };
            skipButton.setOnClickListener(r02);
            this.mNextButton = (Button) findViewById(((Integer) ReflectUtils.getFromInternalR(NeoGameDBColumns._ID, "next_button")).intValue());
            Button button = this.mNextButton;
            AnonymousClass4 r03 = new OnClickListener() {
                public void onClick(View v) {
                    PreferenceActivity.this.setResult(-1);
                    PreferenceActivity.this.finish();
                }
            };
            button.setOnClickListener(r03);
            if (intent.hasExtra(EXTRA_PREFS_SET_NEXT_TEXT)) {
                String buttonText = intent.getStringExtra(EXTRA_PREFS_SET_NEXT_TEXT);
                if (TextUtils.isEmpty(buttonText)) {
                    this.mNextButton.setVisibility(8);
                } else {
                    this.mNextButton.setText(buttonText);
                }
            }
            if (intent.hasExtra(EXTRA_PREFS_SET_BACK_TEXT)) {
                String buttonText2 = intent.getStringExtra(EXTRA_PREFS_SET_BACK_TEXT);
                if (TextUtils.isEmpty(buttonText2)) {
                    backButton.setVisibility(8);
                } else {
                    backButton.setText(buttonText2);
                }
            }
            if (intent.getBooleanExtra(EXTRA_PREFS_SHOW_SKIP, false)) {
                skipButton.setVisibility(0);
            }
        }
    }

    public boolean hasHeaders() {
        return getListView().getVisibility() == 0 && this.mPreferenceManager == null;
    }

    public List<Header> getHeaders() {
        return this.mHeaders;
    }

    public boolean isMultiPane() {
        return hasHeaders() && this.mPrefsContainer.getVisibility() == 0;
    }

    public boolean onIsMultiPane() {
        return getResources().getBoolean(((Integer) ReflectUtils.getFromInternalR("bool", "preferences_prefer_dual_pane")).intValue());
    }

    public boolean onIsHidingHeaders() {
        return getIntent().getBooleanExtra(EXTRA_NO_HEADERS, false);
    }

    public Header onGetInitialHeader() {
        for (int i = 0; i < this.mHeaders.size(); i++) {
            Header h = (Header) this.mHeaders.get(i);
            if (h.fragment != null) {
                return h;
            }
        }
        throw new IllegalStateException("Must have at least one header with a fragment");
    }

    public Header onGetNewHeader() {
        return null;
    }

    public void onBuildHeaders(List<Header> list) {
    }

    public void invalidateHeaders() {
        if (!this.mHandler.hasMessages(2)) {
            this.mHandler.sendEmptyMessage(2);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:118:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x025f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadHeadersFromResource(int r23, java.util.List<cn.nubia.commonui.preference.PreferenceActivity.Header> r24) {
        /*
            r22 = this;
            r16 = 0
            android.content.res.Resources r2 = r22.getResources()     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r0 = r23
            android.content.res.XmlResourceParser r16 = r2.getXml(r0)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            android.util.AttributeSet r8 = android.util.Xml.asAttributeSet(r16)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
        L_0x0010:
            int r19 = r16.next()     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r2 = 1
            r0 = r19
            if (r0 == r2) goto L_0x001e
            r2 = 2
            r0 = r19
            if (r0 != r2) goto L_0x0010
        L_0x001e:
            java.lang.String r14 = r16.getName()     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            java.lang.String r2 = "preference-headers"
            boolean r2 = r2.equals(r14)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            if (r2 != 0) goto L_0x0061
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r3.<init>()     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            java.lang.String r4 = "XML document must start with <preference-headers> tag; found"
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            java.lang.StringBuilder r3 = r3.append(r14)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            java.lang.String r4 = " at "
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            java.lang.String r4 = r16.getPositionDescription()     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            java.lang.String r3 = r3.toString()     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r2.<init>(r3)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            throw r2     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
        L_0x0051:
            r10 = move-exception
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ all -> 0x005a }
            java.lang.String r3 = "Error parsing headers"
            r2.<init>(r3, r10)     // Catch:{ all -> 0x005a }
            throw r2     // Catch:{ all -> 0x005a }
        L_0x005a:
            r2 = move-exception
            if (r16 == 0) goto L_0x0060
            r16.close()
        L_0x0060:
            throw r2
        L_0x0061:
            r9 = 0
            int r15 = r16.getDepth()     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
        L_0x0066:
            int r19 = r16.next()     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r2 = 1
            r0 = r19
            if (r0 == r2) goto L_0x025d
            r2 = 3
            r0 = r19
            if (r0 != r2) goto L_0x007a
            int r2 = r16.getDepth()     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            if (r2 <= r15) goto L_0x025d
        L_0x007a:
            r2 = 3
            r0 = r19
            if (r0 == r2) goto L_0x0066
            r2 = 4
            r0 = r19
            if (r0 == r2) goto L_0x0066
            java.lang.String r14 = r16.getName()     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            java.lang.String r2 = "header"
            boolean r2 = r2.equals(r14)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            if (r2 == 0) goto L_0x0243
            cn.nubia.commonui.preference.PreferenceActivity$Header r11 = new cn.nubia.commonui.preference.PreferenceActivity$Header     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r11.<init>()     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            java.lang.String r2 = "PreferenceHeader"
            java.lang.Object r2 = cn.nubia.commonui.ReflectUtils.getStyleable(r2)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            int[] r2 = (int[]) r2     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            int[] r2 = (int[]) r2     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r0 = r22
            android.content.res.TypedArray r17 = r0.obtainStyledAttributes(r8, r2)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            java.lang.String r2 = "PreferenceHeader_id"
            java.lang.Object r2 = cn.nubia.commonui.ReflectUtils.getStyleable(r2)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            java.lang.Integer r2 = (java.lang.Integer) r2     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            int r2 = r2.intValue()     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r3 = -1
            r0 = r17
            int r2 = r0.getResourceId(r2, r3)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            long r2 = (long) r2     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r11.id = r2     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            java.lang.String r2 = "PreferenceHeader_title"
            java.lang.Object r2 = cn.nubia.commonui.ReflectUtils.getStyleable(r2)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            java.lang.Integer r2 = (java.lang.Integer) r2     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            int r2 = r2.intValue()     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r0 = r17
            android.util.TypedValue r18 = r0.peekValue(r2)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            if (r18 == 0) goto L_0x00e2
            r0 = r18
            int r2 = r0.type     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r3 = 3
            if (r2 != r3) goto L_0x00e2
            r0 = r18
            int r2 = r0.resourceId     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            if (r2 == 0) goto L_0x01e3
            r0 = r18
            int r2 = r0.resourceId     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r11.titleRes = r2     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
        L_0x00e2:
            java.lang.String r2 = "PreferenceHeader_summary"
            java.lang.Object r2 = cn.nubia.commonui.ReflectUtils.getStyleable(r2)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            java.lang.Integer r2 = (java.lang.Integer) r2     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            int r2 = r2.intValue()     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r0 = r17
            android.util.TypedValue r18 = r0.peekValue(r2)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            if (r18 == 0) goto L_0x0109
            r0 = r18
            int r2 = r0.type     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r3 = 3
            if (r2 != r3) goto L_0x0109
            r0 = r18
            int r2 = r0.resourceId     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            if (r2 == 0) goto L_0x01eb
            r0 = r18
            int r2 = r0.resourceId     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r11.summaryRes = r2     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
        L_0x0109:
            java.lang.String r2 = "PreferenceHeader_breadCrumbTitle"
            java.lang.Object r2 = cn.nubia.commonui.ReflectUtils.getStyleable(r2)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            java.lang.Integer r2 = (java.lang.Integer) r2     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            int r2 = r2.intValue()     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r0 = r17
            android.util.TypedValue r18 = r0.peekValue(r2)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            if (r18 == 0) goto L_0x0130
            r0 = r18
            int r2 = r0.type     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r3 = 3
            if (r2 != r3) goto L_0x0130
            r0 = r18
            int r2 = r0.resourceId     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            if (r2 == 0) goto L_0x01f3
            r0 = r18
            int r2 = r0.resourceId     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r11.breadCrumbTitleRes = r2     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
        L_0x0130:
            java.lang.String r2 = "PreferenceHeader_breadCrumbShortTitle"
            java.lang.Object r2 = cn.nubia.commonui.ReflectUtils.getStyleable(r2)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            java.lang.Integer r2 = (java.lang.Integer) r2     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            int r2 = r2.intValue()     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r0 = r17
            android.util.TypedValue r18 = r0.peekValue(r2)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            if (r18 == 0) goto L_0x0157
            r0 = r18
            int r2 = r0.type     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r3 = 3
            if (r2 != r3) goto L_0x0157
            r0 = r18
            int r2 = r0.resourceId     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            if (r2 == 0) goto L_0x01fb
            r0 = r18
            int r2 = r0.resourceId     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r11.breadCrumbShortTitleRes = r2     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
        L_0x0157:
            java.lang.String r2 = "PreferenceHeader_icon"
            java.lang.Object r2 = cn.nubia.commonui.ReflectUtils.getStyleable(r2)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            java.lang.Integer r2 = (java.lang.Integer) r2     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            int r2 = r2.intValue()     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r3 = 0
            r0 = r17
            int r2 = r0.getResourceId(r2, r3)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r11.iconRes = r2     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            java.lang.String r2 = "PreferenceHeader_fragment"
            java.lang.Object r2 = cn.nubia.commonui.ReflectUtils.getStyleable(r2)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            java.lang.Integer r2 = (java.lang.Integer) r2     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            int r2 = r2.intValue()     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r0 = r17
            java.lang.String r2 = r0.getString(r2)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r11.fragment = r2     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r17.recycle()     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            if (r9 != 0) goto L_0x018a
            android.os.Bundle r9 = new android.os.Bundle     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r9.<init>()     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
        L_0x018a:
            int r12 = r16.getDepth()     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
        L_0x018e:
            int r19 = r16.next()     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r2 = 1
            r0 = r19
            if (r0 == r2) goto L_0x0233
            r2 = 3
            r0 = r19
            if (r0 != r2) goto L_0x01a2
            int r2 = r16.getDepth()     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            if (r2 <= r12) goto L_0x0233
        L_0x01a2:
            r2 = 3
            r0 = r19
            if (r0 == r2) goto L_0x018e
            r2 = 4
            r0 = r19
            if (r0 == r2) goto L_0x018e
            java.lang.String r13 = r16.getName()     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            java.lang.String r2 = "extra"
            boolean r2 = r13.equals(r2)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            if (r2 == 0) goto L_0x0203
            android.content.res.Resources r2 = r22.getResources()     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            java.lang.String r3 = "extra"
            r2.parseBundleExtra(r3, r8, r9)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            java.lang.String r2 = "com.android.internal.util.XmlUtils"
            java.lang.String r3 = "skipCurrentTag"
            r4 = 0
            r5 = 1
            r6 = 1
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r7 = 0
            r6[r7] = r16     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r7 = 1
            java.lang.Class[] r7 = new java.lang.Class[r7]     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r20 = 0
            java.lang.Class<android.content.res.XmlResourceParser> r21 = android.content.res.XmlResourceParser.class
            r7[r20] = r21     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            cn.nubia.commonui.ReflectUtils.invoke(r2, r3, r4, r5, r6, r7)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            goto L_0x018e
        L_0x01da:
            r10 = move-exception
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ all -> 0x005a }
            java.lang.String r3 = "Error parsing headers"
            r2.<init>(r3, r10)     // Catch:{ all -> 0x005a }
            throw r2     // Catch:{ all -> 0x005a }
        L_0x01e3:
            r0 = r18
            java.lang.CharSequence r2 = r0.string     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r11.title = r2     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            goto L_0x00e2
        L_0x01eb:
            r0 = r18
            java.lang.CharSequence r2 = r0.string     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r11.summary = r2     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            goto L_0x0109
        L_0x01f3:
            r0 = r18
            java.lang.CharSequence r2 = r0.string     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r11.breadCrumbTitle = r2     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            goto L_0x0130
        L_0x01fb:
            r0 = r18
            java.lang.CharSequence r2 = r0.string     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r11.breadCrumbShortTitle = r2     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            goto L_0x0157
        L_0x0203:
            java.lang.String r2 = "intent"
            boolean r2 = r13.equals(r2)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            if (r2 == 0) goto L_0x0219
            android.content.res.Resources r2 = r22.getResources()     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r0 = r16
            android.content.Intent r2 = android.content.Intent.parseIntent(r2, r0, r8)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r11.intent = r2     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            goto L_0x018e
        L_0x0219:
            java.lang.String r2 = "com.android.internal.util.XmlUtils"
            java.lang.String r3 = "skipCurrentTag"
            r4 = 0
            r5 = 1
            r6 = 1
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r7 = 0
            r6[r7] = r16     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r7 = 1
            java.lang.Class[] r7 = new java.lang.Class[r7]     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r20 = 0
            java.lang.Class<android.content.res.XmlResourceParser> r21 = android.content.res.XmlResourceParser.class
            r7[r20] = r21     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            cn.nubia.commonui.ReflectUtils.invoke(r2, r3, r4, r5, r6, r7)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            goto L_0x018e
        L_0x0233:
            int r2 = r9.size()     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            if (r2 <= 0) goto L_0x023c
            r11.fragmentArguments = r9     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r9 = 0
        L_0x023c:
            r0 = r24
            r0.add(r11)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            goto L_0x0066
        L_0x0243:
            java.lang.String r2 = "com.android.internal.util.XmlUtils"
            java.lang.String r3 = "skipCurrentTag"
            r4 = 0
            r5 = 1
            r6 = 1
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r7 = 0
            r6[r7] = r16     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r7 = 1
            java.lang.Class[] r7 = new java.lang.Class[r7]     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            r20 = 0
            java.lang.Class<android.content.res.XmlResourceParser> r21 = android.content.res.XmlResourceParser.class
            r7[r20] = r21     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            cn.nubia.commonui.ReflectUtils.invoke(r2, r3, r4, r5, r6, r7)     // Catch:{ XmlPullParserException -> 0x0051, IOException -> 0x01da }
            goto L_0x0066
        L_0x025d:
            if (r16 == 0) goto L_0x0262
            r16.close()
        L_0x0262:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: cn.nubia.commonui.preference.PreferenceActivity.loadHeadersFromResource(int, java.util.List):void");
    }

    /* access modifiers changed from: protected */
    public boolean isValidFragment(String fragmentName) {
        if (getApplicationInfo().targetSdkVersion < 19) {
            return true;
        }
        throw new RuntimeException("Subclasses of PreferenceActivity must override isValidFragment(String) to verify that the Fragment class is valid! " + getClass().getName() + " has not checked if fragment " + fragmentName + " is valid.");
    }

    public void setListFooter(View view) {
        this.mListFooter.removeAllViews();
        this.mListFooter.addView(view, new LayoutParams(-1, -2));
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        if (this.mPreferenceManager != null) {
            try {
                Method method = this.mPreferenceManager.getClass().getDeclaredMethod("dispatchActivityStop", new Class[0]);
                boolean isAcc = method.isAccessible();
                if (!isAcc) {
                    method.setAccessible(true);
                }
                method.invoke(this.mPreferenceManager, new Object[0]);
                method.setAccessible(isAcc);
            } catch (NoSuchMethodError e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        this.mHandler.removeMessages(1);
        this.mHandler.removeMessages(2);
        super.onDestroy();
        if (this.mPreferenceManager != null) {
            try {
                Method method = this.mPreferenceManager.getClass().getDeclaredMethod("dispatchActivityDestroy", new Class[0]);
                boolean isAcc = method.isAccessible();
                if (!isAcc) {
                    method.setAccessible(true);
                }
                method.invoke(this.mPreferenceManager, new Object[0]);
                method.setAccessible(isAcc);
            } catch (NoSuchMethodError e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.mHeaders.size() > 0) {
            outState.putParcelableArrayList(HEADERS_TAG, this.mHeaders);
            if (this.mCurHeader != null) {
                int index = this.mHeaders.indexOf(this.mCurHeader);
                if (index >= 0) {
                    outState.putInt(CUR_HEADER_TAG, index);
                }
            }
        }
        if (this.mPreferenceManager != null) {
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            if (preferenceScreen != null) {
                Bundle container = new Bundle();
                preferenceScreen.saveHierarchyState(container);
                outState.putBundle(PREFERENCES_TAG, container);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Bundle state) {
        if (this.mPreferenceManager != null) {
            Bundle container = state.getBundle(PREFERENCES_TAG);
            if (container != null) {
                PreferenceScreen preferenceScreen = getPreferenceScreen();
                if (preferenceScreen != null) {
                    try {
                        preferenceScreen.restoreHierarchyState(container);
                    } catch (BadParcelableException e) {
                        Log.e(TAG, "OnResotoreInstanceState Exception", e);
                    }
                    this.mSavedInstanceState = state;
                    return;
                }
            }
        }
        super.onRestoreInstanceState(state);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (this.mPreferenceManager != null) {
            try {
                Method method = this.mPreferenceManager.getClass().getDeclaredMethod("dispatchActivityResult", new Class[]{Integer.TYPE, Integer.TYPE, Intent.class});
                boolean isAcc = method.isAccessible();
                if (!isAcc) {
                    method.setAccessible(true);
                }
                method.invoke(this.mPreferenceManager, new Object[]{Integer.valueOf(requestCode), Integer.valueOf(resultCode), data});
                method.setAccessible(isAcc);
            } catch (NoSuchMethodError e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public void onContentChanged() {
        super.onContentChanged();
        if (this.mPreferenceManager != null) {
            postBindPreferences();
        }
    }

    /* access modifiers changed from: protected */
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (((Boolean) ReflectUtils.invoke(this, "isResumed", true, false)).booleanValue()) {
            super.onListItemClick(l, v, position, id);
            if (this.mAdapter != null) {
                Object item = this.mAdapter.getItem(position);
                if (item instanceof Header) {
                    onHeaderClick((Header) item, position);
                }
            }
        }
    }

    public void onHeaderClick(Header header, int position) {
        if (header.fragment != null) {
            if (this.mSinglePane) {
                int titleRes = header.breadCrumbTitleRes;
                int shortTitleRes = header.breadCrumbShortTitleRes;
                if (titleRes == 0) {
                    titleRes = header.titleRes;
                    shortTitleRes = 0;
                }
                startWithFragment(header.fragment, header.fragmentArguments, null, 0, titleRes, shortTitleRes);
                return;
            }
            switchToHeader(header);
        } else if (header.intent != null) {
            startActivity(header.intent);
        }
    }

    public Intent onBuildStartFragmentIntent(String fragmentName, Bundle args, int titleRes, int shortTitleRes) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setClass(this, getClass());
        intent.putExtra(EXTRA_SHOW_FRAGMENT, fragmentName);
        intent.putExtra(EXTRA_SHOW_FRAGMENT_ARGUMENTS, args);
        intent.putExtra(EXTRA_SHOW_FRAGMENT_TITLE, titleRes);
        intent.putExtra(EXTRA_SHOW_FRAGMENT_SHORT_TITLE, shortTitleRes);
        intent.putExtra(EXTRA_NO_HEADERS, true);
        return intent;
    }

    public void startWithFragment(String fragmentName, Bundle args, Fragment resultTo, int resultRequestCode) {
        startWithFragment(fragmentName, args, resultTo, resultRequestCode, 0, 0);
    }

    public void startWithFragment(String fragmentName, Bundle args, Fragment resultTo, int resultRequestCode, int titleRes, int shortTitleRes) {
        Intent intent = onBuildStartFragmentIntent(fragmentName, args, titleRes, shortTitleRes);
        if (resultTo == null) {
            startActivity(intent);
        } else {
            resultTo.startActivityForResult(intent, resultRequestCode);
        }
    }

    public void showBreadCrumbs(CharSequence title, CharSequence shortTitle) {
        if (this.mFragmentBreadCrumbs == null) {
            try {
                this.mFragmentBreadCrumbs = (FragmentBreadCrumbs) findViewById(16908310);
                if (this.mFragmentBreadCrumbs != null) {
                    if (this.mSinglePane) {
                        this.mFragmentBreadCrumbs.setVisibility(8);
                        View bcSection = findViewById(((Integer) ReflectUtils.getFromInternalR(NeoGameDBColumns._ID, "breadcrumb_section")).intValue());
                        if (bcSection != null) {
                            bcSection.setVisibility(8);
                        }
                        setTitle(title);
                    }
                    this.mFragmentBreadCrumbs.setMaxVisible(2);
                    this.mFragmentBreadCrumbs.setActivity(this);
                } else if (title != null) {
                    setTitle(title);
                    return;
                } else {
                    return;
                }
            } catch (ClassCastException e) {
                setTitle(title);
                return;
            }
        }
        if (this.mFragmentBreadCrumbs.getVisibility() != 0) {
            setTitle(title);
            return;
        }
        this.mFragmentBreadCrumbs.setTitle(title, shortTitle);
        this.mFragmentBreadCrumbs.setParentTitle(null, null, null);
    }

    public void setParentTitle(CharSequence title, CharSequence shortTitle, OnClickListener listener) {
        if (this.mFragmentBreadCrumbs != null) {
            this.mFragmentBreadCrumbs.setParentTitle(title, shortTitle, listener);
        }
    }

    /* access modifiers changed from: 0000 */
    public void setSelectedHeader(Header header) {
        this.mCurHeader = header;
        int index = this.mHeaders.indexOf(header);
        if (index >= 0) {
            getListView().setItemChecked(index, true);
        } else {
            getListView().clearChoices();
        }
        showBreadCrumbs(header);
    }

    /* access modifiers changed from: 0000 */
    public void showBreadCrumbs(Header header) {
        if (header != null) {
            CharSequence title = header.getBreadCrumbTitle(getResources());
            if (title == null) {
                title = header.getTitle(getResources());
            }
            if (title == null) {
                title = getTitle();
            }
            showBreadCrumbs(title, header.getBreadCrumbShortTitle(getResources()));
            return;
        }
        showBreadCrumbs(getTitle(), null);
    }

    private void switchToHeaderInner(String fragmentName, Bundle args) {
        getFragmentManager().popBackStack(BACK_STACK_PREFS, 1);
        if (!isValidFragment(fragmentName)) {
            throw new IllegalArgumentException("Invalid fragment for this activity: " + fragmentName);
        }
        Fragment f = Fragment.instantiate(this, fragmentName, args);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(((Integer) ReflectUtils.getFromInternalR(NeoGameDBColumns._ID, "prefs")).intValue(), f);
        transaction.commitAllowingStateLoss();
    }

    public void switchToHeader(String fragmentName, Bundle args) {
        Header selectedHeader = null;
        int i = 0;
        while (true) {
            if (i >= this.mHeaders.size()) {
                break;
            } else if (fragmentName.equals(((Header) this.mHeaders.get(i)).fragment)) {
                selectedHeader = (Header) this.mHeaders.get(i);
                break;
            } else {
                i++;
            }
        }
        setSelectedHeader(selectedHeader);
        switchToHeaderInner(fragmentName, args);
    }

    public void switchToHeader(Header header) {
        if (this.mCurHeader == header) {
            getFragmentManager().popBackStack(BACK_STACK_PREFS, 1);
        } else if (header.fragment == null) {
            throw new IllegalStateException("can't switch to header that has no fragment");
        } else {
            switchToHeaderInner(header.fragment, header.fragmentArguments);
            setSelectedHeader(header);
        }
    }

    /* access modifiers changed from: 0000 */
    public Header findBestMatchingHeader(Header cur, ArrayList<Header> from) {
        Header oh;
        ArrayList<Header> matches = new ArrayList<>();
        int j = 0;
        while (true) {
            if (j >= from.size()) {
                break;
            }
            oh = (Header) from.get(j);
            if (cur == oh || (cur.id != -1 && cur.id == oh.id)) {
                matches.clear();
                matches.add(oh);
            } else {
                if (cur.fragment != null) {
                    if (cur.fragment.equals(oh.fragment)) {
                        matches.add(oh);
                    }
                } else if (cur.intent != null) {
                    if (cur.intent.equals(oh.intent)) {
                        matches.add(oh);
                    }
                } else if (cur.title != null && cur.title.equals(oh.title)) {
                    matches.add(oh);
                }
                j++;
            }
        }
        matches.clear();
        matches.add(oh);
        int NM = matches.size();
        if (NM == 1) {
            return (Header) matches.get(0);
        }
        if (NM > 1) {
            for (int j2 = 0; j2 < NM; j2++) {
                Header oh2 = (Header) matches.get(j2);
                if (cur.fragmentArguments != null && cur.fragmentArguments.equals(oh2.fragmentArguments)) {
                    return oh2;
                }
                if (cur.extras != null && cur.extras.equals(oh2.extras)) {
                    return oh2;
                }
                if (cur.title != null && cur.title.equals(oh2.title)) {
                    return oh2;
                }
            }
        }
        return null;
    }

    public void startPreferenceFragment(Fragment fragment, boolean push) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(((Integer) ReflectUtils.getFromInternalR(NeoGameDBColumns._ID, "prefs")).intValue(), fragment);
        if (push) {
            transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(BACK_STACK_PREFS);
        } else {
            transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        }
        transaction.commitAllowingStateLoss();
    }

    public void startPreferencePanel(String fragmentClass, Bundle args, int titleRes, CharSequence titleText, Fragment resultTo, int resultRequestCode) {
        if (this.mSinglePane) {
            startWithFragment(fragmentClass, args, resultTo, resultRequestCode, titleRes, 0);
            return;
        }
        Fragment f = Fragment.instantiate(this, fragmentClass, args);
        if (resultTo != null) {
            f.setTargetFragment(resultTo, resultRequestCode);
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(((Integer) ReflectUtils.getFromInternalR(NeoGameDBColumns._ID, "prefs")).intValue(), f);
        if (titleRes != 0) {
            transaction.setBreadCrumbTitle(titleRes);
        } else if (titleText != null) {
            transaction.setBreadCrumbTitle(titleText);
        }
        transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack(BACK_STACK_PREFS);
        transaction.commitAllowingStateLoss();
    }

    public void finishPreferencePanel(Fragment caller, int resultCode, Intent resultData) {
        if (this.mSinglePane) {
            setResult(resultCode, resultData);
            finish();
            return;
        }
        onBackPressed();
        if (caller != null && caller.getTargetFragment() != null) {
            caller.getTargetFragment().onActivityResult(caller.getTargetRequestCode(), resultCode, resultData);
        }
    }

    public boolean onPreferenceStartFragment(PreferenceFragment caller, Preference pref) {
        startPreferencePanel(pref.getFragment(), pref.getExtras(), pref.getTitleRes(), pref.getTitle(), null, 0);
        return true;
    }

    private void postBindPreferences() {
        if (!this.mHandler.hasMessages(1)) {
            this.mHandler.obtainMessage(1).sendToTarget();
        }
    }

    /* access modifiers changed from: private */
    public void bindPreferences() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            preferenceScreen.bind(getListView());
            if (this.mSavedInstanceState != null) {
                super.onRestoreInstanceState(this.mSavedInstanceState);
                this.mSavedInstanceState = null;
            }
        }
    }

    @Deprecated
    public PreferenceManager getPreferenceManager() {
        return this.mPreferenceManager;
    }

    private void requirePreferenceManager() {
        if (this.mPreferenceManager != null) {
            return;
        }
        if (this.mAdapter == null) {
            throw new RuntimeException("This should be called after super.onCreate.");
        }
        throw new RuntimeException("Modern two-pane PreferenceActivity requires use of a PreferenceFragment");
    }

    @Deprecated
    public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
        requirePreferenceManager();
        try {
            Method method = this.mPreferenceManager.getClass().getDeclaredMethod("setPreferences", new Class[]{PreferenceScreen.class});
            method.setAccessible(true);
            if (((Boolean) method.invoke(this.mPreferenceManager, new Object[]{preferenceScreen})).booleanValue() && preferenceScreen != null) {
                postBindPreferences();
                CharSequence title = getPreferenceScreen().getTitle();
                if (title != null) {
                    setTitle(title);
                }
            }
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    @Deprecated
    public PreferenceScreen getPreferenceScreen() {
        if (this.mPreferenceManager != null) {
            try {
                Method method = this.mPreferenceManager.getClass().getDeclaredMethod("getPreferenceScreen", new Class[0]);
                method.setAccessible(true);
                return (PreferenceScreen) method.invoke(this.mPreferenceManager, new Object[0]);
            } catch (NoSuchMethodError e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    @Deprecated
    public void addPreferencesFromIntent(Intent intent) {
        requirePreferenceManager();
        try {
            Method method = this.mPreferenceManager.getClass().getDeclaredMethod("inflateFromIntent", new Class[]{Intent.class, PreferenceScreen.class});
            method.setAccessible(true);
            setPreferenceScreen((PreferenceScreen) method.invoke(this.mPreferenceManager, new Object[]{intent, getPreferenceScreen()}));
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    @Deprecated
    public void addPreferencesFromResource(int preferencesResId) {
        requirePreferenceManager();
        setPreferenceScreen((PreferenceScreen) ReflectUtils.invoke(this.mPreferenceManager, "inflateFromResource", true, false, new Object[]{this, Integer.valueOf(preferencesResId), getPreferenceScreen()}, Context.class, Integer.TYPE, PreferenceScreen.class));
    }

    @Deprecated
    public Preference findPreference(CharSequence key) {
        if (this.mPreferenceManager == null) {
            return null;
        }
        return this.mPreferenceManager.findPreference(key);
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        if (this.mPreferenceManager != null) {
            try {
                Method method = this.mPreferenceManager.getClass().getDeclaredMethod("dispatchNewIntent", new Class[]{Intent.class});
                method.setAccessible(true);
                method.invoke(this.mPreferenceManager, new Object[]{intent});
            } catch (NoSuchMethodError e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean hasNextButton() {
        return this.mNextButton != null;
    }

    /* access modifiers changed from: protected */
    public Button getNextButton() {
        return this.mNextButton;
    }
}
