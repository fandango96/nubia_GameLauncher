package cn.nubia.gamelauncherx.gamehandle;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.SystemProperties;
import android.provider.Settings.Global;
import android.util.Log;
import cn.nubia.commonui.app.NubiaCenterAlertDialog;
import cn.nubia.commonui.app.NubiaCenterAlertDialog.Builder;
import cn.nubia.gamelauncherx.R;
import cn.nubia.gamelauncherx.commoninterface.ConstantVariable;

public class NubiaCTAPermissionUtils {
    private static final int CTA_OPEN = 0;
    private static final String CTA_PERMISSION = "cta_permission";
    private static final String CTA_PERSIST = "persist.sys.cta.disable";
    public static final String HAS_PERMISSION = "has_permission";
    public static final String SHARED_PREFERENCES_NAME = "data";
    private static final String VIRTUAL_GAME_KEY = "virtual_game_key";

    public static boolean isCTAOK(Context context) {
        if (SystemProperties.getInt(CTA_PERSIST, 0) != 0) {
            return true;
        }
        SharedPreferences CTASetting = context.getSharedPreferences("data", 0);
        Log.d("KongYuan3", " CTASetting 1 = " + CTASetting.getBoolean(CTA_PERMISSION, false));
        return CTASetting.getBoolean(CTA_PERMISSION, false);
    }

    public static void showPermissionDialog(final Context context, final Intent intent) {
        if (context != null) {
            final SharedPreferences CTASetting = context.getSharedPreferences("data", 0);
            Builder builder = new Builder(context);
            builder.setCancelable(false);
            builder.setView((int) R.layout.redmagic_cta_permission);
            builder.setNegativeButton((int) R.string.cta_deny, (OnClickListener) new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ((Activity) context).finish();
                }
            });
            builder.setPositiveButton((int) R.string.cta_confirm, (OnClickListener) new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    CTASetting.edit().putBoolean(NubiaCTAPermissionUtils.CTA_PERMISSION, true).apply();
                    NubiaCTAPermissionUtils.startActivity(context, intent);
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }

    public static void showPermissionDialogHome(final Context context) {
        if (context != null) {
            final Editor CTASetting = context.getSharedPreferences("data", 0).edit();
            Builder builder = new Builder(context);
            builder.setCancelable(false);
            builder.setView((int) R.layout.gamelauncher_cta_permission);
            builder.setNegativeButton((int) R.string.cta_deny, (OnClickListener) new OnClickListener() {
                @TargetApi(11)
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    new AsyncTask<Void, Void, Void>() {
                        /* access modifiers changed from: protected */
                        public Void doInBackground(Void... params) {
                            Global.putInt(context.getContentResolver(), NubiaCTAPermissionUtils.VIRTUAL_GAME_KEY, 0);
                            return null;
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null});
                }
            });
            builder.setPositiveButton((int) R.string.cta_confirm, (OnClickListener) new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    CTASetting.putBoolean(NubiaCTAPermissionUtils.CTA_PERMISSION, true).apply();
                    ConstantVariable.HAS_PERMISSION = true;
                    CTASetting.putBoolean("has_permission", true).apply();
                    dialog.dismiss();
                }
            });
            NubiaCenterAlertDialog dialog = builder.create();
            dialog.show();
            dialog.getWindow().setWindowAnimations(2131624188);
        }
    }

    public static void startActivity(Context context, Intent intent) {
        if (intent != null && context != null) {
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ((Activity) context).finish();
        }
    }
}
