package cn.nubia.gamelauncher.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.nubia.gamelauncher.R;
import cn.nubia.gamelauncher.bean.AppListItemBean;
import cn.nubia.gamelauncher.commoninterface.IOnAppAddedListener;
import cn.nubia.gamelauncher.commoninterface.OnSelectedCountChangeListener;
import java.util.ArrayList;
import java.util.Iterator;

public class HasAddAdapter extends Adapter {
    private int mAddAppCount = -1;
    String mAddedGameStr;
    private ArrayList<AppListItemBean> mList;
    private IOnAppAddedListener mListener = null;
    String mNotAddedGameStr;
    private TextView mNotSelectedTextView = null;
    private OnSelectedCountChangeListener mSelectCountListener = null;
    private TextView mSelectedTextView = null;
    /* access modifiers changed from: private */
    public int mType = 0;

    class HasAddViewHolder extends ViewHolder {
        public ImageView appIcon;
        public TextView appName;
        /* access modifiers changed from: private */
        public View bottomLine;
        public View container;
        public TextView mJoinSwitch;
        public TextView mRemoveSwitch;
        public ImageView radioButton;
        public ImageView switchBtn;
        public TextView titleName;

        public HasAddViewHolder(View itemView) {
            super(itemView);
            this.container = itemView;
            this.titleName = (TextView) itemView.findViewById(R.id.title_name);
            this.appIcon = (ImageView) itemView.findViewById(R.id.app_icon);
            this.appName = (TextView) itemView.findViewById(R.id.app_name);
            this.switchBtn = (ImageView) itemView.findViewById(R.id.app_switch);
            this.mJoinSwitch = (TextView) itemView.findViewById(R.id.join_switch);
            this.mRemoveSwitch = (TextView) itemView.findViewById(R.id.remove_switch);
            this.radioButton = (ImageView) itemView.findViewById(R.id.app_radio_btn);
            this.bottomLine = itemView.findViewById(R.id.bottom_line);
        }
    }

    public void setDataList(ArrayList<AppListItemBean> list) {
        this.mList = list;
        if (this.mType == 1 && this.mList != null && !this.mList.isEmpty()) {
            Iterator it = this.mList.iterator();
            while (it.hasNext()) {
                ((AppListItemBean) it.next()).setSelect(false);
            }
        }
    }

    public void setType(int type) {
        this.mType = type;
    }

    public void setHasAddCount(int count) {
        this.mAddAppCount = count;
    }

