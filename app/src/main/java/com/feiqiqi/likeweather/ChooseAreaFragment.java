package com.feiqiqi.likeweather;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.feiqiqi.likeweather.db.City;
import com.feiqiqi.likeweather.db.County;
import com.feiqiqi.likeweather.db.Province;
import com.feiqiqi.likeweather.util.HttpUtil;
import com.feiqiqi.likeweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;

    private TextView titleTxt;
    private LinearLayout backBtn;
    private ListView listView;

    private ArrayAdapter<String> arrayAdapter;

    private List<String> dataList = new ArrayList<>();

    /**
     * 省
     */
    private List<Province> provincesList;
    /**
     * 市
     */
    private List<City> cityList;
    /**
     * 县
     */
    private List<County> countyList;
    /**
     * 选中省份
     */
    private Province selectProvince;
    /**
     * 选中市
     */
    private City selectCity;
    /**
     * 当前选择级别
     */
    private int currentLevel;


    /**
     * 找到控件的实例
     * 初始化ArrayAdapter，将其设置为ListView的适配器
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleTxt = (TextView) view.findViewById(R.id.title_txt);
        backBtn = (LinearLayout) view.findViewById(R.id.back_btn);
        listView = (ListView) view.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);

        listView.setAdapter(arrayAdapter);
        return view;
    }

    /**
     * 为ListView及Button设置单击事件
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //如果当前选中级别为省级
                if (currentLevel == LEVEL_PROVINCE) {
                    //选择省份就为选中的那一项省份信息
                    selectProvince = provincesList.get(position);
                    //查询市级信息
                    queryCity();

                } else if (currentLevel == LEVEL_CITY) {
                    selectCity = cityList.get(position);
                    queryCounty();
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //如果当前选中级别为县级
                if (currentLevel == LEVEL_COUNTY) {
                    //返回时查询市级信息
                    queryCity();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvince();
                }
            }
        });

        //默认查询省级信息
        queryProvince();
    }

    /**
     * 查询省份信息，优先从数据库查询
     * 如果没有再去服务器查询
     */
    private void queryProvince() {

        titleTxt.setText("中国");
        //
        backBtn.setVisibility(View.GONE);
        //查询所有省级信息
        provincesList = DataSupport.findAll(Province.class);
        if (provincesList.size() > 0) {
            dataList.clear();
            //遍历所有数据
            for (Province province : provincesList) {
                //将省级名字显示在list里面
                dataList.add(province.getProvinceName());
            }

            //刷新适配器
            arrayAdapter.notifyDataSetChanged();
            //默认选择第一项
            listView.setSelection(0);
            //当前选中级别为省级
            currentLevel = LEVEL_PROVINCE;
        }//如果数据库没有数据
        else {
            //创建地址
            String address = "http://guolin.tech/api/china";
            //调用查询服务器数据的方法
            queryFromServer(address, "province");
        }
    }

    /**
     * 查询市级信息，优先从数据库查询
     * 如果没有再去服务器查询
     */
    private void queryCity() {

        //设置标题名为上级选中的省级名
        titleTxt.setText(selectProvince.getProvinceName());
        backBtn.setVisibility(View.VISIBLE);
        //根据上级选中的省级id查询市级信息
        cityList = DataSupport.where("provinceId = ?", String.valueOf(selectProvince.getId())).find(City.class);
        if (cityList.size() > 0) {

            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    /**
     * 查询县级信息，优先从数据库查询
     * 如果没有再去服务器查询
     */
    private void queryCounty() {
        titleTxt.setText(selectCity.getCityName());
        backBtn.setVisibility(View.VISIBLE);
        //根据上级选中的市级id查询县级信息
        countyList = DataSupport.where("cityId = ?", String.valueOf(selectCity.getId())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {

            int provinceCode = selectProvince.getProvinceCode();
            int cityCode = selectCity.getCityId();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");

        }

    }

    /**
     * 根据传入的地址及类型从服务器上查询省市县数据
     */
    private void queryFromServer(String address, final String type) {
        //显示进度对话框
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String responseTxt = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseTxt);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseTxt, selectProvince.getId());

                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseTxt, selectCity.getId());
                }

                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvince();
                            } else if ("city".equals(type)) {
                                queryCity();
                            } else if ("county".equals(type)) {
                                queryCounty();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                //通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }


    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            //不可取消
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
