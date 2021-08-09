package com.gking.simplemusicplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.gking.simplemusicplayer.impl.MyApplicationImpl;

import static com.gking.simplemusicplayer.impl.MyApplicationImpl.myApplication;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(true)return;
        if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            System.out.println("OFFFFFFFFFFFFFFFFFFFFF");
            NotificationManager notificationManager= (NotificationManager) myApplication.getSystemService(MyApplicationImpl.NOTIFICATION_SERVICE);
            Notification notification;
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                final String CHANNEL_ID = "com.gking.simplemusicplayer.notification.channel.id";
//设定的通知渠道名称
                String channelName = "com.gking.simplemusicplayer.notification.channel.name";
                //构建通知渠道
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("通知");
                NotificationCompat.Builder builder = new NotificationCompat.Builder(myApplication, CHANNEL_ID);
                builder.setSmallIcon(R.drawable.ic_launcher_foreground) //设置通知图标
                        .setContentTitle("播放中")//设置通知标题
                        .setContentText("")//设置通知内容
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                notificationManager.createNotificationChannel(channel);
                notification=builder.build();
            }
            else {notification = new Notification.Builder(myApplication)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentTitle("播放中")//设置通知标题
                    .setContentText("")//设置通知内容
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .build();
            }
            notificationManager.notify(10,notification);
        }
        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            System.out.println("ONNNNNNNNNNNNNNNNNNNNNNNNNN");
            NotificationManager notificationManager= (NotificationManager) myApplication.getSystemService(MyApplicationImpl.NOTIFICATION_SERVICE);
            notificationManager.cancel(10);
        }
    }
}
