package cn.nubia.gamelauncherx.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import cn.nubia.gamelauncherx.R;
import cn.nubia.gamelauncherx.util.LogUtil;
import java.util.List;

public class HandleAdapter extends BaseAdapter {
    private static final String TAG = "HandleAdapter";
    private boolean isChecked = false;
    private LayoutInflater layoutInflater;
    private List<BluetoothDevice> mBlueList;
    private Context mContext;
    private int selectIndex = -1;

    private class ViewHolder {
        CheckBox mCheckbox;
        TextView mConnecting;
        TextView mHandleName;

        private ViewHolder() {
        }
    }

    public HandleAdapter(Context context, List<BluetoothDevice> list) {
        this.mContext = context;
        this.mBlueList = list;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return this.mBlueList.size();
    }

    public Object getItem(int position) {
        return this.mBlueList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean isEnabled(int position) {
        if (position == this.selectIndex) {
            return false;
        }
        return super.isEnabled(position);
    }

    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder mViewHolder;
        if (view == null) {
            mViewHolder = new ViewHolder();
            view = this.layoutInflater.inflate(R.layout.handle_list_item, null);
            mViewHolder.mHandleName = view.findViewById(R.id.handle_list_name);
            mViewHolder.mCheckbox = view.findViewById(R.id.tint_checkbox);
            mViewHolder.mConnecting = view.findViewById(R.id.conn_ing);
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) view.getTag();
        }
        String deviceName = this.mBlueList.get(position).getName();
        LogUtil.d(TAG, "[getView] deviceName = " + deviceName);
        TextView textView = mViewHolder.mHandleName;
        if (TextUtils.isEmpty(deviceName)) {
            deviceName = this.mContext.getString(R.string.handle_unknown_device);
        }
        textView.setText(deviceName);
        if (position == this.selectIndex) {
            mViewHolder.mCheckbox.setChecked(this.isChecked);
            mViewHolder.mCheckbox.setVisibility(View.VISIBLE);
            mViewHolder.mHandleName.setTextColor(ContextCompat.getColor(this.mContext, R.color.handle_conn_ing));
            mViewHolder.mConnecting.setVisibility(View.VISIBLE);
        } else {
            mViewHolder.mCheckbox.setChecked(false);
            mViewHolder.mCheckbox.setVisibility(View.GONE);
            mViewHolder.mHandleName.setTextColor(ContextCompat.getColor(this.mContext, R.color.handle_list_name));
            mViewHolder.mConnecting.setVisibility(View.GONE);
        }
        if (mViewHolder.mCheckbox.isChecked() && this.isChecked) {
            mViewHolder.mHandleName.setTextColor(ContextCompat.getColor(this.mContext, R.color.handle_list_name));
            mViewHolder.mConnecting.setVisibility(View.GONE);
        }
        return view;
    }

    public void setCheckedItem(int index) {
        this.selectIndex = index;
    }

    public void setCheckedState(boolean isCheckedState) {
        this.isChecked = isCheckedState;
    }
}
