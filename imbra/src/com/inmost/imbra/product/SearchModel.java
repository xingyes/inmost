package com.inmost.imbra.product;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchModel {

    public static final String CACHE_KEY = "bra_search_params";
    public ArrayList<Integer> sizeArray;
    public ArrayList<SearchParamModel> cupArray;
    public ArrayList<SearchParamModel> brandArray;
    public ArrayList<String>           brandStringArray;
    public ArrayList<SearchParamModel> tagArray;
    public ArrayList<String>           tagStringArray;
    public ArrayList<Integer> priceArray;
    public ArrayList<String>  homeTagArray;
    public long expireDate;

    public int      filterTagIdx = -1;
    public int      filterBrandIdx = -1;
    public int      filterLowPrice = -1;
    public int      filterHighPrice = -1;

    public int      filterHomeIdx = -1;



    public void clear() {
        if (null == sizeArray)
            sizeArray = new ArrayList<Integer>();
        if (null == cupArray)
            cupArray = new ArrayList<SearchParamModel>();
        if (null == brandArray)
            brandArray = new ArrayList<SearchParamModel>();
        if (null == tagArray)
            tagArray = new ArrayList<SearchParamModel>();
        if (null == priceArray)
            priceArray = new ArrayList<Integer>();
        if(null == brandStringArray)
            brandStringArray = new ArrayList<String>();
        if(null == tagStringArray)
            tagStringArray = new ArrayList<String>();

        if(null==homeTagArray)
            homeTagArray = new ArrayList<String>();
        sizeArray.clear();
        cupArray.clear();
        brandArray.clear();
        tagArray.clear();
        priceArray.clear();
        tagStringArray.clear();
        brandStringArray.clear();
        expireDate = -1;
    }


    /**
     *
     * @param json
     */
    public void parse(JSONObject json) {
        clear();
        JSONArray brands = json.optJSONArray("brands");
        for (int i = 0; null != brands && i < brands.length(); i++) {
            SearchParamModel item = new SearchParamModel();
            item.parse(brands.optJSONObject(i));
            brandArray.add(item);
            brandStringArray.add(item.info);
        }

        JSONArray tags = json.optJSONArray("tags");
        for (int i = 0; null != tags && i < tags.length(); i++) {
            SearchParamModel item = new SearchParamModel();
            item.parse(tags.optJSONObject(i));
            tagArray.add(item);
            tagStringArray.add(item.info);
        }
        JSONArray sizes = json.optJSONArray("sizes");
        for (int i = 0; null != sizes && i < sizes.length(); i++) {
            SearchParamModel item = new SearchParamModel();
            item.info = sizes.optString(i);
            item.id = item.info;
            cupArray.add(item);
        }

        for (int i = 0; i < 6; i++)
            sizeArray.add(60 + 5 * i);
        for (int i = 0; i < 4; i++)
            priceArray.add(200 + 200 * i);


        expireDate = json.optLong("expireDate");
        homeTagArray.add("所有");
        homeTagArray.add("你好少女");
        homeTagArray.add("潮我看齐");
        homeTagArray.add("你好少女");
        homeTagArray.add("成熟女人");


    }

    public void setTagIdx(int position) {
        filterTagIdx = position;
    }
    public void setBrandIdx(int position) {
        filterBrandIdx = position;
    }
}
