package com.dup.beauty.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dup.beauty.R;
import com.dup.beauty.model.entity.Category;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 主界面 分类列表 adapter
 * Created by DP on 2016/9/26.
 */
public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder> {
    private Context context;
    private List<Category> categories;

    public CategoriesAdapter(Context context, List<Category> categories) {
        this.categories = categories;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(View.inflate(parent.getContext(), R.layout.item_main_categories, null));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.tv.setText(category.getName());
        int drawable = context.getResources().getIdentifier("icon_category_" + position, "drawable", context.getPackageName());
        holder.iv.setImageResource(drawable);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_main_categories_tv)
        public TextView tv;
        @BindView(R.id.item_main_categories_iv)
        public ImageView iv;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
