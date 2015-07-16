package com.xingy.lib.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xingy.R;

import java.util.ArrayList;
import java.util.List;

public class RadioDialog extends AppDialog implements android.widget.AdapterView.OnItemClickListener
{
	
	/**  
	* Create a new Instance AppRadioDialog.  
	*  
	* @param aContext
	* @param aListener  
	*/
	public RadioDialog(Context aContext, AppDialog.OnClickListener aListener, RadioAdapter aAdapter)
	{
		super(aContext, aListener);
		mAdapter = (null == aAdapter ? new RadioAdapter(aContext) : aAdapter);
	}
	
	/**
	 * 
	*   
	* Class Name:OnRadioSelectListener 
	* Class Description: 
	* Author: xingyao 
	* Modify: xingyao 
	* Modify Date: 2012-3-8 ����06:30:23 
	* Modify Remarks: 
	* @version 1.0.0
	*
	 */
	public interface OnRadioSelectListener
	{
		/**
		 * onDialogClick
		 * @param nButtonId
		 */
		public abstract void onRadioItemClick(int which);
	}
	
	/**
	 * 
	* method Name:setRadioSelectListener    
	* method Description:  
	* @param aListener
	* void  
	* @exception   
	* @since  1.0.0
	 */
	public void setRadioSelectListener(OnRadioSelectListener aListener)
	{
		mRadioListener = aListener;
	}
	
	/**
	 * setSelection
	 * @param nPosition
	 */
	public void setSelection(int nPosition)
	{
		final int nCount = (null != mListView ? mListView.getCount() : 0);
		//if ( (nPosition >= 0) && (nPosition < nCount) )
		if (nPosition < nCount) 
		{
			mAdapter.setPickIdx(nPosition);
			mAdapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * setProperty
	 * @param strCaption
	 * @param strMessage
	 * @param nPostiveBtnTextId
	 * @param nNegativeBtnTextId
	 */
	public void setProperty(String strCaption)
	{
		(mCaption = getComponent(mCaption)).mText = strCaption;
	}
	
	/**
	 * setProperty
	 * @param nCaptionId
	 * @param nMessageId
	 * @param nPostiveBtnTextId
	 * @param nNegativeBtnTextId
	 */
	public void setProperty(int nCaptionId)
	{
		Context pContext = getContext();
		String strCaption = pContext.getString(nCaptionId);
		
		setProperty(strCaption);
		strCaption = null;
	}
	
	/**
	 * 
	* method Name:setList    
	* method Description:  
	* @param aList   
	* void  
	* @exception   
	* @since  1.0.0
	 */
	public void setList(List<String> aList, int nCheckedItem)
	{
		if( null != mAdapter ){
			mAdapter.setList(aList, nCheckedItem);
			mSelectId = nCheckedItem;
		}
	}
	
	public void setList(String[] aOptions, int nCheckedItem) {
		if( null != mAdapter ) {
			mAdapter.setList(aOptions, nCheckedItem);
			mSelectId = nCheckedItem;
		}
	}
	
	/**
	 * onCreate
	 */
	@Override
	protected void onCreate(Bundle aSavedInstanceState)
	{
		super.onCreate(aSavedInstanceState);
		
		// Load the default configuration.
		setContentView(R.layout.dialog_radio);
		
		(mCaption = getComponent(mCaption)).mView = (TextView)findViewById(R.id.radio_dialog_caption);
		mListView = (ListView)findViewById(R.id.radio_list);
		mListView.setOnItemClickListener(this);
		if(null == mAdapter)
			return;
		
		mListView.setAdapter(mAdapter);	
		mListView.setSelection(mSelectId);
	
		this.updateUi();
	}
	
	
	/**
	 * updateUi
	 * Update the UI configuration.
	 */
	protected void updateUi()
	{
		Component aComponents[] = {mCaption};//, mMessage, mPositive, mNegative};
		for ( int nIdx = 0; nIdx < aComponents.length; nIdx++ )
		{
			Component pComponent = aComponents[nIdx];
			pComponent.mView.setText(pComponent.mText);
		}
		
		super.setAttributes();
	}
	
	/*  
	 * Description:
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		//change view
		mAdapter.setPickIdx(pos);
		mAdapter.notifyDataSetChanged();
		//notify out
		mRadioListener.onRadioItemClick(pos);
		this.dismiss();
	}
	
	// Member instances.
	private	 int		mSelectId;
	private  ListView   mListView;
	private  RadioAdapter mAdapter;
	private OnRadioSelectListener  mRadioListener;
	
	/**
	 * Implementation of RadioAdapter
	 * @author lorenchen
	 */
	public static class RadioAdapter extends BaseAdapter
	{
		protected List<String> mList;
		protected Context      mContext;
		protected int          mPickIdx;
			
		/**
		 * 
		* Create a new Instance ChapterAdapter.  
		*
		*/
		public RadioAdapter(Context aContext)
		{
			mList = new ArrayList<String>();
			mContext = aContext;
			mPickIdx = 0;
		}
			
		/**
		 * 
		* method Name:setList    
		* method Description:  
		* @param aList   
		* void  
		* @exception   
		* @since  1.0.0
		 */
		public void setList(List<String> aList, int nCheckedItem)
		{
			mList.clear();
			if(null!=aList)
				mList.addAll(aList);
			
			mPickIdx = nCheckedItem;
		}
		
		public void setList(String[] aOptions, int nCheckedItem) {
			mList.clear();
			final int nLength = (null != aOptions ? aOptions.length : 0);
			for( int nIdx = 0; nIdx < nLength; nIdx++ ) {
				mList.add(aOptions[nIdx]);
			}
			
			mPickIdx = nCheckedItem;
		}
		
		/**
		 * 
		* method Name:setPickIdx    
		* method Description:  
		* @param aIndex   
		* void  
		* @exception   
		* @since  1.0.0
		 */
		public void setPickIdx(int aIndex)
		{
			mPickIdx = aIndex;
		}
		/*  
		 * Description:
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			return (null==mList) ? 0 : mList.size();
		}

		/*  
		 * Description:
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int position) {
			return (null==mList) ? null : mList.get(position);
		}

		/*  
		 * Description:
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}

		/*  
		 * Description:
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ItemHolder holder = null;
			if (null == convertView)
			{
				convertView = View.inflate(mContext, R.layout.radio_item, null);
				holder = new ItemHolder();
				holder.mTick = (ImageView) convertView.findViewById(R.id.radio_item_tick);
				holder.mName = (TextView) convertView.findViewById(R.id.radio_item_name);
				holder.mLine = (View) convertView.findViewById(R.id.radio_item_line);
				convertView.setTag(holder);
			}
			else
			{
				holder = (ItemHolder) convertView.getTag();
			}
			
			// set data
			String strName = mList.get(position);
			holder.mName.setText(strName);
			holder.mName.setTextColor(mContext.getResources().getColor(mPickIdx == position ? R.color.filter_item_checked : R.color.global_text_color ));
			holder.mTick.setVisibility(mPickIdx == position ? View.VISIBLE : View.GONE);
			
			if( position == (mList.size() - 1 ) ) {
				holder.mLine.setVisibility(View.INVISIBLE);
			}else{
				holder.mLine.setVisibility(View.VISIBLE);
			}
			
			return convertView;
		}
		
		private class ItemHolder
		{
			ImageView mTick;
			TextView  mName;
			View mLine;
		}
	}
}
