package com.zzp.code.pzfloatview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

/*
* Author : Zhangzhenpeng
* Function : 悬浮球
* Create When 2019/3/27 17:12
*/
public class PzFloatView extends FrameLayout {

    private static final String TAG = "PzFloatView";

    private WindowManager.LayoutParams wmParams;

    private WindowManager wm;

    private float mTouchStartX;

    private float mTouchStartY;

    private OnPzFloatViewClickListener listener;

    private boolean isAllowTouch = true;

    private Context mContext;

    private long startTouchTime = 0;

    private boolean isShow = false;

    private boolean isAddedToLayout = false;

    private View childView;

    private int floatViewImageRes;

    private boolean canHideInEdge;

    private boolean isUsePermissionVersion;

    private boolean canScrollToLeft;

    private boolean canScrollToTop;

    private boolean canScrollToRight;

    private boolean canScrollToBottom;

    /*is hide in edge*/
    private boolean isHiededInEdeg = false;

    private ObjectAnimator animator;

    private int offsetX = 0;

    private int offsetY = 0;

    /**
     * @param context
     * @param x horizontal offset
     * @param y vertical offset
     * @param childView the child view you want to add
     */
    public PzFloatView(@NonNull Context context, int x, int y, View childView) {
        super(context);
        this.mContext = context;
        this.childView = childView;
        this.offsetX = x;
        this.offsetY = y;
    }




    /**
     * @param context
     * @param x horizontal offset
     * @param y vertical offset
     * @param layoutres the child view Layout Resource you want to add
     */
    public PzFloatView(@NonNull Context context, int x, int y, int layoutres) {
        super(context);
        this.mContext = context;
        this.childView = LayoutInflater.from(getContext()).inflate(layoutres, null);
        this.offsetX = x;
        this.offsetY = y;
    }

    private Activity getActivity(){
        return (Activity) mContext;
    }

    /**
     * just use animator to set floatview alpha to 1
     */
    public void show(){
        if (!isAttachedToWindow() || isShow){
            Log.e(TAG,"Float View is not to attch to window!!!");
            return;
        }
        isShow = true;
        AnimHelper.get().showAnim(this,0,1.0f);
        moveToEdge();

    }

    /**
     * just use animator to set floatview alpha to 0
     */
    public void hide(){
        if (!isAttachedToWindow() || !isShow){
            return;
        }
        isShow = false;
        AnimHelper.get().showAnim(this,1.0f,0f);
    }

