package com.hz.android_amap.activity;

import java.util.List;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.AMap.OnMapClickListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.model.NaviPara;
import com.amap.api.maps2d.overlay.PoiOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.hz.android_amap.R;
import com.hz.android_amap.adapter.ListViewAdapter;
import com.hz.android_amap.utils.AMapUtil;
import com.hz.android_amap.utils.ToastUtil;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity implements
	OnGeocodeSearchListener, OnMapClickListener, OnClickListener,
	LocationSource, AMapLocationListener {

	private AMap aMap;
	private MapView mapView;
	private OnLocationChangedListener mListener;
	private AMapLocationClient mlocationClient;
	private AMapLocationClientOption mLocationOption;
	private ProgressDialog progDialog = null;
	private String cityName;
	private PoiResult poiResult;
	private PoiSearch.Query query;
	private PoiSearch poiSearch;
	private GeocodeSearch geocoderSearch;
	private LatLonPoint latLonPoint;
	private MyLocationStyle myLocationStyle;
	private TextView attendAddress;
	private LatLonPoint attendLatLonPoint;
	private boolean flag = true;
	private boolean firstOpenMap = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		mapView = (MapView) findViewById(R.id.attend_map);
		mapView.onCreate(savedInstanceState);

		init();
	}

	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			setLocationStyle();
			setUpMap();
		}
	}

	private void setLocationStyle() {
		if (myLocationStyle == null)
			myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_marker));
		myLocationStyle.strokeColor(Color.BLACK);
		myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));
		myLocationStyle.strokeWidth(1.0f);
	}

	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
		aMap.setMyLocationStyle(myLocationStyle);
		aMap.setLocationSource(this);
		aMap.getUiSettings().setMyLocationButtonEnabled(true);
		aMap.setMyLocationEnabled(true);

		aMap.setOnMapClickListener(this);
		aMap.setOnMarkerClickListener(new MarkerClickListener());// 添加点击marker监听事件
		aMap.setInfoWindowAdapter(new MapInfoWindowAdapter());// 添加显示infowindow监听事件

		attendAddress = (TextView) findViewById(R.id.attend_address);

		// 地理位置搜索
		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);

		// 位置选择start
		TextView textView = (TextView) findViewById(R.id.address_add);
		textView.setOnClickListener(this);

	}

	/**
	 * 
	 * 点击事件
	 */
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.address_add:
			break;
		default:
			break;
		}
	}

	/**
	 * 地图点击事件
	 */
	@Override
	public void onMapClick(LatLng latLng) {
		if (latLng != null) {
			aMap.clear();
			aMap.setMyLocationStyle(myLocationStyle);
			aMap.getUiSettings().setZoomPosition(
					AMapOptions.ZOOM_POSITION_RIGHT_BUTTOM);
			ListView listView = (ListView) findViewById(R.id.address_list);
			listView.setVisibility(View.GONE);
			latLonPoint = AMapUtil.convertToLatLonPoint(latLng);
			getAddress(latLonPoint);
		}
	}

	/**
	 * 响应逆地理编码
	 * 
	 * @param latLonPoint
	 */
	public void getAddress(LatLonPoint latLonPoint) {
		showProgressDialog("正在获取地址");
		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
				GeocodeSearch.AMAP);
		geocoderSearch.getFromLocationAsyn(query);
	}

	/**
	 * 逆地理编码查询回调
	 */
	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getRegeocodeAddress() != null
					&& result.getRegeocodeAddress().getFormatAddress() != null) {
				String addressName = result.getRegeocodeAddress()
						.getFormatAddress();
				attendAddress.setText(addressName);
				doSearchQuery(addressName);
				aMap.addMarker(new MarkerOptions()
						.anchor(0.5f, 0.5f)
						.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_RED))
						.position(AMapUtil.convertToLatLng(latLonPoint))
						.snippet(addressName));
			} else {
				ToastUtil.show(this, getString(R.string.no_result));
			}
		} else if (rCode == 27) {
			ToastUtil.show(this, getString(R.string.error_network));
		} else if (rCode == 32) {
			ToastUtil.show(this, getString(R.string.error_key));
		} else {
			ToastUtil.show(this, getString(R.string.error_other));
		}
	}

	/**
	 * 地理编码查询回调
	 */
	@Override
	public void onGeocodeSearched(GeocodeResult arg0, int arg1) {

	}

	/**
	 * 地图图层点击事件类
	 * 
	 * @author Smile
	 * 
	 */
	private class MarkerClickListener implements OnMarkerClickListener {
		@Override
		public boolean onMarkerClick(Marker marker) {
			marker.showInfoWindow();
			return false;
		}
	}

	/**
	 * 地图弹出框事件类
	 * 
	 * @author Smile
	 * 
	 */
	private class MapInfoWindowAdapter implements InfoWindowAdapter {

		@Override
		public View getInfoContents(Marker arg0) {
			return null;
		}

		@Override
		public View getInfoWindow(final Marker marker) {
			View view = getLayoutInflater().inflate(
					R.layout.poikeywordsearch_uri, null);
			TextView snippet = (TextView) view.findViewById(R.id.snippet);
			snippet.setText(marker.getSnippet());
			ImageButton button = (ImageButton) view
					.findViewById(R.id.start_amap_app);

			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startAMapNavi(marker);
				}
			});
			return view;
		}
	}

	/**
	 * 启用高德APP导航
	 * 
	 * @param marker
	 */
	@SuppressWarnings("deprecation")
	public void startAMapNavi(Marker marker) {
		NaviPara naviPara = new NaviPara();
		naviPara.setTargetPoint(marker.getPosition());
		naviPara.setNaviStyle(NaviPara.DRIVING_AVOID_CONGESTION);
		try {
			AMapUtils.openAMapNavi(naviPara, getApplicationContext());
		} catch (com.amap.api.maps2d.AMapException e) {
			AMapUtils.getLatestAMapApp(getApplicationContext());
		}
	}

	/**
	 * 开始进行poi搜索
	 */
	protected void doSearchQuery(String keyWord) {
		showProgressDialog("正在搜索:\n" + keyWord);
		query = new PoiSearch.Query(keyWord, "", cityName);
		query.setPageSize(10);
		query.setPageNum(0);
		poiSearch = new PoiSearch(this, query);
		poiSearch.setOnPoiSearchListener(new PoiSearchListener());
		poiSearch.searchPOIAsyn();
	}

	/**
	 * POI搜索返回事件类
	 * 
	 * @author Smile
	 * 
	 */
	class PoiSearchListener implements OnPoiSearchListener {
		@Override
		public void onPoiItemSearched(PoiItem poiItem, int arg1) {

		}

		@Override
		public void onPoiSearched(PoiResult result, int rCode) {
			dissmissProgressDialog();
			if (rCode == 0) {
				if (result != null && result.getQuery() != null) {
					if (result.getQuery().equals(query)) {
						poiResult = result;
						List<PoiItem> poiItems = poiResult.getPois();
						List<SuggestionCity> suggestionCities = poiResult
								.getSearchSuggestionCitys();

						if (poiItems != null && poiItems.size() > 0) {
							aMap.clear();// 清理之前的图标
							aMap.setMyLocationStyle(myLocationStyle);
							PoiOverlay poiOverlay = new PoiOverlay(aMap,
									poiItems);
							poiOverlay.removeFromMap();
							poiOverlay.addToMap();
							poiOverlay.zoomToSpan();
							addAddressList(poiItems);
						} else if (suggestionCities != null
								&& suggestionCities.size() > 0) {
							showSuggestCity(suggestionCities);
						} else {
							ToastUtil.show(MainActivity.this,
									getString(R.string.no_result));
						}
					}
				} else {
					ToastUtil.show(MainActivity.this,
							getString(R.string.no_result));
				}
			} else if (rCode == 27) {
				ToastUtil.show(MainActivity.this,
						getString(R.string.error_network));
			} else if (rCode == 32) {
				ToastUtil.show(MainActivity.this,
						getString(R.string.error_key));
			} else {
				ToastUtil.show(MainActivity.this,
						getString(R.string.error_other));
			}
		}

	}

	/**
	 * 生成地理位置列表
	 * 
	 * @param poiItems
	 */
	private void addAddressList(List<PoiItem> poiItems) {
		ListView view = (ListView) findViewById(R.id.address_list);
		view.setVisibility(View.VISIBLE);
		aMap.getUiSettings().setZoomPosition(
				AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
		final ListViewAdapter adapter = new ListViewAdapter(this, poiItems);
		attendAddress.setText(poiItems.get(0).getProvinceName()
				+ poiItems.get(0).getCityName() + poiItems.get(0).getSnippet());
		attendLatLonPoint = poiItems.get(0).getLatLonPoint();
		view.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		view.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.setSelectIndex(position);
				TextView addressTextView = (TextView) view
						.findViewById(R.id.item_bottom);
				attendAddress
						.setText(addressTextView.getText().toString());
				attendLatLonPoint = (LatLonPoint) view.getTag(R.id.LatLonPoint);
			}
		});
	}

	/**
	 * 展示建议城市
	 * 
	 * @param cities
	 */
	private void showSuggestCity(List<SuggestionCity> cities) {
		String infomation = "推荐城市\n";
		for (int i = 0; i < cities.size(); i++) {
			infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
					+ cities.get(i).getCityCode() + "城市编码:"
					+ cities.get(i).getAdCode() + "\n";
		}
		ToastUtil.show(MainActivity.this, infomation);
	}

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		aMap.setMyLocationStyle(myLocationStyle);
		flag = true;
		if (mlocationClient == null) {
			mlocationClient = new AMapLocationClient(this);
			mLocationOption = new AMapLocationClientOption();
			mlocationClient.setLocationListener(this);
			mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
			mLocationOption.setNeedAddress(true);
			mlocationClient.setLocationOption(mLocationOption);
			mlocationClient.startLocation();
		}
	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (mListener != null && amapLocation != null) {
			if (amapLocation.getErrorCode() == 0) {
				mListener.onLocationChanged(amapLocation);
				if (flag) {
					String address = amapLocation.getAddress();
					setLatLon(amapLocation.getLatitude(),
							amapLocation.getLongitude());
					if (firstOpenMap) {
						attendAddress.setText(amapLocation.getAddress());
						aMap.addMarker(new MarkerOptions()
								.anchor(0.5f, 0.5f)
								.icon(BitmapDescriptorFactory.
										fromResource(R.drawable.location_marker))
								.position(
										new LatLng(amapLocation.getLatitude(),
												amapLocation.getLongitude()))
								.snippet(amapLocation.getAddress()));
						firstOpenMap = false;
					} else {
						attendAddress.setText(address);
						doSearchQuery(address);
					}
					flag = false;
				}
				cityName = amapLocation.getCity();
			} else {
				deactivate();
				String errText = "定位失败," + amapLocation.getErrorInfo();
				if (amapLocation.getErrorCode() == 12) {
					final NormalDialog dialog = new NormalDialog(this);
					dialog.content("定位服务未开启，请打开定位服务!")
							.style(NormalDialog.STYLE_TWO)
							.titleTextSize(23)
							.btnText("打开", "取消")
							.btnTextColor(Color.parseColor("#383838"),
									Color.parseColor("#D4D4D4"))//
							.btnTextSize(16f, 16f).show();

					dialog.setOnBtnClickL(new OnBtnClickL() {
						@Override
						public void onBtnClick() {
							dialog.superDismiss();
							Intent intent = new Intent(
									Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivityForResult(intent, 0);
							return;
						}
					}, new OnBtnClickL() {
						@Override
						public void onBtnClick() {
							dialog.dismiss();
						}
					});

				} else {
					ToastUtil.show(this, errText);
				}
				Log.e("AmapErr", errText);
			}
		}
	}

	private void setLatLon(double latitude, double longitude) {
		if (attendLatLonPoint == null) {
			attendLatLonPoint = new LatLonPoint(latitude, longitude);
		} else {
			attendLatLonPoint.setLatitude(latitude);
			attendLatLonPoint.setLongitude(longitude);
		}
	}

	@Override
	public void deactivate() {
		mListener = null;
		if (mlocationClient != null) {
			mlocationClient.stopLocation();
			mlocationClient.onDestroy();
		}
		mlocationClient = null;
		mLocationOption = null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		deactivate();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	private void showProgressDialog(String str) {
		if (progDialog == null)
			progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(false);
		progDialog.setMessage(str);
		progDialog.show();
	}

	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}
}


















