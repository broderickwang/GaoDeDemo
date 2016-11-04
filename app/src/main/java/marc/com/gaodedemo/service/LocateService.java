package marc.com.gaodedemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.amap.api.maps.model.LatLng;

import java.io.IOException;
import java.util.TimerTask;

import marc.com.gaodedemo.MyApplication;
import marc.com.gaodedemo.util.APPData;
import marc.com.gaodedemo.util.MapUtil;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LocateService extends Service {
	public LocateService() {
	}

	Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {

			MyApplication.mLocationClient.startLocation();

			handler.postDelayed(this, 1000*20/**10*/);
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
	public static void loadOkhttp(LatLng latLng,String str_time) {

		double x = latLng.longitude;
		double y = latLng.latitude;

//		MapUtil.MillierConvertion(latLng.latitude,latLng.longitude);
//		CoordinateConversion cc = new CoordinateConversion();
//		double[] ds = cc.latLon2UTM_Doubles(latLng.latitude,latLng.longitude);
		String[] webMokt = MapUtil.lonLat2WebMercator(latLng.longitude,latLng.latitude);

//		Log.i("TAG", "loadOkhttp: webmocarte 1="+webMokt[0]+","+webMokt[1]);



		String URL = "http://219.146.254.74:7007/servlet/GDLngServlet";
		//"http://219.146.254.74:7001/servlet/GDLngServlet";
		//"http://192.168.9.68:7001/servlet/GDLngServlet";
		//"http://192.168.2.105:8080/ServletDemo/servlet/GDServlet"
		OkHttpClient client = new OkHttpClient();



		HttpUrl.Builder urlBuilder = HttpUrl.parse(URL).newBuilder();
		/*urlBuilder.addQueryParameter("latitude", String.valueOf(x));
		urlBuilder.addQueryParameter("longitude", String.valueOf(y));*/
		/*urlBuilder.addQueryParameter("latitude", String.valueOf(latLng.latitude));
		urlBuilder.addQueryParameter("longitude", String.valueOf(latLng.longitude));*/
		/*urlBuilder.addQueryParameter("latitude", String.valueOf(ds[0]));
		urlBuilder.addQueryParameter("longitude", String.valueOf(ds[1]));*/
		urlBuilder.addQueryParameter("latitude", String.valueOf(webMokt[0]));
		urlBuilder.addQueryParameter("longitude", String.valueOf(webMokt[1]));
		urlBuilder.addQueryParameter("time",str_time);
		urlBuilder.addQueryParameter("user", APPData.userName);
		String url = urlBuilder.build().toString();

		Request request = new Request.Builder()
				.url(url)
				.build();

		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(okhttp3.Call call, IOException e) {
				Log.e("TAG", " loadOkhttp= onFailure: ", e);
			}

			@Override
			public void onResponse(okhttp3.Call call, Response response) throws IOException {
				Log.i("TAG", "loadOkhttp= onResponse: " + response.toString());
			}
		});
	}
}
