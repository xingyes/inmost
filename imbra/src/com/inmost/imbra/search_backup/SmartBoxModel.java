package com.inmost.imbra.search_backup;

import java.util.ArrayList;

class SmartBoxModel {
	
	private ArrayList<AutoCompleteModel> mAutoCompleteModels;
	private AutoCompleteCatModel mAutoCompleteCatModel;
	
	public ArrayList<AutoCompleteModel> getAutoComleteModels()
	{
		return this.mAutoCompleteModels;
	}
	public void setAutoCompleteModels(ArrayList<AutoCompleteModel> models)
	{
		this.mAutoCompleteModels = models;
	}
	
	public AutoCompleteCatModel getAutoCompleteCatModel()
	{
		return this.mAutoCompleteCatModel;
	}
	public void setAutoCompleteCatModel(AutoCompleteCatModel model)
	{
		this.mAutoCompleteCatModel = model;
	}

}
