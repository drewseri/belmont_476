package edu.msu.cse.belmont.msuproximity;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ServiceActivity extends Activity {

	private Activity act = this;
	private TextView status;
	private static Intent serviceIntent;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service);
		status = (TextView)this.findViewById(R.id.textStatus);
		if(!isServiceRunning()) {
			status.setText(R.string.regno);
		}
		else {
			status.setText(R.string.regyes);
		}
	}
	
	private boolean isServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (LocalService.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	private void StartLocalService() {
		Intent i= new Intent(act, LocalService.class);
		serviceIntent = i;
		i.putExtra("KEY", "LocalService");
		act.startService(i);
	}
	
	private void StopLocalService() {
		if(serviceIntent != null) {
			act.stopService(serviceIntent);
		}
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
	
	public void onStartService(View view) {
		StartLocalService();
		status.setText(R.string.regyes);
	}
	
	public void onStopService(View view) {
		StopLocalService();
		status.setText(R.string.regno);
	}
	
	public void onBack(View view) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

}
