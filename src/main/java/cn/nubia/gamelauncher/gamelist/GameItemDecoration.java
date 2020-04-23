package cn.nubia.gamelauncher.gamelist;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.State;
import android.view.View;

public class GameItemDecoration extends ItemDecoration {
    private int leftRight;
    private int topBottom;

    public GameItemDecoration(int leftRight2, int topBottom2) {
        this.leftRight = leftRight2;
        this.topBottom = topBottom2;
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left = this.leftRight;
        outRect.right = this.leftRight;
        outRect.bottom = this.topBottom;
        outRect.top = this.topBottom;
    }
}
