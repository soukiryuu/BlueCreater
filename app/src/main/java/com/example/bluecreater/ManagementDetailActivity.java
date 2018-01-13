package com.example.bluecreater;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ManagementDetailActivity extends AppCompatActivity {

    private TextView tv_arr[] = new TextView[9];
    private int sum1,sum2,tmp,sumtime_month,sum_hour,sum_minu;
    private int before_sum;
    private int start_time = 0;
    private int end_time = 0;
    private int break_time = 0;
    private int ses_time = 0;
    private int dis_time = 0;
    private int ent_time = 0;
    private int in_busi_time = 0;
    public boolean select_data_flg = false;
    private EditText remarks_edit;
    public String key_arr[] = {"start_time","end_time","break_time","sum1","ses_time","dispatch_time",
                                "entrusted_time","in_busi_time","sum2","remarks1","remarks2","remarks",
                                "management_time","insert_day","insert_year_month","user_id","del_flg"};
    public String select_data[] = new String[17];
    public Button create_btn,save_btn;
    private DBHelper dbHelper = null;
    public ContentValues contentValues = new ContentValues();
    public ContentValues sumtime_contentValues = new ContentValues();
    private TimePickerDialog.OnTimeSetListener timepd_tsl;
    private String user_id = "test0003";
    private String management_time,insert_year_month;
    private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginCheck();

        ViewPager mViewPager = (ViewPager)findViewById(R.id.management_pager);
        PagerAdapter mPagerAdapter = new ManagementPagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);

        final Intent it = getIntent();
        String day_str = it.getStringExtra("day");
        management_time = it.getStringExtra("day2");
        insert_year_month = it.getStringExtra("day3");
        TextView tv = (TextView)findViewById(R.id.click_date_tv);
        tv.setText(day_str);


