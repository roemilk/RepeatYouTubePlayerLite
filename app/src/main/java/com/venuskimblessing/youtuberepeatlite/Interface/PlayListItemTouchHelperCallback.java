package com.venuskimblessing.youtuberepeatlite.Interface;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class PlayListItemTouchHelperCallback extends ItemTouchHelper.Callback{
    private final String TAG = "PlayListItemTouchHelperCallback";

    public interface OnItemMoveListener{
        void onItemMove(int fromPosition, int toPosition);
        void onItemSwipe(int position);
    }

    private boolean mOrderChanged = false;

    private OnItemMoveListener mItemMoveListener = null;
    public PlayListItemTouchHelperCallback(OnItemMoveListener listener) {
        this.mItemMoveListener = listener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mOrderChanged = true;
        mItemMoveListener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mItemMoveListener.onItemSwipe(viewHolder.getAdapterPosition());
    }
}