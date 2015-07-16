package com.inmost.imbra.search;

import com.xingy.lib.model.BaseModel;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchModel extends BaseModel implements Serializable, Cloneable { 
	private static final long 	serialVersionUID 	= 2L;
	public static final int 	SORT_DEFAULT 		= 0;
	public static final int 	SORT_SALE_DEC 		= 1;
	public static final int 	SORT_PRICE_DEC 		= 2;
	public static final int 	SORT_PRICE_ASC 		= 3;
	public static final int 	SORT_REVIEW 		= 4;
	
	public static final int 	SORT_HASGOOD_ON 	= 1;
	public static final int 	SORT_HASGOOD_OFF 	= 0;

	private int currentPage = 0;		// 当前页
	private int pageSize = 20;			// 页大小
	private int sort = SORT_DEFAULT;	// 排序字段( 0:默认, 1: 销量降序, 2: 价格升序, 3:价格降序  4: 评论 )
	private String path;				// 导航id路径
	private String option ;				// 属性
	private String keyWord;				// 关键字
	private String classid;				// 品类id
	private int areaCode;				// 站点id
	
	private String mCategoryName;		// 类目名称
	private int level;					// 类目级别
	private String manufacturer;		// 品牌
	private String price;				// 价格
	
	private int hasGood = SORT_HASGOOD_OFF;			// 有货筛选   1：进行有货筛选  0：不进行筛选

	
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}

	public int getHasGood()
	{
		return this.hasGood;
	}
	public void setHasGood(int hasgood) {
		this.hasGood = hasgood;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	
	public String getCategoryName(){
		return mCategoryName;
	}
	
	public void setCategoryName(String pCategoryName){
		this.mCategoryName = pCategoryName;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getKeyWord() {
		return keyWord;
	}
	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public void setOption(String option){
		this.option = option;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public void setAreaCode(int areaCode) {
		this.areaCode = areaCode;
	}
	
	public int getAreaCode(){
		return this.areaCode;
	}
	public String getClassId() {
		return classid;
	}
	public void setClassId(String classid) {
		this.classid = classid;
	}
	
	// http://list.51buy.com/608-0-6-11-24-0-1-4279e16649 a 3215e10631f3216e1093-.html
	
	public void setOption(int attrId, int optionId){
		
		option = option == null ? "" : option;

		String attr = String.valueOf(attrId), opt = String.valueOf(optionId), item = attr + "e" + opt;

		if( option.indexOf( attr + "e" )  > -1 ){
			option = option.replaceAll("(" + attr + "e\\d+)", "$1o" + opt );
		}
		else{
			option = option.equals("") ? item : ( option + "a" + item );
		}
	}	
	
	public String getOption(){
		return option;
	}
	
	public String [] getOptions(int attrId){
		
		if( option == null )	return null;
		
		Pattern p = Pattern.compile("(?:^|a)" + attrId + "e([\\d+o]+)");
		Matcher m = p.matcher(option);
		String g = null;
		while(m.find()) {
            g = m.group(1);
    		return g.split("o");
        }
		
		return null;
	}
	
/*	@Override
	public Object clone() {  
        SearchModel o = null;  
        try {  
            o = (SearchModel) super.clone();  
        } catch (CloneNotSupportedException e) {  
            e.printStackTrace();  
        }  
        return o;  
	}*/
}
