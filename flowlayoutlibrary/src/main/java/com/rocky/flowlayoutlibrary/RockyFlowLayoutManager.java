package com.rocky.flowlayoutlibrary;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rocky
 * @date 2019/5/5.，
 * description： 重写RecyclerView.LayoutManager  以至于实现flowlayout功能
 * 思路整理
 * 1.首先 定义 item
 * 2、列容器
 * 3、定义行，行属性
 * 4、根据行的内容   摆放行 形成列
 * 5、摆放，计算位置 摆放行和列
 * 6、计算 屏幕上显示 的内容  滑动出去的 隐藏 放入缓存
 */
public class RockyFlowLayoutManager extends RecyclerView.LayoutManager {

    private Row row;
    private int verticalScrollOffset = 0;//竖直方向偏移量
    private int maxWidth;    //最大容器的宽度


    //第二部 构造方法 开启测量过则
    public RockyFlowLayoutManager() {
        //开启测量规则 设置rv的高为wrap_content
        setAutoMeasureEnabled(true);
    }

    //第一步  继承RecyclerView.LayoutManager 是实现该方法
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        //让宽高自适应
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public int getTotalHeight() {
        return totalHeight;
    }

    //第三步，定义item  具体要绘制的 如 textview 或者 imageview 等
    public class Item {
        int itemHeight;
        View view;
        Rect rect;//记录具体item的位置

        public void setRect(Rect rect) {
            this.rect = rect;
        }

        public Item(int itemHeight, View view, Rect rect) {
            this.itemHeight = itemHeight;
            this.view = view;
            this.rect = rect;


        }

    }

    //第四步，定义行
    public class Row {
        //定义行头的起始y坐标
        float startTop;

        public void setStartTop(float startTop) {
            this.startTop = startTop;
        }

        //定义行的最大高度
        float maxHeight;

        public void setMaxHeight(float maxHeight) {
            this.maxHeight = maxHeight;
        }

        //定义行 容器 存储 具体item
        List<Item> items = new ArrayList<>();

        public void addItem(Item view) {
            items.add(view);
        }
    }

    //第五步  定义一个list  放所有行
    private List<Row> rows = new ArrayList<>();

    //保存所有的Item的上下左右的偏移量信息
    private SparseArray<Rect> allItemFrames = new SparseArray<>();
    //计算显示的内容的高度
    protected int totalHeight = 0;
    //具体的item的 宽高和位置坐标值
    protected int width, height;
    private int left, top, right;


    //第六步，处理所有 item的 位置
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
//        super.onLayoutChildren(recycler, state);
        Log.e("RecyclerView", "Rocky override onLayoutChildren(Recycler recycler, State state");

        totalHeight = 0;//每次重新摆放 item的时候 把高度设置为0
        int currentLineTop = top;
        //当前行使用的的宽度
        int currentLineWidth = 0;
        int itemLeft;
        int itemTop;
        int maxItemHeight = 0;
        row = new Row();
        rows.clear();
        allItemFrames.clear();
        removeAllViews();

        if (0 == getItemCount()) {

            //回收所有是视图
            detachAndScrapAttachedViews(recycler);
            verticalScrollOffset = 0;
            return;
        }

        if (0 == getChildCount() && state.isPreLayout()) {
            //当前 屏幕没有item的话结束
            return;
        }

//        onLayoutChildren(); 方法在recyclerview 初始化时 会执行两次

        detachAndScrapAttachedViews(recycler);

        if (0 == getChildCount()) {
            //当前 屏幕没有 item  宽高位置 初始化
            width = getWidth();
            height = getHeight();
            left = getPaddingLeft();
            right = getPaddingRight();
            top = getPaddingTop();
            maxWidth = width - left - right;
        }

        int itemCount = getItemCount();

