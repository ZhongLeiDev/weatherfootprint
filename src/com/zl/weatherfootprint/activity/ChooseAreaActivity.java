package com.zl.weatherfootprint.activity;

import java.util.ArrayList;
import java.util.List;

import com.zl.weatherfootprint.R;
import com.zl.weatherfootprint.announcementutils.AnnounceUtils.InjectView;
import com.zl.weatherfootprint.announcementutils.InjectMethods;
import com.zl.weatherfootprint.db.WeatherFootprintDBHelper;
import com.zl.weatherfootprint.model.City;
import com.zl.weatherfootprint.model.County;
import com.zl.weatherfootprint.model.Province;
import com.zl.weatherfootprint.util.HttpCallbackListener;
import com.zl.weatherfootprint.util.HttpUtil;
import com.zl.weatherfootprint.util.Utility;
import com.zl.weatherfootprint.util.WTextUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity{
	@InjectView(id = R.id.title_text) 
	TextView titleText;
	@InjectView(id = R.id.list_view) 
	ListView listView;
	private enum ItemLevel {
		LEVEL_PROVINCE,
		LEVEL_CITY,
		LEVEL_COUNTY
	}
	private ProgressDialog progressDialog;
	private ArrayAdapter<String> adapter;
	private WeatherFootprintDBHelper dbhelper;
	private List<String> dataList = new ArrayList<String>();
	
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	
	private Province selectedProvince;
	private City selectedCity;
	private ItemLevel currentItemLevel;
	
	private long currentTime = 0;
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		
		InjectMethods.autoInjectAllField(this);//初始化控件绑定
		dbhelper = WeatherFootprintDBHelper.getInstance(this);
		
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dataList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (currentItemLevel == ItemLevel.LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(position);
					queryCities();
				} else if (currentItemLevel == ItemLevel.LEVEL_CITY) {
					selectedCity = cityList.get(position);
					queryCounties();
				}
			}			
		});
		queryProvinces();
	}
	
	/**
	 * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
	 */
	private void queryProvinces() {
		// TODO Auto-generated method stub
		provinceList = dbhelper.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentItemLevel = ItemLevel.LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "province");
		}
	}

	private void queryCounties() {
		// TODO Auto-generated method stub
		countyList = dbhelper.loadCounties(selectedCity.getId());
		if (countyList.size() > 0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentItemLevel = ItemLevel.LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}

	private void queryCities() {
		// TODO Auto-generated method stub
		cityList = dbhelper.loadCities(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentItemLevel = ItemLevel.LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}
	
	private void queryFromServer(final String code, final String type) {
		// TODO Auto-generated method stub
		String address;
		if (!WTextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendGetHttprequest(address, new HttpCallbackListener() {

			@Override
			public void onFinished(String response) {
				// TODO Auto-generated method stub
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvinceResponse(dbhelper, response);
				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(dbhelper, response, selectedProvince.getId());
				} else if ("county".equals(type)) {
					result = Utility.handleCountiesResponse(dbhelper, response, selectedCity.getId());
				}
				if (result) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("county".equals(type)) {
								queryCounties();
							}
						}								
					});
				}
				
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(getApplicationContext(), "加载失败!", Toast.LENGTH_SHORT).show();
					}
					
				});
			}
			
		});
	}

	private void showProgressDialog() {
		// TODO Auto-generated method stub
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCancelable(false);
		}
		progressDialog.show();
	}
	
	private void closeProgressDialog() {
		// TODO Auto-generated method stub
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	
	@Override
	public void onBackPressed() {
		if (currentItemLevel == ItemLevel.LEVEL_COUNTY) {
			queryCities();
		} else if (currentItemLevel == ItemLevel.LEVEL_CITY) {
			queryProvinces();
		} else {
			if (System.currentTimeMillis() - currentTime < 1000) {
				finish();
			}
			currentTime = System.currentTimeMillis();
			Toast.makeText(getApplicationContext(), "再次点击退出应用！", Toast.LENGTH_SHORT).show();
		}
	}
	

}
