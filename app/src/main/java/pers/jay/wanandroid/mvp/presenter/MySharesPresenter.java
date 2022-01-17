package pers.jay.wanandroid.mvp.presenter;

import android.app.Application;

import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import pers.jay.wanandroid.base.BaseWanObserver;
import pers.jay.wanandroid.common.CollectHelper;
import pers.jay.wanandroid.model.Article;
import pers.jay.wanandroid.model.ArticleInfo;
import pers.jay.wanandroid.model.ShareUserArticles;
import pers.jay.wanandroid.mvp.contract.MySharesContract;
import pers.jay.wanandroid.result.WanAndroidResponse;
import pers.jay.wanandroid.utils.rx.RxScheduler;

@FragmentScope
public class MySharesPresenter
        extends BasePresenter<MySharesContract.Model, MySharesContract.View> {

    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public MySharesPresenter(MySharesContract.Model model, MySharesContract.View rootView) {
        super(model, rootView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }

    public void loadData(int page) {

        mModel.getMyShares(page)
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .compose(RxScheduler.Obs_io_main())
              .subscribe(new BaseWanObserver<WanAndroidResponse<ShareUserArticles>>(mRootView) {

                  @Override
                  protected void onStart() {
                      super.onStart();
                      mRootView.showLoading();
                  }

                  @Override
                  public void onSuccess(WanAndroidResponse<ShareUserArticles> response) {
                      ShareUserArticles shareUserArticles = response.getData();
                      ArticleInfo articleInfo = shareUserArticles.getShareArticles();
                      mRootView.showData(articleInfo);
                  }
              });
    }

    public void collectArticle(Article item, int position) {
        CollectHelper.with(mRootView).target(item).position(position).collect();
    }

    public void delete(int id, int position) {
        mModel.deleteShare(id)
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .compose(RxScheduler.Obs_io_main())
        .subscribe(new BaseWanObserver<WanAndroidResponse>(mRootView) {
            @Override
            public void onSuccess(WanAndroidResponse wanAndroidResponse) {
                mRootView.deleteSuccess(position);
            }
        });
    }
}
