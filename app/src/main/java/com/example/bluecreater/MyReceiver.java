package com.example.bluecreater;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d("Receive","呼ばれたよ〜");
        SharedPreferences sharedPreferences = context.getSharedPreferences("BEBPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("login_flg",false);
        editor.commit();
//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
