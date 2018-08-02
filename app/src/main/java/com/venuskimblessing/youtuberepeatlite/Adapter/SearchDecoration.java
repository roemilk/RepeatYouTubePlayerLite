package com.venuskimblessing.youtuberepeatlite.Adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

public class SearchDecoration extends RecyclerView.ItemDecoration{
    private int dividerSize;
    private int outerSize;

    public SearchDecoration(Context context) {
        dividerSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics());
        outerSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = outerSize;
            outRect.bottom = dividerSize;
        } else if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
            outRect.bottom = dividerSize;
        } else {
            outRect.bottom = outerSize;
        }
    }
}
