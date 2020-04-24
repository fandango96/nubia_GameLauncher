package cn.nubia.gamelauncherx.controller;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import cn.nubia.gamelauncherx.R;
import cn.nubia.gamelauncherx.adapter.HasAddAdapter;
import cn.nubia.gamelauncherx.bean.AppListItemBean;
import cn.nubia.gamelauncherx.commoninterface.IGetAppStatusDataCallBack;
import cn.nubia.gamelauncherx.model.AppAddModel;
import java.util.ArrayList;
import java.util.Iterator;

public class AppListController {
    /* access modifiers changed from: private */
    public HasAddAdapter mAdapter;
    private RecyclerView mAppAddList;
    private AppAddModel mAppAddModel;
    private IGetAppStatusDataCallBack mCallBack;
    private Context mContext;
    private Handler mHandler;
    /* access modifiers changed from: private */
    public ArrayList<AppListItemBean> mList;

    public AppListController() {
        this.mContext = null;
        this.mAppAddModel = null;
        this.mAppAddList = null;
        this.mAdapter = null;
        this.mList = new ArrayList<>();
        this.mHandler = null;
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    public void init(Activity activity) {
        Log.i("lsm", "AppListController init");
        this.mContext = activity.getApplicationContext();
        this.mAppAddList = (RecyclerView) activity.findViewById(R.id.app_add_list);
        this.mAppAddList.setLayoutManager(new LinearLayoutManager(this.mContext, 1, false));
        this.mAppAddModel = AppAddModel.getInstance();
        if (this.mAppAddModel.getAppAddedList() == null || this.mAppAddModel.getAppNotAddList() == null) {
            Log.i("lsm", "AppListController init wait callback");
        } else {
            this.mList.clear();
            this.mList.addAll(this.mAppAddModel.getAppAddedList());
            this.mList.addAll(this.mAppAddModel.getAppNotAddList());
            initAdapter(this.mAppAddModel.getAppAddedList().size());
        }
        AppAddModel appAddModel = this.mAppAddModel;
        AnonymousClass1 r1 = new IGetAppStatusDataCallBack() {
            public void onLoadAddAppListDone(ArrayList<AppListItemBean> list, int hasAddCount) {
                AppListController.this.mList.clear();
                AppListController.this.mList.addAll(list);
                if (AppListController.this.mAdapter == null) {
                    AppListController.this.initAdapter(hasAddCount);
                    return;
                }
                AppListController.this.mAdapter.setHasAddCount(hasAddCount);
                AppListController.this.mAdapter.notifyDataSetChanged();
            }
        };
        this.mCallBack = r1;
        appAddModel.resisterGetAppStatusDataCallBack(r1);
    }

    /* access modifiers changed from: private */
    public void initAdapter(int hasAddCount) {
        this.mAdapter = new HasAddAdapter();
        this.mAdapter.setOnAppAddedListener(this.mAppAddModel);
        this.mAdapter.setType(0);
        this.mAdapter.setDataList(this.mList);
        this.mAppAddList.setAdapter(this.mAdapter);
        this.mAdapter.setHasAddCount(hasAddCount);
    }

    /* access modifiers changed from: 0000 */
    public void resetSelectStatue() {
        this.mList.clear();
        ArrayList<AppListItemBean> appAddList = this.mAppAddModel.getAppAddedList();
        if (appAddList != null && appAddList.size() > 0) {
            Iterator it = appAddList.iterator();
            while (it.hasNext()) {
                ((AppListItemBean) it.next()).setSelect(true);
            }
            this.mList.addAll(appAddList);
        }
        ArrayList<AppListItemBean> appNotAddList = this.mAppAddModel.getAppNotAddList();
        if (appNotAddList != null && appNotAddList.size() > 0) {
            Iterator it2 = appNotAddList.iterator();
            while (it2.hasNext()) {
                ((AppListItemBean) it2.next()).setSelect(false);
            }
            this.mList.addAll(appNotAddList);
        }
        if (this.mAdapter != null) {
            Log.i("lsm", "AppListController resetSelectStatue mList = " + this.mList + "   size == " + this.mList.size());
            if (appAddList != null) {
                this.mAdapter.setHasAddCount(appAddList.size());
            }
            this.mAdapter.notifyDataSetChanged();
        }
    }

    public void onResume() {
        Log.i("lsm", "AppListController onResume");
        resetSelectStatue();
    }

    public void onPasue() {
    }

    public void onDestory() {
        AppAddModel.getInstance().unResisterGetAppStatusDataCallBack(this.mCallBack);
        this.mCallBack = null;
    }
}
