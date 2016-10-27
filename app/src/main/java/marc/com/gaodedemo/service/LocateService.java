package marc.com.gaodedemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import java.util.TimerTask;

import marc.com.gaodedemo.MyApplication;

public class LocateService extends Service {
	public LocateService() {
	}

	Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {

			MyApplication.mLocationClient.startLocation();

			handler.postDelayed(this, 1000*60*10);
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handler.post(runnable);

		return super.onStartCommand(intent,flags,startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		handler.removeCallbacks(runnable);
	}
}
