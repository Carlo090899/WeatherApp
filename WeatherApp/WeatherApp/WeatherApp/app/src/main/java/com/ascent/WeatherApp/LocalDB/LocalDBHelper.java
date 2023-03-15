package com.ascent.WeatherApp.LocalDB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import com.ascent.WeatherApp.model.UserModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class LocalDBHelper extends SQLiteOpenHelper {

    public static String dbName = "weatherGps_db.db";
    public static int dbVersion = 1;
    public static String dbPath = "";
    Context myContext;
    SQLiteDatabase db;
    Cursor cursor = null;


    public LocalDBHelper(Context context) {
        super(context, dbName, null, dbVersion);
        myContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            db.disableWriteAheadLogging();
        }
    }


    private boolean ExistDatabase() {
        File myFile = new File(dbPath + dbName);
        return myFile.exists();

    }


    private void CopyDatabase() {
        try {
            InputStream myInput = myContext.getAssets().open(dbName);
            OutputStream myOutput = new FileOutputStream(dbPath + dbName);
            byte[] myBuffer = new byte[2048];
            int length;
            while ((length = myInput.read(myBuffer)) > 0) {
                myOutput.write(myBuffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
    public void StartWork() {
        dbPath = myContext.getFilesDir().getParent() + "/databases/";
        System.out.println("=============dbpath================" + dbPath );
        if (!ExistDatabase()) {
            this.getWritableDatabase();
            CopyDatabase();
        } else {
            this.getWritableDatabase();
        }
    }








    public UserModel getUser() {
        db = getReadableDatabase();
        UserModel t=null;
        try {
            cursor = db.rawQuery("SELECT username, password FROM auth_user", null);
            if (cursor.moveToFirst()) {
                t=new UserModel();
                t.setUsername(cursor.getString(0));
                t.setPassword(cursor.getString(1));
            }

            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return t;
    }

}
