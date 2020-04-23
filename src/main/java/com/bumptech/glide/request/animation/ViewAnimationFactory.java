package com.bumptech.glide.request.animation;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class ViewAnimationFactory<R> implements GlideAnimationFactory<R> {
    private final AnimationFactory animationFactory;
    private GlideAnimation<R> glideAnimation;

    private static class ConcreteAnimationFactory implements AnimationFactory {
        private final Animation animation;

        public ConcreteAnimationFactory(Animation animation2) {
            this.animation = animation2;
        }

        public Animation build() {
            return this.animation;
        }
    }

    private static class ResourceAnimationFactory implements AnimationFactory {
        private final int animationId;
        private final Context context;

        public ResourceAnimationFactory(Context context2, int animationId2) {
            this.context = context2.getApplicationContext();
            this.animationId = animationId2;
        }

        public Animation build() {
            return AnimationUtils.loadAnimation(this.context, this.animationId);
        }
    }

    public ViewAnimationFactory(Animation animation) {
        this((AnimationFactory) new ConcreteAnimationFactory(animation));
    }

    public ViewAnimationFactory(Context context, int animationId) {
        this((AnimationFactory) new ResourceAnimationFactory(context, animationId));
    }

    ViewAnimationFactory(AnimationFactory animationFactory2) {
        this.animationFactory = animationFactory2;
    }

    public GlideAnimation<R> build(boolean isFromMemoryCache, boolean isFirstResource) {
        if (isFromMemoryCache || !isFirstResource) {
            return NoAnimation.get();
        }
        if (this.glideAnimation == null) {
            this.glideAnimation = new ViewAnimation(this.animationFactory);
        }
        return this.glideAnimation;
    }
}
