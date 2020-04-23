package com.bumptech.glide.request.animation;

import android.view.View;
import com.bumptech.glide.request.animation.GlideAnimation.ViewAdapter;

public class ViewPropertyAnimation<R> implements GlideAnimation<R> {
    private final Animator animator;

    public interface Animator {
        void animate(View view);
    }

    public ViewPropertyAnimation(Animator animator2) {
        this.animator = animator2;
    }

    public boolean animate(R r, ViewAdapter adapter) {
        if (adapter.getView() != null) {
            this.animator.animate(adapter.getView());
        }
        return false;
    }
}
