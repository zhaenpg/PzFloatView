package com.zzp.code.pzfloatview;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

/*
* Author : Zhangzhenpeng
* Description : CoordinateHelper is Help PzFloatView to get the coordinate it need to scroll
* Create When 2019/7/19 15:39
* TODO:None
* Last update by
* When
* Update Description :
*/
public class CoordinateHelper {

    private static final String TAG = "PzFloatView";

    private static CoordinateHelper INSTANCE = null;

    private CoordinateHelper(){}

    public static CoordinateHelper get(){
        if (INSTANCE == null){
            INSTANCE = CoordinateHelperFactory.INSTANCE;
        }
        return INSTANCE;
    }

    /*Point class is use to record the position need to scroll*/
    public static class Point{
        @Deprecated
        boolean isStartVerticalAnim;
        int x;
        int y;
        int hideX;
        int hideY;

        public Point(int x, int y, int hideX, int hideY, boolean isStartVerticalAnim) {
            this.x = x;
            this.y = y;
            this.hideX = hideX;
            this.hideY = hideY;
            this.isStartVerticalAnim = isStartVerticalAnim;
        }

        public boolean isStartVerticalAnim() {
            return isStartVerticalAnim;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getHideX() {
            return hideX;
        }

        public int getHideY() {
            return hideY;
        }
    }

    public enum Direction {

        LEFT,
        TOP,
        RIGHT,
        BOTTOM,
        DONT_SCROLL
    }

    /**
     * scroll to the target position
     * @param x
     * @param y
     * @return
     */
    public Point getTargetPosition(Activity activity,PzFloatView targetView, int x, int y){

        Direction targetScrollPosition =  getNeedToScrollPosition(activity,targetView,x,y);
        Log.d(TAG, "getTargetPosition: move to this direction --> " + targetScrollPosition);
        Point point;
        switch (targetScrollPosition){
            case LEFT:
                point = new Point(0,y,0 - targetView.getWidth() / 2,y,false);
                break;
            case TOP:
                point = new Point(x,0,x,0 - targetView.getHeight() / 2,true);
                break;
            case RIGHT:
                point = new Point(getNeededWidth(activity) - targetView.getWidth(),y,
                        getNeededWidth(activity) - targetView.getWidth() / 2,y,false);
                break;
            case BOTTOM:
                point = new Point(x,getNeededHeight(activity) - targetView.getWidth(),x,
                        getNeededHeight(activity) - targetView.getWidth() / 2 ,true);
                break;
            case DONT_SCROLL:
                point = null;
                break;
            default:
                point = new Point(0,y,0 - targetView.getWidth() / 2,y ,false);
                break;
        }
        return point;
    }

    /**
     * get the direction need to scroll
     * @param currentX
     * @param currentY
     * @return
     */
    private Direction getNeedToScrollPosition(Activity activity,PzFloatView floatView,int currentX,int currentY){
        //not set the direction can scroll
        //so set don't scroll
        if (!floatView.isCanScrollToTop()
                && !floatView.isCanScrollToLeft()
                && !floatView.isCanScrollToRight()
                && !floatView.isCanScrollToBottom()){
            return Direction.DONT_SCROLL;
        }
        if (isNearLeftArea(activity,currentX)){
            return toJudgeWhereToScrollInLeft(activity,floatView,currentX,currentY);
        }else {
            return toJudgeWhereToScrollInRight(activity,floatView,currentX,currentY);
        }
    }

    private Direction toJudgeWhereToScrollInLeft(Activity activity,PzFloatView floatView,int currentX,int currentY){

        if (isNearTopArea(activity,currentY)){
            return currentX > currentY ?
                    (floatView.isCanScrollToTop() ? Direction.TOP : Direction.LEFT)
                    :(floatView.isCanScrollToLeft() ? Direction.LEFT : Direction.TOP);
        }else {
            return currentX > getNeededHeight(activity) - currentY ?
                    floatView.isCanScrollToBottom() ? Direction.BOTTOM  : Direction.LEFT
                    : floatView.isCanScrollToBottom() ? Direction.LEFT : Direction.BOTTOM;
        }
    }

    private Direction toJudgeWhereToScrollInRight(Activity activity,PzFloatView floatView,int currentX,int currentY){
        if (isNearTopArea(activity,currentY)){

            if (!floatView.isCanScrollToTop() && !floatView.isCanScrollToRight()){
                return Direction.LEFT;
            }

            return getNeededWidth(activity) - currentX > currentY ?
                    floatView.isCanScrollToTop() ? Direction.TOP : Direction.RIGHT
                    : floatView.isCanScrollToRight() ? Direction.RIGHT : Direction.TOP;
        }else {
            if (!floatView.isCanScrollToBottom() && !floatView.isCanScrollToRight()){
                return Direction.LEFT;
            }

            return getNeededWidth(activity) - currentX > getNeededHeight(activity) - currentY ?
                    floatView.isCanScrollToBottom() ? Direction.BOTTOM : Direction.RIGHT
                    : floatView.isCanScrollToRight() ? Direction.RIGHT : Direction.BOTTOM;
        }
    }

    private boolean isNearTopArea(Activity activity,int currentY){
        return currentY < getNeededHeight(activity) / 2;
    }

    /**
     * @param currentX
     * @return is in the left area
     */
    private boolean isNearLeftArea(Activity activity,int currentX){
        return currentX < getNeededWidth(activity) / 2;
    }

    /**
     * get the app real use width
     * @return
     */
    private int getNeededWidth(Context context){
        //use this width in some devices is not adapter
//        int screenWidth = ScreenUtil.getScreenWdith();

        //use this width can adapter most devices
        int width = getDecorView(context).getWidth();
        return width;
    }

    /**
     * as same as width
     * @return
     */
    private int getNeededHeight(Context context){
        int height = getDecorView(context).getHeight();
        return height;
    }

    /**
     * get the app's decor view
     * @param context
     * @return
     */
    private View getDecorView(Context context){
        Activity activity = (Activity) context;
        View decorView = activity.getWindow().getDecorView();
        return decorView;
    }


    private static final class CoordinateHelperFactory{
        private static final CoordinateHelper INSTANCE = new CoordinateHelper();
    }
}
