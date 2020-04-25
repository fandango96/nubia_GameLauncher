package cn.nubia.gamelauncherx.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Process;
import android.os.UserHandle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cn.nubia.gamelauncherx.R;
import cn.nubia.gamelauncherx.adapter.HasAddAdapter;
import cn.nubia.gamelauncherx.bean.AppListItemBean;
import cn.nubia.gamelauncherx.commoninterface.IGetAppStatusDataCallBack;
import cn.nubia.gamelauncherx.commoninterface.OnSelectedCountChangeListener;
import cn.nubia.gamelauncherx.model.AppAddModel;
import cn.nubia.gamelauncherx.util.CommonUtil;
import java.util.ArrayList;
import java.util.Iterator;

public class UninstallController implements OnSelectedCountChangeListener {
    private static final int DELETE_ALL_USERS = 2;
    /* access modifiers changed from: private */
    public HasAddAdapter mAdapter = null;
    private AppAddModel mAppAddModel = null;
    private IGetAppStatusDataCallBack mCallBack = null;
    private TextView mCancelText = null;
    /* access modifiers changed from: private */
    public Context mContext = null;
    /* access modifiers changed from: private */
    public ArrayList<AppListItemBean> mList = new ArrayList<>();
    private TextView mMiddleTitle;
    /* access modifiers changed from: private */
    public String mSelectAllStr = null;
    /* access modifiers changed from: private */
    public TextView mSelectAllText = null;
    private String mSelectNoneStr = null;
    private TextView mUninstallBtn = null;
    private RecyclerView mUninstallList = null;

