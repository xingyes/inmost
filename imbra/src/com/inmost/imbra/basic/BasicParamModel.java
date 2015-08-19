package com.inmost.imbra.basic;

import com.xingy.lib.IAreav2;
import com.xingy.lib.model.FullDistrictModel;

import org.json.JSONObject;

public class BasicParamModel {

    public BasicParamItemModel  brandModel;
    public BasicParamItemModel  funcModel;
    public BasicParamItemModel  pricerangeModel;
    public BasicParamItemModel  guideModel;
    public BasicParamItemModel  optiontypeModel;
    public BasicParamItemModel  storeMapdModel;

    public BasicParamItemModel  sizeModel;
    public BasicParamItemModel  cupModel;

    public int  filterBrandIdx = -1;
    public int  filterFuncIdx = -1;
    public  int   filterHighPriceIdx = -1;
    public  int   filterLowPriceIdx = -1;
    public  int   filterHomeIdx = -1;

    public BasicParamModel()
    {
        clear();
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
        clear();
        JSONObject item = json.optJSONObject("brand");
        if(item!=null)
        {
            brandModel.parse(item);
        }

        item = json.optJSONObject("brafunc");
        if(item!=null)
        {
            funcModel.parse(item);
        }

        item = json.optJSONObject("pricerange");
        if(item!=null)
        {
            pricerangeModel.parse(item);
        }

        item = json.optJSONObject("guide");
        if(item!=null)
        {
            guideModel.parse(item);
        }

        item = json.optJSONObject("optiontype");
        if(item!=null)
        {
            optiontypeModel.parse(item);
        }

        item = json.optJSONObject("storesmap");
        if(item!=null)
        {
            storeMapdModel.parse(item);
        }
        JSONObject brands = json.optJSONObject("brand");
        if(brands!=null)
        {
            brandModel.parse(brands);
        }


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

}
