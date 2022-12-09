package com.said.oubella.so.player.helpers;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Source: https://gist.github.com/liangzhitao/e57df3c3232ee446d464
 * But edited to match my needs
 */
public final class ItemDecorator extends RecyclerView.ItemDecoration {

    public static final int VERTICAL = 0;
    public static final int GRID = 1;

    private final int displayMode;
    private final int spacing;

    public ItemDecorator(int spacing, int displayMode) {
        this.spacing = spacing;
        this.displayMode = displayMode;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildViewHolder(view).getAdapterPosition();
        int itemCount = state.getItemCount();
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        setSpacingForDirection(outRect, layoutManager, position, itemCount);
    }

    private void setSpacingForDirection(Rect outRect, RecyclerView.LayoutManager layoutManager, int position, int itemCount) {
        switch (displayMode) {
            case VERTICAL:
                outRect.bottom = position == itemCount - 1 ? spacing : 0;
                outRect.right = spacing;
                outRect.left = spacing;
                outRect.top = spacing;
                break;
            case GRID:
                int spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
                if (position >= 0) {
                    int column = position % spanCount;
                    outRect.left = spacing - column * spacing / spanCount;
                    outRect.right = (column + 1) * spacing / spanCount;
                    if (position < spanCount) outRect.top = spacing;
                    outRect.bottom = spacing;
                } else {
                    outRect.left = 0;
                    outRect.right = 0;
                    outRect.top = 0;
                    outRect.bottom = 0;
                }
                break;
        }
    }
}