package cn.nubia.gamelauncher.gamelist;

import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public class GameListPagerAdapter extends PagerAdapter {
    private List<RecyclerView> mViewList;

    public GameListPagerAdapter(List<RecyclerView> mViewList2) {
        this.mViewList = mViewList2;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) this.mViewList.get(position));
    }

    public Object instantiateItem(ViewGroup container, int position) {
        container.addView((View) this.mViewList.get(position));
        return this.mViewList.get(position);
    }

    public int getCount() {
        if (this.mViewList == null) {
            return 0;
        }
        return this.mViewList.size();
    }

    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
