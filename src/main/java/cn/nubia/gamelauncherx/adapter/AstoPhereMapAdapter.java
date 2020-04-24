package cn.nubia.gamelauncherx.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import cn.nubia.gamelauncherx.R;
import cn.nubia.gamelauncherx.view.SimpleEditImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;

public class AstoPhereMapAdapter extends RecyclerView.Adapter<AstoPhereMapAdapter.ViewHolder>
{
    public static final String ASSETS_PREFIX = "file:///android_asset/";
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public SimpleEditImageView mEditImageView;
    /* access modifiers changed from: private */
    public int mSelectedPosition;
    private List<String> mUrlList;

    class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        /* access modifiers changed from: private */
        public ImageView iconImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.iconImageView = itemView.findViewById(R.id.entrance_image);
        }
    }

    public AstoPhereMapAdapter(Context context, List<String> datas, SimpleEditImageView editImageView) {
        this.mContext = context;
        this.mUrlList = datas;
        this.mEditImageView = editImageView;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.astophere_layout_item, null));
    }

    public void onBindViewHolder(ViewHolder holder, final int position) {
        final String url = "file:///android_asset/" + this.mUrlList.get(position);
        fillImageView(holder, url, position);
        holder.iconImageView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                try {
                    Glide.with(AstoPhereMapAdapter.this.mContext).asBitmap().load(url).into((Target) new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull final Bitmap resource,
                                @Nullable final Transition<? super Bitmap> transition)
                        {
                            AstoPhereMapAdapter.this.mEditImageView.setBitmap(resource);
                        }
                    });
                    AstoPhereMapAdapter.this.mSelectedPosition = position;
                    AstoPhereMapAdapter.this.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if (position == this.mSelectedPosition) {
            holder.iconImageView.setForeground(this.mContext.getDrawable(R.drawable.selected));
        } else {
            holder.iconImageView.setForeground(null);
        }
    }

    private void fillImageView(ViewHolder holder, String url, int position) {
        Glide.with(this.mContext).load(url).override(108, 108).into((Target) new ViewTarget<ImageView, Drawable>(holder.iconImageView) {
            @Override
            public void onResourceReady(@NonNull final Drawable resource,
                    @Nullable final Transition<? super Drawable> transition)
            {
                this.view.setImageDrawable(resource);
            }
        });
    }

    public int getItemCount() {
        return this.mUrlList.size();
    }

    public long getItemId(int position) {
        return position;
    }
}
