package com.jandy.jwidget.mpv.presenter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleObserver;

import com.jandy.jwidget.mpv.model.MPVModel;
import com.jandy.jwidget.mpv.view.IViewDelegate;


public abstract class MPVFragmentPresenter<M extends MPVModel, T extends IViewDelegate> extends Fragment {
    protected T mView;
    protected M mModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mView = getViewClass().newInstance();
            mModel = getModelImpl();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView.createView(getActivity(), inflater, container, savedInstanceState);
        return mView.getRootView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView.initWidget();
        bindViews();
        getLifecycle().addObserver((LifecycleObserver) mView);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (mView == null) {
            try {
                mView = getViewClass().newInstance();
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mView != null) {
            mView.clear();
            getLifecycle().removeObserver((LifecycleObserver) mView);
        }
    }


    protected void bindViews() {
    }

    protected abstract M getModelImpl();

    protected abstract Class<T> getViewClass();
}
