package cn.nubia.gamelauncherx.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.nubia.gamelauncherx.commoninterface.ConstantVariable;
import java.util.ArrayList;

public class AppAddProvider extends ContentProvider {
    public static final String APPADD_COMPONENT = "component";
    public static final String APPADD_ID = "_id";
    public static final String APPADD_IMAGE_URL = "imageUrl";
    public static final String APPADD_ISADD = "isAdd";
    public static final String APPADD_ISGAME = "isGame";
    public static final String APPADD_NAME = "gamename";
    public static final String AUTHORITY = "cn.nubia.gamelauncher.db.AppAddProvider";
    private static final String MAX_ID = "max(_id)";
    public static final String NOTIFY = "notify";
    public static final String TABLE_APPADD_NAME = "appadd";
    public static final String TABLE_USER_REMOVE_NAME = "user_remove";
    private SQLiteOpenHelper mSqliteHelper = null;

    public boolean onCreate() {
        this.mSqliteHelper = new AppAddDataBaseHelper(getContext());
        this.mSqliteHelper.getWritableDatabase().close();
        return false;
    }

    @Nullable
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        SQLiteDatabase db = this.mSqliteHelper.getReadableDatabase();
        String tableName = getTableName(uri);
        if (tableName == null) {
            Log.i("lsm", "query uri is error format");
            return null;
        }
        if (MAX_ID.equals(selection)) {
            cursor = db.rawQuery("select max(_id) from " + tableName, null);
        } else {
            cursor = db.query(tableName, projection, selection, selectionArgs, null, null, null);
        }
        return cursor;
    }

    @Nullable
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase sqLiteDatabase = this.mSqliteHelper.getWritableDatabase();
        String tableName = getTableName(uri);
        if (tableName == null) {
            Log.i("lsm", "insert uri is error format");
        } else {
            long result = sqLiteDatabase.insert(tableName, null, values);
            sqLiteDatabase.close();
            sendNotify(uri);
            Log.i("lsm", "tableName == " + tableName + " provider insert value == " + values + " result == " + result);
        }
        return null;
    }

    private void sendNotify(Uri uri) {
        if (uri != null && "true".equals(uri.getQueryParameter(NOTIFY))) {
            getContext().getContentResolver().notifyChange(uri, null);
            Log.i("lsm", "sendNotify uri == " + uri);
        }
    }

    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = this.mSqliteHelper.getWritableDatabase();
        String tableName = getTableName(uri);
        if (tableName == null) {
            Log.i("lsm", "delete uri is error format");
            return -1;
        }
        sqLiteDatabase.delete(tableName, selection, selectionArgs);
        sqLiteDatabase.close();
        sendNotify(uri);
        return 0;
    }

    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = this.mSqliteHelper.getWritableDatabase();
        String tableName = getTableName(uri);
        if (tableName == null) {
            Log.i("lsm", "delete uri is error format");
            return -1;
        }
        sqLiteDatabase.update(tableName, values, selection, selectionArgs);
        sqLiteDatabase.close();
        sendNotify(uri);
        return 0;
    }

    private String getTableName(Uri uri) {
        if (uri.getPathSegments().get(0) != null) {
            return (String) uri.getPathSegments().get(0);
        }
        return null;
    }

    public Bundle call(String method, String arg, Bundle extras) {
        return doCall(method, arg, extras);
    }

    public Bundle doCall(String method, String arg, Bundle extras) {
        if (TextUtils.isEmpty(method) || !method.equals("getAllWhiteList")) {
            return null;
        }
        Bundle result = new Bundle();
        ArrayList<String> whiteList = new ArrayList<>();
        for (String temp : ConstantVariable.LOCAL_GAME_IMAGE_MAP.keySet()) {
            whiteList.add(temp);
        }
        result.putStringArrayList("whiteList", whiteList);
        return result;
    }
}
