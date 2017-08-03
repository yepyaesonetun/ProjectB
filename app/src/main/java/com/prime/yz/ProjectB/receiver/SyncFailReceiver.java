package com.prime.yz.ProjectB.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.prime.yz.ProjectB.sync.SyncUtils;


public class SyncFailReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SyncUtils.triggerPhotoSync();
    }
}
