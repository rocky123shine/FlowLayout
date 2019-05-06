package com.rocky.rockyflowlayout;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.rocky.flowlayoutlibrary.RockyFlowLayoutManager;
import com.rocky.flowlayoutlibrary.RockyItemDecoration;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = ProductAdapter.class.getSimpleName();
    private List<TeamData> classifies;
    private Context context;

    public ProductAdapter(Context context, List<TeamData> classifies) {
        this.context = context;
        this.classifies = classifies;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProductHolder(View.inflate(context, R.layout.product_item, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ProductHolder productHolder = (ProductHolder) holder;
        TeamData classify = classifies.get(position);

        final RockyFlowLayoutManager flowLayoutManager = new RockyFlowLayoutManager();
        productHolder.title.setText(classify.getTitle());
        if (productHolder.itemView.getTag() == null) {
            productHolder.des.addItemDecoration(new RockyItemDecoration(dp2px(10)));
            productHolder.itemView.setTag("item");
        }
//        productHolder.des.addItemDecoration(new SpaceItemDecoration(dp2px(10)));
        productHolder.des.setLayoutManager(flowLayoutManager);
        productHolder.des.setAdapter(new FlowAdapter(classify.getItems()));
    }

    public String getTitle(int position) {
        return classifies.get(position).getTitle();
    }

    @Override
    public int getItemCount() {
        return classifies.size();
    }

    class ProductHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private RecyclerView des;

        public ProductHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            des = (RecyclerView) itemView.findViewById(R.id.des);
        }
    }

    class FlowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<Des> list;
        private Des selectDes;

        public FlowAdapter(List<Des> list) {
            this.list = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyHolder(View.inflate(context, R.layout.flow_item, null));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            TextView textView = ((MyHolder) holder).text;

            final Des des = list.get(position);
            if (des.isSelect) {
                textView.setBackground(context.getResources().getDrawable(R.drawable.product_item_select_back));
            } else {
                textView.setBackground(context.getResources().getDrawable(R.drawable.product_item_back));
            }
            textView.setText(des.des);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (des != selectDes) {
                        if (selectDes != null) {
                            selectDes.isSelect = false;
                        }
                    }
                    des.isSelect = true;
                    selectDes = des;
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {

            private TextView text;

            public MyHolder(View itemView) {
                super(itemView);
                text = (TextView) itemView.findViewById(R.id.flow_text);
            }
        }
    }

    private int dp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
    }
}
