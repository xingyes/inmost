package com.inmost.imbra.basic;

import android.text.TextUtils;

import com.xingy.lib.IAreav2;
import com.xingy.lib.IPageCache;
import com.xingy.lib.model.FullDistrictModel;
import com.xingy.util.Config;

import org.json.JSONException;
import org.json.JSONObject;

public class BasicParamModel {

    public static final String  BRAND_CACHE_KEY = "BRAND_CACHE_KEY";
    public static final String  FUNC_CACHE_KEY  = "FUNC_CACHE_KEY";
    public static final String  PRICE_CACHE_KEY = "PRICE_CACHE_KEY";
    public static final String  GUIDE_CACHE_KEY = "GUIDE_CACHE_KEY";

    public static final String  OPTION_CACHE_KEY = "OPTION_CACHE_KEY";
    public static final String  STOREMAP_CACHE_KEY = "STOREMAP_CACHE_KEY";

    public BasicParamItemModel  brandModel;
    public BasicParamItemModel  funcModel;
    public BasicParamItemModel  pricerangeModel;
    public BasicParamItemModel  guideModel;
    public BasicParamItemModel  optiontypeModel;
    public BasicParamItemModel  storeMapdModel;

    public BasicParamItemModel  sizeModel;
    public BasicParamItemModel  cupModel;

    public long               areaVer;
    public int  filterBrandIdx = -1;
    public int  filterFuncIdx = -1;
    public  int   filterHighPriceIdx = -1;
    public  int   filterLowPriceIdx = -1;
    public  int   filterHomeIdx = -1;

    public BasicParamModel()
    {
        clear();
        setCupSize();
    }

    public void clear() {
        if (null == brandModel)
            brandModel = new BasicParamItemModel();
        else
            brandModel.clear();
        if (null == funcModel)
            funcModel = new BasicParamItemModel();
        else
            funcModel.clear();
        if (null == pricerangeModel)
            pricerangeModel = new BasicParamItemModel();
        else
            pricerangeModel.clear();
        if (null == guideModel)
            guideModel = new BasicParamItemModel();
        else
            guideModel.clear();
        if (null == optiontypeModel)
            optiontypeModel = new BasicParamItemModel();
        else
            optiontypeModel.clear();
        if (null==storeMapdModel)
            storeMapdModel = new BasicParamItemModel();
        else
            storeMapdModel.clear();
        if(null == sizeModel)
            sizeModel = new BasicParamItemModel();
        else
            sizeModel.clear();
        if(null == cupModel)
            cupModel = new BasicParamItemModel();
        else
            cupModel.clear();

    }

    /**
     *
     * @param json
     */
    public void parse(JSONObject json) {
        IPageCache cache = new IPageCache();
        JSONObject item = json.optJSONObject("brand");
        long ver = 0;
        if(item!=null)
        {
            ver = item.optLong("v");
            if(ver!=brandModel.ver) {
                brandModel.clear();
                brandModel.parse(item);
                cache.set(BRAND_CACHE_KEY, item.toString(), 86400);
            }
        }

        item = json.optJSONObject("brafunc");
        if(item!=null)
        {
            ver = item.optLong("v");
            if(ver!=funcModel.ver) {
               funcModel.clear();
                funcModel.parse(item);
                cache.set(FUNC_CACHE_KEY, item.toString(), 86400);
            }
        }

        item = json.optJSONObject("pricerange");
        if(item!=null)
        {
            ver = item.optLong("v");
            if(ver!=pricerangeModel.ver) {
                pricerangeModel.clear();
                pricerangeModel.parse(item);
                cache.set(PRICE_CACHE_KEY, item.toString(), 86400);
            }
        }

        item = json.optJSONObject("guide");
        if(item!=null)
        {
            ver = item.optLong("v");
            if(ver!=guideModel.ver) {
                guideModel.clear();
                guideModel.parse(item);
                cache.set(GUIDE_CACHE_KEY, item.toString(), 86400);
            }
        }

        item = json.optJSONObject("optiontype");
        if(item!=null)
        {
            ver = item.optLong("v");
            if(ver!=optiontypeModel.ver) {
                optiontypeModel.clear();
                optiontypeModel.parse(item);

                ParamDtModel pm = new ParamDtModel();
                pm.id = "0";
                pm.info = "全部";
                optiontypeModel.dtArray.add(0,pm);
                optiontypeModel.dtStrArray.add(0,pm.info);

                cache.set(OPTION_CACHE_KEY, item.toString(), 86400);
            }
        }

        item = json.optJSONObject("storesmap");
        if(item!=null)
        {
            ver = item.optLong("v");
            if(ver!=storeMapdModel.ver) {
                storeMapdModel.clear();
                storeMapdModel.parse(item);
                cache.set(STOREMAP_CACHE_KEY, item.toString(), 86400);
            }
        }

        item = json.optJSONObject("addresses");
        if(null!=item)
        {
            ver = item.optLong("v");
            if(areaVer!=ver)
                cache.set(Config.DISTRICT_PARAM_CACHEKEY, item.toString(), 86400);
        }


        //setCupSize();

    }

