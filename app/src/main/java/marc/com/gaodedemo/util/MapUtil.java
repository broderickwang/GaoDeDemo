package marc.com.gaodedemo.util;

import android.util.Log;

import com.amap.api.maps.model.LatLng;

import java.math.BigDecimal;

/**
 * Created by Broderick on 2016/11/2.
 */

public class MapUtil {
	public static double PI = 3.14159265358979324;

	public static double aa = 6378245.0;
	public static double ee = 0.00669342162296594323;

	//web墨卡托投影坐标，xy的偏移量
	public static double ver_x = 566.255;
	public static double ver_y = 20.463;

	public static LatLng gcj_decrypt_exact(LatLng latLng){
		double initDelta = 0.1;
		double threshold = 0.000000001;

		double dLat = initDelta;
		double dLon = initDelta;

		double mLat = latLng.latitude - dLat;
		double mLon = latLng.longitude - dLon;

		double pLat = latLng.latitude + dLat;
		double pLon = latLng.longitude + dLon;

		double wgsLat = 0,wgsLon = 0 ,i = 0;


		while(true){
			wgsLat = (mLat+pLat)/2;
			wgsLon = (mLon+pLon)/2;

			LatLng temp = gcj_encrypt(wgsLat,wgsLon);

			dLat = temp.latitude - latLng.latitude;
			dLon = temp.longitude - latLng.longitude;

			if((Math.abs(dLat)<threshold) && (Math.abs(dLon)<threshold))
				break;
			if(dLat > 0)
				pLat = wgsLat;
			else
				mLat = wgsLat;
			if(dLon > 0)
				pLon = wgsLon;
			else
				mLon = wgsLon;
			if(++i > 10000)
				break;
		}

		LatLng gcj_ext_lng = new LatLng(wgsLat,wgsLon);
		return gcj_ext_lng;

	}

	public static LatLng gcj_encrypt (double wgsLat,double wgsLon) {
		LatLng gcj_en_lat ;//= new LatLng();
		if (outOfChina(wgsLat, wgsLon)){
			gcj_en_lat = new LatLng(wgsLat,wgsLon);
			return gcj_en_lat;
		}
//			return {'lat': wgsLat, 'lon': wgsLon};

		LatLng d = delta(wgsLat, wgsLon);
		return d;
//		return {'lat' : wgsLat + d.lat,'lon' : wgsLon + d.lon};
	}
	public static LatLng delta (double lat,double lon) {
		// Krasovsky 1940
		//
		// a = 6378245.0, 1/f = 298.3
		// b = a * (1 - f)
		// ee = (a^2 - b^2) / a^2;
		double a = 6378245.0; //  a: 卫星椭球坐标投影到平面地图坐标系的投影因子。
		double ee = 0.00669342162296594323; //  ee: 椭球的偏心率。
		double dLat = transformLat(lon - 105.0, lat - 35.0);
		double dLon = transformLon(lon - 105.0, lat - 35.0);
		double radLat = lat / 180.0 * PI;
		double magic = Math.sin(radLat);
		magic = 1 - ee * magic * magic;
		double sqrtMagic = Math.sqrt(magic);
		dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * PI);
		dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * PI);
//		return {'lat': dLat, 'lon': dLon};

		LatLng delta_lat = new LatLng(dLat,dLon);
		Log.i("TAG", "delta: "+delta_lat.latitude+","+delta_lat.longitude);
		return delta_lat;
	}
	public static boolean outOfChina (double lat, double lon) {
		if (lon < 72.004 || lon > 137.8347)
			return true;
		if (lat < 0.8293 || lat > 55.8271)
			return true;
		return false;
	}
	public static double transformLat (double x,double y) {
		double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
		ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(y * PI) + 40.0 * Math.sin(y / 3.0 * PI)) * 2.0 / 3.0;
		ret += (160.0 * Math.sin(y / 12.0 * PI) + 320 * Math.sin(y * PI / 30.0)) * 2.0 / 3.0;
		Log.i("TAG", "transform lat: "+ret);
		return ret;
	}
	public static double transformLon (double x,double y) {
		double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
		ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(x * PI) + 40.0 * Math.sin(x / 3.0 * PI)) * 2.0 / 3.0;
		ret += (150.0 * Math.sin(x / 12.0 * PI) + 300.0 * Math.sin(x / 30.0 * PI)) * 2.0 / 3.0;
		Log.i("TAG", "transform lon: "+ret);
		return ret;
	}
	public static void transform(double wgLat, double wgLon){
		double mgLat,mgLon;
		if (outOfChina(wgLat, wgLon))
		{
			mgLat = wgLat;
			mgLon = wgLon;
			return;
		}
		double dLat = transformLat(wgLon - 105.0, wgLat - 35.0);
		double dLon = transformLon(wgLon - 105.0, wgLat - 35.0);
		double radLat = wgLat / 180.0 * PI;
		double magic = Math.sin(radLat);
		magic = 1 - ee * magic * magic;
		double sqrtMagic = Math.sqrt(magic);
		dLat = (dLat * 180.0) / ((aa * (1 - ee)) / (magic * sqrtMagic) * PI);
		dLon = (dLon * 180.0) / (aa / sqrtMagic * Math.cos(radLat) * PI);
		mgLat = wgLat + dLat;
		mgLon = wgLon + dLon;

		Log.i("TAG", "transform2: "+mgLat+","+mgLon);

	}

	public static double[] MillierConvertion(double lat, double lon)
	{
		double L = 6381372 * Math.PI * 2;//地球周长
		double W=L;// 平面展开后，x轴等于周长
		double H=L/2;// y轴约等于周长一半
		double mill=2.3;// 米勒投影中的一个常数，范围大约在正负2.3之间
		double x = lon * Math.PI / 180;// 将经度从度数转换为弧度
		double y = lat * Math.PI / 180;// 将纬度从度数转换为弧度
		y=1.25 * Math.log( Math.tan( 0.25 * Math.PI + 0.4 * y ) );// 米勒投影的转换
		// 弧度转为实际距离
		x = ( W / 2 ) + ( W / (2 * Math.PI) ) * x;
		y = ( H / 2 ) - ( H / ( 2 * mill ) ) * y;
		double[] result=new double[2];
		result[0]=x;
		result[1]=y;
		Log.i("TAG", "MillierConvertion: "+x+","+y);
		return result;
	}

	public static String[] lonLat2WebMercator(double x,  double y)
	{
		String[] str_back = new String[2];
		double x1 = x/180*20037508.34-ver_x;
		double y1 = Math.log(Math.tan((90+y)*Math.PI/360))/(Math.PI/180);
		y1 = y1 *20037508.34/180-ver_y;
		BigDecimal bd = new BigDecimal(x1);
		String str = bd.toPlainString();

		str_back[0] = str;
		str_back[1] = String.valueOf(y1);


		return str_back;
	}

}
