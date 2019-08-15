package com.venuskimblessing.tuberepeatfree.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import static android.content.Intent.EXTRA_CHOSEN_COMPONENT;

public class ShareAppReciver extends BroadcastReceiver {
    public static final String TAG = "ShareAppReciver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String selectedAppPackage = String.valueOf(intent.getExtras().get(EXTRA_CHOSEN_COMPONENT));
        Toast.makeText(context, "공유 앱 이름 : " + selectedAppPackage, Toast.LENGTH_LONG).show();
    }
}