    private void setCupSize()
    {
        ParamDtModel pd = new ParamDtModel();
        pd.info = "A";
        cupModel.dtArray.add(pd);
        cupModel.dtStrArray.add(pd.info);

        pd = new ParamDtModel();
        pd.info = "B";
        cupModel.dtArray.add(pd);
        cupModel.dtStrArray.add(pd.info);

        pd = new ParamDtModel();
        pd.info = "C";
        cupModel.dtArray.add(pd);
        cupModel.dtStrArray.add(pd.info);

        pd = new ParamDtModel();
        pd.info = "D";
        cupModel.dtArray.add(pd);
        cupModel.dtStrArray.add(pd.info);

        pd = new ParamDtModel();
        pd.info = "E";
        cupModel.dtArray.add(pd);
        cupModel.dtStrArray.add(pd.info);

        pd = new ParamDtModel();
        pd.info = "F";
        cupModel.dtArray.add(pd);
        cupModel.dtStrArray.add(pd.info);

        pd = new ParamDtModel();
        pd.info = "G";
        cupModel.dtArray.add(pd);
        cupModel.dtStrArray.add(pd.info);

        /**
         * size
         */

        pd = new ParamDtModel();
        pd.info = "60";
        sizeModel.dtArray.add(pd);
        sizeModel.dtStrArray.add(pd.info);
        pd = new ParamDtModel();
        pd.info = "65";
        sizeModel.dtArray.add(pd);
        sizeModel.dtStrArray.add(pd.info);
        pd = new ParamDtModel();
        pd.info = "70";
        sizeModel.dtArray.add(pd);
        sizeModel.dtStrArray.add(pd.info);
        pd = new ParamDtModel();
        pd.info = "75";
        sizeModel.dtArray.add(pd);
        sizeModel.dtStrArray.add(pd.info);
        pd = new ParamDtModel();
        pd.info = "80";
        sizeModel.dtArray.add(pd);
        sizeModel.dtStrArray.add(pd.info);
        pd = new ParamDtModel();
        pd.info = "85";
        sizeModel.dtArray.add(pd);
        sizeModel.dtStrArray.add(pd.info);
    }


    public void loadCache()
    {
        IPageCache cache = new IPageCache();
        JSONObject  json = null;
        String content="";
        try {
            content = cache.get(BRAND_CACHE_KEY);
            if (!TextUtils.isEmpty(content)) {
                json = new JSONObject(content);
                brandModel.parse(json);
            }

            content = cache.get(FUNC_CACHE_KEY);
            if (!TextUtils.isEmpty(content)) {
                json = new JSONObject(content);
                funcModel.parse(json);
            }

            content = cache.get(PRICE_CACHE_KEY);
            if (!TextUtils.isEmpty(content)) {
                json = new JSONObject(content);
                pricerangeModel.parse(json);
            }

            content = cache.get(GUIDE_CACHE_KEY);
            if (!TextUtils.isEmpty(content)) {
                json = new JSONObject(content);
                guideModel.parse(json);
            }

            content = cache.get(OPTION_CACHE_KEY);
            if (!TextUtils.isEmpty(content)) {
                json = new JSONObject(content);
                optiontypeModel.parse(json);

                ParamDtModel pm = new ParamDtModel();
                pm.id = "0";
                pm.info = "全部";
                optiontypeModel.dtArray.add(0,pm);
                optiontypeModel.dtStrArray.add(0,pm.info);
            }

            content = cache.get(STOREMAP_CACHE_KEY);
            if (!TextUtils.isEmpty(content)) {
                json = new JSONObject(content);
                storeMapdModel.parse(json);
            }

            content = cache.get(Config.DISTRICT_PARAM_CACHEKEY);
            if (!TextUtils.isEmpty(content)) {
                json = new JSONObject(content);
                areaVer = json.optLong("v");
            }
        }catch (JSONException e)
        {

        }

    }

}
