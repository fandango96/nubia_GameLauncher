package cn.nubia.gamelauncherx.recycler;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import cn.nubia.gamelauncherx.R;
import cn.nubia.gamelauncherx.view.DrawableLeftTextView;

public class BannerViewHolder extends ViewHolder {
    ImageView mCardView;
    DrawableLeftTextView mGameNameView;
    ImageView mIconView;
    ImageView mMaskView;
    TextView mModifyAtmosphere;
    ImageView mMoreOptions;
    FrameLayout mMoreOptionsList;
    ImageView mShadowView;
    TextView mStateText;
    ImageView mTitleBg;
    TextView mUninstallGame;

    public BannerViewHolder(View view) {
        super(view);
        this.mGameNameView = (DrawableLeftTextView) view.findViewById(R.id.game_name);
        this.mTitleBg = (ImageView) view.findViewById(R.id.title_bg);
        this.mCardView = (ImageView) view.findViewById(R.id.game_banner);
        this.mMaskView = (ImageView) view.findViewById(R.id.card_mask);
        this.mShadowView = (ImageView) view.findViewById(R.id.shadow);
        this.mIconView = (ImageView) view.findViewById(R.id.icon);
        this.mMoreOptions = (ImageView) view.findViewById(R.id.more_options);
        this.mMoreOptionsList = (FrameLayout) view.findViewById(R.id.more_options_list);
        this.mModifyAtmosphere = (TextView) view.findViewById(R.id.modify_atmosphere);
        this.mUninstallGame = (TextView) view.findViewById(R.id.uninstall_game);
        this.mStateText = (TextView) view.findViewById(R.id.download_state_text);
    }
}
