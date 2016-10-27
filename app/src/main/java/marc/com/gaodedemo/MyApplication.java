package marc.com.gaodedemo;

import android.icu.text.SimpleDateFormat;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

//import org.xutils.x;

import java.util.Date;

/**
 * Created by Broderick on 2016/10/25.
 */

public class MyApplication extends MultiDexApplication {
	//声明AMapLocationClient类对象
	public static AMapLocationClient mLocationClient = null;

	//声明AMapLocationClientOption对象
	public AMapLocationClientOption mLocationOption = null;

	@Override
	public void onCreate() {
		super.onCreate();

		/*x.Ext.init(this);
		x.Ext.setDebug(true);*/

		//初始化定位
		mLocationClient = new AMapLocationClient(getApplicationContext());
		//设置定位回调监听
		mLocationClient.setLocationListener(Main.mLocationListener);

		//初始化AMapLocationClientOption对象
		mLocationOption = new AMapLocationClientOption();

		//设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
		mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//获取一次定位结果：
//该方法默认为false。
		mLocationOption.setOnceLocation(true);

//获取最近3s内精度最高的一次定位结果：
//设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
		mLocationOption.setOnceLocationLatest(true);

		//设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
		mLocationOption.setInterval(1000);
		//设置是否返回地址信息（默认返回地址信息）
		mLocationOption.setNeedAddress(true);
		//设置是否强制刷新WIFI，默认为true，强制刷新。
		mLocationOption.setWifiActiveScan(false);
		//设置是否允许模拟位置,默认为false，不允许模拟位置
		mLocationOption.setMockEnable(false);
		//给定位客户端对象设置定位参数
		mLocationClient.setLocationOption(mLocationOption);
	}
}
