package marc.com.gaodedemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import marc.com.gaodedemo.service.LocateService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Main extends AppCompatActivity implements LocationSource
		, NavigationView.OnNavigationItemSelectedListener {

	MapView mMapView = null;
	static AMap mAmap;
	static MarkerOptions mMarkerOptions;
	UiSettings settings;
	@Bind(R.id.search)
	EditText search;
	private LocationManager locationManager;
	private String locationProvider;

	//声明定位回调监听器
	public static AMapLocationListener mLocationListener = new AMapLocationListener() {
		@Override
		public void onLocationChanged(AMapLocation aMapLocation) {
			if (aMapLocation != null) {
				StringBuilder stringBuilder = new StringBuilder();
				if (aMapLocation.getErrorCode() == 0) {
					//可在其中解析amapLocation获取相应内容。
					stringBuilder.append("定位结果来源" + aMapLocation.getLocationType());//获取当前定位结果来源，如网络定位结果，详见定位类型表
					stringBuilder.append(" 纬度" + aMapLocation.getLatitude());//获取纬度
					stringBuilder.append(",经度" + aMapLocation.getLongitude());//获取经度
					aMapLocation.getAccuracy();//获取精度信息
					aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
					aMapLocation.getCountry();//国家信息
					aMapLocation.getProvince();//省信息
					aMapLocation.getCity();//城市信息
					aMapLocation.getDistrict();//城区信息
					stringBuilder.append("-街道:" + aMapLocation.getStreet());//街道信息
					aMapLocation.getStreetNum();//街道门牌号信息
					aMapLocation.getCityCode();//城市编码
					aMapLocation.getAdCode();//地区编码
					aMapLocation.getAoiName();//获取当前定位点的AOI信息
					//获取定位时间 aMapLocation.getTime()
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date = new Date(System.currentTimeMillis());
					stringBuilder.append(" time = " + df.format(date));
					Log.i("TAG", "onLocationChanged: " + stringBuilder.toString());

					LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());

					mAmap.clear();

					final Marker marker = mAmap.addMarker(new MarkerOptions().
							position(latLng).
							title("user").
							snippet(stringBuilder.toString()));

					mAmap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
							latLng,//新的中心点坐标
							18, //新的缩放级别
							0, //俯仰角0°~45°（垂直与地图时为0）
							0  ////偏航角 0~360° (正北方为0)
					)));

				} else {
					//定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
					Log.e("AmapError", "location Error, ErrCode:"
							+ aMapLocation.getErrorCode() + ", errInfo:"
							+ aMapLocation.getErrorInfo());
				}
				MyApplication.mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
			}
		}
	};
	@Bind(R.id.toolbar)
	Toolbar toolbar;
	@Bind(R.id.draw_layout)
	DrawerLayout drawLayout;

	ActionBarDrawerToggle mDrawerToggle;
	@Bind(R.id.navigation)
	NavigationView navigation;
	@Bind(R.id.pos)
	FloatingActionButton pos;
	@Bind(R.id.route)
	FloatingActionButton route;
	Intent locateService;

	////
	GeocodeSearch geocodeSearch;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		initToolbar();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			//透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}

		//获取地图控件引用
		mMapView = (MapView) findViewById(R.id.mapview);
		//在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
		mMapView.onCreate(savedInstanceState);

		initMap();

		initPermission();

		initData();

		addListner();

	}

	private void loadOkhttp() {
		OkHttpClient client = new OkHttpClient();

		HttpUrl.Builder urlBuilder =
				HttpUrl.parse("http://192.168.2.105:8080/ServletDemo/servlet/GDServlet")
						.newBuilder();
		urlBuilder.addQueryParameter("latitude", "1.0");
		urlBuilder.addQueryParameter("longitude", "android");
		String url = urlBuilder.build().toString();

		Request request = new Request.Builder()
				.url(url)
				.build();

		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				Log.e("TAG", "onFailure: ", e);
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				Log.i("TAG", "onResponse: " + response.toString());
			}
		});
	}

	private void loadD() {
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("http://192.168.9.45")
				.addConverterFactory(GsonConverterFactory.create())
				.build();


		/*call.enqueue(new Callback<NewsBean>() {
			@Override
			public void onResponse(Call<NewsBean> call, Response<NewsBean> response) {

				NewsBean body = response.body();
				NewsBean.ResultBean resultBean = body.getResult();
				adaptor.setData(resultBean.getData());
				adaptor.notifyDataSetChanged();
				dlg.dismiss();
				if(newsRef != null)
					newsRef.setRefreshing(false);
			}

			@Override
			public void onFailure(Call<NewsBean> call, Throwable t) {

			}
		});*/
	}


	private void initMap() {

		mAmap = mMapView.getMap();
		mAmap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				return false;
			}
		});
