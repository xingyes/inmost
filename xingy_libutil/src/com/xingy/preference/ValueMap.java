package com.xingy.preference;

import java.util.Vector;

final public class ValueMap 
{
	/**
	* Create a new Instance ValueMap.  
	 */
	public ValueMap()
	{
		mValues = null;
	}
	
	/**
	* method Name:addValue    
	* method Description:  
	* @param aKey
	* @param aValue   
	* void  
	* @exception   
	* @since  1.0.0
	 */
	public void addValue(final String aKey, final String aValue)
	{
		if ( 0 >= aKey.length() )
			return ;
		
		if ( null == mValues )
		{
			mValues = new Vector<Element>();
		}
		
		final int nCount = mValues.size();
		for ( int nIdx = 0; nIdx < nCount; nIdx++ )
		{
			Element pEntry = mValues.elementAt(nIdx);
			if ( (null != pEntry) && (pEntry.getKey().equals(aKey)) )
			{
				pEntry.setValue(aValue);
				return ;
			}
		}
		
		// Create a new instance.
		Element pEntry = new Element(aKey, aValue);
		mValues.add(pEntry);
	}
	
	/**
	* method Name:setValue    
	* method Description:  
	* @param aKey
	* @param aValue   
	* void  
	* @exception   
	* @since  1.0.0
	 */
	public boolean setValue(final String aKey, final String aValue)
	{
		if ( (0 >= aKey.length()) || (null == mValues) )
			return false;
		
		final int nCount = mValues.size();
		for ( int nIdx = 0; nIdx < nCount; nIdx++ )
		{
			Element pEntry = mValues.elementAt(nIdx);
			if ( (null != pEntry) && (pEntry.getKey().equals(aKey)) )
			{
				pEntry.setValue(aValue);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	* method Name:size    
	* method Description:  
	* @return   
	* int  
	* @exception   
	* @since  1.0.0
	 */
	public int size()
	{
		return (null != mValues ? mValues.size() : 0);
	}
	
	/**
	* method Name:clear    
	* method Description:     
	* void  
	* @exception   
	* @since  1.0.0
	 */
	public void clear()
	{
		if ( null != mValues )
		{
			mValues.clear();
		}
	}
	
	/**
	* method Name:elementAt    
	* method Description:  
	* @param nIndex
	* @return   
	* ValueMap.ValEntry  
	* @exception   
	* @since  1.0.0
	 */
	public Element elementAt(int nIndex)
	{
		final int nCount = (null != mValues ? mValues.size() : 0);
		if ( (0 > nIndex) || (nIndex >= nCount) )
			return null;
		
		return mValues.elementAt(nIndex);
	}
	
	/**
	* method Name:getElement    
	* method Description:  
	* @param strKey
	* @return   
	* Element  
	* @exception   
	* @since  1.0.0
	 */
	public Element getElement(final String strKey)
	{
		for ( Element pElement : mValues )
		{
			if ( pElement.mKey.equals(strKey) )
				return pElement;
		}
		
		return null;
	}
	
	/**
	 * toString
	 */
	public String toString(final String aConnect, final String aSplit)
	{
		mString = "";
		final int nCount = (null != mValues ? mValues.size() : 0);
		for ( int nIdx = 0; nIdx < nCount; nIdx++ )
		{
			final Element pEntry = mValues.elementAt(nIdx);
			if ( mString.length() > 0 )
			{
				mString += aSplit;
			}
			
			// Append the item.
			mString += pEntry.getKey() + aConnect + pEntry.getValue();
		}
		
		return mString;
	}
	
	/**
	* Class Name:ValEntry 
	* Class Description: 
	* Author: lorenchen 
	* Modify: lorenchen 
	* Modify Date: Mar 26, 2011 11:53:32 AM 
	* Modify Remarks: 
	* @version 1.0.0
	*
	 */
	public final class Element
	{
		/**
		* Create a new Instance ValueMap.  
		* @param aKey
		* @param aValue
		 */
		public Element(final String aKey, final String aValue)
		{
			mKey = aKey;
			mValue = aValue;
		}
		
		/**
		* method Name:getKey    
		* method Description:  
		* @return   
		* String  
		* @exception   
		* @since  1.0.0
		 */
		public final String getKey()
		{
			return mKey;
		}
		
		/**
		* method Name:getValue    
		* method Description:  
		* @return   
		* String  
		* @exception   
		* @since  1.0.0
		 */
		public final String getValue()
		{
			return mValue;
		}
		
		/**
		* method Name:setValue    
		* method Description:  
		* @param aValue   
		* void  
		* @exception   
		* @since  1.0.0
		 */
		public void setValue(final String aValue)
		{
			mValue = aValue;
		}
		
		// Member instance.
		private String  mKey;
		private String  mValue;
	}
	
	private Vector<Element>   mValues;  // Values.
	private String            mString;  // String cache.
}