    public void setOnSelectCountChangeListener(OnSelectedCountChangeListener l) {
        this.mSelectCountListener = l;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HasAddViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.app_add_item_layout, parent, false));
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final HasAddViewHolder hasAddViewHolder = (HasAddViewHolder) holder;
        if (this.mType == 0) {
            if (position == 0 || position == this.mAddAppCount) {
                hasAddViewHolder.titleName.setVisibility(0);
                this.mAddedGameStr = hasAddViewHolder.titleName.getContext().getString(R.string.added_games);
                this.mNotAddedGameStr = hasAddViewHolder.titleName.getContext().getString(R.string.not_added_games);
                hasAddViewHolder.titleName.setText(((position != 0 || this.mAddAppCount <= 0) ? this.mList.size() - this.mAddAppCount : this.mAddAppCount) + ((position != 0 || this.mAddAppCount <= 0) ? this.mNotAddedGameStr : this.mAddedGameStr));
                if (position == 0) {
                    this.mSelectedTextView = hasAddViewHolder.titleName;
                } else {
                    this.mNotSelectedTextView = hasAddViewHolder.titleName;
                }
            } else {
                hasAddViewHolder.titleName.setVisibility(8);
            }
            if (this.mAddAppCount <= 0 || position != this.mAddAppCount - 1) {
                hasAddViewHolder.bottomLine.setVisibility(8);
            } else {
                hasAddViewHolder.bottomLine.setVisibility(0);
            }
            hasAddViewHolder.radioButton.setVisibility(8);
            hasAddViewHolder.switchBtn.setVisibility(0);
            hasAddViewHolder.switchBtn.setImageResource(((AppListItemBean) this.mList.get(position)).select ? R.mipmap.switch_button_press : R.mipmap.switch_button);
            doSwitchTextColorChange(hasAddViewHolder, position);
            hasAddViewHolder.switchBtn.setTag(this.mList.get(position));
        } else {
            hasAddViewHolder.radioButton.setVisibility(0);
            hasAddViewHolder.switchBtn.setVisibility(8);
            hasAddViewHolder.radioButton.setTag(this.mList.get(position));
            hasAddViewHolder.radioButton.setImageResource(((AppListItemBean) this.mList.get(position)).select ? R.mipmap.radio_select_button_press : R.mipmap.radio_select_button);
            hasAddViewHolder.radioButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    HasAddAdapter.this.doRadioBtnClick((ImageView) v, position);
                }
            });
            if (position == this.mList.size() - 1) {
                hasAddViewHolder.bottomLine.setVisibility(0);
            } else {
                hasAddViewHolder.bottomLine.setVisibility(8);
            }
        }
        hasAddViewHolder.switchBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                HasAddAdapter.this.doSwitchBtnClick((ImageView) v, position);
                HasAddAdapter.this.doSwitchTextColorChange(hasAddViewHolder, position);
            }
        });
        hasAddViewHolder.container.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Log.i("lsm", "hasAddViewHolder.container onClick position == " + position);
                if (HasAddAdapter.this.mType == 0) {
                    HasAddAdapter.this.doSwitchBtnClick(hasAddViewHolder.switchBtn, position);
                    HasAddAdapter.this.doSwitchTextColorChange(hasAddViewHolder, position);
                    return;
                }
                HasAddAdapter.this.doRadioBtnClick(hasAddViewHolder.radioButton, position);
            }
        });
        hasAddViewHolder.appName.setText(((AppListItemBean) this.mList.get(position)).name);
        hasAddViewHolder.appIcon.setImageBitmap(((AppListItemBean) this.mList.get(position)).icon);
    }

    /* access modifiers changed from: 0000 */
    public void doSwitchTextColorChange(HasAddViewHolder hasAddViewHolder, int position) {
        if (((AppListItemBean) this.mList.get(position)).select) {
            hasAddViewHolder.mRemoveSwitch.setAlpha(1.0f);
            hasAddViewHolder.mJoinSwitch.setAlpha(0.5f);
            return;
        }
        hasAddViewHolder.mRemoveSwitch.setAlpha(0.5f);
        hasAddViewHolder.mJoinSwitch.setAlpha(1.0f);
    }

    /* access modifiers changed from: 0000 */
    public void doSwitchBtnClick(ImageView clickView, int position) {
        AppListItemBean bean = (AppListItemBean) clickView.getTag();
        if (this.mListener != null) {
            IOnAppAddedListener iOnAppAddedListener = this.mListener;
            String componetName = bean.getComponetName();
            AppListItemBean appListItemBean = (AppListItemBean) this.mList.get(position);
            boolean z = !((AppListItemBean) this.mList.get(position)).select;
            appListItemBean.select = z;
            iOnAppAddedListener.onAppAddedCallback(componetName, z);
            clickView.setImageResource(((AppListItemBean) this.mList.get(position)).select ? R.mipmap.switch_button_press : R.mipmap.switch_button);
            updateTitleView();
        }
    }

    /* access modifiers changed from: 0000 */
    public void doRadioBtnClick(ImageView clickView, int position) {
        ((AppListItemBean) this.mList.get(position)).setSelect(!((AppListItemBean) this.mList.get(position)).isSelect());
        clickView.setImageResource(((AppListItemBean) this.mList.get(position)).select ? R.mipmap.radio_select_button_press : R.mipmap.radio_select_button);
        if (this.mSelectCountListener != null) {
            this.mSelectCountListener.onSelectedCountChangeListener(getCheckedCount());
        }
    }

    private void updateTitleView() {
        if (this.mSelectedTextView != null) {
            this.mSelectedTextView.setText(getCheckedCount() + this.mAddedGameStr);
        }
        if (this.mNotSelectedTextView != null) {
            this.mNotSelectedTextView.setText((this.mList.size() - getCheckedCount()) + this.mNotAddedGameStr);
        }
    }

    public int getItemCount() {
        return this.mList.size();
    }

    private int getCheckedCount() {
        if (this.mList == null || this.mList.size() <= 0) {
            return 0;
        }
        int count = 0;
        Iterator it = this.mList.iterator();
        while (it.hasNext()) {
            if (((AppListItemBean) it.next()).isSelect()) {
                count++;
            }
        }
        return count;
    }

    public void setOnAppAddedListener(IOnAppAddedListener listener) {
        this.mListener = listener;
    }
}
