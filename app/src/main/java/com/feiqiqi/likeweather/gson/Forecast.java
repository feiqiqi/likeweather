package com.feiqiqi.likeweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 未来几天预测
 */
public class Forecast {

    /**
     * 日期
     */
    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    /**
     * 天气状况
     */
    @SerializedName("cond")
    public More more;

    public class Temperature {

        /**
         * 最高温
         */
        public String max;

        /**
         * 最低温
         */
        public String min;
    }

    public class More {
        @SerializedName("tet_d")
        public String info;
    }
}
