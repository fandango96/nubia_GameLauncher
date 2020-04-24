package cn.nubia.gamelauncherx.recycler;

public class ItemTransformation {
    final float mCardAlpha;
    final float mLightAlpha;
    final float mLightShadowAlpha;
    final float mMoreOptionsAlpha;
    final float mScaleX;
    final float mScaleY;
    final float mShadowAlpha;
    final float mTextAlpha;
    final float mTranslationX;
    final float mTranslationY;
    final float mViewMaskAlpha;

    public ItemTransformation(float scaleX, float scaleY, float translationX, float translationY, float mastAlpha, float textAlpha, float shadowAlpha, float lightAlpha, float lightShadowAlpha, float cardAlpha, float moreOptionsAlpha) {
        this.mScaleX = scaleX;
        this.mScaleY = scaleY;
        this.mTranslationX = translationX;
        this.mTranslationY = translationY;
        this.mViewMaskAlpha = mastAlpha;
        this.mTextAlpha = textAlpha;
        this.mShadowAlpha = shadowAlpha;
        this.mLightAlpha = lightAlpha;
        this.mLightShadowAlpha = lightShadowAlpha;
        this.mCardAlpha = cardAlpha;
        this.mMoreOptionsAlpha = moreOptionsAlpha;
    }
}
