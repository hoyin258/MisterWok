package us.misterwok.app.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import us.misterwok.app.Application;
import us.misterwok.app.BuildVariants;
import us.misterwok.app.R;
import us.misterwok.app.activity.MainActivity;
import us.misterwok.app.activity.OrderActivity;
import us.misterwok.app.receiver.GcmBroadcastReceiver;

public class GcmIntentService extends IntentService {

    private static final String TAG = "GcmIntentService";
    private NotificationManager mNotificationManager;

    public GcmIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("", "recevied");
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Application.notificationId += 1;
                sendNotification(extras.getString("message"));
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg)
                        .setDefaults(Notification.DEFAULT_SOUND |
                                Notification.DEFAULT_VIBRATE |
                                Notification.DEFAULT_LIGHTS)
                        .setAutoCancel(true);


        if (BuildVariants.IS_ADMIN) {
            Intent orderIntent = new Intent(this, OrderActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(OrderActivity.class);
            stackBuilder.addNextIntent(orderIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    intent, 0);
            mBuilder.setContentIntent(contentIntent);
        }
        mNotificationManager.notify(Application.notificationId, mBuilder.build());
    }
}