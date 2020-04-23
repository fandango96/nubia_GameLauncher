package cn.nubia.gamelauncher.gamecenter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import cn.nubia.gamelauncher.bean.ResponseBean;
import cn.nubia.gamelauncher.commoninterface.ConstantVariable;
import cn.nubia.gamelauncher.commoninterface.IRequestListener;
import cn.nubia.gamelauncher.util.CommonUtil;
import cn.nubia.gamelauncher.util.HTTPUtils;
import cn.nubia.gamelauncher.util.LogUtil;
import cn.nubia.gamelauncher.util.WorkThread;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import java.util.ArrayList;
import org.json.JSONObject;

public class BusinessRequestorImp {
    private Handler mHandler = new Handler() {
    };

    /* access modifiers changed from: private */
    public void runOnCalledThread(Runnable runnable) {
        WorkThread.runOnWorkThread(runnable);
    }

    public ResponseBean jSONObjectToBean(JSONObject jSONObject, int type) {
        try {
            if (jSONObject.getInt("StateCode") == 1) {
                return new ResponseBean(jSONObject, type);
            }
        } catch (Exception e) {
            LogUtil.e("BusinessRequestorImp", "jSONObjectToBean Error!! " + e.fillInStackTrace());
        }
        return null;
    }

    public void getApplicationByPackageName(Context context, String packageName, IRequestListener listener) {
        if (!ConstantVariable.HAS_PERMISSION || CommonUtil.isInternalVersion()) {
            Log.i("lsm", "has not the permission");
            if (listener != null) {
                listener.responseError("has not the net permission");
                return;
            }
            return;
        }
        Log.i("lsm", "getApplicationByPackageName connect to net##########");
        httpPost(HTTPUtils.getSoftByPackageName() + new PackageParams(packageName).getParams(), context, 100, listener);
    }

    public void getApplicationsByPackageNames(Context context, ArrayList<String> packageNames, IRequestListener listener) {
        if (!ConstantVariable.HAS_PERMISSION || CommonUtil.isInternalVersion()) {
            Log.i("lsm", "has not the permission");
            if (listener != null) {
                listener.responseError("has not the permission");
                return;
            }
            return;
        }
        Log.i("lsm", "getApplicationsByPackageNames connect to net##########");
        httpPost(HTTPUtils.getSoftListByPackageNames() + new PackageParams(packageNames).getParams(), context, 200, listener);
    }

    private void httpPost(String url, Context context, int type, IRequestListener listener) {
        final Context context2 = context;
        final String str = url;
        final int i = type;
        final IRequestListener iRequestListener = listener;
        WorkThread.runOnWorkThread(new Runnable() {
            public void run() {
                Volley.newRequestQueue(context2.getApplicationContext()).add(new JsonObjectRequest(1, str, null, new Listener<JSONObject>() {
                    public void onResponse(JSONObject jsonObject) {
                        final ResponseBean result = BusinessRequestorImp.this.jSONObjectToBean(jsonObject, i);
                        BusinessRequestorImp.this.runOnCalledThread(new Runnable() {
                            public void run() {
                                if (iRequestListener == null && result == null) {
                                    LogUtil.e("BusinessRequestorImp", "Http post error!! listener(" + iRequestListener + "):result(" + result + ")");
                                } else {
                                    iRequestListener.responseInfo(result);
                                }
                            }
                        });
                    }
                }, new ErrorListener() {
                    public void onErrorResponse(VolleyError volleyError) {
                        final String errorMsg = volleyError.toString();
                        BusinessRequestorImp.this.runOnCalledThread(new Runnable() {
                            public void run() {
                                if (iRequestListener != null) {
                                    iRequestListener.responseError(errorMsg);
                                    LogUtil.e("BusinessRequestorImp", "Http post error!! ErrorListener(" + errorMsg + ")");
                                }
                            }
                        });
                    }
                }));
            }
        });
    }
}
