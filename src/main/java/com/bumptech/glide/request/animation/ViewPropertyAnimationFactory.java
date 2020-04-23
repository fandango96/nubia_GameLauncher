package com.bumptech.glide.request.animation;

import com.bumptech.glide.request.animation.ViewPropertyAnimation.Animator;

public class ViewPropertyAnimationFactory<R> implements GlideAnimationFactory<R> {
    private ViewPropertyAnimation<R> animation;
    private final Animator animator;

    public ViewPropertyAnimationFactory(Animator animator2) {
        this.animator = animator2;
    }

    public GlideAnimation<R> build(boolean isFromMemoryCache, boolean isFirstResource) {
        if (isFromMemoryCache || !isFirstResource) {
            return NoAnimation.get();
        }
        if (this.animation == null) {
            this.animation = new ViewPropertyAnimation<>(this.animator);
        }
        return this.animation;
    }
}
