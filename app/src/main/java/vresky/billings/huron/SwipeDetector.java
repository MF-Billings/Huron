package vresky.billings.huron;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Matt on 22/12/2016.
 */

// NOTE this class will be removed in the next commit. Kept temporarily as reference
public class SwipeDetector implements View.OnTouchListener {

    public enum Action {
        LEFT_TO_RIGHT, 
        RIGHT_TO_LEFT, 
        TOP_TO_BOTTOM, 
        BOTTOM_TO_TOP, 
        NONE            // when no action is detected
    }

    private static final String TAG = "SwipeDetector";
    private static final int MIN_DISTANCE = 100;
    private float downX, downY, upX, upY;
    private Action mSwipeDetected = Action.NONE;

    public boolean swipeDetected() {
        return mSwipeDetected != Action.NONE;
    }

    public Action getAction() {
        return mSwipeDetected;
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
                mSwipeDetected = Action.NONE;
                return false; // allow other events like Click to be processed
            }
            case MotionEvent.ACTION_MOVE: {
                upX = event.getX();
                upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                // horizontal swipe detection
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // left or right
                    if (deltaX < 0) {
                        Log.d(TAG, "Swipe Left to Right");
                        mSwipeDetected = Action.LEFT_TO_RIGHT;
                        return true;
                    }
                    if (deltaX > 0) {
                        Log.d(TAG, "Swipe Right to Left");
                        mSwipeDetected = Action.RIGHT_TO_LEFT;
                        return true;
                    }
                } else
                    // vertical swipe detection
                    if (Math.abs(deltaY) > MIN_DISTANCE) {
                        // top or down
                        if (deltaY < 0) {
                            Log.d(TAG, "Swipe Top to Bottom");
                            mSwipeDetected = Action.TOP_TO_BOTTOM;
                            return false;
                        }
                        if (deltaY > 0) {
                            Log.d(TAG, "Swipe Bottom to Top");
                            mSwipeDetected = Action.BOTTOM_TO_TOP;
                            return false;
                        }
                    }
                return true;
            }
        }
        return false;
    }
}
