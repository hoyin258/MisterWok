package us.misterwok.app.activity;

import android.support.v7.app.ActionBarActivity;

import com.google.analytics.tracking.android.EasyTracker;

/**
 * Created by hoyin on 14/4/14.
 */
public class BaseActivity extends ActionBarActivity {
    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

}
