package com.example.bluecreater;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by watanabehiroaki on 2017/10/15.
 */

public class DBHelper {

    public static final String TAG = "DBHelper";

    public SQLiteDatabase db;
    private final DBOpenHelper dbOpenHelper;
    public Context m_context;

    public DBHelper(final Context context) {
        this.dbOpenHelper = new DBOpenHelper(context);
        this.m_context = context;
        establishDb();
    }

    private void establishDb() {
        if (this.db == null) {
            this.db = this.dbOpenHelper.getWritableDatabase();
//            cleanup();
        }else {
            Log.d("establishDb","null");
        }
    }

    public void cleanup() {
        if (this.db != null) {
            this.db.close();
            this.db = null;
        }
    }

    /**
     * Databaseが削除できればtrue。できなければfalse
     * @param context
     * @return
     */
    public boolean isDatabaseDelete(final Context context) {
        boolean result = false;
        if (this.db != null) {
            File file = context.getDatabasePath(dbOpenHelper.getDatabaseName());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                result = this.db.deleteDatabase(file);
            }
        }
        return result;
    }

    public void execFileSQL(SQLiteDatabase db, String fileName){
        InputStream in = null;
        InputStreamReader inReader = null;
        BufferedReader reader = null;
        try {
            // 文字コード(UTF-8)を指定して、ファイルを読み込み
            in = m_context.getAssets().open(fileName);
            inReader = new InputStreamReader(in, "UTF-8");
            reader = new BufferedReader(inReader);

            // ファイル内の全ての行を処理
            String s;
            while((s = reader.readLine()) != null){
                // 先頭と末尾の空白除去
                s = s.trim();

                // 文字が存在する場合（空白行は処理しない）
                if (0 < s.length()){
                    // SQL実行
                    db.execSQL(s);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (inReader != null) {
                try {
                    inReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
