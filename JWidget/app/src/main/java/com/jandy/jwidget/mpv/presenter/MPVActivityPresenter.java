package com.jandy.jwidget.mpv.presenter;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleObserver;


import com.jandy.jwidget.mpv.model.MPVModel;
import com.jandy.jwidget.mpv.view.IViewDelegate;

import java.util.ArrayList;
import java.util.List;

public abstract class MPVActivityPresenter<M extends MPVModel,T extends IViewDelegate> extends AppCompatActivity {

    protected T mView;
    protected M mModel;

    public MPVActivityPresenter(){
        try {
            mView = getViewClass().newInstance();
            mModel = getModelImpl();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mView != null){
            mView.createView(this,getLayoutInflater(),null,savedInstanceState);
            setContentView(mView.getRootView());
            mView.initWidget();
            bindViews();
            getLifecycle().addObserver((LifecycleObserver) mView);
            initData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mView != null){
            getLifecycle().removeObserver((LifecycleObserver) mView);
        }
    }

    /**
     * 获取Intent中的字符串
     * @param key
     * @return
     */
    protected String getIntentExtraString(String key){
        return getIntent() != null ? getIntent().getStringExtra(key) : "";
    }

    /**
     *
     * @param key
     * @return
     */
    protected int getIntentExtraInt(String key){
        return getIntent() != null ? getIntent().getIntExtra(key,0) : 0;
    }

    /**
     *
     * @param key
     * @return
     */
    protected List<String> getIntentExtraStringList(String key){
        return getIntent() != null ? getIntent().getStringArrayListExtra(key) : new ArrayList<>();
    }

    protected String getBundleWithString(String key){
        Bundle bundle = getIntent() != null ? getIntent().getBundleExtra("params") : null;
        if(bundle==null)
            return "";
        String value=bundle.getString(key);
        return value != null ?  value: "";
    }

    protected int getBundleWithInt(String key){
        Bundle bundle = getIntent() != null ? getIntent().getBundleExtra("params") : null;
        return bundle != null ? bundle.getInt(key,0) : 0;
    }

    protected void bindViews(){}

    protected void initData() {
    }

    protected abstract M getModelImpl();

    protected abstract Class<T> getViewClass();
}
