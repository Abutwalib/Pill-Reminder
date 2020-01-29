package com.example.piremedtime;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.piremedtime.NotificationTest;


public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Get id and message
        int notificationId=intent.getIntExtra("notificationId",0);
        String pillname=intent.getStringExtra("PillName");
        String nopills=intent.getStringExtra("NoPills");
        String description=intent.getStringExtra("Description");
        int session=intent.getIntExtra("Sessions",2);
        String mess=" Take "+nopills+" Capsules/Tablets Of "+pillname+" \n "+  "("+description+")"+"\n \n";
        //when notification is tapped call main activity
        Intent mainIntent=new Intent(context, NotificationTest.class);
        PendingIntent contentIntent=PendingIntent.getActivity(context,0,mainIntent,0);
        NotificationManager myNotificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        //Prepare Notification
        Notification.Builder builder=new Notification.Builder(context);
        builder.setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Pill Reminder!!!")
                .setContentText(mess)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL);



        //Notify
        myNotificationManager.notify(notificationId,builder.build());


    }
}
