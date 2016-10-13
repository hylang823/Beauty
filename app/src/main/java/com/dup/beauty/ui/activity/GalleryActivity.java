package com.dup.beauty.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.dup.beauty.R;
import com.dup.beauty.app.BaseActivity;
import com.dup.beauty.model.entity.Gallery;
import com.dup.beauty.model.entity.Picture;
import com.dup.beauty.presenter.contract.IGalleryPresenter;
import com.dup.beauty.presenter.impl.GalleryPresenter;
import com.dup.beauty.ui.adapter.PicturesAdapter;
import com.dup.beauty.view.IGalleryView;
import com.jaeger.library.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * 图库界面，显示图库中图片们
 */
public class GalleryActivity extends BaseActivity implements IGalleryView, PicturesAdapter.OnItemClickListener {
    @BindView(R.id.toolbar_title)
    public TextView titleTv;
    @BindView(R.id.gallery_recyclerview)
    public RecyclerView recyclerView;

    private IGalleryPresenter mPresenter;
    private Gallery mGallery;
    private PicturesAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gallery;
    }

    @Override
    protected void bindPresenters() {
        mPresenter = new GalleryPresenter(this, this);
    }

    @Override
    protected void initView() {
        super.initView();
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));
        ButterKnife.bind(GalleryActivity.this);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
    }

    @Override
    protected void initData() {
        super.initData();
        Bundle extras = getIntent().getExtras();
        //这里获取到的gallery没有list图片数据
        mGallery = (Gallery) extras.getSerializable("GALLERY");

        if (mGallery == null) {
            finish();
            return;
        }

        //设置标题
        titleTv.setText(mGallery.getTitle());
        //获取网络数据
        mPresenter.fetchGalleryWithId(mGallery.getId());
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

    @OnCheckedChanged(R.id.toolbar_favorite)
    public void OnFavoritePress(CompoundButton compoundButton, boolean checked) {
        //TODO 收藏功能
    }

    @Override
    protected void initAction() {
        super.initAction();
    }

    @Override
    public void onItemClick(int position, Picture picture) {
        Intent intent = new Intent();
        intent.putExtra("GALLERY", mGallery);
        intent.putExtra("POSITION", position);
        intent.setClass(this, PictureActivity.class);
        startActivity(intent);
    }

    @Override
    public void onGalleryWithId(Gallery gallery, long id) {
        //这里获取到的gallery有list图片数据
        mGallery = gallery;
        //设置adapter
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mAdapter = new PicturesAdapter(this, gallery.getList(), metric.widthPixels);
        mAdapter.setItemClickListener(this);
        recyclerView.setAdapter(mAdapter);
    }
}