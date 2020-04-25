package cn.nubia.gamelauncherx.recycler;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import cn.nubia.gamelauncherx.GameLauncherApplication;
import cn.nubia.gamelauncherx.R;
import cn.nubia.gamelauncherx.activity.CustomAstophereMapActivity;
import cn.nubia.gamelauncherx.bean.AppListItemBean;
import cn.nubia.gamelauncherx.bean.NeoIconDownloadInfo;
import cn.nubia.gamelauncherx.controller.UninstallController;
import cn.nubia.gamelauncherx.util.BitmapUtils;
import cn.nubia.gamelauncherx.util.CommonUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;

import java.util.HashMap;
import java.util.List;

public class BannerListAdapter extends RecyclerView.Adapter<BannerViewHolder>
{
    public static final String ASSETS_PREFIX = "file:///android_asset/";
    public static int LIST_MIN_COUNT = 5;
    private static final String TAG = "BannerListAdapter";
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public List<AppListItemBean> mList;
    private HashMap<Integer, BannerViewHolder> mNeoDownloadIconMap = new HashMap<>();

    public BannerListAdapter(List<AppListItemBean> list, Context context) {
        this.mList = list;
        this.mContext = context;
    }

    @NonNull
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BannerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.game_item, parent, false));
    }

    public void onBindViewHolder(@NonNull final BannerViewHolder holder, int position) {
        if (position != 0) {
            holder.mCardView.setTag(R.id.tag_first, Integer.valueOf(position));
        }
        final int realPosition = getRealPosition(position);
        holder.mModifyAtmosphere.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(BannerListAdapter.this.mContext, CustomAstophereMapActivity.class);
                intent.putExtra("position", realPosition);
                BannerListAdapter.this.mContext.startActivity(intent);
                holder.mMoreOptionsList.setVisibility(View.GONE);
            }
        });
        holder.mUninstallGame.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                UninstallController mUninstallController = new UninstallController();
                if (BannerListAdapter.this.mList != null && BannerListAdapter.this.mList.size() > 0) {
                    String componetName = BannerListAdapter.this.mList.get(realPosition).getComponetName();
                    if (componetName != null && componetName.length() > 0) {
                        mUninstallController.showConfirmDialog(BannerListAdapter.this.mContext, componetName.substring(0, componetName.indexOf(",")));
                    }
                }
                holder.mMoreOptionsList.setVisibility(View.GONE);
            }
        });
        String gameName = this.mList.get(realPosition).getName();
        holder.mGameNameView.setText(gameName);
        holder.mStateText.setVisibility(View.GONE);
        holder.mIconView.setTag(null);
        Bitmap icon = this.mList.get(getRealPosition(position)).getIcon();
        if (icon != null) {
            holder.mCardView.setImageResource(R.mipmap.default_card);
            if (this.mList.get(getRealPosition(position)).isDownloadItem()) {
                NeoIconDownloadInfo info = this.mList.get(getRealPosition(position)).getDownloadInfo();
                this.mNeoDownloadIconMap.put(Integer.valueOf(info.app_id), holder);
                holder.mIconView.setTag(Integer.valueOf(info.app_id));
                Bitmap processIcon = info.processIcon;
                if (processIcon != null) {
                    holder.mIconView.setBackground(BitmapUtils.convertBitmapToDrawable(processIcon));
                } else {
                    holder.mIconView.setBackground(BitmapUtils.convertBitmapToDrawable(icon));
                }
                holder.mStateText.setVisibility(View.VISIBLE);
                holder.mStateText.setText(CommonUtil.convertToShowStateText(info.status));
                updateGameNameView(holder, info.icon, gameName);
            } else {
                holder.mStateText.setVisibility(View.GONE);
                updateGameNameView(holder, icon, gameName);
                holder.mIconView.setBackground(BitmapUtils.convertBitmapToDrawable(icon));
                holder.mIconView.setTag(null);
            }
            holder.mIconView.setVisibility(View.VISIBLE);
        }
        if (CommonUtil.isInternalVersion()) {
            holder.mGameNameView.setMaxLines(2);
        }
        String url = this.mList.get(realPosition).getImageUrl();
        if (isAddPrefix(url)) {
            url = "file:///android_asset/" + url;
        }
        holder.mCardView.setTag(R.id.tag_second, url);
        String updateTime = this.mList.get(realPosition).getUpdateTime();
        if (updateTime == null) {
            updateTime = "";
        }
        fillCardView(holder, url, position, updateTime);
    }

    private Bitmap adapterBottomIcon(Bitmap topBitmap) {
        Bitmap srcScale = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(GameLauncherApplication.CONTEXT.getResources(), R.mipmap.icon_card_bg).copy(Config.ARGB_8888, true), topBitmap.getWidth(), topBitmap.getHeight(), false);
        Canvas canvas = new Canvas(srcScale);
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_ATOP));
        canvas.drawBitmap(topBitmap, 0.0f, 0.0f, paint);
        return srcScale;
    }

    /* access modifiers changed from: 0000 */
    public void updateGameNameView(BannerViewHolder holder, Bitmap icon, String text) {
        Drawable drawable;
        holder.mGameNameView.setText(text);
        if (CommonUtil.isInternalVersion()) {
            drawable = BitmapUtils.convertBitmapToDrawable(adapterBottomIcon(icon));
        } else {
            drawable = BitmapUtils.convertBitmapToDrawable(icon);
        }
        int size = this.mContext.getResources().getDimensionPixelSize(R.dimen.text_left_icon_size);
        drawable.setBounds(0, 0, size, size);
        holder.mGameNameView.setCompoundDrawablePadding(this.mContext.getResources().getDimensionPixelSize(R.dimen.text_compound_drawable_padding));
        holder.mGameNameView.setCompoundDrawables(drawable, null, null, null);
    }

    private boolean isAddPrefix(String url) {
        return url == null || url.equals("") || (!url.contains("http") && !url.contains("storage"));
    }

    private void fillCardView(final BannerViewHolder holder, String url, int position, String updateTime) {
        Glide.with(this.mContext).load(url).signature(new ObjectKey(updateTime)).transform(new BannerCardTransformation(this.mContext)).placeholder(
                R.mipmap.default_card).into((Target) new ViewTarget<ImageView, Drawable>(holder.mCardView) {
            @Override
            public void onResourceReady(@NonNull final Drawable resource,
                    @Nullable final Transition<? super Drawable> transition)
            {
                holder.mIconView.setVisibility(View.GONE);
                this.view.setImageDrawable(resource);
            }
        });
    }

    public int getRealPosition(int position) {
        return position % this.mList.size();
    }

    public int getItemCount() {
        return this.mList.size() >= LIST_MIN_COUNT ? this.mList.size() : this.mList.size() * 2;
    }

    public void resetNeoDownloadMap() {
        this.mNeoDownloadIconMap.clear();
    }

    public void updateNeoDownloadIcon(AppListItemBean bean) {
        NeoIconDownloadInfo info = bean.getDownloadInfo();
        if (info != null && this.mNeoDownloadIconMap.containsKey(Integer.valueOf(info.app_id))) {
            ImageView imageView = this.mNeoDownloadIconMap.get(Integer.valueOf(info.app_id)).mIconView;
            if (imageView.getTag() != null && imageView.getTag().equals(Integer.valueOf(info.app_id))) {
                bean.icon = info.icon;
                imageView.setBackground(BitmapUtils.convertBitmapToDrawable(info.processIcon));
                updateGameNameView(this.mNeoDownloadIconMap.get(Integer.valueOf(info.app_id)), info.icon, info.title);
                this.mNeoDownloadIconMap.get(Integer.valueOf(info.app_id)).mStateText.setVisibility(View.VISIBLE);
                this.mNeoDownloadIconMap.get(Integer.valueOf(info.app_id)).mStateText.setText(CommonUtil.convertToShowStateText(info.status));
            }
        }
    }

    public void onViewRecycled(BannerViewHolder holder) {
        if (holder != null) {
            Glide.with(holder.mCardView.getContext()).clear(holder.mCardView);
            holder.mGameNameView.setCompoundDrawables(null, null, null, null);
            holder.mCardView.setImageResource(R.mipmap.default_card);
            holder.mIconView.setVisibility(View.GONE);
        }
        super.onViewRecycled(holder);
    }
}
