package com.feiqiqi.likeweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 适应度，洗车，运动推荐
 */
public class Suggestion {

    /**
     * 适应度
     */
    @SerializedName("comf")
    public Comfort comfort;

    /**
     * 洗车
     */
    @SerializedName("cw")
    public CarWash carWash;

    /**
     * 运动
     */
    @SerializedName("sport")
    public Sport sport;


    public class Comfort {
        @SerializedName("txt")
        public String info;
    }

    public class CarWash {

        @SerializedName("txt")
        public String info;
    }

    public class Sport {

        @SerializedName("txt")
        public String info;
    }
}
