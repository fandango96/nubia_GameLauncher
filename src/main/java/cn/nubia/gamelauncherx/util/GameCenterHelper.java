package cn.nubia.gamelauncherx.util;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import cn.nubia.gamelauncherx.R;

public class GameCenterHelper {
    public static void startUsersGameSetttings(Context context, boolean isWeekly) {
        try {
            ReflectUtilities.requestCPUBoost();
            Intent intent = new Intent();
            intent.setAction("cn.nubia.settings.action.GAME_CENTER");
            if (isWeekly) {
                intent.putExtra("gcs_start_type", "summary_keyword_week");
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startGameCenterSearchPage(Context context) {
        try {
            startGameCenterActivity(context, "gameplace://search");
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            showGameCenterNotFoundDialog(context);
        }
    }

    public static void startGameCenterPlaza(Context context) {
        try {
            startGameCenterActivity(context, "gameplace://gamesquare");
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            showGameCenterNotFoundDialog(context);
        }
    }

    public static void startGameCenterHome(Context context) {
        try {
            startGameCenterActivity(context, "gameplace://home");
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            showGameCenterNotFoundDialog(context);
        }
    }

    public static void startGameCenterActivity(Context context, String data) {
        ReflectUtilities.requestCPUBoost();
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setAction("android.intent.action.VIEW");
        intent.setData(Uri.parse(data));
        if (!CommonUtil.isNX627J_Project() || !clickGameCenter(context, intent)) {
            context.startActivity(intent);
        }
    }

    private static boolean clickGameCenter(Context context, Intent intent) {
        if (intent.resolveActivityInfo(context.getPackageManager(), 0) != null) {
            return false;
        }
        showGameCenterNotFoundDialog(context);
        return true;
    }

    private static void showGameCenterNotFoundDialog(Context context) {
        new AlertDialog.Builder(context, 2131624188).setMessage((CharSequence) context.getString(R.string.gamecenter_not_fonund_dialog)).setPositiveButton(R.string.exit_ok, (OnClickListener) new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        }).create().show();
    }
}
