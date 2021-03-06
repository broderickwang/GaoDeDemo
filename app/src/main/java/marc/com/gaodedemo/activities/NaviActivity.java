package marc.com.gaodedemo.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.MyNaviListener;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviStaticInfo;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.autonavi.tbt.NaviStaticInfo;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.autonavi.wtbt.CarLocation;

import java.util.ArrayList;
import java.util.List;

import marc.com.gaodedemo.R;

public class NaviActivity extends AppCompatActivity implements AMapNaviListener, AMapNaviViewListener {
	List<NaviLatLng> sList,eList,mWayPointList;


	AMapNavi mAmapNavi;
	AMapNaviView mAmapNaviView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navi);
		mAmapNaviView = (AMapNaviView)findViewById(R.id.naviview);

		Intent i = getIntent();
		sList = new ArrayList<>();
		sList.add((NaviLatLng) i.getParcelableExtra("start"));
		eList = new ArrayList<>();
		eList.add((NaviLatLng) i.getParcelableExtra("end"));

		mAmapNavi = AMapNavi.getInstance(getApplicationContext());
		addListner();



	}

	@Override
	protected void onResume() {
		super.onResume();
		mAmapNaviView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mAmapNaviView.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAmapNaviView.onDestroy();
		//since 1.6.0 不再在naviview destroy的时候自动执行AMapNavi.stopNavi();请自行执行
		mAmapNavi.stopNavi();
		mAmapNavi.destroy();
	}

	private void addListner(){
		mAmapNavi.addAMapNaviListener(this);
		mAmapNaviView.setAMapNaviViewListener(this);
	}

	@Override
	public void hideCross() {

	}

	@Override
	public void onInitNaviFailure() {

	}

	@Override
	public void onInitNaviSuccess() {
		/**
		 * 方法:
		 *   int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute);
		 * 参数:
		 * @congestion 躲避拥堵
		 * @avoidhightspeed 不走高速
		 * @cost 避免收费
		 * @hightspeed 高速优先
		 * @multipleroute 多路径
		 *
		 * 说明:
		 *      以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
		 * 注意:
		 *      不走高速与高速优先不能同时为true
		 *      高速优先与避免收费不能同时为true
		 */
		int strategy=0;
		try {
			strategy = mAmapNavi.strategyConvert(true, false, false, false, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mAmapNavi.calculateDriveRoute(sList, eList, null, strategy);
	}

	@Override
	public void onStartNavi(int i) {

	}

	@Override
	public void onTrafficStatusUpdate() {

	}

	@Override
	public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

	}

	@Override
	public void onGetNavigationText(int i, String s) {

	}

	@Override
	public void onEndEmulatorNavi() {

	}

	@Override
	public void onArriveDestination() {

	}

	@Override
	public void onArriveDestination(NaviStaticInfo naviStaticInfo) {

	}

	@Override
	public void onArriveDestination(AMapNaviStaticInfo aMapNaviStaticInfo) {

	}

	@Override
	public void onCalculateRouteSuccess() {
		mAmapNavi.startNavi(NaviType.GPS);
	}

	@Override
	public void onCalculateRouteFailure(int i) {

	}

	@Override
	public void onReCalculateRouteForYaw() {

	}

	@Override
	public void onReCalculateRouteForTrafficJam() {

	}

	@Override
	public void onArrivedWayPoint(int i) {

	}

	@Override
	public void onGpsOpenStatus(boolean b) {

	}

	@Override
	public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

	}

	@Override
	public void onNaviInfoUpdate(NaviInfo naviInfo) {

	}

	@Override
	public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

	}

	@Override
	public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

	}

	@Override
	public void showCross(AMapNaviCross aMapNaviCross) {

	}

	@Override
	public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

	}

	@Override
	public void hideLaneInfo() {

	}

	@Override
	public void onCalculateMultipleRoutesSuccess(int[] ints) {

	}

	@Override
	public void notifyParallelRoad(int i) {

	}

	@Override
	public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

	}

	@Override
	public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

	}

	@Override
	public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

	}

	@Override
	public void onNaviSetting() {

	}

	@Override
	public void onNaviCancel() {

	}

	@Override
	public boolean onNaviBackClick() {
		return false;
	}

	@Override
	public void onNaviMapMode(int i) {

	}

	@Override
	public void onNaviTurnClick() {

	}

	@Override
	public void onNextRoadClick() {

	}

	@Override
	public void onScanViewButtonClick() {

	}

	@Override
	public void onLockMap(boolean b) {

	}

	@Override
	public void onNaviViewLoaded() {

	}
}