//		mAmap.setMapType(AMap.MAP_TYPE_SATELLITE); //卫星地图

		settings = mAmap.getUiSettings();
		//设置放大缩小按钮不现实
		settings.setZoomControlsEnabled(false);
	}

	private void initPermission() {
		ArrayList<String> permissions = new ArrayList<>();
		//android 6.0之后申请权限
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {
			//申请WRITE_EXTERNAL_STORAGE权限
			permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
		}
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {
			//申请WRITE_EXTERNAL_STORAGE权限
			permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
		}

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {
			//申请WRITE_EXTERNAL_STORAGE权限
			permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		}
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {
			//申请WRITE_EXTERNAL_STORAGE权限
			permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
		}
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
				!= PackageManager.PERMISSION_GRANTED) {
			//申请WRITE_EXTERNAL_STORAGE权限
			permissions.add(Manifest.permission.READ_PHONE_STATE);
		}
		String[] arr_permison;
		if (permissions.size() != 0) {
			arr_permison = new String[permissions.size()];
			for (int i = 0; i < permissions.size(); i++) {
				arr_permison[i] = permissions.get(i);
			}
			ActivityCompat.requestPermissions(this,
					arr_permison, 0);//自定义的code
		} else {
			locateService = new Intent(Main.this, LocateService.class);
			startService(locateService);
		}
	}

	private void initToolbar() {
		toolbar.setTitleTextColor(Color.WHITE);
		toolbar.inflateMenu(R.menu.app_menu);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mDrawerToggle = new ActionBarDrawerToggle(this, drawLayout, toolbar,
				R.string.drawer_open, R.string.drawer_close);
		mDrawerToggle.syncState();
		drawLayout.setDrawerListener(mDrawerToggle);
		//navigationicon需要最后设置,不然无效
//		toolbar.setOnMenuItemClickListener();
		toolbar.setNavigationIcon(R.drawable.menu41);

		navigation.setNavigationItemSelectedListener(this);

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 0) {
			locateService = new Intent(Main.this, LocateService.class);
			startService(locateService);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();

		stopService(locateService);

		System.exit(0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		//在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
		mMapView.onSaveInstanceState(outState);
	}

	@Override
	public void activate(OnLocationChangedListener onLocationChangedListener) {
		MyApplication.mLocationClient.startLocation();
	}

	@Override
	public void deactivate() {

	}

	@OnClick({R.id.pos, R.id.route})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.pos:
				MyApplication.mLocationClient.startLocation();
				break;
			case R.id.route:
				break;
		}
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		item.setChecked(true);
		drawLayout.closeDrawers();
		switch (item.getItemId()) {
			case R.id.home:
				break;
			case R.id.signal:
				break;
		}
		return false;
	}

	private void getLocation() {
		//获取地理位置管理器
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//获取所有可用的位置提供器
		List<String> providers = locationManager.getProviders(true);
		if (providers.contains(LocationManager.GPS_PROVIDER)) {
			//如果是GPS
			locationProvider = LocationManager.GPS_PROVIDER;
		} else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
			//如果是Network
			locationProvider = LocationManager.NETWORK_PROVIDER;
		} else {
			Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
			return;
		}
		//获取Location
		checkPermission("android.permission.INTERNET", 0, 0);
		Location location = locationManager.getLastKnownLocation(locationProvider);
		if (location != null) {
			//不为空,显示地理位置经纬度
			showLocation(location);
		}
		//监视地理位置变化
		locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
	}

	private void showLocation(Location location) {
		String locationStr = "维度：" + location.getLatitude() + "\n"
				+ "经度：" + location.getLongitude();
		Log.i("TAG", "showLocation: " + locationStr);

		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

		mAmap.clear();

		final Marker marker = mAmap.addMarker(new MarkerOptions().
				position(latLng).
				title("user").
				snippet(locationStr));

		mAmap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
				latLng,//新的中心点坐标
				18, //新的缩放级别
				0, //俯仰角0°~45°（垂直与地图时为0）
				0  ////偏航角 0~360° (正北方为0)
		)));

	}

	LocationListener locationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle arg2) {

		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onLocationChanged(Location location) {
			//如果位置发生变化,重新显示
			showLocation(location);

		}
	};

	private void initData() {
		geocodeSearch = new GeocodeSearch(this);

	}

	private void addListner() {
		geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
			@Override
			public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

			}

			@Override
			public void onGeocodeSearched(GeocodeResult result, int rCode) {
				if (rCode == 1000) {
					if (result != null && result.getGeocodeAddressList() != null
							&& result.getGeocodeAddressList().size() > 0) {
						GeocodeAddress address = result.getGeocodeAddressList().get(0);

						LatLng lng = new LatLng(address.getLatLonPoint().getLatitude(),
								address.getLatLonPoint().getLongitude());

						/*mAmap.animateCamera(CameraUpdateFactory.newLatLngZoom(lng, 15));
						geoMarker.setPosition(AMapUtil.convertToLatLng(address.getLatLonPoint()));*/

						MarkerOptions markerOption = new MarkerOptions();
						markerOption.position(lng);
						markerOption.title("search").snippet("test search!");

						markerOption.draggable(true);
						markerOption.icon(

								BitmapDescriptorFactory.fromBitmap(BitmapFactory
										.decodeResource(getResources(),
												R.drawable.emarker)));
						// 将Marker设置为贴地显示，可以双指下拉看效果
						markerOption.setFlat(true);
						mAmap.addMarker(markerOption);
						mAmap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
								lng,//新的中心点坐标
								18, //新的缩放级别
								0, //俯仰角0°~45°（垂直与地图时为0）
								0  ////偏航角 0~360° (正北方为0)
						)));

						String addressName = "经纬度值:" + address.getLatLonPoint() + "\n位置描述:"
								+ address.getFormatAddress();
						Toast.makeText(Main.this, addressName, Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(Main.this, "nothings", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(Main.this, "errorcode:" + rCode, Toast.LENGTH_SHORT).show();
				}
			}
		});
		search.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(
									Main.this
											.getCurrentFocus()
											.getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
					searchAddr(search.getText().toString(), "qingdao");
				}
				return false;
			}
		});
	}

	private void searchAddr(String name, String city) {
		GeocodeQuery query = new GeocodeQuery(name, city);
		geocodeSearch.getFromLocationNameAsyn(query);
	}
}
