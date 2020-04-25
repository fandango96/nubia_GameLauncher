package cn.nubia.gamelauncherx.test;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.nubia.gamelauncherx.GameLauncherApplication;

public class CopyTestDataUtil {
    private String mDbExportRootPath = null;

    /* JADX WARNING: Removed duplicated region for block: B:25:0x00c7 A[SYNTHETIC, Splitter:B:25:0x00c7] */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00cc A[Catch:{ Exception -> 0x00eb }] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00f3 A[SYNTHETIC, Splitter:B:43:0x00f3] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00f8 A[Catch:{ Exception -> 0x00fc }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void exportAppDbFile() {
        Time r10 = new Time();
        r10.setToNow();
        StringBuilder r13 = new StringBuilder();
        Context r14 = GameLauncherApplication.CONTEXT;
        String r15 = "gameLauncher_file";
        r13.append(r14.getExternalFilesDir(r15));
        r13.append(File.separator);
        r13.append("gamelauncher");
        String r9 = r13.toString();
        File r8 = new File(r9);
        if (!r8.exists()) {
            r8.mkdir();
        }
        r13 = new StringBuilder();
        r13.append(r9);
        r13.append(File.separator);
        r13.append(r10.hour);
        r13.append("_");
        r13.append(r10.minute);
        r13.append("_");
        r13.append(r10.second);
        String r12 = r13.toString();
        File r11 = new File(r12);
        if (!r11.exists()) {
            r11.mkdir();
        }
        r13 = new StringBuilder();
        r13.append(r12);
        r13.append(File.separator);
        r13.append("appadd.db");
        String r3 = r13.toString();
        File r2 = new File(r3);
        if (!r2.exists()) {
            try
            {
                r2.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                Log.i("lsm", "TestDataReceiver exportAppDbFile finished");
                return;
            }
        }
        FileInputStream r5;
        try
        {
            r5 = new FileInputStream(GameLauncherApplication.CONTEXT.getDatabasePath("appadd.db"));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            Log.i("lsm", "TestDataReceiver exportAppDbFile finished");
            return;
        }
        FileOutputStream r7;
        try
        {
            r7 = new FileOutputStream(r2);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            try
            {
                r5.close();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
            Log.i("lsm", "TestDataReceiver exportAppDbFile finished");
            return;
        }
        byte[] r0 = new byte[1024];
        int ret;
        do
        {
            try
            {
                ret = r5.read(r0);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                break;
            }
            if (ret <= 0)
            {
                break;
            }
            try
            {
                r7.write(r0);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                break;
            }
        } while (true);
        try
        {
            r5.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        try
        {
            r7.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        Log.i("lsm", "TestDataReceiver exportAppDbFile finished");
        /*
            r16 = this;
            android.text.format.Time r10 = new android.text.format.Time
            r10.<init>()
            r10.setToNow()
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            android.content.Context r14 = cn.nubia.gamelauncher.GameLauncherApplication.CONTEXT
            java.lang.String r15 = "gameLauncher_file"
            java.io.File r14 = r14.getExternalFilesDir(r15)
            java.lang.StringBuilder r13 = r13.append(r14)
            java.lang.String r14 = java.io.File.separator
            java.lang.StringBuilder r13 = r13.append(r14)
            java.lang.String r14 = "gamelauncher"
            java.lang.StringBuilder r13 = r13.append(r14)
            java.lang.String r9 = r13.toString()
            java.io.File r8 = new java.io.File
            r8.<init>(r9)
            boolean r13 = r8.exists()
            if (r13 != 0) goto L_0x0037
            r8.mkdir()
        L_0x0037:
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.StringBuilder r13 = r13.append(r9)
            java.lang.String r14 = java.io.File.separator
            java.lang.StringBuilder r13 = r13.append(r14)
            int r14 = r10.hour
            java.lang.StringBuilder r13 = r13.append(r14)
            java.lang.String r14 = "_"
            java.lang.StringBuilder r13 = r13.append(r14)
            int r14 = r10.minute
            java.lang.StringBuilder r13 = r13.append(r14)
            java.lang.String r14 = "_"
            java.lang.StringBuilder r13 = r13.append(r14)
            int r14 = r10.second
            java.lang.StringBuilder r13 = r13.append(r14)
            java.lang.String r12 = r13.toString()
            java.io.File r11 = new java.io.File
            r11.<init>(r12)
            boolean r13 = r11.exists()
            if (r13 != 0) goto L_0x0076
            r11.mkdir()
        L_0x0076:
            r4 = 0
            r6 = 0
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.StringBuilder r13 = r13.append(r12)
            java.lang.String r14 = java.io.File.separator
            java.lang.StringBuilder r13 = r13.append(r14)
            java.lang.String r14 = "appadd.db"
            java.lang.StringBuilder r13 = r13.append(r14)
            java.lang.String r3 = r13.toString()
            java.io.File r2 = new java.io.File
            r2.<init>(r3)
            boolean r13 = r2.exists()     // Catch:{ Exception -> 0x0108 }
            if (r13 != 0) goto L_0x009f
            r2.createNewFile()     // Catch:{ Exception -> 0x0108 }
        L_0x009f:
            java.io.FileInputStream r5 = new java.io.FileInputStream     // Catch:{ Exception -> 0x0108 }
            android.content.Context r13 = cn.nubia.gamelauncher.GameLauncherApplication.CONTEXT     // Catch:{ Exception -> 0x0108 }
            java.lang.String r14 = "appadd.db"
            java.io.File r13 = r13.getDatabasePath(r14)     // Catch:{ Exception -> 0x0108 }
            r5.<init>(r13)     // Catch:{ Exception -> 0x0108 }
            java.io.FileOutputStream r7 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x010a, all -> 0x0101 }
            r7.<init>(r2)     // Catch:{ Exception -> 0x010a, all -> 0x0101 }
            r13 = 1024(0x400, float:1.435E-42)
            byte[] r0 = new byte[r13]     // Catch:{ Exception -> 0x00bf, all -> 0x0104 }
        L_0x00b5:
            int r13 = r5.read(r0)     // Catch:{ Exception -> 0x00bf, all -> 0x0104 }
            if (r13 <= 0) goto L_0x00d7
            r7.write(r0)     // Catch:{ Exception -> 0x00bf, all -> 0x0104 }
            goto L_0x00b5
        L_0x00bf:
            r1 = move-exception
            r6 = r7
            r4 = r5
        L_0x00c2:
            r1.printStackTrace()     // Catch:{ all -> 0x00f0 }
            if (r4 == 0) goto L_0x00ca
            r4.close()     // Catch:{ Exception -> 0x00eb }
        L_0x00ca:
            if (r6 == 0) goto L_0x00cf
            r6.close()     // Catch:{ Exception -> 0x00eb }
        L_0x00cf:
            java.lang.String r13 = "lsm"
            java.lang.String r14 = "TestDataReceiver exportAppDbFile finished"
            android.util.Log.i(r13, r14)
            return
        L_0x00d7:
            if (r5 == 0) goto L_0x00dc
            r5.close()     // Catch:{ Exception -> 0x00e4 }
        L_0x00dc:
            if (r7 == 0) goto L_0x00e1
            r7.close()     // Catch:{ Exception -> 0x00e4 }
        L_0x00e1:
            r6 = r7
            r4 = r5
            goto L_0x00cf
        L_0x00e4:
            r1 = move-exception
            r1.printStackTrace()
            r6 = r7
            r4 = r5
            goto L_0x00cf
        L_0x00eb:
            r1 = move-exception
            r1.printStackTrace()
            goto L_0x00cf
        L_0x00f0:
            r13 = move-exception
        L_0x00f1:
            if (r4 == 0) goto L_0x00f6
            r4.close()     // Catch:{ Exception -> 0x00fc }
        L_0x00f6:
            if (r6 == 0) goto L_0x00fb
            r6.close()     // Catch:{ Exception -> 0x00fc }
        L_0x00fb:
            throw r13
        L_0x00fc:
            r1 = move-exception
            r1.printStackTrace()
            goto L_0x00fb
        L_0x0101:
            r13 = move-exception
            r4 = r5
            goto L_0x00f1
        L_0x0104:
            r13 = move-exception
            r6 = r7
            r4 = r5
            goto L_0x00f1
        L_0x0108:
            r1 = move-exception
            goto L_0x00c2
        L_0x010a:
            r1 = move-exception
            r4 = r5
            goto L_0x00c2
        */
    }
}
