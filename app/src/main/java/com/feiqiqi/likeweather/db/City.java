package com.feiqiqi.likeweather.db;

import org.litepal.crud.DataSupport;

/*市数据*/
public class City extends DataSupport {

    private int id;

    /*市名字*/
    private String cityName;

    /*市id*/
    private int cityId;

    /*当前市所属省id*/
    private int provinceId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
