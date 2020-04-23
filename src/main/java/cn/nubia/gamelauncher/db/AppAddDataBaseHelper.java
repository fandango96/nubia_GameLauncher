package cn.nubia.gamelauncher.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppAddDataBaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "appadd.db";
    private static final int DATABASE_VERSION = 1;
    private String CREATE_APPADD = "create table if not exists appadd( _id integer primary key autoincrement, component text not null, isAdd integer,isGame integer,imageUrl text,gamename text)";
    private String CREATE_USER_REMOVE = "create table if not exists user_remove( _id integer primary key autoincrement, component text not null)";

    public AppAddDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(this.CREATE_APPADD);
        db.execSQL(this.CREATE_USER_REMOVE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
