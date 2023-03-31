package com.jandy.jwidget.mpv.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

public abstract class MPVView implements IViewDelegate, LifecycleObserver {

    protected final SparseArray<View> mViews = new SparseArray<View>();

    protected View mRootView;
    protected Context mContext;

    @Override
    public void createView(Context context, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = context;
        int resId = getLayoutResourceId();
        if (resId != 0) {
            mRootView = inflater.inflate(resId, container, false);
        } else {
            mRootView = initContentView();
        }
    }


    protected abstract int getLayoutResourceId();

    protected View initContentView() {
        return null;
    }

    @Override
    public View getRootView() {
        return mRootView;
    }

    public <T extends View> T bindView(int id) {
        T view = (T) mViews.get(id);
        if (view == null) {
            view = (T) mRootView.findViewById(id);
            mViews.put(id, view);
        }
        return view;
    }

    public <T extends View> T findById(int id) {
        return (T) bindView(id);
    }

    @Override
    public void clear() {
        Log.d("jandy","MPVView clear 清除----------");
        if (mRootView != null) {

        }
        if (mViews != null)
            mViews.clear();
    }

    /**
     * 点击监听
     *
     * @param listener
     * @param ids
     */
    public void setOnClickListener(View.OnClickListener listener, int... ids) {
        if (ids == null) {
            return;
        }
        for (int id : ids) {
            findById(id).setOnClickListener(listener);
        }
    }

    /**
     * @param listener
     * @param ids
     */
    public void setOnLongClickListener(View.OnLongClickListener listener, int... ids) {
        if (ids == null) {
            return;
        }
        for (int id : ids) {
            findById(id).setOnLongClickListener(listener);
        }
    }


    protected Activity getActivity() {
        return (Activity) mContext;
    }

    protected Context getContext() {
        return mContext;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate(LifecycleOwner owner) {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy(LifecycleOwner owner) {
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart(LifecycleOwner owner) {
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop(LifecycleOwner owner) {
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume(LifecycleOwner owner) {
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause(LifecycleOwner owner) {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    void onAny(LifecycleOwner owner) {
    }
}
