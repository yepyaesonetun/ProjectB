package com.prime.yz.ProjectB;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.prime.yz.ProjectB.account.GenericAccountService;
import com.prime.yz.ProjectB.adapter.PhotoCursorRVAdapter;
import com.prime.yz.ProjectB.db.ProjectSqliteHelper;
import com.prime.yz.ProjectB.helper.MyConstant;
import com.prime.yz.ProjectB.provider.ProjectBContentProvider;
import com.prime.yz.ProjectB.sync.SyncUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int PHOTO_LOADER = 1;

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.linear_sync)
    LinearLayout linear_sync;

    @BindView(R.id.progress)
    ProgressBar progressBar;

    @BindView(R.id.progress_status)
    TextView progress_status;

    private PhotoCursorRVAdapter photoCursorRVAdapter;
    private boolean isActive = false;
    private BroadcastReceiver syncStatusReceiver;
    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        if (!ContentResolver.getMasterSyncAutomatically()) {
            SyncUtils.enableAutoSync();
        }
        progressDialog = new ProgressDialog(this);

        syncStatusReceiver = new SyncStatusReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyConstant.SYNC_STATUS_BROADCAST);
        registerReceiver(syncStatusReceiver, filter);

        statrtSync();

        getSupportLoaderManager().initLoader(PHOTO_LOADER, null, this);

        init();
    }

    private void statrtSync() {

        SyncUtils.CreateSyncAccount(getApplicationContext());
        SyncUtils.triggerPhotoSync();

    }

    private void init() {

        swipeRefreshLayout.setOnRefreshListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        photoCursorRVAdapter = new PhotoCursorRVAdapter(null, ProjectSqliteHelper.COLUMN_PHOTO_ID);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(photoCursorRVAdapter);


    }

    @Override
    protected void onStart() {
        super.onStart();
        isActive = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive = false;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        boolean isSyncActive = ContentResolver.isSyncActive(GenericAccountService.getAccount(), MyConstant.AUTHORITY);

        if (!isSyncActive) {
            SyncUtils.triggerPhotoSync();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: ID : " + id );
        CursorLoader cursorLoader = null;
        String[] projectionPhoto = {
                ProjectSqliteHelper.COLUMN_PHOTO_ID,
                ProjectSqliteHelper.COLUMN_PHOTO_ALBUM_ID,
                ProjectSqliteHelper.COLUMN_PHOTO_TITLE,
                ProjectSqliteHelper.COLUMN_PHOTO_URL
        };
        switch (id) {
            case PHOTO_LOADER:
                cursorLoader = new CursorLoader(this,
                        ProjectBContentProvider.CONTENT_URI_PHOTO,
                        projectionPhoto,
                        null,
                        null,
                        null);
                break;
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        Log.d(TAG, "onLoadFinished: ID : " + loader.getId());
        if (!isActive) {
            return;
        }
        switch (loader.getId()) {
            case PHOTO_LOADER:
                photoCursorRVAdapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (!isActive) {
            return;
        }

        switch (loader.getId()) {
            case PHOTO_LOADER:
                photoCursorRVAdapter.swapCursor(null);
                break;
        }
    }

    class SyncStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getStringExtra(MyConstant.SYNC_STATUS)) {
                case MyConstant.SYNC_STATUS_START:
//                    if (progressDialog != null && progressDialog.isShowing()) {
//                        progressDialog.dismiss();
//                    }
//                    progressDialog = prepareProgressDialog("Preparing Data");
//                    progressDialog.show();

                    linear_sync.setVisibility(View.VISIBLE);
                    break;
                case MyConstant.SYNC_STATUS_STOP:
//                    if (progressDialog != null && progressDialog.isShowing()) {
//                        progressDialog.dismiss();
//                    }
                    linear_sync.setVisibility(View.GONE);
                    Log.d("TT", "onReceive: SYNC FINISHED");
                    getSupportLoaderManager().getLoader(PHOTO_LOADER).startLoading();

                    break;
            }
        }

    }
    private ProgressDialog prepareProgressDialog(@NonNull String message) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        return progressDialog;
    }
}