    /**
     * add to targetWindow
     * this method can be called only once
     * @return
     */
    public boolean addToWindow() {

        if (isAddedToLayout){
            return true;
        }

        postDelayed(new Runnable() {
            @Override
            public void run() {
                init(mContext,childView,offsetX,offsetY);

                if (wm != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        if (!isAttachedToWindow()) {
                            wm.addView(PzFloatView.this, wmParams);
                            isAddedToLayout = true;
                        }
                    } else {
                        try {
                            if (getParent() == null) {
                                wm.addView(PzFloatView.this, wmParams);
                            }
                            isAddedToLayout = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        },0L);

        return isAddedToLayout;
    }

    /**
     * remove floatview from window
     * if u want to show it again
     * u must call addToWindow() again
     * @return
     */
    public boolean removeFromWindow() {
        isAddedToLayout = false;
        if (wm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                if (isAttachedToWindow()) {
                    wm.removeViewImmediate(this);
                    return true;
                }
            } else {
                try {
                    if (getParent() != null) {
                        wm.removeViewImmediate(this);
                        return true;
                    }
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        return isAllowTouch;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setAlpha(1.0f);
                stopAnim();
                startTouchTime = System.currentTimeMillis();
                mTouchStartX = (int) event.getRawX() - this.getMeasuredWidth() / 2;
                mTouchStartY = (int) event.getRawY() - this.getMeasuredHeight() / 2 ;
                return true;
            case MotionEvent.ACTION_MOVE:
                wmParams.x = (int) event.getRawX() - this.getMeasuredWidth() / 2;
                wmParams.y = (int) event.getRawY() - this.getMeasuredHeight() / 2 ;
                // 刷新
                if (Math.abs(wmParams.y - mTouchStartY) > 10 || Math.abs(wmParams.x - mTouchStartX) > 10) {

                    updateFloatViewPosition(wmParams.x,wmParams.y);


                    //当移动到屏幕中心
//                    if (wmParams.y >= trashcanImageView.getTop()+trashcanView.getY() - (getHeight())
//                            && wmParams.y<= trashcanImageView.getBottom()+trashcanView.getY()
//                            && wmParams.x >= trashcanImageView.getLeft() - (getWidth())
//                            && wmParams.x <= trashcanImageView.getRight()
//                    ){
//                        trashcanImageView.setImageResource(ResourceUtil.getDrawableId(context, "xinhun_float_icon_focus"));
//                    }else{
//                        trashcanImageView.setImageResource(ResourceUtil.getDrawableId(context, "xinhun_float_icon_normal"));
//                    }

                }

                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:


                if (isClick()){
                    Log.d(TAG, "onTouchEvent: click event");
                    if (listener != null){
                        listener.onFloatViewClick();
                    }
                }else {
                    moveToEdge();
                }

                //非点击事件
//                if (!isClick()){
////                    Log.d(TAG, "onTouchEvent: not click event = ");
//                    //是否在提示区域
////                    if (wmParams.y >= trashcanImageView.getTop()+trashcanView.getY()- getHeight()
////                            && wmParams.y<= trashcanImageView.getBottom()+trashcanView.getY()
////                            && wmParams.x >= trashcanImageView.getLeft() - getWidth()
////                            && wmParams.x <= trashcanImageView.getRight()){
////                        boolean isShow = SharePrefUtil.getBoolean(SharePrefConstant.KEY_FLOAT_SHOW_SHAKE_WINDOW,true);
////                        if (isShow){
////                            showMobileShakeView();
////                        }
////                        hide();
////                    }else {
//                        moveToEdge();
////                    }
//
//                }else {
//                    moveToEdge();
//                    //当前非隐藏状态时，显示个人管理弹窗
////                    if (!isHideInEdge()){
//////                        myFloatWindowShow();
////                        hide();
////                    }else {
////                        //从隐藏状态切换为显示状态
////                        moveToEdge();
////                    }
//
//                    //点击事件回调
//                    if (listener != null) {
//                        listener.onFloatViewClick();
//                    }
//                }

                return true;
            default:
                break;
        }
        return false;

    }

    private boolean isClick(){
        long endTouchTime = System.currentTimeMillis();
        boolean isClick = endTouchTime - startTouchTime < 120;
        startTouchTime = endTouchTime;
        return isClick;

    }

    public boolean isShow() {
        return isShow;
    }

    /*
    *
    * to set flaotView type when need to request Permissions
    * */
    private void setFloatViewAdaptToPermission(WindowManager.LayoutParams params){

        int type;
        if (Build.VERSION.SDK_INT >= 19) {
            if (Build.VERSION.SDK_INT >= 26) {
                type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else if (Build.VERSION.SDK_INT > 24) {
                type = WindowManager.LayoutParams.TYPE_PHONE;
            } else {
                type = WindowManager.LayoutParams.TYPE_TOAST;
                if ("Xiaomi".equals(Build.MANUFACTURER)) {
                    type = WindowManager.LayoutParams.TYPE_PHONE;
                }
            }
        } else {
            type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        params.type = type;
    }

    /**
     * set layout no limit flag
     * @param params
     */
    private void setCanScrollBeyondEdge(WindowManager.LayoutParams params){
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    }

    /**
     * @param context
     * @param childView the child view you want to add in layout
     * @param x horizontal offset
     * @param y vertical offset
     */
    private void init(@NonNull final Context context, View childView, int x, int y) {
        int flags = ((Activity)context).getWindow().getAttributes().flags;
        wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

        wmParams = new WindowManager.LayoutParams();
        //set the start position
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.x =  x;
        wmParams.y =  y;

        /*
        * the system dialog also use this type,
        * it doesn't need to request permission
        * but u must set the right token
        * otherwise it will appear some problems
        * */
        wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;

        /*
        * in huawei phone WindowManager.LayoutParams.TYPE_APPLICATION_PANEL
        * need to request permission
        * so use TYPE_APPLICATION instead of it
        * */
        if (SystemUtils.isHuawei()){
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;

        }
        if (isUsePermissionVersion){
            setFloatViewAdaptToPermission(wmParams);
        }
        if (canHideInEdge){
            setCanScrollBeyondEdge(wmParams);
        }
        this.setBackgroundResource(floatViewImageRes);

        if (childView != null) {
            this.childView = childView;
            addView(this.childView);
        }
    }

    /**
     * @param x
     * @param y
     */
    private void updateFloatViewPosition(int x, int y) {
        wmParams.x = x;
        wmParams.y = y;
        if(isAttachedToWindow()){
            wm.updateViewLayout(this, wmParams);
        }
    }

    private void stopAnim(){
        if (animator != null){
            animator.cancel();
            animator.removeAllListeners();
            animator = null;
        }
    }

    private CoordinateHelper.Point mCurrentPoint;

    /**
     * scroll to screen edge
     */
    private void moveToEdge(){
        isHiededInEdeg = false;
        mCurrentPoint = CoordinateHelper.get().getTargetPosition(getActivity(),this,wmParams.x,wmParams.y);
        if (mCurrentPoint.isStartVerticalAnim){
            startMoveAnim(wmParams.y,mCurrentPoint.getY(),0);
        }else {
            startMoveAnim(wmParams.x,mCurrentPoint.getX(),0);
        }

    }

    /**
     * set the floatView hide half in edge
     */
    private void hideInEdge(){

        if (!canHideInEdge) return;

        if (mCurrentPoint != null){
            isHiededInEdeg = true;
            if (mCurrentPoint.isStartVerticalAnim){
                startMoveAnim(mCurrentPoint.getY(),mCurrentPoint.getHideY(),700);
            }else {
                startMoveAnim(mCurrentPoint.getX(),mCurrentPoint.getHideX(),700);
            }
        }
    }

    /**
     * @param start startPosition
     * @param end endPosition
     * @param startDelay delay time to start
     */
    private void startMoveAnim(final int start, final int end, long startDelay){

        animator = ObjectAnimator.ofInt(PzFloatView.this,mCurrentPoint.isStartVerticalAnim ?
                "translationY" : "translationX",(start),  end);
        animator.setDuration(500);
        animator.setRepeatCount(0);
        animator.setStartDelay(startDelay);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mCurrentPoint.isStartVerticalAnim){
                    wmParams.y = (int)animation.getAnimatedValue();
                }else {
                    wmParams.x = (int)animation.getAnimatedValue();
                }
                updateFloatViewPosition(wmParams.x,wmParams.y);

                if (!isHiededInEdeg){
                    hideInEdge();
                }
            }
        });
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    /**
     * set the click callback
     * @param listener
     * @return
     */
    public PzFloatView setClickListener(OnPzFloatViewClickListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * set the floatView's background resource
     * @param floatViewImageRes
     * @return
     */
    public PzFloatView setFloatViewImageRes(int floatViewImageRes) {
        this.floatViewImageRes = floatViewImageRes;
        return this;
    }

    /**
     * set if the floatView can hide half in edge
     * @param canHideInEdge
     * @return
     */
    public PzFloatView setCanHideInEdge(boolean canHideInEdge) {
        this.canHideInEdge = canHideInEdge;
        return this;
    }

    /**
     * set if show the floatView is need to request permission or not
     * if use request permission
     * it can adapt most devices
     * @param usePermissionVersion
     * @return
     */
    public PzFloatView setUsePermissionVersion(boolean usePermissionVersion) {
        isUsePermissionVersion = usePermissionVersion;
        return this;
    }

    /**
     * set floatView can scrool to left edge or not
     * @param canScrollToLeft
     * @return
     */
    public PzFloatView setCanScrollToLeft(boolean canScrollToLeft) {
        this.canScrollToLeft = canScrollToLeft;
        return this;
    }

    /**
     * as same as setCanScrollToLeft
     * @param canScrollToTop
     * @return
     */
    public PzFloatView setCanScrollToTop(boolean canScrollToTop) {
        this.canScrollToTop = canScrollToTop;
        return this;
    }

    /**
     * as same as setCanScrollToLeft
     * @param canScrollToRight
     * @return
     */
    public PzFloatView setCanScrollToRight(boolean canScrollToRight) {
        this.canScrollToRight = canScrollToRight;
        return this;
    }

    /**
     * as same as setCanScrollToLeft
     * @param canScrollToBottom
     * @return
     */
    public PzFloatView setCanScrollToBottom(boolean canScrollToBottom) {
        this.canScrollToBottom = canScrollToBottom;
        return this;
    }

    public boolean isCanScrollToLeft() {
        return canScrollToLeft;
    }

    public boolean isCanScrollToTop() {
        return canScrollToTop;
    }

    public boolean isCanScrollToRight() {
        return canScrollToRight;
    }

    public boolean isCanScrollToBottom() {
        return canScrollToBottom;
    }
}
