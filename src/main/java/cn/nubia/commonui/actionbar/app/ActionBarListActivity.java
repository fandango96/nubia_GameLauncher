package cn.nubia.commonui.actionbar.app;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import cn.nubia.commonui.R;

public class ActionBarListActivity extends ActionBarActivity {
    protected ListAdapter mAdapter;
    private boolean mFinishedStart = false;
    private Handler mHandler = new Handler();
    protected ListView mList;
    private OnItemClickListener mOnClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            ActionBarListActivity.this.onListItemClick((ListView) parent, v, position, id);
        }
    };
    private Runnable mRequestFocus = new Runnable() {
        public void run() {
            ActionBarListActivity.this.mList.focusableViewAvailable(ActionBarListActivity.this.mList);
        }
    };

    public void onContentChanged() {
        super.onContentChanged();
        this.mList = (ListView) findViewById(16908298);
        if (this.mList == null) {
            throw new RuntimeException("Your content must have a ListView whose id attribute is 'android.R.id.list'");
        }
        this.mList.setOnItemClickListener(this.mOnClickListener);
        if (this.mFinishedStart) {
            setListAdapter(this.mAdapter);
        }
        this.mHandler.post(this.mRequestFocus);
        this.mFinishedStart = true;
    }

    public void setListAdapter(ListAdapter adapter) {
        synchronized (this) {
            ensureList();
            this.mAdapter = adapter;
            this.mList.setAdapter(adapter);
        }
    }

    public void setSelection(int position) {
        this.mList.setSelection(position);
    }

    public int getSelectedItemPosition() {
        return this.mList.getSelectedItemPosition();
    }

    public long getSelectedItemId() {
        return this.mList.getSelectedItemId();
    }

    public ListView getListView() {
        ensureList();
        return this.mList;
    }

    public ListAdapter getListAdapter() {
        return this.mAdapter;
    }

    private void ensureList() {
        if (this.mList == null) {
            setContentView(R.layout.nubia_list_content_simple);
        }
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Bundle state) {
        ensureList();
        super.onRestoreInstanceState(state);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        this.mHandler.removeCallbacks(this.mRequestFocus);
        super.onDestroy();
    }

    /* access modifiers changed from: protected */
    public void onListItemClick(ListView l, View v, int position, long id) {
    }
}
