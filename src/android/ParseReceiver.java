package org.apache.cordova.core;

import com.parse.ParsePushBroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ParseReceiver extends ParsePushBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            ParsePlugin.sendExtras(extras);
        }
    }
}
