package com.rocky.flowlayoutlibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import java.util.List;

/**
 * @author rocky
 * @date 2019/5/6.
 * description：实现悬浮组名的 装饰基类
 * BaseData 分组 一组的数据  包括 组名 和 组数据列表List<T>
 * <p>
 * D 是 具体数据类  但是要包括 BaseData
 */
public class RockySuspensionDecoration<T, D extends BaseData<T>> extends RecyclerView.ItemDecoration {
    private List<D> datas;
    private Paint mPaint;//绘制组名的画笔
    private Rect mBounds;//用于测量文字的宽高的存放
    private static int COLOR_TITLE_BG = Color.parseColor("#eeeeee");
    private static int COLOR_TITLE_FONT = Color.parseColor("#aaaaaa");
    private static int mTitleFontSize;//title字体大小
    private int mHeaderViewCount = 0;
    private int mTitleHeight = 0;
    private int paddingLeft;

    public int getHeaderViewCount() {
        return mHeaderViewCount;
    }

    public RockySuspensionDecoration setHeaderViewCount(int headerViewCount) {
        mHeaderViewCount = headerViewCount;
        return this;
    }

    /**
     * @param context
     * @param dList
     * @param color_title_bg   标题背景颜色
     * @param color_title_font 字体颜色
     * @param title_size       字体大小 单位 sp
     * @param title_height     组名高度 单位 dp
     * @param paddingLeft      据左边的距离  单位dp
     */
    public RockySuspensionDecoration(Context context, List<D> dList, int color_title_bg,
                                     int color_title_font,
                                     int title_size, int title_height, int paddingLeft) {
        super();
        COLOR_TITLE_BG = color_title_bg;
        COLOR_TITLE_FONT = color_title_font;
        this.datas = dList;
        mPaint = new Paint();
        mBounds = new Rect();
        //dp转px
        mTitleHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, title_height, context.getResources().getDisplayMetrics());
        this.paddingLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paddingLeft, context.getResources().getDisplayMetrics());
        mTitleFontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, title_size, context.getResources().getDisplayMetrics());
        mPaint.setTextSize(mTitleFontSize);
        mPaint.setAntiAlias(true);
    }

    /**
     * 默认字体大小和颜色
     *
     * @param context
     * @param dList
     */
    public RockySuspensionDecoration(Context context, List<D> dList) {
        super();
        this.datas = dList;
        mPaint = new Paint();
        mBounds = new Rect();
        //dp转px
        mTitleHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, context.getResources().getDisplayMetrics());
        paddingLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics());
        mTitleFontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, context.getResources().getDisplayMetrics());
        mPaint.setTextSize(mTitleFontSize);
        mPaint.setAntiAlias(true);
    }

    //最后调用 绘制在最上层
    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int pos = ((LinearLayoutManager) parent.getLayoutManager()).findFirstVisibleItemPosition();

        pos -= getHeaderViewCount();
        if (datas == null || datas.isEmpty() || pos > datas.size() - 1 || pos < 0) {
            return;//越界
        }
        String tag = datas.get(pos).getTitle();
        View child = parent.findViewHolderForLayoutPosition(pos + getHeaderViewCount()).itemView;//出现一个奇怪的bug，有时候child为空，所以将 child = parent.getChildAt(i)。-》 parent.findViewHolderForLayoutPosition(pos).itemView
        boolean flag = false;//定义一个flag，Canvas是否位移过的标志
        if (pos < datas.size() - 1) {
            //当前第一个可见的Item的tag，不等于其后一个item的tag，说明悬浮的View要切换了
            if (null != tag && !tag.equals(datas.get(pos + 1).getTitle())) {

                //当第一个可见的item在屏幕中还剩的高度小于title区域的高度时，我们也该开始做悬浮Title的“交换动画”
                if (child.getHeight() + child.getTop() < mTitleHeight) {
                    c.save();//每次绘制前 保存当前Canvas状态，
                    flag = true;
                    //这里是将下面画的悬浮的部分往上移动mTitleHeight的距离,这里平移是下面画的悬浮title部分，
                    //这里是从0到-mTitleHeight的过程，直至整个悬浮的title消失
                    c.translate(0, child.getHeight() + child.getTop() - mTitleHeight);
                }
            }
        }

        /**
         * 实际上这里是绘制悬浮的title部分，永远在顶部显示
         */
        mPaint.setColor(COLOR_TITLE_BG);
        //这里实际上是在一个固定的位置添加一个矩形的title罢了
        c.drawRect(parent.getPaddingLeft(), parent.getPaddingTop(), parent.getRight() - parent.getPaddingRight(), parent.getPaddingTop() + mTitleHeight, mPaint);
        mPaint.setColor(COLOR_TITLE_FONT);
        //将文本通过画笔来算出它占据的空间
        mPaint.getTextBounds(tag, 0, tag.length(), mBounds);
        //这里也是算的左下角的坐标啊，到底是什么回事啊
        c.drawText(tag, child.getPaddingLeft() + paddingLeft,
                parent.getPaddingTop() + mTitleHeight - (mTitleHeight / 2 - mBounds.height() / 2),
                mPaint);
        //只有在做了切换悬浮title动画的时候才会有该操作
        if (flag)
            c.restore();//恢复画布到之前保存的状态
    }
}
