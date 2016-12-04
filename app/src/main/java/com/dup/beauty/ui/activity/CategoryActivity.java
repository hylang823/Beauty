package com.dup.beauty.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.dup.beauty.R;
import com.dup.beauty.app.BaseActivity;
import com.dup.beauty.model.entity.Category;
import com.dup.beauty.model.entity.Gallery;
import com.dup.beauty.presenter.contract.ICategoryPresenter;
import com.dup.beauty.presenter.contract.ILoginRegisterPresenter;
import com.dup.beauty.presenter.impl.CategoryPresenter;
import com.dup.beauty.presenter.impl.LoginRegisterPresenter;
import com.dup.beauty.ui.adapter.GalleriesAdapter;
import com.dup.beauty.ui.widget.ColorSwipeRefreshLayout;
import com.dup.beauty.view.ICategoryView;
import com.dup.beauty.view.ILoginRegisterView;
import com.dup.changeskin.SkinManager;
import com.jaeger.library.StatusBarUtil;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 一个分类中的图库列表界面
 * <ul>
 * <li>{@link ICategoryPresenter}</li>
 * <li>{@link CategoryPresenter}</li>
 * <li>{@link ICategoryView}</li>
 * </ul>
 */
public class CategoryActivity extends BaseActivity implements ICategoryView, GalleriesAdapter.OnItemClickListener {
    @BindView(R.id.toolbar_title)
    public TextView titleTv;
    @BindView(R.id.category_gallery_recyclerview)
    public XRecyclerView recyclerView;
    @BindView(R.id.swipe_refresh)
    public ColorSwipeRefreshLayout refreshLayout;

    private ICategoryPresenter mPresenter;
    private GalleriesAdapter mAdapter;
    private Category category;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_category;
    }

    @Override
    protected void initView() {
        super.initView();

        StatusBarUtil.setColor(this, SkinManager.getInstance().getResourceManager().getColor("status_bar_bg"),0);
        ButterKnife.bind(CategoryActivity.this);

        //设置recyclerview
        recyclerView.setPullRefreshEnabled(false);
        recyclerView.setLoadingMoreEnabled(true);
        recyclerView.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
    }

    @Override
    protected void initData() {
        super.initData();
        Bundle bundle = getIntent().getExtras();
        category = (Category) bundle.getSerializable("CATEGORY");
        if (category == null) {
            finish();
            return;
        }

        //设置标题
        titleTv.setText(category.getTitle());
        //获取网络数据
        mPresenter.fetchGalleriesWithId(category.getId());
    }

    @Override
    protected void initAction() {
        super.initAction();
        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
                mPresenter.fetchMoreGalleriesWithId();
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected boolean isSupportSwipeBack() {
        return true;
    }

    @Override
    protected void bindPresenters() {
        super.bindPresenters();
        mPresenter = new CategoryPresenter(this, this);
    }

    /**
     * 退回按钮点击事件
     *
     * @param view
     */
    @OnClick(R.id.toolbar_back)
    public void onBackPress(View view) {
        finish();
    }

    /**
     * 获取到此分类的图库们
     *
     * @param galleries 带图片们数据
     * @param page      页数
     * @param id
     */
    @Override
    public void onGalleriesWithId(List<Gallery> galleries, int page, long id) {
        //设置adapter
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mAdapter = new GalleriesAdapter(this, galleries, metric.widthPixels);
        mAdapter.setItemClickListener(this);
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * 获取到下一页数据
     *
     * @param galleries 带图片们数据
     * @param page      第几页
     */
    @Override
    public void onMoreGalleriesWithId(List<Gallery> galleries, int page) {
        recyclerView.loadMoreComplete();
        int nowCount = mAdapter.getItemCount();
        mAdapter.getData().addAll(galleries);
        mAdapter.notifyItemRangeInserted(nowCount + 1, galleries.size());//这里加1是考虑进了header
    }

    @Override
    public void onItemClick(int position, Gallery gallery) {
        Intent intent = new Intent();
        intent.putExtra("GALLERY", gallery);
        intent.putExtra("POSITION", position - 1);//考虑header
        intent.putExtra("GALLERIES", mPresenter.getGalleries());
        intent.setClass(this, GalleryActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDataLoad(boolean isFinish) {
        refreshLayout.setRefreshing(!isFinish);
    }
}