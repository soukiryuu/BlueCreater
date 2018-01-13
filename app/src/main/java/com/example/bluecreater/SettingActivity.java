package com.example.bluecreater;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends ListActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
//                    mTextMessage.setText(R.string.title_home);
                    Intent setting_it = new Intent(SettingActivity.this,Calender.class);
                    startActivity(setting_it);
                    finish();
                    return true;
                case R.id.navigation_dashboard:
                    Intent bebuser_it = new Intent(SettingActivity.this,BEBUserActivity.class);
                    startActivity(bebuser_it);
//                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
//                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    private String title[] = {"プッシュ通知","パスワード","勤怠設定","BEB（Admin）の追加設定","ログアウト"};
    private String lessons[] = {"・通知の【ON/OFF】、通知時刻の設定","・ログインパスワードの設定","・勤怠時刻の初期値設定","・BEB管理画面のタブを追加します。","・ログアウトします。"};
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        loginCheck();

        BottomNavigationView navigationView = (BottomNavigationView)findViewById(R.id.navigation);
        navigationView.setSelectedItemId(R.id.navigation_notifications);

        SettingList settingList_1 = new SettingList();
        settingList_1.setTitle(title[0]);
        settingList_1.setLessons(lessons[0]);

        SettingList settingList_2 = new SettingList();
        settingList_2.setTitle(title[1]);
        settingList_2.setLessons(lessons[1]);

        SettingList settingList_3 = new SettingList();
        settingList_3.setTitle(title[2]);
        settingList_3.setLessons(lessons[2]);

        SettingList settingList_4 = new SettingList();
        settingList_4.setTitle(title[3]);
        settingList_4.setLessons(lessons[3]);

        SettingList settingList_5 = new SettingList();
        settingList_5.setTitle(title[4]);
        settingList_5.setLessons(lessons[4]);


        List<SettingList> list = new ArrayList<SettingList>();
        list.add(settingList_1);
        list.add(settingList_2);
        list.add(settingList_3);
        list.add(settingList_4);
        list.add(settingList_5);

        ListAdapter adapter = new ListAdapter(getApplicationContext(),list);

        setListAdapter(adapter);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void loginCheck() {
        sharedPreferences = getSharedPreferences("BEBPreferences", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        boolean login_flg = sharedPreferences.getBoolean("login_flg",false);
        if (login_flg == false) {
            Intent login_intent = new Intent(SettingActivity.this,LoginActivity.class);
            startActivity(login_intent);
            finish();
        }
    }

    class ListAdapter extends ArrayAdapter<SettingList> {

        private LayoutInflater mInflater;
        private TextView title_tv,lessons_tv;
        private Button mButton;

        public ListAdapter(Context context, List<SettingList> objects) {
            super(context, 0, objects);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.setting_layout, null);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("click", String.valueOf(position));
                        switch (position) {
                            case 0:
                                break;
                            case 1:
                                break;
                            case 2:
                                break;
                            case 3:
                                break;
                            case 4:
                                editor.putBoolean("login_flg",false);
                                editor.commit();
                                Intent login_intent = new Intent(SettingActivity.this,LoginActivity.class);
                                login_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(login_intent);
                                finish();
                                break;
                        }
                    }
                });
            }
            final SettingList item = this.getItem(position);
            if(item != null){
                title_tv = (TextView)convertView.findViewById(R.id.list_title);
                title_tv.setText(item.getTitle());
                lessons_tv = (TextView)convertView.findViewById(R.id.lessons);
                lessons_tv.setText(item.getLessons());
            }
            return convertView;

        }

    }


}
