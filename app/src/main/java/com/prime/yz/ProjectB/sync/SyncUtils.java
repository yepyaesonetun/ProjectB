package com.prime.yz.ProjectB.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncStatusObserver;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.prime.yz.ProjectB.account.GenericAccountService;
import com.prime.yz.ProjectB.helper.MyConstant;


/**
 * Created by SantaClaus on 04/04/2017.
 */

public class SyncUtils {
    private static final long SYNC_FREQUENCY = 60 * 60; // 1 hour ( in seconds )
    private static final String CONTENT_AUTHORITY = MyConstant.AUTHORITY;
    private static final String PREF_SETUP_COMPLETE = "setup_complete";

    public static void CreateSyncAccount(Context context){
        boolean newAccount = false;
        boolean setupComplete = PreferenceManager
                .getDefaultSharedPreferences(context).getBoolean(PREF_SETUP_COMPLETE,false);

        Account account = GenericAccountService.getAccount();
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(account,null,null)){

            // Informs the system that this account supports sync
            ContentResolver.setIsSyncable(account,CONTENT_AUTHORITY,1);
            // Informs the system that this account is eligible for auto sync when the network is up
            ContentResolver.setSyncAutomatically(account,CONTENT_AUTHORITY,true);

            ContentResolver.addPeriodicSync(account,CONTENT_AUTHORITY,new Bundle(),SYNC_FREQUENCY);

            newAccount = true;
        }
        if (newAccount || !setupComplete){
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putBoolean(PREF_SETUP_COMPLETE,true).commit();
        }

    }

   public static void triggerPhotoSync(){
       Bundle bundle = new Bundle();
       bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
       bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
       bundle.putInt("SYNC_TRIGGER", 1);
       ContentResolver.requestSync(GenericAccountService.getAccount(),
               MyConstant.AUTHORITY, bundle);
       Log.i("TT", "triggerPhotoSync: Sync called");
   }

    public static boolean isPeriodicSyncScheduled() {
        return !ContentResolver.getPeriodicSyncs(GenericAccountService.getAccount(), MyConstant.AUTHORITY).isEmpty();
    }

    public static void enableAutoSync() {
        ContentResolver.setMasterSyncAutomatically(true);
        ContentResolver.setSyncAutomatically(GenericAccountService.getAccount(), MyConstant.AUTHORITY, true);
    }

    public static void setStickySync(){
        ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_SETTINGS, new SyncStatusObserver() {
            @Override
            public void onStatusChanged(int which) {
                SyncUtils.enableAutoSync();
            }
        });
    }
}
