package com.zl.weatherfootprint.util;

import com.zl.weatherfootprint.db.WeatherFootprintDBHelper;
import com.zl.weatherfootprint.model.City;
import com.zl.weatherfootprint.model.County;
import com.zl.weatherfootprint.model.Province;

public class Utility {
	
	public static boolean handleProvinceResponse(WeatherFootprintDBHelper dbhelper, String response) {
		if (!WTextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");
			if (!WTextUtils.isArrayEmpty(allProvinces)) {
				for (String p : allProvinces) {
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					dbhelper.saveProvince(province);
				}
				return true;
			}
			
		}
		return false;
	}
	
	public static boolean handleCitiesResponse(WeatherFootprintDBHelper dbhelper, String response, int provinceId) {
		if (!WTextUtils.isEmpty(response)) {
			String[] allCities = response.split(",");
			if (!WTextUtils.isArrayEmpty(allCities)) {
				for (String c : allCities) {
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					dbhelper.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	public static boolean handleCountiesResponse(WeatherFootprintDBHelper dbhelper, String response, int cityId) {
		if (!WTextUtils.isEmpty(response)) {
			String[] allCounties = response.split(",");
			if (!WTextUtils.isArrayEmpty(allCounties)) {
				for (String c : allCounties) {
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					dbhelper.saveCounty(county);
				}
				return true;
			}
		}
		return false; 
	}

}
