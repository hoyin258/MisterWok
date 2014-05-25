package us.misterwok.app.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

public class GooglePlayServiceHelper {

    public static final String EXTRA_MESSAGE = "message";

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "GooglePlayServiceHelper";
    private String SENDER_ID = "686585567342";

    private Activity activity;
    private GoogleCloudMessaging gcm;
    private SharedPreferences sharedPreferences;

    public GooglePlayServiceHelper(Activity activity) {
        this.activity = activity;
        sharedPreferences =PreferenceManager.getDefaultSharedPreferences(activity);
    }


    public boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                activity.finish();
            }
            return false;
        }
        return true;
    }


    public void init() {
        String regId = getRegistrationId();
        if (regId == null || !isValidVersion()) {
            Log.d("", "registerInBackground");
            registerInBackground();
        }
    }


    public boolean isValidVersion() {
        return getAppVersion() == getLastVersion();
    }

    public int getAppVersion() {
        try {
            PackageInfo packageInfo = activity.getPackageManager()
                    .getPackageInfo(activity.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public void setLastVersion() {
        Log.d("", "App Version=" + getAppVersion());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("version", getAppVersion() + "");
        editor.commit();
    }

    public int getLastVersion() {
        try {
            return Integer.parseInt(sharedPreferences.getString("version",""));
        } catch (Exception ex) {
            return 0;
        }
    }

    public void setRegistrationId(String regId) {
        Log.d("","Registration ID=" + regId);
        sharedPreferences.edit().putString("push", regId).commit();
    }

    public String getRegistrationId() {
        return sharedPreferences.getString("push","");
    }

    public void registerInBackground() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(activity);
                    }
                    String regId = gcm.register(SENDER_ID);
                    Log.d("", "Device registered");
                    setRegistrationId(regId);
                    setLastVersion();
                    sendRegistrationIdToBackend(regId);

                } catch (IOException ex) {
                    Log.d("", "Registered error :" + ex.getMessage());
                }
                return null;
            }

            private void sendRegistrationIdToBackend(String regId) {
                try {

                    //TODO connect API
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onPostExecute(Void aVoid) {

            }
        }.execute();
    }

}