    public void init(final Activity activity) {
        this.mContext = activity.getApplicationContext();
        this.mContext = activity.getApplicationContext();
        this.mCancelText = (TextView) activity.findViewById(R.id.cancel);
        this.mCancelText.setClickable(true);
        this.mCancelText.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                activity.finish();
            }
        });
        this.mSelectAllText = (TextView) activity.findViewById(R.id.all_select);
        this.mSelectAllStr = this.mContext.getString(R.string.all_select);
        this.mSelectNoneStr = this.mContext.getString(R.string.select_none);
        this.mSelectAllText.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (UninstallController.this.mList != null && UninstallController.this.mList.size() > 0) {
                    Iterator it = UninstallController.this.mList.iterator();
                    while (it.hasNext()) {
                        ((AppListItemBean) it.next()).setSelect(UninstallController.this.mSelectAllText.getText().equals(UninstallController.this.mSelectAllStr));
                    }
                    UninstallController.this.mAdapter.notifyDataSetChanged();
                }
                if (UninstallController.this.mSelectAllText.getText().equals(UninstallController.this.mSelectAllStr)) {
                    UninstallController.this.onSelectedCountChangeListener(UninstallController.this.mList.size());
                } else {
                    UninstallController.this.onSelectedCountChangeListener(0);
                }
            }
        });
        this.mMiddleTitle = (TextView) activity.findViewById(R.id.middle_name);
        this.mUninstallList = (RecyclerView) activity.findViewById(R.id.unintsall_list);
        this.mUninstallList.setLayoutManager(new LinearLayoutManager(this.mContext, RecyclerView.VERTICAL, false));
        this.mAppAddModel = AppAddModel.getInstance();
        if (this.mAppAddModel.getAppAddedList() != null) {
            this.mList.clear();
            this.mList.addAll(this.mAppAddModel.getAppAddedList());
            initAdapter(this.mAppAddModel.getAppAddedList().size());
        }
        if (this.mList == null || this.mList.size() <= 0) {
            this.mUninstallBtn = (TextView) activity.findViewById(R.id.uninstall_btn);
        } else {
            this.mUninstallBtn = (TextView) activity.findViewById(R.id.uninstall_btn);
            this.mUninstallBtn.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    ArrayList<AppListItemBean> list = UninstallController.this.getUninstallList();
                    if (list != null && list.size() > 0) {
                        UninstallController.this.showConfirmDialog(activity);
                    }
                }
            });
        }
        updateButtonView(false);
        AppAddModel appAddModel = this.mAppAddModel;
        IGetAppStatusDataCallBack r1 = new IGetAppStatusDataCallBack() {
            public void onLoadAddAppListDone(ArrayList<AppListItemBean> list, int hasAddCount) {
                UninstallController.this.mList.clear();
                if (list != null && list.size() > 0) {
                    for (int i = 0; i < hasAddCount; i++) {
                        UninstallController.this.mList.add(list.get(i));
                    }
                }
                if (UninstallController.this.mAdapter == null) {
                    UninstallController.this.initAdapter(hasAddCount);
                    return;
                }
                if (UninstallController.this.mList.isEmpty()) {
                    UninstallController.this.updateButtonView(false);
                } else {
                    UninstallController.this.updateButtonView(true);
                }
                UninstallController.this.onSelectedCountChangeListener(0);
                UninstallController.this.mAdapter.notifyDataSetChanged();
                UninstallController.this.mAdapter.setHasAddCount(hasAddCount);
            }
        };
        this.mCallBack = r1;
        appAddModel.resisterGetAppStatusDataCallBack(r1);
    }

    /* access modifiers changed from: 0000 */
    public void updateButtonView(boolean isEnable) {
        this.mUninstallBtn.setClickable(isEnable);
        this.mUninstallBtn.setAlpha(isEnable ? 1.0f : 0.36f);
        this.mUninstallBtn.setBackgroundResource(isEnable ? R.drawable.delete_btn : R.mipmap.delete_unpress);
        if (this.mList == null || this.mList.size() <= 0) {
            this.mSelectAllText.setClickable(false);
            this.mSelectAllText.setAlpha(0.36f);
            return;
        }
        this.mSelectAllText.setClickable(true);
        this.mSelectAllText.setAlpha(1.0f);
    }

    /* access modifiers changed from: private */
    public void initAdapter(int hasAddCount) {
        this.mAdapter = new HasAddAdapter();
        this.mAdapter.setOnAppAddedListener(this.mAppAddModel);
        this.mAdapter.setType(1);
        this.mAdapter.setDataList(this.mList);
        this.mAdapter.setOnSelectCountChangeListener(this);
        this.mUninstallList.setAdapter(this.mAdapter);
        this.mAdapter.setHasAddCount(hasAddCount);
    }

    /* access modifiers changed from: private */
    public void showConfirmDialog(Context context) {
        new AlertDialog.Builder(context, 2131624188).setMessage((CharSequence) this.mContext.getString(R.string.sure_uninstall)).setPositiveButton((CharSequence) this.mContext.getString(R.string.confirm), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                UninstallController.this.uninstallApps(UninstallController.this.getUninstallList());
            }
        }).setNegativeButton((CharSequence) this.mContext.getString(R.string.cancel), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        }).create().show();
    }

    public void showConfirmDialog(final Context context, final String pkgName) {
        new AlertDialog.Builder(context, 2131624188).setMessage((CharSequence) context.getString(R.string.sure_uninstall)).setPositiveButton((CharSequence) context.getString(R.string.confirm), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                UninstallController.this.doUninstallApp(context, pkgName);
            }
        }).setNegativeButton((CharSequence) context.getString(R.string.cancel), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        }).create().show();
    }

    /* access modifiers changed from: private */
    public ArrayList<AppListItemBean> getUninstallList() {
        if (this.mList == null || this.mList.size() <= 0) {
            return null;
        }
        ArrayList<AppListItemBean> list = new ArrayList<>();
        Iterator it = this.mList.iterator();
        while (it.hasNext()) {
            AppListItemBean bean = (AppListItemBean) it.next();
            if (bean.isSelect()) {
                list.add(bean);
            }
        }
        return list;
    }

    public void onResume() {
    }

    public void onPasue() {
    }

    public void onDestory() {
        AppAddModel.getInstance().unResisterGetAppStatusDataCallBack(this.mCallBack);
        this.mCallBack = null;
    }

    /* access modifiers changed from: private */
    public void uninstallApps(final ArrayList<AppListItemBean> list) {
        new AsyncTask<Void, Void, Void>() {
            /* access modifiers changed from: protected */
            public Void doInBackground(Void... voids) {
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    AppListItemBean bean = (AppListItemBean) it.next();
                    if (bean.getComponetName() != null) {
                        Log.i("lsm", "uninstall package name = " + bean.getComponetName() + "  sucess == " + UninstallController.this.doUninstallApp(UninstallController.this.mContext, CommonUtil.createComponentName(bean.getComponetName()).getPackageName()));
                    } else {
                        Log.i("lsm", "uninstall package name = null");
                    }
                }
                return null;
            }
        }.execute(new Void[0]);
    }

    public boolean doUninstallApp(Context context, String pkgName) {
        try {
            UserHandle user = Process.myUserHandle();
            IBinder iBinder = (IBinder) Class.forName("android.os.ServiceManager").getMethod("getService", new Class[]{String.class}).invoke(null, new Object[]{"package"});
            Class<?> stub = Class.forName("android.content.pm.IPackageManager$Stub");
            Object iPackageManager = stub.getDeclaredMethod("asInterface", new Class[]{IBinder.class}).invoke(stub, new Object[]{iBinder});
            iPackageManager.getClass().getDeclaredMethod("deletePackageAsUser", new Class[]{String.class, Integer.TYPE, Class.forName("android.content.pm.IPackageDeleteObserver"), Integer.TYPE, Integer.TYPE}).invoke(iPackageManager, new Object[]{pkgName, Integer.valueOf(-1), null, Integer.valueOf(((Integer) UserHandle.class.getMethod("getIdentifier", new Class[0]).invoke(user, new Object[0])).intValue()), Integer.valueOf(2)});
            return true;
        } catch (Exception ee) {
            ee.printStackTrace();
            return false;
        }
    }

    public void onSelectedCountChangeListener(int newCount) {
        if (newCount < this.mList.size() || this.mList.isEmpty()) {
            this.mSelectAllText.setText(this.mSelectAllStr);
        } else if (newCount == this.mList.size()) {
            this.mSelectAllText.setText(this.mSelectNoneStr);
        }
        this.mMiddleTitle.setText(this.mContext.getString(R.string.has_slect_num) + "（" + newCount + "）");
        if (getUninstallList() == null || (getUninstallList() != null && getUninstallList().size() == 0)) {
            updateButtonView(false);
        } else {
            updateButtonView(true);
        }
    }
}
