package com.feiqiqi.likeweather.util;

import android.text.TextUtils;

import com.feiqiqi.likeweather.db.City;
import com.feiqiqi.likeweather.db.County;
import com.feiqiqi.likeweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * 解析与处理JSON类型数据
 */
public class Utility {

    /**
     * 解析及处理服务器返回的省级数据
     */

    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {

                /**使用JSONArray和JSONObject将数据解析出来，然后组装成实体类对象，最后调用save()方法保存数据到数据库*/
                JSONArray allProvinse = new JSONArray(response);
                for (int i = 0; i < allProvinse.length(); i++) {
                    JSONObject provinceObject = allProvinse.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCityResponse(String response, int provinceId) {

        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCity = new JSONArray(response);
                for (int i = 0; i < allCity.length(); i++) {
                    JSONObject cityObject = allCity.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityId(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * 解析并处理服务器返回的县级数据
     */

    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounty = new JSONArray(response);
                for (int i = 0; i < allCounty.length(); i++) {
                    JSONObject countyObject = allCounty.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
