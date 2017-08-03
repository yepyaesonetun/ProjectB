package com.prime.yz.ProjectB.common;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.prime.yz.ProjectB.R;


public abstract class BaseActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(getLayoutResource());
    }

//    protected final void setUpFont() {
//        FontHelper.applyFont(this, this.findViewById(android.R.id.content).getRootView());
//    }

    protected final void setupToolbar(boolean isChild) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(toolbar.getTitle());
                if (isChild) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            }
        }
    }

    protected final void setupToolbarText(String text) {
        toolbar.setTitle(text);
    }

    protected final void setupToolbarBgColor(String color) {
        toolbar.setBackgroundColor(Color.parseColor(color));
    }

    protected final void setupToolbarTextColor(String color) {
        toolbar.setTitleTextColor(Color.parseColor(color));
    }

    protected final void setupToolbarIcon(int icon) {
        toolbar.setNavigationIcon(icon);
    }

    @LayoutRes
    protected abstract int getLayoutResource();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
