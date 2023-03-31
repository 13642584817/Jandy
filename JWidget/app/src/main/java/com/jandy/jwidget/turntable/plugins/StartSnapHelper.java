package com.jandy.jwidget.turntable.plugins;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 */
public class StartSnapHelper extends LinearSnapHelper {

    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager,
                                              @NonNull View targetView) {
        int[] out = new int[2];
        out[0] = 0;
        out[1] = ((TurnableLayoutManager) layoutManager).getSnapHeight();
        return out;
    }

    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        TurnableLayoutManager custLayoutManager = (TurnableLayoutManager) layoutManager;
        return custLayoutManager.findSnapView();
    }
}