        for (int i = 0; i < itemCount; i++) {

            View viewForPosition = recycler.getViewForPosition(i);
            if (viewForPosition.getVisibility() == View.GONE) {
                continue;
            }
            //测量view的 margin 属性值
            measureChildWithMargins(viewForPosition, 0, 0);

            int decoratedMeasuredWidth = getDecoratedMeasuredWidth(viewForPosition);
            int decoratedMeasuredHeight = getDecoratedMeasuredHeight(viewForPosition);
            int childUseWidth = decoratedMeasuredWidth;
            int childUseHeight = decoratedMeasuredHeight;
            //判断 当前item的宽度 加上原来的 是否大于最大宽度
            if (currentLineWidth + childUseWidth <= maxWidth) {
                itemLeft = left + currentLineWidth;
                itemTop = currentLineTop;
                Rect rect = allItemFrames.get(i);
                if (rect == null) {
                    rect = new Rect();
                }
                rect.set(itemLeft, itemTop,
                        itemLeft + decoratedMeasuredWidth,
                        itemTop + decoratedMeasuredHeight);
                allItemFrames.put(i, rect);
                currentLineWidth += childUseWidth;
                maxItemHeight = Math.max(maxItemHeight, childUseHeight);//保存 最大高
                row.addItem(new Item(childUseHeight, viewForPosition, rect));//加入行容器
                row.setStartTop(currentLineTop);
                row.setMaxHeight(maxItemHeight);

            } else {
                //此时 一行 放不下 需要换行

                //格式化每一行内容  让view 剧中显示
                formatAboveRow();

                //更新绘制的坐标
                currentLineTop += maxItemHeight;
                totalHeight += maxItemHeight;
                itemTop = currentLineTop;
                itemLeft = left;
                Rect rect = allItemFrames.get(i);
                if (rect == null) {
                    rect = new Rect();
                }
                rect.set(itemLeft, itemTop,
                        itemLeft + decoratedMeasuredWidth,
                        itemTop + decoratedMeasuredHeight);
                allItemFrames.put(i, rect);
                currentLineWidth = childUseWidth;
                maxItemHeight = childUseHeight;
                row.addItem(new Item(childUseHeight, viewForPosition, rect));
                row.setStartTop(currentLineTop);
                row.setMaxHeight(maxItemHeight);
            }

            //最后一个item 刷新布局
            if (i == getItemCount() - 1) {
                formatAboveRow();
                totalHeight += maxItemHeight;
            }


        }
        totalHeight = Math.max(totalHeight, getVertivalSpace());
        fillLayout(recycler, state);//填充 摆放
    }

    private void fillLayout(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.isPreLayout() || getItemCount() == 0) {
            // 跳过preLayout，preLayout主要用于支持动画
            return;

        }

        // 在 scroll offset 状态下显示 区域
        Rect displayFrame = new Rect(getPaddingLeft(), getPaddingTop() + verticalScrollOffset,
                getWidth() - getPaddingRight(), verticalScrollOffset + (getHeight() - getPaddingBottom()));


        int size = rows.size();
        for (int i = 0; i < size; i++) {
            Row row = rows.get(i);
            float lineTop = row.startTop;
            float lineBottom = lineTop + row.maxHeight;
            //判断是否在屏幕中
            if (lineTop < displayFrame.bottom && displayFrame.top < lineBottom) {
                List<Item> views = row.items;
                int size1 = views.size();
                for (int i1 = 0; i1 < size1; i1++) {
                    View scrap = views.get(i1).view;
                    measureChildWithMargins(scrap, 0, 0);
                    addView(scrap);

                    Rect rect = views.get(i1).rect;
                    //显示
                    layoutDecoratedWithMargins(scrap, rect.left, rect.top - verticalScrollOffset,
                            rect.right, rect.bottom - verticalScrollOffset);

                }

            } else {
                //屏幕外 进行缓存

                List<Item> items = row.items;
                int size1 = items.size();
                for (int i1 = 0; i1 < size1; i1++) {
                    View view = items.get(i1).view;
                    removeAndRecycleView(view, recycler);
                }
            }
        }

    }

    private int getVertivalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    public int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    private void formatAboveRow() {
        List<Item> items = row.items;
        int size = items.size();
        for (int i = 0; i < size; i++) {
            Item item = items.get(i);
            View view = item.view;
            int position = getPosition(view);
            //如果该item的位置不在该行中间位置的话，进行重新放置

            Log.d("RockyFlowLayoutManager", "position:" + position);

            if (allItemFrames.get(position).top < row.startTop + (row.maxHeight - items.get(i).itemHeight) / 2) {
                Rect rect = allItemFrames.get(position);
                rect.set(allItemFrames.get(position).left,
                        (int) (row.startTop + (row.maxHeight - items.get(i).itemHeight) / 2),
                        allItemFrames.get(position).right,
                        (int) (row.startTop + (row.maxHeight - items.get(i).itemHeight) / 2 + getDecoratedMeasuredHeight(view)));


                allItemFrames.put(position, rect);
                item.setRect(rect);
                items.set(i, item);

            }


        }
        row.items = items;
        rows.add(row);
        row = new Row();
    }

    /**
     * 竖直方向需要滑动的条件
     *
     * @return
     */
    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.d("TAG", "totalHeight:" + totalHeight);
        //实际要滑动的距离
        int travel = dy;

        //如果滑动到最顶部
        if (verticalScrollOffset + dy < 0) {//限制滑动到顶部之后，不让继续向上滑动了
            travel = -verticalScrollOffset;//verticalScrollOffset=0
        } else if (verticalScrollOffset + dy > totalHeight - getVertivalSpace()) {//如果滑动到最底部
            travel = totalHeight - getVertivalSpace() - verticalScrollOffset;//verticalScrollOffset=totalHeight - getVerticalSpace()
        }

        //将竖直方向的偏移量+travel
        verticalScrollOffset += travel;

        // 平移容器内的item
        offsetChildrenVertical(-travel);
        fillLayout(recycler, state);
        return travel;
    }
}
