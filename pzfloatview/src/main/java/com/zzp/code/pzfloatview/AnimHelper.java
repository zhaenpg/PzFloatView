package com.zzp.code.pzfloatview;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class AnimHelper{


    private static AnimHelper INSTANCE = null;

    private AnimHelper(){}

    public static AnimHelper get(){
        if (INSTANCE == null){
            INSTANCE = AnimHelperFactory.INSTANCE;
        }
        return INSTANCE;
    }

    public void showAnim(View tartgetView, float startAlpha, final float endAlpha){
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(tartgetView,"alpha",startAlpha,endAlpha);
        alphaAnimator.setDuration(600);
        alphaAnimator.setInterpolator(new DecelerateInterpolator());
        alphaAnimator.start();
    }

    private ObjectAnimator animator;

//    /**
//     * 滚动动画
//     * 动画结束时隐藏半个宽度
//     * @param start
//     * @param end
//     * @param startDelay
//     */
//    public void startVerticalMoveAnim(View targetView, final int start, final int end, long startDelay, ValueAnimator.AnimatorUpdateListener listener){
//
//        animator = ObjectAnimator.ofInt(targetView, "translationY",start,  end);
//        animator.setDuration(500);
//        animator.setRepeatCount(0);
//        animator.setStartDelay(startDelay);
//        animator.addUpdateListener(listener);
//        animator.setInterpolator(new LinearInterpolator());
//        animator.start();
//    }
//
//    /**
//     * 滚动动画
//     * 动画结束时隐藏半个宽度
//     * @param start
//     * @param end
//     * @param startDelay
//     */
//    public void startHorizontalMoveAnim(View targetView, final int start, final int end, long startDelay, ValueAnimator.AnimatorUpdateListener listener){
//
//        animator = ObjectAnimator.ofInt(targetView, "translationX",start,  end);
//        animator.setDuration(500);
//        animator.setRepeatCount(0);
//        animator.setStartDelay(startDelay);
//        animator.addUpdateListener(listener);
//        animator.setInterpolator(new LinearInterpolator());
//        animator.start();
//    }

    private static class AnimHelperFactory{
         private static final AnimHelper INSTANCE = new AnimHelper();
    }
}
