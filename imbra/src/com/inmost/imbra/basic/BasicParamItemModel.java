package com.inmost.imbra.basic;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class BasicParamItemModel {

    public long  ver;
    public String msg;
    public ArrayList<ParamDtModel> dtArray;
    public ArrayList<String> dtStrArray;
    public int          idx = -1;

    public BasicParamItemModel()
    {
        clear();
    }

    public void clear() {
        if (null == dtArray)
            dtArray = new ArrayList<ParamDtModel>();
        else
            dtArray.clear();
        if(dtStrArray == null)
            dtStrArray = new ArrayList<String>();
        else
            dtStrArray.clear();
        ver = 0;
        msg= "";
        idx = -1;
    }


    /**
     *
     * @param json
     */
    public void parse(JSONObject json) {
        clear();
        int err = json.optInt("err",-1);
        if(err!=0)
            return;

        msg = json.optString("msg");
        ver = json.optLong("v");
        JSONArray dta = json.optJSONArray("dt");
        for (int i = 0; null != dta && i < dta.length(); i++) {
            ParamDtModel item = new ParamDtModel();
            item.parse(dta.optJSONObject(i));
            dtArray.add(item);
            dtStrArray.add(item.info);
        }

    }
}
