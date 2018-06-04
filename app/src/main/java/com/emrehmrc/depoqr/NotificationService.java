package com.emrehmrc.depoqr;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.emrehmrc.depoqr.connection.ConnectionClass;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {
    final static int PERIOD = 15000;
    final static int DELAY = 0;
    Context context;
    Notification notification;
    Timer timer;
    ConnectionClass connectionClass;
    int many;
    int myInt;
    SharedPreferences preferences, sharedpreferences;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        context = getApplicationContext();
        connectionClass = new ConnectionClass();
        //Toast.makeText(this, "Servis Çalıştı", Toast.LENGTH_SHORT).show();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
               new AnyDiffrences().execute("");
            }
        }, DELAY, PERIOD);
    }

    private void sendNotification() {
        long when = System.currentTimeMillis();//notificationın ne zaman gösterileceği
        String baslik = "Bildirim Deneme";//notification başlık
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, AnaSayfa.class);
        PendingIntent pending = PendingIntent.getActivity(context, 0, intent, 0);//Notificationa tıklanınca açılacak activityi belirliyoruz


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            notification = new Notification(R.mipmap.ic_launcher, "Yeni Bildirim", when);
            try {
                Method deprecatedMethod = notification.getClass().getMethod("setLatestEventInfo", Context.class, CharSequence.class, CharSequence.class, PendingIntent.class);
                deprecatedMethod.invoke(notification, context,baslik,"Üye Eklendi", "cevap",
                        pending);
            } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {

            }
        } else {
            // Use new API
            notification = new Notification(R.mipmap.ic_launcher, "Yeni Bildirim", when);
            Notification.Builder builder = new Notification.Builder(context)
                    .setContentIntent(pending)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(baslik)
                    .setContentText("Üye Eklendi");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                notification = builder.build();
            }
        }


        notification.flags |= Notification.FLAG_AUTO_CANCEL;//notificationa tıklanınca notificationın otomatik silinmesi için
        notification.defaults |= Notification.DEFAULT_SOUND;//notification geldiğinde bildirim sesi çalması için
        notification.defaults |= Notification.DEFAULT_VIBRATE;//notification geldiğinde bildirim titremesi için
        nm.notify(0, notification);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class AnyDiffrences extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
          many=0;
        }

        protected void onPostExecute(String r) {
             myInt = preferences.getInt("first", -1);
            if(myInt<many){
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("first", many);
                editor.commit();
                sendNotification();

            }


        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN();
                String query = "select * from Notification";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                while (rs.next()) {
                    many++;
                }
            } catch (Exception ex) {
            }
            return "";

        }
    }
}

