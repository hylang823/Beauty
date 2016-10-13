package com.dup.beauty.presenter.impl;

import android.app.Activity;

import com.dup.beauty.R;
import com.dup.beauty.app.Constant;
import com.dup.beauty.model.api.ApiClient;
import com.dup.beauty.model.entity.Galleries;
import com.dup.beauty.presenter.contract.ICategoryPresenter;
import com.dup.beauty.util.DialogUtil;
import com.dup.beauty.util.L;
import com.dup.beauty.util.StringUtil;
import com.dup.beauty.view.ICategoryView;
import com.sdsmdg.tastytoast.TastyToast;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DP on 2016/9/18.
 */
public class CategoryPresenter implements ICategoryPresenter {
    private Activity mActivity;
    private ICategoryView mCategoryView;

    //记录该加载第几页了
    private int pageNum = 2;
    //记录分类id
    private long id = 0;

    public CategoryPresenter(Activity activity, ICategoryView categoryView) {
        this.mActivity = activity;
        this.mCategoryView = categoryView;
    }

    @Override
    public void fetchGalleriesWithId(final long id) {
        ApiClient.getApiService(mActivity).getGalleries(1, Constant.PAGE_COUNT, id)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Observer<Galleries>() {
                    @Override
                    public void onCompleted() {
                        pageNum++;
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.e("从网络 获取该分类 图库数据失败." + e.getMessage());
                        TastyToast.makeText(mActivity.getApplicationContext(),
                                StringUtil.getStrRes(mActivity.getApplicationContext(), R.string.loadmore_error)
                                , TastyToast.LENGTH_SHORT, TastyToast.WARNING);
                    }

                    @Override
                    public void onNext(Galleries galleries) {
                        mCategoryView.onGalleriesWithId(galleries.getGalleries(),pageNum,id);
                    }
                });
    }

    @Override
    public void fetchMoreGalleriesWithId() {
        DialogUtil.getInstance().showDialog(mActivity);
        ApiClient.getApiService(mActivity).getGalleries(pageNum, Constant.PAGE_COUNT, 0).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Observer<Galleries>() {
                    @Override
                    public void onCompleted() {
                        pageNum++;
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.e("从网络 获取该分类 更多图库数据失败." + e.getMessage());
                        TastyToast.makeText(mActivity.getApplicationContext(),
                                StringUtil.getStrRes(mActivity.getApplicationContext(), R.string.loadmore_error)
                                , TastyToast.LENGTH_SHORT, TastyToast.WARNING);
                    }

                    @Override
                    public void onNext(Galleries galleries) {
                        mCategoryView.onMoreGalleriesWithId(galleries.getGalleries(),pageNum);
                        DialogUtil.getInstance().dismissDialog();
                    }
                });
    }

}