//        create_btn = (Button)findViewById(R.id.create_btn);
//        create_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    dbHelper = new DBHelper(getApplicationContext());
//                    dbHelper.execFileSQL(dbHelper.db, "create_management_month_table.sql");
//
//                } catch(Exception e) {
//                    Log.d("ManagementDetailActivity", e.getMessage());
//                }
//            }
//        });

        save_btn = (Button)findViewById(R.id.save_btn);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i=0; i < key_arr.length; i++) {
                    if (i < 9){
                        contentValues.put(key_arr[i], tv_arr[i].getText().toString());
                    }else {
                        switch (i) {
                            case 9:
                                contentValues.put(key_arr[i],"備考1");
                                break;
                            case 10:
                                contentValues.put(key_arr[i],"備考2");
                                break;
                            case 11:
                                contentValues.put(key_arr[i], remarks_edit.getText().toString());
                                break;
                            case 12:
                                contentValues.put(key_arr[i], management_time);
                                break;
                            case 13:
                                final Date date = new Date(System.currentTimeMillis());
                                Log.d("日付", df.format(date));
                                contentValues.put(key_arr[i], df.format(date));
                                break;
                            case 14:
                                contentValues.put(key_arr[i], insert_year_month);
                                break;
                            case 15:
                                contentValues.put(key_arr[i], user_id);
                                break;
                            case 16:
                                contentValues.put(key_arr[i], 0);
                                break;
                        }
                    }

                }

                try {
                    dbHelper = new DBHelper(getApplicationContext());
                    long update_res = dbHelper.db.update("management_day",contentValues,"management_time = ? AND insert_year_month = ? AND user_id = ?",
                            new String[]{management_time,insert_year_month,user_id});
                    if (update_res >= 1){
                        Log.d("SQL結果", String.valueOf(update_res)+":更新完了");
                        dbHelper = new DBHelper((getApplicationContext()));
                        Cursor cursor = dbHelper.db.rawQuery("SELECT sum_management_time_minute FROM management_month WHERE insert_year_month = ? AND user_id = ?",
                                new String[]{insert_year_month,user_id});
                        if (cursor.getCount() > 0) {
                            while (cursor.moveToNext()) {
                                int select_int = cursor.getColumnIndex("sum_management_time_minute");
                                int sum_time = Integer.parseInt(cursor.getString(select_int));
                                Log.d("sum_time", String.valueOf(sum_time));
                                Log.d("sum2", String.valueOf(sum2));
                                tmp = sum2 - before_sum;
                                if (before_sum < sum2) {
                                    sumtime_month = sum_time + tmp;
                                    Log.d("if", String.valueOf(sumtime_month));
                                }else if (before_sum > sum2) {
                                    sumtime_month = sum_time - tmp;
                                    Log.d("else if", String.valueOf(sumtime_month));
                                }else {
                                    sumtime_month = sum_time;
                                    Log.d("else", String.valueOf(sumtime_month));
                                }

                                sum_hour = sumtime_month/60;
                                sum_minu = sumtime_month%60;
                                String time = String.format("%1$02d",sum_hour)+":"+String.format("%1$02d",sum_minu);
                                sumtime_contentValues.put("sum_management_time", time);
                                sumtime_contentValues.put("sum_management_time_minute", sumtime_month);

                                try {
                                    long sumtime_update_res = dbHelper.db.update("management_month",sumtime_contentValues,"insert_year_month = ? AND user_id = ?",
                                            new String[]{insert_year_month,user_id});
                                    Log.d("ManagementDetailActivity:sumtime_update", String.valueOf(sumtime_update_res));
                                }catch (Exception e) {
                                    Log.d("ManagementDetailActivity:sumtime_update", e.getMessage());
                                }
                            }
                        }else {
                            try {
                                sumtime_contentValues.put("sum_management_time", tv_arr[8].getText().toString());
                                sumtime_contentValues.put("sum_management_time_minute", sum2);
                                sumtime_contentValues.put("insert_year_month", insert_year_month);
                                sumtime_contentValues.put("user_id", user_id);
                                sumtime_contentValues.put("del_flg", 0);
                                long id = dbHelper.db.insert("management_month",null,sumtime_contentValues);
                                Log.d("ManagementDetailActivity:monthtime_select", String.valueOf(id));
                            }catch (Exception e) {
                                Log.d("ManagementDetailActivity:monthtime_select", e.getMessage());
                            }
                        }
                    }else {
                        try {
                            long id = dbHelper.db.insert("management_day",null,contentValues);
//                            String sql = "insert into management_day values('"+tv_arr[0].getText().toString()+"','"+tv_arr[1].getText().toString()+"','"
//                                    +tv_arr[2].getText().toString()+"','"+tv_arr[3].getText().toString()+"','"+tv_arr[4].getText().toString()+"','"
//                                    +tv_arr[5].getText().toString()+"','"+tv_arr[6].getText().toString()+"','"+tv_arr[7].getText().toString()+"','"
//                                    +tv_arr[8].getText().toString()+"','"+"備考1"+"','"+"備考2"+"','"+"備考"+"','"+management_time+"','"+df.format(date)+"','"
//                                    +insert_year_month+"','"+user_id+"','"+0+"');";
//                            Log.d("SQL結果", sql);
//                    dbHelper.db.execSQL(sql);
                            Log.d("SQL結果", String.valueOf(id));
                            dbHelper = new DBHelper((getApplicationContext()));
                            Cursor cursor = dbHelper.db.rawQuery("SELECT sum_management_time_minute FROM management_month WHERE insert_year_month = ? AND user_id = ?",
                                    new String[]{insert_year_month,user_id});
                            if (cursor.getCount() > 0) {
                                while (cursor.moveToNext()) {
                                    int select_int = cursor.getColumnIndex("sum_management_time_minute");
                                    int sum_time = Integer.parseInt(cursor.getString(select_int));
                                    Log.d("sum_time", String.valueOf(sum_time));
                                    Log.d("sum2", String.valueOf(sum2));

                                    sumtime_month = sum_time + sum2;

                                    sum_hour = sumtime_month/60;
                                    sum_minu = sumtime_month%60;
                                    String time = String.format("%1$02d",sum_hour)+":"+String.format("%1$02d",sum_minu);
                                    sumtime_contentValues.put("sum_management_time", time);
                                    sumtime_contentValues.put("sum_management_time_minute", sumtime_month);

                                    try {
                                        long sumtime_update_res = dbHelper.db.update("management_month",sumtime_contentValues,"insert_year_month = ? AND user_id = ?",
                                                new String[]{insert_year_month,user_id});
                                        Log.d("ManagementDetailActivity:sumtime_update", String.valueOf(sumtime_update_res));
                                    }catch (Exception e) {
                                        Log.d("ManagementDetailActivity:sumtime_update", e.getMessage());
                                    }
                                }
                            }else {
                                try {
                                    sumtime_contentValues.put("sum_management_time", tv_arr[8].getText().toString());
                                    sumtime_contentValues.put("sum_management_time_minute", sum2);
                                    sumtime_contentValues.put("insert_year_month", insert_year_month);
                                    sumtime_contentValues.put("user_id", user_id);
                                    sumtime_contentValues.put("del_flg", 0);
                                    long res_id = dbHelper.db.insert("management_month",null,sumtime_contentValues);
                                    Log.d("ManagementDetailActivity:monthtime_select", String.valueOf(res_id));
                                }catch (Exception e) {
                                    Log.d("ManagementDetailActivity:monthtime_select", e.getMessage());
                                }
                            }
                        }catch (SQLException e){
                            Log.d("ManagementDetailActivity:insert", e.getMessage());
                        }
                    }



                }catch (SQLException e){
                    Log.d("ManagementDetailActivity:update", e.getMessage());
                }
            }
        });

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    public void loginCheck() {
        sharedPreferences = getSharedPreferences("BEBPreferences", Context.MODE_PRIVATE);
        Log.d("preferences",sharedPreferences.getString("user_id","error"));
        boolean login_flg = sharedPreferences.getBoolean("login_flg",false);
        if (login_flg == false) {
            Intent login_intent = new Intent(ManagementDetailActivity.this,LoginActivity.class);
            startActivity(login_intent);
            finish();
        }
        user_id = sharedPreferences.getString("user_id","error");
    }

    public void MyTimePickerDialog (final TextView my_tv) {
        Log.d("MyTimePickerDialog", String.valueOf(my_tv));

        timepd_tsl =
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Log.d("onTimeSet", String.format("%1$02d",hourOfDay)+":"+String.format("%1$02d",minute));
                        my_tv.setText(String.format("%1$02d",hourOfDay)+":"+String.format("%1$02d",minute));

                        if (my_tv == tv_arr[0]) {
                            start_time = (hourOfDay*60)+minute;
                        }else if (my_tv == tv_arr[1]) {
                            end_time = (hourOfDay*60)+minute;
                        }else if (my_tv == tv_arr[2]) {
                            break_time = (hourOfDay*60)+minute;
                        }else if (my_tv == tv_arr[4]) {
                            ses_time = (hourOfDay*60)+minute;
                        }else if (my_tv == tv_arr[5]) {
                            dis_time = (hourOfDay*60)+minute;
                        }else if (my_tv == tv_arr[6]) {
                            ent_time = (hourOfDay*60)+minute;
                        }else if (my_tv == tv_arr[7]) {
                            in_busi_time = (hourOfDay*60)+minute;
                        }else  {

                        }

                        SumCalculation(start_time,end_time,break_time,ses_time,dis_time,ent_time,in_busi_time);
                    }
                };

        TimePickerDialog tpd =
                new TimePickerDialog(
                        ManagementDetailActivity.this,
                        timepd_tsl,
                        0,0,true
                );
        tpd.show();
    }

    //合計の算出メソッド
    public void SumCalculation (int s, int e, int b,int ses,int ent,int dis,int in_busi) {

        sum1 = e - s - b;
        Log.d("Sum1Calculation", String.valueOf(sum1));
        if (sum1 < 0) {
            sum1 = (e+1440) - s - b;
        }else  {

        }

        sum2 = ses + ent + dis + in_busi;

        if (sum1 == sum2) {
            tv_arr[3].setBackgroundResource(R.drawable.time_match_background);
            tv_arr[8].setBackgroundResource(R.drawable.time_match_background);
        }else {
            tv_arr[3].setBackgroundResource(R.drawable.time_error_background);
            tv_arr[8].setBackgroundResource(R.drawable.time_error_background);
        }

        int hour = sum1/60;
        int minu = sum1%60;
        tv_arr[3].setText(String.format("%1$02d",hour)+":"+String.format("%1$02d",minu));

        int hour2 = sum2/60;
        int minu2 = sum2%60;
        tv_arr[8].setText(String.format("%1$02d",hour2)+":"+String.format("%1$02d",minu2));
    }

    private class ManagementPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // レイアウトファイル名を配列で指定します。
            int[] pages = {R.layout.management_detail_content, R.layout.attendance_setting, R.layout.notification_setting};

            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View layout ;
            layout = inflater.inflate(pages[position], null);

            if (position == 0) {
                tv_arr[0] = (TextView)layout.findViewById(R.id.start_clock);
                tv_arr[1] = (TextView)layout.findViewById(R.id.end_clock);
                tv_arr[2] = (TextView)layout.findViewById(R.id.break_clock);
                tv_arr[3] = (TextView)layout.findViewById(R.id.sum1_clock);
                tv_arr[4] = (TextView)layout.findViewById(R.id.ses_clock);
                tv_arr[5] = (TextView)layout.findViewById(R.id.dispatch_clock);
                tv_arr[6] = (TextView)layout.findViewById(R.id.entrusted_clock);
                tv_arr[7] = (TextView)layout.findViewById(R.id.in_busi_clock);
                tv_arr[8] = (TextView)layout.findViewById(R.id.sum2_clock);

                remarks_edit = (EditText)layout.findViewById((R.id.remarks_et));

                for (int i=0; i<tv_arr.length; i++) {
                    tv_arr[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MyTimePickerDialog((TextView) v);
                        }
                    });
                }

                try {
                    dbHelper = new DBHelper(getApplicationContext());
                    Cursor cursor = dbHelper.db.rawQuery("SELECT * FROM management_day WHERE management_time = ? AND insert_year_month = ? AND user_id = ?",
                            new String[]{management_time,insert_year_month,user_id});
                    if (cursor.getCount() > 0) {
                        select_data_flg = true;
                        while (cursor.moveToNext()) {
                            for (int i=0; i < select_data.length; i++) {
                                int select_int = cursor.getColumnIndex(key_arr[i]);
                                select_data[i] = cursor.getString(select_int);
                                if (i < 9) {
                                    String[] time_arr = select_data[i].split(":",0);
                                    int hour = Integer.parseInt(time_arr[0]);
                                    int minute = Integer.parseInt(time_arr[1]);
                                    Log.d("select time", hour+":"+minute);
                                    switch (i) {
                                        case 0:
                                            start_time = (hour*60)+minute;
                                            break;
                                        case 1:
                                            end_time = (hour*60)+minute;
                                            break;
                                        case 2:
                                            break_time = (hour*60)+minute;
                                            break;
                                        case 4:
                                            ses_time = (hour*60)+minute;
                                            break;
                                        case 5:
                                            dis_time = (hour*60)+minute;
                                            break;
                                        case 6:
                                            ent_time = (hour*60)+minute;
                                            break;
                                        case 7:
                                            in_busi_time = (hour*60)+minute;
                                            break;
                                    }
                                }
                                Log.d("select", select_data[i]);
                            }

                            SumCalculation(start_time,end_time,break_time,ses_time,dis_time,ent_time,in_busi_time);
                        }

                    }else {
                        select_data_flg = false;
                    }

                }catch (Exception e) {
                    Log.d("select", e.getMessage());
                }

                if (select_data_flg == true){
                    for (int i=0; i < 9; i++) {
                        tv_arr[i].setText(select_data[i]);
                    }

                    remarks_edit.setText(select_data[11]);
                    String[] sumtime_arr = select_data[8].split(":",0);
                    int hour = Integer.parseInt(sumtime_arr[0]);
                    int minute = Integer.parseInt(sumtime_arr[1]);
                    before_sum = (hour*60)+minute;
                    Log.d("before_sum", String.valueOf(before_sum));


                }

//                create_btn = (Button)layout.findViewById(R.id.create_btn);
//                create_btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
////                        DBHelper dBHelper = null;
//                        try {
//                            dBHelper = new DBHelper(getApplicationContext());
//                        } catch(Exception e) {
//                            Log.d("ManagementDetailActivity", e.getMessage());
//                        }
//                    }
//                });
            }


            ((ViewPager)container).addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager)container).removeView((View)object);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }
    }

}
