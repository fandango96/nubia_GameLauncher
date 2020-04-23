package cn.nubia.gamelauncher.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.nubia.commonui.actionbar.internal.widget.TintCheckBox;
import cn.nubia.gamelauncher.R;
import cn.nubia.gamelauncher.util.LogUtil;
import java.util.List;

public class HandleAdapter extends BaseAdapter {
    private static final String TAG = "HandleAdapter";
    private boolean isChecked = false;
    private LayoutInflater layoutInflater;
    private List<BluetoothDevice> mBlueList;
    private Context mContext;
    private int selectIndex = -1;

    private class ViewHolder {
        TintCheckBox mCheckbox;
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
        return (long) position;
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
            mViewHolder.mHandleName = (TextView) view.findViewById(R.id.handle_list_name);
            mViewHolder.mCheckbox = (TintCheckBox) view.findViewById(R.id.tint_checkbox);
            mViewHolder.mConnecting = (TextView) view.findViewById(R.id.conn_ing);
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) view.getTag();
        }
        String deviceName = ((BluetoothDevice) this.mBlueList.get(position)).getName();
        LogUtil.d(TAG, "[getView] deviceName = " + deviceName);
        TextView textView = mViewHolder.mHandleName;
        if (TextUtils.isEmpty(deviceName)) {
            deviceName = this.mContext.getString(R.string.handle_unknown_device);
        }
        textView.setText(deviceName);
        if (position == this.selectIndex) {
            mViewHolder.mCheckbox.setChecked(this.isChecked);
            mViewHolder.mCheckbox.setVisibility(0);
            mViewHolder.mHandleName.setTextColor(ContextCompat.getColor(this.mContext, R.color.handle_conn_ing));
            mViewHolder.mConnecting.setVisibility(0);
        } else {
            mViewHolder.mCheckbox.setChecked(false);
            mViewHolder.mCheckbox.setVisibility(8);
            mViewHolder.mHandleName.setTextColor(ContextCompat.getColor(this.mContext, R.color.handle_list_name));
            mViewHolder.mConnecting.setVisibility(8);
        }
        if (mViewHolder.mCheckbox.isChecked() && this.isChecked) {
            mViewHolder.mHandleName.setTextColor(ContextCompat.getColor(this.mContext, R.color.handle_list_name));
            mViewHolder.mConnecting.setVisibility(8);
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
