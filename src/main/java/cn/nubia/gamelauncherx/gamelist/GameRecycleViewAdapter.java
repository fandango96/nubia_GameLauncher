package cn.nubia.gamelauncherx.gamelist;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import cn.nubia.gamelauncherx.R;
import cn.nubia.gamelauncherx.activity.AppAddActivity;
import cn.nubia.gamelauncherx.bean.NeoIconDownloadInfo;
import cn.nubia.gamelauncherx.controller.NeoDownloadManager;
import cn.nubia.gamelauncherx.util.CommonUtil;
import java.util.HashMap;
import java.util.List;

public class GameRecycleViewAdapter extends RecyclerView.Adapter<GameRecycleViewAdapter.EntranceViewHolder>
{
    /* access modifiers changed from: private */
    public List<GameEntranceItem> gameEntranceList;
    /* access modifiers changed from: private */
    public Context mContext;
    private int mIndex;
    private HashMap<Integer, EntranceViewHolder> mNeoDownloadIconMap = new HashMap<>();
    private int mPageSize;

    class EntranceViewHolder extends RecyclerView.ViewHolder
    {
        /* access modifiers changed from: private */
        public ImageView entranceIconImageView;
        /* access modifiers changed from: private */
        public TextView entranceNameTextView;
        public View neoContainerView;
        public TextView neoEntranceNameTextView;
        public TextView stateText;

        public EntranceViewHolder(View itemView) {
            super(itemView);
            this.entranceIconImageView = (ImageView) itemView.findViewById(R.id.entrance_image);
            if (CommonUtil.isInternalVersion()) {
                this.entranceIconImageView.setBackgroundResource(R.mipmap.icon_card_bg);
            }
            this.entranceNameTextView = (TextView) itemView.findViewById(R.id.entrance_name);
            this.neoContainerView = itemView.findViewById(R.id.neo_text_container);
            this.neoEntranceNameTextView = (TextView) itemView.findViewById(R.id.neo_entrance_name);
            this.stateText = (TextView) itemView.findViewById(R.id.state_name);
        }
    }

    public GameRecycleViewAdapter(Context context, List<GameEntranceItem> datas, int index, int pageSize) {
        this.mContext = context;
        this.mIndex = index;
        this.mPageSize = pageSize;
        this.gameEntranceList = datas;
    }

    public EntranceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EntranceViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.item_home_entrance, null));
    }

    public void onBindViewHolder(EntranceViewHolder holder, int position) {
        final int pos = position + (this.mIndex * this.mPageSize);
        if (pos >= this.gameEntranceList.size()) {
            holder.entranceNameTextView.setText(R.string.add_game);
            holder.entranceIconImageView.setImageResource(R.mipmap.add_game_icon);
            holder.itemView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(GameRecycleViewAdapter.this.mContext, AppAddActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        GameRecycleViewAdapter.this.mContext.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return;
        }
        if (((GameEntranceItem) this.gameEntranceList.get(pos)).isDownloadItem()) {
            NeoIconDownloadInfo info = ((GameEntranceItem) this.gameEntranceList.get(pos)).info;
            this.mNeoDownloadIconMap.put(Integer.valueOf(info.app_id), holder);
            holder.neoContainerView.setTag(Integer.valueOf(info.app_id));
            holder.neoContainerView.setVisibility(View.VISIBLE);
            holder.entranceNameTextView.setVisibility(View.GONE);
            holder.neoEntranceNameTextView.setText(((GameEntranceItem) this.gameEntranceList.get(pos)).getName());
            holder.stateText.setText(CommonUtil.convertToShowStateText(info.status));
            holder.entranceIconImageView.setImageDrawable(new BitmapDrawable(info.processIcon));
        } else {
            holder.neoContainerView.setVisibility(View.GONE);
            holder.entranceNameTextView.setVisibility(View.VISIBLE);
            holder.entranceNameTextView.setText(((GameEntranceItem) this.gameEntranceList.get(pos)).getName());
            holder.entranceIconImageView.setImageDrawable(new BitmapDrawable(((GameEntranceItem) this.gameEntranceList.get(pos)).getImage()));
        }
        holder.itemView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (((GameEntranceItem) GameRecycleViewAdapter.this.gameEntranceList.get(pos)).isDownloadItem()) {
                    NeoDownloadManager.getInstance().doClick(((GameEntranceItem) GameRecycleViewAdapter.this.gameEntranceList.get(pos)).info.app_id);
                    return;
                }
                Intent intent = new Intent();
                intent.setComponent(CommonUtil.createComponentName(((GameEntranceItem) GameRecycleViewAdapter.this.gameEntranceList.get(pos)).getComponetName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                GameRecycleViewAdapter.this.mContext.startActivity(intent);
            }
        });
    }

    public int getItemCount() {
        int itemCount = this.mPageSize;
        if (this.gameEntranceList.size() + 1 < (this.mIndex + 1) * this.mPageSize) {
            return (this.gameEntranceList.size() + 1) - (this.mIndex * this.mPageSize);
        }
        return itemCount;
    }

    public long getItemId(int position) {
        return (long) ((this.mIndex * this.mPageSize) + position);
    }

    public void resetNeoDownloadMap() {
        this.mNeoDownloadIconMap.clear();
    }

    public void updateNeoDownloadIcon(NeoIconDownloadInfo info) {
        if (info != null && this.mNeoDownloadIconMap.containsKey(Integer.valueOf(info.app_id))) {
            EntranceViewHolder viewHolder = (EntranceViewHolder) this.mNeoDownloadIconMap.get(Integer.valueOf(info.app_id));
            if (viewHolder.neoContainerView.getVisibility() == View.VISIBLE && viewHolder.neoContainerView.getTag() != null && viewHolder.neoContainerView.getTag().equals(Integer.valueOf(info.app_id))) {
                viewHolder.stateText.setText(CommonUtil.convertToShowStateText(info.status));
                viewHolder.entranceIconImageView.setImageDrawable(new BitmapDrawable(info.processIcon));
            }
        }
    }
}
