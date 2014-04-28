package edu.msu.cse.belmont.msuproximity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class Alarm extends BroadcastReceiver 
{    
	private double interval;
	
     @Override
     public void onReceive(Context context, Intent intent) 
     {   
         PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
         PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
         wl.acquire();      
         
         
         
         // TODO
         // We determine which zone we are in (TODO)
         // TODO
         
         String zoneWeAreIn="the Breslin Center";
         
         
         
         // Create an artificial back stack
         // This is so when we open the notification, back takes us to MainActivity
         TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
         
         
         if(zoneWeAreIn!=null)
         {
	         Intent newIntent = new Intent();                    
	         if(zoneWeAreIn=="Sparty")
	         {
	        	 // Set parent in artificial back stack
		         stackBuilder.addParentStack(SpartyInfoActivity.class);
		         
		         // Make it start up SpartyInfoActivity
	        	 newIntent.setClassName(context, "edu.msu.cse.belmont.msuproximity.SpartyInfoActivity");     	
	         }
	         else if(zoneWeAreIn=="Beaumont Tower")
	         {
		         stackBuilder.addParentStack(BeaumontInfoActivity.class);
	        	 newIntent.setClassName(context, "edu.msu.cse.belmont.msuproximity.BeaumontInfoActivity");
	         }
	         else if(zoneWeAreIn=="the Breslin Center")
	         {
		         stackBuilder.addParentStack(BreslinInfoActivity.class);
	        	 newIntent.setClassName(context, "edu.msu.cse.belmont.msuproximity.BreslinInfoActivity");
	         }
	         else 
	         {
	        	 // Default, if zoneWeAreIn is not one of the above
		         Toast.makeText(context, "Wrong zone string name", Toast.LENGTH_LONG).show();
		         newIntent.setClassName(context, "edu.msu.cse.belmont.msuproximity.MainActivity");
	         }
	         newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	         
	         
	         // Finish setting up artifical back stack
	         stackBuilder.addNextIntent(newIntent);
	         
	         // Create a pending intent so it's only launched if clicked
	         PendingIntent pendingIntent =
	         stackBuilder.getPendingIntent(
	         0,
	         PendingIntent.FLAG_UPDATE_CURRENT
	         );
	         	
	         // Create the android notification
	         NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
	         builder.setSmallIcon(R.drawable.ic_launcher);
	         builder.setContentTitle(context.getString(R.string.app_name));
	         builder.setContentText(context.getString(R.string.youre_at) + " "+zoneWeAreIn);
	         builder.setAutoCancel(true);
	         builder.setContentIntent(pendingIntent);
	         NotificationManager mNotificationManager =
	         (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	         
	         // Launch the android notification
	         mNotificationManager.notify(0, builder.build());
         }
         else
         {
        	 // THIS WONT BE DEAD CODE WHEN WE HAVE REAL CODE IN HERE
	         Toast.makeText(context, "Alarm went off but we are not in a zone", Toast.LENGTH_LONG).show();
         }
         
         
         wl.release();
     }

 public void SetAlarm(Context context){
	 
     AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
     Intent i = new Intent(context, Alarm.class);
     PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
     am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 10 * 1, pi); // Millisec * Second * Minute
 	}

 public void CancelAlarm(Context context)
 	{
     Intent intent = new Intent(context, Alarm.class);
     PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
     AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
     alarmManager.cancel(sender);
 	}

 public void set (double i){
	 interval = i;
 }
	 
	 
}