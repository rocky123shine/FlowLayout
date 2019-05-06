package com.rocky.flowlayoutlibrary;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author rocky
 * @date 2019/5/5.
 * description：解决rv 嵌套 rv的问题
 */
public class NestedRecyclerView extends RecyclerView {
    public NestedRecyclerView(@NonNull Context context) {
        super(context);
    }

    public NestedRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);

        RockyFlowLayoutManager layoutManager = (RockyFlowLayoutManager) getLayoutManager();
        int widthMode = View.MeasureSpec.getMode(widthSpec);
        int measureWidth = View.MeasureSpec.getSize(widthSpec);
        int heightMode = View.MeasureSpec.getMode(heightSpec);

        int measureHeight = View.MeasureSpec.getSize(heightSpec);

        int width,height;

        if (widthMode == View.MeasureSpec.EXACTLY) {
            width = measureWidth;
        }else {
            width = getContext().getResources().getDisplayMetrics().widthPixels;
        }

        if (heightMode == View.MeasureSpec.EXACTLY) {
            height = measureHeight;
        } else {
            height = layoutManager.getTotalHeight() + getPaddingTop() + getPaddingBottom();
        }
        setMeasuredDimension(width, height);



    }
}
