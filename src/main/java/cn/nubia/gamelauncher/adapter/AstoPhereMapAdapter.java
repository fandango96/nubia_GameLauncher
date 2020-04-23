package cn.nubia.gamelauncher.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import cn.nubia.gamelauncher.R;
import cn.nubia.gamelauncher.view.SimpleEditImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import java.util.List;

public class AstoPhereMapAdapter extends Adapter<ViewHolder> {
    public static final String ASSETS_PREFIX = "file:///android_asset/";
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public SimpleEditImageView mEditImageView;
    /* access modifiers changed from: private */
    public int mSelectedPosition;
    private List<String> mUrlList;

    class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        /* access modifiers changed from: private */
        public ImageView iconImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.iconImageView = (ImageView) itemView.findViewById(R.id.entrance_image);
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
        final String url = "file:///android_asset/" + ((String) this.mUrlList.get(position));
        fillImageView(holder, url, position);
        holder.iconImageView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                try {
                    Glide.with(AstoPhereMapAdapter.this.mContext).load(url).asBitmap().into((Target) new SimpleTarget<Bitmap>() {
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
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
        Glide.with(this.mContext).load(url).override(108, 108).into((Target) new ViewTarget<ImageView, GlideDrawable>(holder.iconImageView) {
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                ((ImageView) this.view).setImageDrawable(resource);
            }
        });
    }

    public int getItemCount() {
        return this.mUrlList.size();
    }

    public long getItemId(int position) {
        return (long) position;
    }
}
