package cn.nubia.gamelauncherx.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.nubia.gamelauncherx.R;
import cn.nubia.gamelauncherx.activity.GameSpaceActivity;
import cn.nubia.gamelauncherx.util.CommonUtil;
import java.util.ArrayList;

public class GuildPageAdapter extends RecyclerView.Adapter
{
    /* access modifiers changed from: private */
    public GameSpaceActivity mActivity;
    private ArrayList<Integer> mDataHint;
    private ArrayList<Integer> mDatas;
    GuidePageHolder mHasAddViewHolder;

    class GuidePageHolder extends RecyclerView.ViewHolder
    {
        public ImageView guildPageImage;
        public ImageView guildPageItemWindowBg;
        public ImageView guildPageLeftSlid;
        public ImageView guildPageMidSlid;
        public ImageView guildPageRightSlid;
        public Button guildPageStartExperience;
        public TextView guildPageTextView;

        public GuidePageHolder(View itemView) {
            super(itemView);
            this.guildPageImage = (ImageView) itemView.findViewById(R.id.guild_page_image);
            this.guildPageTextView = (TextView) itemView.findViewById(R.id.guild_page_hint);
            this.guildPageLeftSlid = (ImageView) itemView.findViewById(R.id.guild_page_slid_left);
            this.guildPageMidSlid = (ImageView) itemView.findViewById(R.id.guild_page_slid_mid);
            this.guildPageRightSlid = (ImageView) itemView.findViewById(R.id.guild_page_slid_right);
            if (CommonUtil.isInternalVersion()) {
                this.guildPageRightSlid.setVisibility(View.GONE);
            }
            this.guildPageStartExperience = (Button) itemView.findViewById(R.id.start_experience);
            this.guildPageItemWindowBg = (ImageView) itemView.findViewById(R.id.guild_page_window_bg);
        }
    }

    public GuildPageAdapter(GameSpaceActivity context, ArrayList<Integer> data, ArrayList<Integer> dataHint) {
        this.mActivity = context;
        this.mDatas = data;
        this.mDataHint = dataHint;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        return new GuidePageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.guild_page_item_layout, parent, false));
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        this.mHasAddViewHolder = (GuidePageHolder) holder;
        this.mHasAddViewHolder.guildPageImage.setBackgroundResource(((Integer) this.mDatas.get(position)).intValue());
        this.mHasAddViewHolder.guildPageTextView.setText(((Integer) this.mDataHint.get(position)).intValue());
        this.mHasAddViewHolder.guildPageStartExperience.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                GuildPageAdapter.this.mActivity.showContentWithAnim();
                GuildPageAdapter.this.mActivity.setFirstStartGameSpaceValue();
                GuildPageAdapter.this.mHasAddViewHolder.itemView.setVisibility(View.GONE);
                GuildPageAdapter.this.mActivity.setGuildPageRecyclerInVisible();
            }
        });
        if (position != this.mDatas.size() - 1) {
            this.mHasAddViewHolder.guildPageStartExperience.setVisibility(View.INVISIBLE);
        } else {
            this.mHasAddViewHolder.guildPageStartExperience.setVisibility(View.VISIBLE);
        }
        switch (position) {
            case 0:
                this.mHasAddViewHolder.guildPageLeftSlid.setBackgroundResource(R.mipmap.guild_page_slide_light);
                this.mHasAddViewHolder.guildPageMidSlid.setBackgroundResource(R.mipmap.guild_page_slide_default);
                this.mHasAddViewHolder.guildPageRightSlid.setBackgroundResource(R.mipmap.guild_page_slide_default);
                return;
            case 1:
                this.mHasAddViewHolder.guildPageLeftSlid.setBackgroundResource(R.mipmap.guild_page_slide_default);
                this.mHasAddViewHolder.guildPageMidSlid.setBackgroundResource(R.mipmap.guild_page_slide_light);
                this.mHasAddViewHolder.guildPageRightSlid.setBackgroundResource(R.mipmap.guild_page_slide_default);
                return;
            case 2:
                this.mHasAddViewHolder.guildPageLeftSlid.setBackgroundResource(R.mipmap.guild_page_slide_default);
                this.mHasAddViewHolder.guildPageMidSlid.setBackgroundResource(R.mipmap.guild_page_slide_default);
                this.mHasAddViewHolder.guildPageRightSlid.setBackgroundResource(R.mipmap.guild_page_slide_light);
                return;
            default:
                return;
        }
    }

    public int getItemCount() {
        return this.mDatas.size();
    }
}
