package cn.nubia.gamelauncherx.gamelist;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class GameItemDecoration extends RecyclerView.ItemDecoration
{
    private int leftRight;
    private int topBottom;

    public GameItemDecoration(int leftRight2, int topBottom2) {
        this.leftRight = leftRight2;
        this.topBottom = topBottom2;
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left = this.leftRight;
        outRect.right = this.leftRight;
        outRect.bottom = this.topBottom;
        outRect.top = this.topBottom;
    }
}
