package info.guron.redminer;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ListView;

import info.guron.socket_redmine.Main;

/**
 * Created by guron on 02.03.14.
 */
public class MyListView extends ListView {
    public GestureDetectorCompat mDetector;
    private float startY = -1;
    private float height_hat = 0;
    private Context context;
    private FrameLayout layout;

    public MyListView(Context context) {
        super(context);
//        this.context = context;
//        mDetector = new GestureDetectorCompat(this.getContext(),new MyGestureListener());
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        this.context = context;
//        mDetector = new GestureDetectorCompat(this.getContext(),new MyGestureListener());
    }

    public MyListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //this.context = context;
        //mDetector = new GestureDetectorCompat(this.getContext(),new MyGestureListener());
    }
//    @Override
//    public boolean onTouchEvent(android.view.MotionEvent ev) {
//        int action = MotionEventCompat.getActionMasked(ev);
//        if(action==MotionEvent.ACTION_UP) { //обнуляем при поднятии пальца
//            startY = -1;
//            //height_hat = 0;
//            System.out.println(height_hat);
//        }
//        this.mDetector.onTouchEvent(ev);
//        //return super.onTouchEvent(ev);
//        return true;
//    }

//    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
//        private static final String DEBUG_TAG = "Gestures";
//
//        @Override
//        public boolean onScroll (MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
//            height_hat = height_hat + distanceY;
//            System.out.println( height_hat);
//            System.out.println("padding = " + ((TasksList)context).findViewById(R.id.UpdateLayout).getPaddingTop());
//            layout = (FrameLayout)((TasksList) context).findViewById(R.id.UpdateLayout);
//            //ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
//            //layoutParams.height = -Math.round(height_hat);
//            //layout.setLayoutParams(layoutParams);
//
//            //layout.animate().rotation(-Math.round(height_hat)).start();
//            layout.startAnimation(new ani());
//                    //
//            return true;
//        }
//    }
//
//    class myAnimation extends Animation{
//        public myAnimation(FrameLayout layout){
//
//        }
//    }

//    class ani extends Animation
//    {
//
//        public ani(){}
//        @Override
//        protected void applyTransformation(float interpolatedTime, Transformation t) {
//            int newHeight;
//            if(height_hat>-25) return;
//            ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
//            layoutParams.height = -Math.round(height_hat);
//            layout.setLayoutParams(layoutParams);
//        }
//
//        @Override
//        public void initialize(int width, int height, int parentWidth, int parentHeight) {
//            super.initialize(width, height, parentWidth, parentHeight);
//            //initialHeight = actualHeight;
//
//        }
//
//        @Override
//        public boolean willChangeBounds() {
//            return true;
//        }
//    };
}
