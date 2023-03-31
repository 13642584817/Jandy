package com.jandy.jwidget.mpv.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface IViewDelegate {
    void createView(Context context, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    View getRootView();

    void initWidget();

    void clear();
}
