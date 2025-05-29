package com.example.firelogin.notifications;

import static com.example.firelogin.StaticFunctions.print;
import static com.example.firelogin.StaticFunctions.showToastAlert;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class NotificationUtil {
    public static final String CHANNEL_ID="Secovo_notifs";
    public static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public static Boolean createNotificationChannel(Context ctx){
        try {
            String name = "Canal de Eventos";
            String description = "Canal para Notificaciones de Eventos";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel chanel = new NotificationChannel(CHANNEL_ID, name, importance);
                chanel.setDescription(description);

                NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(chanel);
                return true;
            }
        }catch(Exception ex){
            showToastAlert(ctx,"CANAL DE NOTIFICACIONES:"+ex.getMessage());
        }
        return false;
    }
    static public int[][] getCurrentDate(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate now = LocalDate.now();
            LocalTime time = LocalTime.now();
            int[][] dateTime=new int[][]{
                    {now.getDayOfMonth() , now.getMonthValue() ,now.getYear()},
                    {time.getHour(),time.getMinute(),time.getSecond()}

                };
            return dateTime;
        }
        return null;
    }
    static public String[] getFormatedDate(Date date){
        return sdf.format(date).split(" ");
    }
    static public void createNotification(Context ctx,String title,int[] date,int[] time) {
        try {
            Intent intent = new Intent(ctx, MiBroadcastReceiver.class);
            intent.putExtra("titulo", title);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(ctx.ALARM_SERVICE);
            Calendar calendar = createCalendarObject(date, time);

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }catch (Exception e){
            showToastAlert(ctx,"ERROR EN LA NOTIFICACION:"+e.getMessage());
        }
    }
    static public void createNotification(Context ctx,String title,String DDMMYYYY,int[] time){
        int[] date = splitDate(DDMMYYYY);
        createNotification(ctx,title,date,time);
    }
    static public Calendar createCalendarObject(int[] splitedDate, int[] time){
        if(time==null){
            time=new int[]{0,0,0};
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set( splitedDate[2], splitedDate[1]-1, splitedDate[0],
                time[0],time[1],time[2]);
        return calendar;
    }
    public static int[] splitDate(String date){
        String[] subDate = date.split("/");
        return new int[]{
                Integer.parseInt(subDate[0]),
                Integer.parseInt(subDate[1]),
                Integer.parseInt(subDate[2])
        };
    }
}
