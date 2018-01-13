package com.example.bluecreater;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {

    private Button login_btn;
    private DBHelper dbHelper = null;
    private InputFilter[] filters = { new MyFilter()};
    private EditText id_edit;
    private EditText pass_edit;
    private ContentValues contentValues = new ContentValues();
    private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
//    private Intent intent = new Intent(LoginActivity.this,Calender.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("BEBPreferences", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        id_edit = (EditText)findViewById(R.id.user_id_edit);
        pass_edit = (EditText)findViewById(R.id.user_password_edit);
        pass_edit.setFilters(filters);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("ユーザー新規作成")
                .setMessage("ユーザー情報が存在しません。"+ "\n" +"ユーザー情報を新規作成しますか？")
                .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper = new DBHelper(getApplicationContext());
                        dbHelper.execFileSQL(dbHelper.db, "create_user_data_table.sql");
                        dbHelper.execFileSQL(dbHelper.db, "create_user_setting_table.sql");
                        dbHelper.execFileSQL(dbHelper.db, "create_management_month_table.sql");
                        dbHelper.execFileSQL(dbHelper.db, "create_management_day_table.sql");

                        contentValues.put("user_id",id_edit.getText().toString());
                        contentValues.put("password",pass_edit.getText().toString());
                        final Date date = new Date(System.currentTimeMillis());
                        contentValues.put("signin_date",df.format(date));
                        contentValues.put("del_flg",0);

                        try{
                            dbHelper = new DBHelper(getApplicationContext());
                            long insert_res = dbHelper.db.insert("user_data",null,contentValues);
                            Log.d("SQL結果", String.valueOf(insert_res));
//                            SharedPreferences sharedPreferences = getSharedPreferences("BEBPreferences", Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("user_id",id_edit.getText().toString());
                            editor.commit();

                            timeStart();

                            Intent intent = new Intent(LoginActivity.this,Calender.class);
                            startActivity(intent);
                            finish();
                        }catch (Exception e) {
                            Log.d("user_insert_error",e.getMessage());
                        }
                    }
                })
                .setNegativeButton("いいえ", null);

        login_btn = (Button)findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,Calender.class);
//                startActivity(intent);
//                finish();
                dbHelper = new DBHelper(getApplicationContext());
                String query = "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='user_data';";
                Cursor c = dbHelper.db.rawQuery(query, null);
                c.moveToFirst();
                String result = c.getString(0);
                Log.d("result",result);
                if (result.equals("0")) {
                    Log.d("result","if");
                    if (pass_edit.length() >= 8){
                        builder.show();
                    }else {
                        Toast.makeText(LoginActivity.this,"半角英数字8文字以上で登録してください。",Toast.LENGTH_SHORT).show();
                    }


                }else {
                    dbHelper = new DBHelper((getApplicationContext()));
                    Cursor cursor = dbHelper.db.rawQuery("SELECT user_id FROM user_data WHERE user_id = ? AND password = ?",
                            new String[]{id_edit.getText().toString(),pass_edit.getText().toString()});
                    if (cursor.getCount() == 1) {
                        editor.putString("user_id",id_edit.getText().toString());
                        editor.commit();
                        timeStart();
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(LoginActivity.this,"ID又はパスワードが正しくありません。",Toast.LENGTH_SHORT).show();
                    }
                    Log.d("result","else");
                }
            }
        });
    }

    public void timeStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND,1800);
        Intent service_intent = new Intent(getApplicationContext(),MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,service_intent,0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);

        editor.putBoolean("login_flg",true);
        editor.commit();
    }

    class MyFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if( source.toString().matches("^[a-zA-Z0-9]+$") ){
                Log.d("filter","ok");
                return source;
            }else{
                Log.d("filter","ng");
                return "";
            }
        }
    }
}
