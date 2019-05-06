package com.rocky.flowlayoutlibrary;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author rocky
 * @date 2019/5/5.
 * description：为rv添加线
 */
public class RockyItemDecoration extends RecyclerView.ItemDecoration {

    private final int space;

    public RockyItemDecoration(int space){
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.top = space;
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;
    }
}
