package com.prime.yz.ProjectB.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;


import com.prime.yz.ProjectB.db.ProjectSqliteHelper;
import com.prime.yz.ProjectB.helper.MyConstant;
import com.prime.yz.ProjectB.helper.ServiceHelper;
import com.prime.yz.ProjectB.model.PhotoModel;
import com.prime.yz.ProjectB.provider.ProjectBContentProvider;
import com.prime.yz.ProjectB.receiver.SyncFailReceiver;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by SantaClaus on 04/04/2017.
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String TAG = "SyncAdapter";
    private final ContentResolver contentResolver;
    private ServiceHelper.ApiService service;
    private Call<ArrayList<PhotoModel>> callPhotos;

    public SyncAdapter(Context context, boolean autoInitialize){
        super(context, autoInitialize);
        contentResolver = context.getContentResolver();
        service = ServiceHelper.getClient(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.i(TAG," Sync running");
        if (extras.getInt("SYNC_TRIGGER") == 1) {
            sendSyncStatusBroadcast(MyConstant.SYNC_STATUS_START,false);
            boolean result = PhotoSync();
            sendSyncStatusBroadcast(MyConstant.SYNC_STATUS_STOP,result);
        }

        Log.i(TAG,"Sync Completed");
    }

    private boolean PhotoSync() {
        Log.i(TAG,"Photo Syncing");
        callPhotos = service.getPhotos(1);
        boolean result=false;

        ArrayList<PhotoModel> photoList = new ArrayList<>();
        ArrayList<ContentValues> contentValuesList = new ArrayList<>();

        try {
            Response<ArrayList<PhotoModel>> responsePhotos = callPhotos.execute();
            photoList.addAll(responsePhotos.body());


            ContentValues[] arrContentValues = contentValuesList.toArray(new ContentValues[0]);

            getContext().getContentResolver().bulkInsert(ProjectBContentProvider.CONTENT_URI_PHOTO, arrContentValues);

            contentValuesList.clear();

            for (PhotoModel model: photoList){
                Log.e(TAG,"Sync Photo"+model.toString());
                contentValuesList.add(photModelToContentValues(model));
            }

            photoList.clear();

            if (!contentValuesList.isEmpty()){
                getContext().getContentResolver().delete(ProjectBContentProvider.CONTENT_URI_PHOTO,null,null);
            }
            arrContentValues = contentValuesList.toArray(new ContentValues[contentValuesList.size()]);
            getContext().getContentResolver().bulkInsert(ProjectBContentProvider.CONTENT_URI_PHOTO, arrContentValues);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private ContentValues photModelToContentValues(PhotoModel model) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProjectSqliteHelper.COLUMN_PHOTO_ID, model.getId());
        contentValues.put(ProjectSqliteHelper.COLUMN_PHOTO_ALBUM_ID, model.getAlbumId());
        contentValues.put(ProjectSqliteHelper.COLUMN_PHOTO_TITLE, model.getTitle());
        contentValues.put(ProjectSqliteHelper.COLUMN_PHOTO_URL, model.getUrl());

        return contentValues;
    }

    private void sendSyncStatusBroadcast(String status,boolean syncResult) {
        Intent intent = new Intent();
        intent.setAction(MyConstant.SYNC_STATUS_BROADCAST);
        intent.putExtra(MyConstant.SYNC_STATUS, status);
        intent.putExtra(MyConstant.SYNC_RESULT,syncResult);
        getContext().sendBroadcast(intent);
    }

    private void sendSyncFailBroadcast(){
        Intent intent = new Intent(getContext(), SyncFailReceiver.class);
        getContext().sendBroadcast(intent);
    }
}
