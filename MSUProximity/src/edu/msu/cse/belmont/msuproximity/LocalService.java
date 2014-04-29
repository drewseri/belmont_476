package edu.msu.cse.belmont.msuproximity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class LocalService extends Service {
    private NotificationManager mNM;

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = R.string.local_service_started;
    
    //Create the alarm
    private Alarm alarm = new Alarm();
    
    private LocationManager locationManager = null;
    private ActiveListener activeListener = new ActiveListener();
    
    private double latitude = 0;
    private double longitude = 0;
    private boolean valid = false;
    
    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        LocalService getService() {
            return LocalService.this;
        }
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        
        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
        
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        
        registerListeners();
        
        
        // Set alarm
        alarm.SetAlarm(LocalService.this);
        
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }
    
    // *** I don't know if we need this service function or not for the alarm
    // *** I added it but its commented out for now 
//    public void onStart(Context context,Intent intent, int startId)
//    {
//        alarm.SetAlarm(context);
//    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);
        
        unregisterListeners();
        
        alarm.CancelAlarm(LocalService.this);

        // Tell the user we stopped.
        Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.local_service_started);

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.icon, text,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, ServiceActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.local_service_label),
                       text, contentIntent);

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }
    
    private void registerListeners() {
        unregisterListeners();
        
        // Create a Criteria object
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(false);
        
        String bestAvailable = locationManager.getBestProvider(criteria, true);
        
        if(bestAvailable != null) {
            locationManager.requestLocationUpdates(bestAvailable, 500, 1, activeListener);
            Location location = locationManager.getLastKnownLocation(bestAvailable);
            onLocation(location);
        }
    }
    
    private void unregisterListeners() {
        locationManager.removeUpdates(activeListener);
    }
    

    public void checkIfWeShouldSendANotification() {

        //
    	// Determine which zone we are in
    	//
    	
        String zoneWeAreIn=null;
        
        double spartyLat = 42.731138;
        double spartyLong = -84.487508;
       
        double beaumontLat = 42.732829;
        double beaumontLong = -84.482467;
        
        double bresLat = 42.7284;
        double bresLong = -84.492033;

    	float[] spartyDist = new float[1];
    	Location.distanceBetween(latitude,longitude, spartyLat, spartyLong, spartyDist);

    	float[] beaumontDist = new float[1];
    	Location.distanceBetween(latitude,longitude, beaumontLat, beaumontLong, beaumontDist);
    	
    	float[] bresDist = new float[1];
    	Location.distanceBetween(latitude,longitude, bresLat, bresLong, bresDist);
    	
    	if(spartyDist[0]<20) zoneWeAreIn = "Sparty";
    	else if (beaumontDist[0]<20) zoneWeAreIn = "Beaumont Tower";
    	else if (bresDist[0]<20) zoneWeAreIn = "the Breslin Center";
        
    	
    	
        // Create an artificial back stack
        // This is so when we open the notification, back takes us to MainActivity
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        
        
        if(zoneWeAreIn!=null)
        {
	         Intent newIntent = new Intent();                    
	         if(zoneWeAreIn=="Sparty")
	         {
	        	 // Set parent in artificial back stack
		         stackBuilder.addParentStack(SpartyInfoActivity.class);
		         
		         // Make it start up SpartyInfoActivity
	        	 newIntent.setClassName(this, "edu.msu.cse.belmont.msuproximity.SpartyInfoActivity");     	
	         }
	         else if(zoneWeAreIn=="Beaumont Tower")
	         {
		         stackBuilder.addParentStack(BeaumontInfoActivity.class);
	        	 newIntent.setClassName(this, "edu.msu.cse.belmont.msuproximity.BeaumontInfoActivity");
	         }
	         else if(zoneWeAreIn=="the Breslin Center")
	         {
		         stackBuilder.addParentStack(BreslinInfoActivity.class);
	        	 newIntent.setClassName(this, "edu.msu.cse.belmont.msuproximity.BreslinInfoActivity");
	         }
	         else 
	         {
	        	 // Default, if zoneWeAreIn is not one of the above
		         Toast.makeText(this, "Wrong zone string name", Toast.LENGTH_LONG).show();
		         newIntent.setClassName(this, "edu.msu.cse.belmont.msuproximity.MainActivity");
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
	         NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
	         builder.setSmallIcon(R.drawable.ic_launcher);
	         builder.setContentTitle(this.getString(R.string.app_name));
	         builder.setContentText(this.getString(R.string.youre_at) + " "+zoneWeAreIn);
	         builder.setAutoCancel(true);
	         builder.setContentIntent(pendingIntent);
	         NotificationManager mNotificationManager =
	         (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
	         
	         // Launch the android notification
	         mNotificationManager.notify(0, builder.build());
        }
        else
        {
        	Log.i("not in any zone. "+spartyDist[0]+" "+beaumontDist[0]+" "+bresDist[0], "Not in any zone right now.");
	         //Toast.makeText(this, "Alarm went off but we are not in a zone", Toast.LENGTH_LONG).show();
        }
    }
    
    private void onLocation(Location location) {
        if(location == null) {
            return;
        }
        
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        valid = true;
       
        
        checkIfWeShouldSendANotification();
        //alarm.set(latitude);

        Log.i("LAT", ""+latitude);

    }
    
    private class ActiveListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
            onLocation(location);
			
		}

		@Override
		public void onProviderDisabled(String provider) {
        	registerListeners();            
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

        
    };
    
}