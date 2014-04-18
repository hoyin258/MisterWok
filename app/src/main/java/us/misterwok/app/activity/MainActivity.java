package us.misterwok.app.activity;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

import java.util.ArrayList;

import us.misterwok.app.Constants;
import us.misterwok.app.R;
import us.misterwok.app.fragment.CategoryFragment;
import us.misterwok.app.fragment.NavigationDrawerFragment;
import us.misterwok.app.obj.LeftMenuItem;

public class MainActivity extends BaseActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final int INDEX_MENU = 0;
    public static final int INDEX_CART = 1;
    public static final int INDEX_USER = 2;

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private boolean isConfirmLeft = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = getTitle();
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        initDrawerItems();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        switch (position) {
            case INDEX_MENU:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container,
                                CategoryFragment.newInstance(),
                                CategoryFragment.class.getCanonicalName())
                        .commit();
                break;
            case INDEX_CART:
                Intent i = new Intent(MainActivity.this, CartActivity.class);
                startActivity(i);
                break;
            case INDEX_USER:
                SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), Activity.MODE_PRIVATE);
                String name = sharedPreferences.getString(Constants.PREFERENCE_NAME, null);
                if (TextUtils.isEmpty(name)) {
                    onFacebookLogin();
                } else {
                    onFacebookLogout();
                }
                break;
            default:
                break;
        }
    }

    public void onSectionAttached(int titleId) {
        mTitle = getString(titleId);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (isConfirmLeft) {
            Toast.makeText(MainActivity.this, R.string.exit_confirm_message, Toast.LENGTH_SHORT).show();
            isConfirmLeft = false;
        } else {
            super.onBackPressed();
        }
    }

    public void onCartButtonClick(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        String name = sharedPreferences.getString(Constants.PREFERENCE_NAME, null);
        if (TextUtils.isEmpty(name)) {
            onFacebookLogin();
        } else {
            onNavigationDrawerItemSelected(1);
        }
    }

    private void initDrawerItems() {

        ArrayList<LeftMenuItem> leftMenuItems = new ArrayList<LeftMenuItem>();
        leftMenuItems.add(new LeftMenuItem(R.drawable.ic_action_star, getString(R.string.title_menu)));
        leftMenuItems.add(new LeftMenuItem(R.drawable.ic_action_cart, getString(R.string.title_cart)));

        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), Activity.MODE_PRIVATE);
        String name = sharedPreferences.getString(Constants.PREFERENCE_NAME, null);
        if (TextUtils.isEmpty(name)) {
            leftMenuItems.add(new LeftMenuItem(R.drawable.ic_action_user, getString(R.string.title_login)));
        } else {
            leftMenuItems.add(new LeftMenuItem(R.drawable.ic_action_back, getString(R.string.title_logout)));
        }
        mNavigationDrawerFragment.setLeftMenuItems(leftMenuItems);
    }

    private void onFacebookLogin() {

        Session.openActiveSession(this, true, new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state,
                             Exception exception) {
                if (session.isOpened()) {
                    Request.newMeRequest(session, new Request.GraphUserCallback() {
                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            if (user != null) {
                                SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(Constants.PREFERENCE_NAME, user.getId());
                                editor.putString(Constants.PREFERENCE_FACEBOOK_ID, user.getId());
                                editor.commit();
                                initDrawerItems();

                            }
                        }
                    }).executeAsync();
                }
            }
        });
    }

    private void onFacebookLogout() {
        Session session = Session.getActiveSession();
        if (session != null) {
            if (!session.isClosed()) {
                session.closeAndClearTokenInformation();
            }
        } else {
            session = new Session(MainActivity.this);
            Session.setActiveSession(session);
            session.closeAndClearTokenInformation();
        }
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        initDrawerItems();
    }
}
