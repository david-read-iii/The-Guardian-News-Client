package com.davidread.newsfeed;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * {@link RecyclerViewOnItemClickListener} provides a way to mimic an OnItemClickListener seen in
 * other list view objects through building upon {@link RecyclerView.OnItemTouchListener}.
 */
public class RecyclerViewOnItemClickListener implements RecyclerView.OnItemTouchListener {

    /**
     * {@link GestureDetector} object for detecting a single click on an item.
     */
    private final GestureDetector gestureDetector;

    /**
     * {@link OnItemClickListener} concrete class that defines what to do when
     * {@link RecyclerViewOnItemClickListener} detects an item click.
     */
    private final OnItemClickListener onItemClickListener;

    /**
     * Constructs a new {@link RecyclerViewOnItemClickListener} object.
     *
     * @param context             {@link Context} for the {@link GestureDetector}.
     * @param onItemClickListener {@link OnItemClickListener} concrete class that defines what to
     *                            do when an item click is detected.
     */
    public RecyclerViewOnItemClickListener(Context context, OnItemClickListener onItemClickListener) {
        this.gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }
        });
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * {@link RecyclerView.OnItemTouchListener} callback method invoked when a motion event is
     * detected over the recycler view.
     *
     * @param rv {@link RecyclerView} where the motion view is detected.
     * @param e  {@link MotionEvent} describing what touch event happened.
     * @return True to steal motion events from the children and have them dispatched to this
     * ViewGroup through onTouchEvent().
     */
    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && onItemClickListener != null && gestureDetector.onTouchEvent(e)) {
            onItemClickListener.onItemClick(child, rv.getChildLayoutPosition(child));
        }
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    /**
     * Interface definition for a callback to be invoked when an item in a {@link RecyclerView} has
     * been clicked.
     */
    public interface OnItemClickListener {

        /**
         * Callback method to be invoked when an item in this {@link RecyclerView} has been clicked.
         *
         * @param view     {@link View} within the {@link RecyclerView} that was clicked.
         * @param position int representing the position of the view within the adapter.
         */
        void onItemClick(View view, int position);
    }
}
